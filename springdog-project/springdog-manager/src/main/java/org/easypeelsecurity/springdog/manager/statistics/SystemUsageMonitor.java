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

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;

/**
 * Monitor system usage.
 *
 * @author PENEKhun
 */
public class SystemUsageMonitor implements SystemUsageMetrics {

  @Override
  public Double systemCpuUsagePercent() {
    OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
    if (osBean instanceof com.sun.management.OperatingSystemMXBean operatingSystemMXBean) {
      return formatDouble(operatingSystemMXBean.getCpuLoad() * 100);
    }
    return null;
  }

  @Override
  public Double heapMemoryUsagePercent() {
    MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
    MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
    long usedMemory = heapMemoryUsage.getUsed();
    long maxMemory = heapMemoryUsage.getMax();
    return formatDouble((double) usedMemory / maxMemory) * 100;
  }

  @Override
  public Double diskUsagePercent() {
    File root = new File("/");
    long totalSpace = root.getTotalSpace();
    long freeSpace = root.getFreeSpace();
    return formatDouble((double) (totalSpace - freeSpace) / totalSpace) * 100;
  }
}
