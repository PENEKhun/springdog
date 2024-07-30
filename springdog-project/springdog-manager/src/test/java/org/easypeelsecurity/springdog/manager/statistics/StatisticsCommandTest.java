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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.easypeelsecurity.springdog.shared.ratelimit.model.Endpoint;
import org.easypeelsecurity.springdog.shared.statistics.model.EndpointMetric;
import org.easypeelsecurity.springdog.shared.statistics.model.SystemMetric;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.configuration.CayenneRuntime;
import org.apache.cayenne.query.ObjectSelect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class StatisticsCommandTest {

  @Mock
  private CayenneRuntime mockCayenneRuntime;

  @Mock
  private ObjectContext mockContext;

  @InjectMocks
  private StatisticsCommand statisticsCommand;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    when(mockCayenneRuntime.newContext()).thenReturn(mockContext);
  }

  @Test
  void testStoreSystemMetrics() {
    // given
    double cpuUsage = 50.0;
    double memoryUsage = 60.0;
    double diskUsage = 70.0;

    SystemMetric mockSystemMetric = mock(SystemMetric.class);
    when(mockContext.newObject(SystemMetric.class)).thenReturn(mockSystemMetric);

    // when
    statisticsCommand.storeSystemMetrics(cpuUsage, memoryUsage, diskUsage);

    // then
    verify(mockSystemMetric).setCpuUsagePercent(cpuUsage);
    verify(mockSystemMetric).setMemoryUsagePercent(memoryUsage);
    verify(mockSystemMetric).setDiskUsagePercent(diskUsage);
    verify(mockContext).commitChanges();
  }

  @Test
  void testStoreSystemMetricsWithInvalidValues() {
    // given invalid values
    double invalidCpuUsage = -10.0;
    double invalidMemoryUsage = 110.0;
    double invalidDiskUsage = -100.0;

    // when & then
    assertThrows(IllegalArgumentException.class, () -> {
      statisticsCommand.storeSystemMetrics(invalidCpuUsage, 50.0, 50.0);
    });
    assertThrows(IllegalArgumentException.class, () -> {
      statisticsCommand.storeSystemMetrics(50.0, invalidMemoryUsage, 50.0);
    });
    assertThrows(IllegalArgumentException.class, () -> {
      statisticsCommand.storeSystemMetrics(50.0, 50.0, invalidDiskUsage);
    });
  }

  @Test
  void testUpsertEndpointMetricsWithInvalidEndpoint() {
    // given
    String targetHandler =
        "java.lang.String org.easypeelsecurity.springdogtest.ExampleController.example(java.lang.String)";
    long[] responseTimes = {100L, 200L, 300L, 500L, 400L};
    long failureCount = 10L;

    Endpoint mockEndpoint = mock(Endpoint.class);
    EndpointMetric mockEndpointMetric = mock(EndpointMetric.class);

    when(ObjectSelect.query(Endpoint.class)
        .where(Endpoint.METHOD_SIGNATURE.eq(targetHandler))
        .selectOne(mockContext)).thenReturn(mockEndpoint);

    when(ObjectSelect.query(EndpointMetric.class)
        .where(EndpointMetric.ENDPOINT.eq(mockEndpoint).andExp(EndpointMetric.METRIC_DATE.eq(LocalDate.now())))
        .selectOne(mockContext)).thenReturn(mockEndpointMetric);

    // when & then
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          statisticsCommand.upsertEndpointMetrics("unknown-methodSignature", responseTimes, failureCount,
              LocalDate.now());
        }, "Endpoint not found");
  }

  @Test
  void testUpsertEndpointMetricsWithInvalidResponseTime() {
    // given
    String targetHandler =
        "java.lang.String org.easypeelsecurity.springdogtest.ExampleController.example(java.lang.String)";
    long[] responseTimes = {100L, 200L, 300L, 500L, 400L, -200L};

    // when & then
    assertThrows(IllegalArgumentException.class,
        () -> {
          statisticsCommand.upsertEndpointMetrics(targetHandler, responseTimes, 10L,
              LocalDate.now());
        }, "Response time must be greater than 0");
  }

  @Test
  void testUpsertEndpointMetricsWithInvalidFailureCount() {
    // given
    String targetHandler =
        "java.lang.String org.easypeelsecurity.springdogtest.ExampleController.example(java.lang.String)";
    long failureCount = -10L;

    // when & then
    assertThrows(IllegalArgumentException.class,
        () -> {
          statisticsCommand.upsertEndpointMetrics(targetHandler, new long[] {10L, 10L}, failureCount,
              LocalDate.now());
        }, "Failure count must be non-negative");
  }
}
