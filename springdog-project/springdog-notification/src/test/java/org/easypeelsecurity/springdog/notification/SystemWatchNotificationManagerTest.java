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

package org.easypeelsecurity.springdog.notification;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.Map;

import org.easypeelsecurity.springdog.notification.email.MetricContext;
import org.easypeelsecurity.springdog.notification.email.SystemWatchEmailNotification;
import org.easypeelsecurity.springdog.shared.configuration.SystemWatchProperties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SystemWatchNotificationManagerTest {

  @Mock
  private SystemWatchProperties properties;

  @Mock
  private SystemWatchEmailNotification emailNotification;

  @Mock
  private MetricContext cpuContext;

  @Mock
  private MetricContext memoryContext;

  @Mock
  private MetricContext diskContext;

  @InjectMocks
  private SystemWatchNotificationManager manager;

  @BeforeEach
  void setUp() throws NoSuchFieldException, IllegalAccessException {
    MockitoAnnotations.openMocks(this);
    when(properties.getCpuThreshold()).thenReturn(80.0);
    when(properties.getMemoryThreshold()).thenReturn(90.0);
    when(properties.getDiskThreshold()).thenReturn(85.0);

    manager = new SystemWatchNotificationManager(properties, emailNotification) {
      @Override
      protected void initializeMetricContexts() {
        // Do nothing, we'll set the contexts using reflection
      }
    };

    // Use reflection to set metricContexts
    Field metricContextsField = SystemWatchNotificationManager.class.getDeclaredField("metricContexts");
    metricContextsField.setAccessible(true);
    Map<String, MetricContext> contexts = Map.of(
        "CPU", cpuContext,
        "Memory", memoryContext,
        "Disk", diskContext
    );
    metricContextsField.set(manager, contexts);
  }

  @Test
  void checkMetrics_AllBelowThreshold() {
    when(properties.isEnabled()).thenReturn(true);
    manager.checkMetrics(70.0, 80.0, 75.0);

    verify(cpuContext).checkMetric(70.0, 80.0);
    verify(memoryContext).checkMetric(80.0, 90.0);
    verify(diskContext).checkMetric(75.0, 85.0);
  }

  @Test
  void checkMetrics_CpuAboveThreshold() {
    when(properties.isEnabled()).thenReturn(true);
    manager.checkMetrics(85.0, 80.0, 75.0);

    verify(cpuContext).checkMetric(85.0, 80.0);
    verify(memoryContext).checkMetric(80.0, 90.0);
    verify(diskContext).checkMetric(75.0, 85.0);
  }

  @Test
  void checkMetrics_MemoryAboveThreshold() {
    when(properties.isEnabled()).thenReturn(true);
    manager.checkMetrics(70.0, 95.0, 75.0);

    verify(cpuContext).checkMetric(70.0, 80.0);
    verify(memoryContext).checkMetric(95.0, 90.0);
    verify(diskContext).checkMetric(75.0, 85.0);
  }

  @Test
  void checkMetrics_DiskAboveThreshold() {
    when(properties.isEnabled()).thenReturn(true);
    manager.checkMetrics(70.0, 80.0, 90.0);

    verify(cpuContext).checkMetric(70.0, 80.0);
    verify(memoryContext).checkMetric(80.0, 90.0);
    verify(diskContext).checkMetric(90.0, 85.0);
  }

  @Test
  void checkMetrics_AllAboveThreshold() {
    when(properties.isEnabled()).thenReturn(true);
    manager.checkMetrics(90.0, 95.0, 95.0);

    verify(cpuContext).checkMetric(90.0, 80.0);
    verify(memoryContext).checkMetric(95.0, 90.0);
    verify(diskContext).checkMetric(95.0, 85.0);
  }

  @Test
  void testCheckMetrics_Disabled() {
    when(properties.isEnabled()).thenReturn(false);

    manager.checkMetrics(85.0, 70.0, 95.0);

    // Verify that checkMetric was not called for any metric
    verify(cpuContext, never()).checkMetric(anyDouble(), anyDouble());
    verify(memoryContext, never()).checkMetric(anyDouble(), anyDouble());
    verify(diskContext, never()).checkMetric(anyDouble(), anyDouble());
  }
}
