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

import java.util.List;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;

/**
 * Monitor system usage.
 *
 * @author PENEKhun
 */
public class SystemUsageMonitor implements SystemUsageMetrics {
  private final SystemInfo systemInfo;
  private final HardwareAbstractionLayer hardware;

  /**
   * Constructor.
   */
  public SystemUsageMonitor() {
    this.systemInfo = new SystemInfo();
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
}
