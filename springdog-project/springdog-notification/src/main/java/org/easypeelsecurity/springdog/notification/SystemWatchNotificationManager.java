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

package org.easypeelsecurity.springdog.notification;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import org.easypeelsecurity.springdog.notification.email.MetricContext;
import org.easypeelsecurity.springdog.notification.email.SystemWatchEmailNotification;
import org.easypeelsecurity.springdog.shared.settings.NotificationGlobalSetting;
import org.easypeelsecurity.springdog.shared.settings.SpringdogSettingManager;
import org.easypeelsecurity.springdog.shared.settings.SpringdogSettings;
import org.easypeelsecurity.springdog.shared.settings.SystemWatchSetting;

/**
 * Manages the overall system watch process.
 * This class coordinates the checking of all system metrics.
 */
@Service
public class SystemWatchNotificationManager {
  private final Map<String, MetricContext<String, Double>> metricContexts;
  private final SpringdogSettingManager settingManager;
  private final SystemWatchEmailNotification emailNotification;

  /**
   * Constructs a new SystemWatchManager.
   *
   * @param emailNotification The email notification service to use.
   */
  public SystemWatchNotificationManager(SpringdogSettingManager settingManager,
      SystemWatchEmailNotification emailNotification) {
    this.settingManager = settingManager;
    this.emailNotification = emailNotification;
    this.metricContexts = new HashMap<>();
    initializeMetricContexts();
  }

  /**
   * Initializes the metric contexts for CPU, Memory, and Disk.
   */
  protected void initializeMetricContexts() {
    metricContexts.put("CPU", new MetricContext("CPU", emailNotification));
    metricContexts.put("Memory", new MetricContext("Memory", emailNotification));
    metricContexts.put("Disk", new MetricContext("Disk", emailNotification));
  }

  /**
   * Checks all system metrics against their respective thresholds.
   *
   * @param cpuUsage    The current CPU usage.
   * @param memoryUsage The current memory usage.
   * @param diskUsage   The current disk usage.
   */
  public void checkMetrics(double cpuUsage, double memoryUsage, double diskUsage) {
    SpringdogSettings springdogSettings = settingManager.getSettings();
    SystemWatchSetting systemWatchSetting = springdogSettings.getSystemWatchSetting();
    NotificationGlobalSetting notificationGlobalSetting = springdogSettings.getNotificationGlobalSetting();
    if (!systemWatchSetting.isEnabled() || !notificationGlobalSetting.isEnabled()) {
      return;
    }

    metricContexts.get("CPU").checkMetric(cpuUsage, systemWatchSetting.getCpuThreshold());
    metricContexts.get("Memory").checkMetric(memoryUsage, systemWatchSetting.getMemoryThreshold());
    metricContexts.get("Disk").checkMetric(diskUsage, systemWatchSetting.getDiskThreshold());
  }
}
