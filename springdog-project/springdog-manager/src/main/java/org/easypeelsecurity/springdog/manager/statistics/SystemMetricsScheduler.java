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

import java.util.ArrayList;
import java.util.List;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import org.easypeelsecurity.springdog.notification.email.EmailService;
import org.easypeelsecurity.springdog.notification.email.content.ContentParameters;
import org.easypeelsecurity.springdog.notification.email.content.EmailContent;
import org.easypeelsecurity.springdog.notification.email.content.systemwatch.SystemWatchEmailContent;
import org.easypeelsecurity.springdog.notification.email.content.systemwatch.SystemWatchParameters;
import org.easypeelsecurity.springdog.shared.configuration.SystemWatchProperties;

/**
 * Scheduler to monitor system usage.
 *
 * @author PENEKhun
 */
@Component
@EnableScheduling
public class SystemMetricsScheduler {

  private final StatisticsCommand statisticsCommand;
  private final SystemWatchProperties systemWatchProperties;
  private final EmailService emailService;

  /**
   * Constructor.
   */
  public SystemMetricsScheduler(StatisticsCommand statisticsCommand,
      SystemWatchProperties systemWatchProperties, EmailService emailService) {
    this.statisticsCommand = statisticsCommand;
    this.systemWatchProperties = systemWatchProperties;
    this.emailService = emailService;
  }

  /**
   * Store system metrics.
   */
  @Scheduled(fixedRateString = "${springdog.systemMetricsScheduler.fixedRate:300000}")
  public void storeSystemsMetrics() {
    SystemUsageMonitor systemUsageMonitor = new SystemUsageMonitor();

    double cpuUsagePercent = systemUsageMonitor.systemCpuUsagePercent();
    double memoryUsagePercent = systemUsageMonitor.getSystemMemoryUsagePercent();
    double diskUsagePercent = systemUsageMonitor.diskUsagePercent();

    statisticsCommand.storeSystemMetrics(cpuUsagePercent, memoryUsagePercent, diskUsagePercent);
    List<EmailContent> emailMessages = new ArrayList<>();
    if (systemWatchProperties.isEnabled()) {
      if (cpuUsagePercent > systemWatchProperties.getCpuThreshold() &&
          systemWatchProperties.getCpuThreshold() != 0) {
        EmailContent emailContent = new SystemWatchEmailContent();
        ContentParameters parameters = new SystemWatchParameters("CPU", cpuUsagePercent);
        emailContent.setParameters(parameters);
        emailMessages.add(emailContent);
      }
      if (memoryUsagePercent > systemWatchProperties.getMemoryThreshold() &&
          systemWatchProperties.getMemoryThreshold() != 0) {
        EmailContent emailContent = new SystemWatchEmailContent();
        ContentParameters parameters = new SystemWatchParameters("Memory", memoryUsagePercent);
        emailContent.setParameters(parameters);
        emailMessages.add(emailContent);
      }
      if (diskUsagePercent > systemWatchProperties.getDiskThreshold() &&
          systemWatchProperties.getDiskThreshold() != 0) {
        EmailContent emailContent = new SystemWatchEmailContent();
        ContentParameters parameters = new SystemWatchParameters("Disk", diskUsagePercent);
        emailContent.setParameters(parameters);
        emailMessages.add(emailContent);
      }

      if (!emailMessages.isEmpty()) {
        emailService.sendMails(emailMessages.toArray(new EmailContent[0]));
      }
    }
  }
}
