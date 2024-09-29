/*
 * Copyright 2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.easypeelsecurity.springdog.shared.settings;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SpringdogSettingManagerImplTest {

  private SpringdogSettingManagerImpl settingManager;
  private static final String SPRINGDOG_SETTING_FILE = "springdog-settings.json";
  private File settingsFile;

  @TempDir
  Path tempDir;

  @BeforeEach
  public void setUp() {
    settingsFile = tempDir.resolve(SPRINGDOG_SETTING_FILE).toFile();
    settingManager = new SpringdogSettingManagerImpl(settingsFile);
  }

  @AfterEach
  public void tearDown() {
    if (settingsFile.exists()) {
      settingsFile.delete();
    }
  }

  @Test
  void testGetSettings_DefaultSettings() {
    SpringdogSettings settings = settingManager.getSettings();
    assertNotNull(settings);
    assertNotNull(settings.getNotificationGlobalSetting());
    assertNotNull(settings.getSystemWatchSetting());
    assertNotNull(settings.getSlowResponseSetting());
    assertTrue(settingsFile.exists());
  }

  @Test
  void testUpdateSystemWatchSetting() {
    // given
    SystemWatchSetting newSetting = new SystemWatchSetting();
    newSetting.setEnabled(true);
    newSetting.setCpuThreshold(50);

    // when
    settingManager.updateSystemWatchSetting(newSetting);

    // then
    SpringdogSettings settings = new SpringdogSettingManagerImpl(settingsFile).getSettings();
    assertThat(settings.getSystemWatchSetting().isEnabled()).isTrue();
    assertThat(settings.getSystemWatchSetting().getCpuThreshold()).isEqualTo(50);
  }

  @Test
  void testUpdateSlowResponseSetting() {
    // given
    SlowResponseSetting newSetting = new SlowResponseSetting();
    newSetting.setEnabled(true);
    newSetting.setResponseTimeMs(1027);

    // when
    settingManager.updateSlowResponseSetting(newSetting);

    // then
    SpringdogSettings settings = new SpringdogSettingManagerImpl(settingsFile).getSettings();
    assertThat(settings.getSlowResponseSetting().isEnabled()).isTrue();
    assertThat(settings.getSlowResponseSetting().getResponseTimeMs()).isEqualTo(1027);
  }

  @Test
  void testConcurrentAccess() throws InterruptedException {
    Thread thread1 = new Thread(() -> {
      SystemWatchSetting newSetting = new SystemWatchSetting();
      newSetting.setEnabled(true);
      newSetting.setCpuThreshold(20);
      settingManager.updateSystemWatchSetting(newSetting);
    });

    Thread thread2 = new Thread(() -> {
      SystemWatchSetting newSetting = new SystemWatchSetting();
      newSetting.setEnabled(false);
      newSetting.setCpuThreshold(30);
      settingManager.updateSystemWatchSetting(newSetting);
    });

    thread1.start();
    thread2.start();

    thread1.join();
    thread2.join();

    SpringdogSettings settings = new SpringdogSettingManagerImpl(settingsFile).getSettings();
    assertThat(settings.getSystemWatchSetting().isEnabled()).isFalse();
    assertThat(settings.getSystemWatchSetting().getCpuThreshold()).isEqualTo(30);
  }
}
