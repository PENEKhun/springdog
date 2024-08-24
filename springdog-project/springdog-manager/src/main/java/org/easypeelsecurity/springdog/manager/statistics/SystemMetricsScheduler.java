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

package org.easypeelsecurity.springdog.manager.statistics;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import org.easypeelsecurity.springdog.domain.statistics.StatisticsService;
import org.easypeelsecurity.springdog.notification.SystemWatchNotificationManager;

/**
 * Scheduler to monitor system usage.
 *
 * @author PENEKhun
 */
@Component
@EnableScheduling
public class SystemMetricsScheduler {

  private final StatisticsService statisticsService;
  private final SystemWatchNotificationManager systemWatchNotificationManager;

  /**
   * Constructor.
   */
  public SystemMetricsScheduler(StatisticsService statisticsService,
      SystemWatchNotificationManager systemWatchNotificationManager) {
    this.statisticsService = statisticsService;
    this.systemWatchNotificationManager = systemWatchNotificationManager;
  }

  /**
   * Store system metrics.
   */
  @Scheduled(fixedRateString = "${springdog.systemMetricsScheduler.fixedRate:20000}") // TODO: 시간 변경
  public void storeSystemsMetrics() {
    SystemUsageMonitor systemUsageMonitor = new SystemUsageMonitor();

    double cpuUsagePercent = systemUsageMonitor.systemCpuUsagePercent();
    double memoryUsagePercent = systemUsageMonitor.getSystemMemoryUsagePercent();
    double diskUsagePercent = systemUsageMonitor.diskUsagePercent();

    statisticsService.storeSystemMetrics(cpuUsagePercent, memoryUsagePercent, diskUsagePercent);
    systemWatchNotificationManager.checkMetrics(cpuUsagePercent, memoryUsagePercent, diskUsagePercent);
  }
}
