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

package org.easypeelsecurity.springdog.shared.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

/**
 * System metric DTO.
 *
 * @author PENEKhun
 */
@SuppressWarnings("all")
@Getter
public class SystemMetricDto {

  private Double cpuUsagePercent;
  private Double memoryUsagePercent;
  private Double diskUsagePercent;
  private Double jvmHeapUsagePercent;
  private Double jvmNonHeapUsagePercent;
  private Long jvmTotalMemoryUsed;
  private Long networkInBytes;
  private Long networkOutBytes;
  private LocalDateTime timestamp;

  @Builder
  public SystemMetricDto(Double cpuUsagePercent, Double memoryUsagePercent, Double diskUsagePercent,
      Double jvmHeapUsagePercent, Double jvmNonHeapUsagePercent, Long jvmTotalMemoryUsed,
      Long networkInBytes, Long networkOutBytes, LocalDateTime timestamp) {
    this.cpuUsagePercent = cpuUsagePercent;
    this.memoryUsagePercent = memoryUsagePercent;
    this.diskUsagePercent = diskUsagePercent;
    this.jvmHeapUsagePercent = jvmHeapUsagePercent;
    this.jvmNonHeapUsagePercent = jvmNonHeapUsagePercent;
    this.jvmTotalMemoryUsed = jvmTotalMemoryUsed;
    this.networkInBytes = networkInBytes;
    this.networkOutBytes = networkOutBytes;
    if (timestamp == null) {
      this.timestamp = LocalDateTime.now();
    } else {
      this.timestamp = timestamp;
    }
  }
}
