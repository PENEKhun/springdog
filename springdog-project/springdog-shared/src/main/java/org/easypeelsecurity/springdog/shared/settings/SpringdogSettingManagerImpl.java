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

import java.io.File;
import java.io.IOException;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

/**
 * SpringdogSettingManager.
 */

@Component
public class SpringdogSettingManagerImpl implements SpringdogSettingManager {
  private static final Logger logger = LoggerFactory.getLogger(SpringdogSettingManagerImpl.class);
  private static final String SPRINGDOG_SETTING_FILE = "springdog-settings.json";
  private final ObjectMapper objectMapper = new ObjectMapper();
  private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
  private SpringdogSettings settings;
  private final File settingsFile;

  /**
   * Constructor.
   */
  @Autowired
  public SpringdogSettingManagerImpl() {
    this(new File(SPRINGDOG_SETTING_FILE));
  }

  /**
   * Constructor.
   */
  public SpringdogSettingManagerImpl(File settingsFile) {
    this.settingsFile = settingsFile;
  }

  /**
   * Get or Load settings.
   * @return SpringdogSettings
   */
  public SpringdogSettings getSettings() {
    rwLock.readLock().lock();
    try {
      if (this.settings == null) {
        rwLock.readLock().unlock();
        rwLock.writeLock().lock();
        try {
          if (this.settings == null) {
            this.settings = loadOrCreateSettings();
          }
        } finally {
          rwLock.writeLock().unlock();
        }
        rwLock.readLock().lock();
      }
      return this.settings;
    } finally {
      rwLock.readLock().unlock();
    }
  }

  private void updateSettings(SpringdogSettings newSettings) {
    rwLock.writeLock().lock();
    try {
      this.settings = newSettings;
      saveSettings();
    } catch (IOException e) {
      throw new IllegalStateException("Failed to save springdog settings.", e);
    } finally {
      rwLock.writeLock().unlock();
    }
  }

  /**
   * Update system watch setting.
   * @param newSetting new setting
   */
  public void updateSystemWatchSetting(SystemWatchSetting newSetting) {
    rwLock.writeLock().lock();
    try {
      SpringdogSettings existSetting = getSettings();
      existSetting.changeSystemWatchSetting(newSetting);
      updateSettings(existSetting);
    } finally {
      rwLock.writeLock().unlock();
    }
  }

  /**
   * Update notification global setting.
   * @param newSetting new setting
   */
  public void updateNotificationGlobalSetting(NotificationGlobalSetting newSetting) {
    rwLock.writeLock().lock();
    try {
      SpringdogSettings existSetting = getSettings();
      existSetting.changeNotificationGlobalSetting(newSetting);
      updateSettings(existSetting);
    } finally {
      rwLock.writeLock().unlock();
    }
  }

  /**
   * Update slow response setting.
   * @param newSetting new setting
   */
  public void updateSlowResponseSetting(SlowResponseSetting newSetting) {
    rwLock.writeLock().lock();
    try {
      SpringdogSettings existSetting = getSettings();
      existSetting.changeSlowResponseSetting(newSetting);
      updateSettings(existSetting);
    } finally {
      rwLock.writeLock().unlock();
    }
  }

  private SpringdogSettings loadOrCreateSettings() {
    try {
      objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      if (!settingsFile.exists()) {
        SpringdogSettings defaultSetting = new SpringdogSettings();
        objectMapper.writeValue(settingsFile, defaultSetting);
        return defaultSetting;
      } else {
        return objectMapper.readValue(settingsFile, SpringdogSettings.class);
      }
    } catch (UnrecognizedPropertyException e) {
      logger.warn("Unknown property detected. Overwriting configuration with default settings.", e);
      SpringdogSettings defaultSetting = new SpringdogSettings();
      try {
        objectMapper.writeValue(settingsFile, defaultSetting);
      } catch (IOException ioException) {
        logger.error("Failed to write default settings.", ioException);
      }
      return defaultSetting;
    } catch (IOException e) {
      logger.error("Failed to load springdog settings.", e);
      return new SpringdogSettings(); // Return default settings instead of exiting
    }
  }

  private void saveSettings() throws IOException {
    objectMapper.writeValue(settingsFile, this.settings);
  }
}
