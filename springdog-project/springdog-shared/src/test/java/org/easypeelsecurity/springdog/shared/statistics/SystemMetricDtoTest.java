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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SystemMetricDtoTest {

  @Test
  @DisplayName("Should create SystemMetricDto object correctly.")
  void createSystemMetricDtoSuccessfully() {
    // given
    double cpuUsage = 75.5;
    double memoryUsage = 60.0;
    double diskUsage = 85.0;
    LocalDateTime timestamp = LocalDateTime.now();

    // when
    SystemMetricDto systemMetricDto = new SystemMetricDto(cpuUsage, memoryUsage, diskUsage, timestamp);

    // then
    assertEquals(cpuUsage, systemMetricDto.getCpuUsagePercent());
    assertEquals(memoryUsage, systemMetricDto.getMemoryUsagePercent());
    assertEquals(diskUsage, systemMetricDto.getDiskUsagePercent());
    assertEquals(timestamp, systemMetricDto.getTimestamp());
  }
}
