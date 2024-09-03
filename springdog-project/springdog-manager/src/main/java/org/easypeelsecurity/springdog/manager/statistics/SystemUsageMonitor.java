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

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.List;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;

/**
 * Monitor system usage.
 *
 * @author PENEKhun
 */
public class SystemUsageMonitor implements SystemUsageMetrics {
  private final SystemInfo systemInfo;
  private final MemoryMXBean memoryMXBean;
  private final HardwareAbstractionLayer hardware;

  /**
   * Constructor.
   */
  public SystemUsageMonitor() {
    this.systemInfo = new SystemInfo();
    this.memoryMXBean = ManagementFactory.getMemoryMXBean();
    this.hardware = systemInfo.getHardware();
  }

  @Override
  public Double getSystemMemoryUsagePercent() {
    GlobalMemory memory = hardware.getMemory();
    long totalMemory = memory.getTotal();
    long availableMemory = memory.getAvailable();
    double usedMemoryPercent = (double) (totalMemory - availableMemory) / totalMemory * 100;
    return formatDouble(usedMemoryPercent);
  }

  @Override
  public Double systemCpuUsagePercent() {
    CentralProcessor processor = hardware.getProcessor();
    double[] loadAverage = processor.getSystemLoadAverage(1);
    return formatDouble(loadAverage[0]);
  }

  @Override
  public Double diskUsagePercent() {
    FileSystem fileSystem = systemInfo.getOperatingSystem().getFileSystem();
    List<OSFileStore> fileStores = fileSystem.getFileStores();
    long totalSpace = 0;
    long usedSpace = 0;
    for (OSFileStore store : fileStores) {
      totalSpace += store.getTotalSpace();
      usedSpace += (store.getTotalSpace() - store.getUsableSpace());
    }
    if (totalSpace == 0) {
      return null;
    }
    return formatDouble((double) usedSpace / totalSpace * 100);
  }

  @Override
  public long getJvmHeapUsed() {
    return memoryMXBean.getHeapMemoryUsage().getUsed();
  }

  @Override
  public long getJvmHeapMax() {
    return memoryMXBean.getHeapMemoryUsage().getMax();
  }

  @Override
  public Double getJvmHeapUsagePercent() {
    MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
    return formatDouble((double) heapMemoryUsage.getUsed() / heapMemoryUsage.getMax() * 100);
  }

  @Override
  public long getJvmNonHeapUsed() {
    return memoryMXBean.getNonHeapMemoryUsage().getUsed();
  }

  @Override
  public long getJvmNonHeapMax() {
    return memoryMXBean.getNonHeapMemoryUsage().getMax();
  }

  @Override
  public Double getJvmNonHeapUsagePercent() {
    MemoryUsage nonHeapMemoryUsage = memoryMXBean.getNonHeapMemoryUsage();
    long max = nonHeapMemoryUsage.getMax();
    if (max < 0) {
      return -1.0; // Max is undefined
    }
    return formatDouble((double) nonHeapMemoryUsage.getUsed() / max * 100);
  }

  @Override
  public long getJvmTotalMemoryUsed() {
    return getJvmHeapUsed() + getJvmNonHeapUsed();
  }

  @Override
  public long getJvmTotalMemoryMax() {
    return getJvmHeapMax() + getJvmNonHeapMax();
  }

  @Override
  public Double getJvmTotalMemoryUsagePercent() {
    long totalUsed = getJvmTotalMemoryUsed();
    long totalMax = getJvmTotalMemoryMax();
    if (totalMax <= 0) {
      return -1.0; // Unable to calculate percentage
    }
    return formatDouble((double) totalUsed / totalMax * 100);
  }

  @Override
  public long getNetworkInBytes() {
    return hardware.getNetworkIFs().stream()
        .mapToLong(NetworkIF::getBytesRecv)
        .sum();
  }

  @Override
  public long getNetworkOutBytes() {
    return hardware.getNetworkIFs().stream()
        .mapToLong(NetworkIF::getBytesSent)
        .sum();
  }
}
