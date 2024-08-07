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
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.easypeelsecurity.springdog.shared.statistics.model.SystemMetric;

import org.apache.cayenne.ObjectContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SystemMetricConverterTest {

  @Mock
  private ObjectContext context;

  @Test
  @DisplayName("Should convert SystemMetric to SystemMetricDto correctly.")
  void convertSystemMetricToDtoSuccessfully() {
    // given
    SystemMetric systemMetric = new SystemMetric();
    systemMetric.setCpuUsagePercent(75.5);
    systemMetric.setMemoryUsagePercent(60.0);
    systemMetric.setDiskUsagePercent(85.0);
    systemMetric.setTimestamp(LocalDateTime.now());

    // when
    SystemMetricDto systemMetricDto = SystemMetricConverter.convert(systemMetric);

    // then
    assertEquals(systemMetric.getCpuUsagePercent(), systemMetricDto.getCpuUsagePercent());
    assertEquals(systemMetric.getMemoryUsagePercent(), systemMetricDto.getMemoryUsagePercent());
    assertEquals(systemMetric.getDiskUsagePercent(), systemMetricDto.getDiskUsagePercent());
    assertEquals(systemMetric.getTimestamp(), systemMetricDto.getTimestamp());
  }

  @Test
  @DisplayName("Should convert SystemMetricDto to SystemMetric correctly.")
  void convertDtoToSystemMetricSuccessfully() {
    // given
    SystemMetricDto systemMetricDto = new SystemMetricDto(75.5, 60.0, 85.0, LocalDateTime.now());
    SystemMetric systemMetricMock = new SystemMetric();
    when(context.newObject(SystemMetric.class)).thenReturn(systemMetricMock);

    // when
    SystemMetric systemMetric = SystemMetricConverter.convert(context, systemMetricDto);

    // then
    assertEquals(75.5, systemMetric.getCpuUsagePercent());
    assertEquals(60.0, systemMetric.getMemoryUsagePercent());
    assertEquals(85.0, systemMetric.getDiskUsagePercent());
    assertEquals(systemMetricDto.getTimestamp(), systemMetric.getTimestamp());
  }
}
