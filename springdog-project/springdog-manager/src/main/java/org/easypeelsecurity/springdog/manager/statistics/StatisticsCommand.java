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

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import org.easypeelsecurity.springdog.shared.statistics.model.SystemMetric;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.configuration.CayenneRuntime;

/**
 * Command for statistics.
 *
 * @author PENEKhun
 */
@Service
public class StatisticsCommand {
  private final CayenneRuntime springdogRepository;

  /**
   * Constructor.
   */
  public StatisticsCommand(@Qualifier("springdogRepository") CayenneRuntime springdogRepository) {
    this.springdogRepository = springdogRepository;
  }

  /**
   * Store system metrics.
   *
   * @param cpuUsagePercent    cpu usage percentage
   * @param memoryUsagePercent memory usage percentage
   * @param diskUsagePercent   disk usage percentage
   */
  public void storeSystemMetrics(double cpuUsagePercent, double memoryUsagePercent, double diskUsagePercent) {
    if (Double.isNaN(cpuUsagePercent) || Double.isNaN(memoryUsagePercent) || Double.isNaN(diskUsagePercent)) {
      throw new IllegalArgumentException("percentage must be a number");
    }

    if (cpuUsagePercent < 0 || cpuUsagePercent > 100) {
      throw new IllegalArgumentException("CPU usage percentage must be between 0 and 100");
    }

    if (memoryUsagePercent < 0 || memoryUsagePercent > 100) {
      throw new IllegalArgumentException("Memory usage percentage must be between 0 and 100");
    }

    if (diskUsagePercent < 0 || diskUsagePercent > 100) {
      throw new IllegalArgumentException("Disk usage percentage must be between 0 and 100");
    }

    ObjectContext context = springdogRepository.newContext();
    SystemMetric systemMetric = context.newObject(SystemMetric.class);
    systemMetric.setCpuUsagePercent(cpuUsagePercent);
    systemMetric.setMemoryUsagePercent(memoryUsagePercent);
    systemMetric.setDiskUsagePercent(diskUsagePercent);

    context.commitChanges();
  }
}
