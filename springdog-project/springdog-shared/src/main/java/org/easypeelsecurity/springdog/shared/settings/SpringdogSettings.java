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

import lombok.Getter;

/**
 * SpringdogSettings.
 */
@Getter
public class SpringdogSettings {
  private NotificationGlobalSetting notificationGlobalSetting;
  private SystemWatchSetting systemWatchSetting;
  private SlowResponseSetting slowResponseSetting;

  SpringdogSettings() {
    this.notificationGlobalSetting = new NotificationGlobalSetting();
    this.systemWatchSetting = new SystemWatchSetting();
    this.slowResponseSetting = new SlowResponseSetting();
  }

  /**
   * Change notification global setting.
   * @param newSetting new setting
   */
  public void changeNotificationGlobalSetting(NotificationGlobalSetting newSetting) {
    newSetting.validate();
    if (newSetting.getTemplates() == null || newSetting.getTemplates().isEmpty()) {
      newSetting.setTemplates(this.notificationGlobalSetting.getTemplates());
    }
    this.notificationGlobalSetting = newSetting;
  }

  /**
   * Change system watch setting.
   * @param newSetting new setting
   */
  public void changeSystemWatchSetting(SystemWatchSetting newSetting) {
    newSetting.validate();
    this.systemWatchSetting = newSetting;
  }

  /**
   * Change slow response setting.
   * @param newSetting new setting
   */
  public void changeSlowResponseSetting(SlowResponseSetting newSetting) {
    newSetting.validate();
    this.slowResponseSetting = newSetting;
  }
}
