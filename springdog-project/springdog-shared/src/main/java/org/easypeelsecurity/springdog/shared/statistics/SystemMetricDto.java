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

package org.easypeelsecurity.springdog.shared.statistics;

import java.time.LocalDateTime;

/**
 * System metric DTO.
 *
 * @author PENEKhun
 */
@SuppressWarnings("all")
public class SystemMetricDto {

  private Double cpuUsagePercent;
  private Double memoryUsagePercent;
  private Double diskUsagePercent;
  private LocalDateTime timestamp;

  public SystemMetricDto(double cpuUsagePercent, double memoryUsagePercent, double diskUsagePercent,
      LocalDateTime timestamp) {
    this.cpuUsagePercent = cpuUsagePercent;
    this.memoryUsagePercent = memoryUsagePercent;
    this.diskUsagePercent = diskUsagePercent;
    this.timestamp = timestamp;
  }

  public Double getCpuUsagePercent() {
    return cpuUsagePercent;
  }

  public Double getMemoryUsagePercent() {
    return memoryUsagePercent;
  }

  public Double getDiskUsagePercent() {
    return diskUsagePercent;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }
}
