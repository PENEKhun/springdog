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

package org.easypeelsecurity.springdog.domain.statistics.converter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.easypeelsecurity.springdog.domain.statistics.model.SystemMetric;
import org.easypeelsecurity.springdog.shared.dto.SystemMetricDto;

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
    SystemMetricDto systemMetricDto = SystemMetricDto.builder()
        .cpuUsagePercent(1.0)
        .memoryUsagePercent(2.0)
        .diskUsagePercent(3.0)
        .jvmHeapUsagePercent(4.0)
        .jvmNonHeapUsagePercent(5.0)
        .jvmTotalMemoryUsed(6L)
        .networkInBytes(7L)
        .networkOutBytes(8L)
        .timestamp(LocalDateTime.of(2024, 1, 1, 0, 0))
        .build();
    SystemMetric systemMetricMock = new SystemMetric();
    when(context.newObject(SystemMetric.class)).thenReturn(systemMetricMock);

    // when
    SystemMetric systemMetric = SystemMetricConverter.convert(context, systemMetricDto);

    // then
    assertThat(systemMetric.getCpuUsagePercent()).isEqualTo(1.0);
    assertThat(systemMetric.getMemoryUsagePercent()).isEqualTo(2.0);
    assertThat(systemMetric.getDiskUsagePercent()).isEqualTo(3.0);
    assertThat(systemMetric.getJvmHeapUsagePercent()).isEqualTo(4.0);
    assertThat(systemMetric.getJvmNonHeapUsagePercent()).isEqualTo(5.0);
    assertThat(systemMetric.getJvmTotalMemoryUsed()).isEqualTo(6L);
    assertThat(systemMetric.getNetworkInBytes()).isEqualTo(7L);
    assertThat(systemMetric.getNetworkOutBytes()).isEqualTo(8L);
    assertThat(systemMetric.getTimestamp()).isEqualTo(LocalDateTime.of(2024, 1, 1, 0, 0));
  }
}

