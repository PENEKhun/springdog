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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.easypeelsecurity.springdog.notification.email.MetricContext;
import org.easypeelsecurity.springdog.notification.email.SystemWatchEmailNotification;
import org.easypeelsecurity.springdog.shared.settings.NotificationGlobalSetting;
import org.easypeelsecurity.springdog.shared.settings.SpringdogSettingManager;
import org.easypeelsecurity.springdog.shared.settings.SpringdogSettings;
import org.easypeelsecurity.springdog.shared.settings.SystemWatchSetting;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SystemWatchNotificationManagerTest {

  @Mock
  private SpringdogSettingManager mockSettingManager;

  @Mock
  private SystemWatchEmailNotification mockEmailNotification;

  @Mock
  private SpringdogSettings mockSpringdogSettings;

  @Mock
  private SystemWatchSetting mockSystemWatchSetting;

  @Mock
  private NotificationGlobalSetting mockNotificationGlobalSetting;

  @Mock
  private Map<String, MetricContext<String, Double>> mockMetricContexts;

  @Mock
  private MetricContext<String, Double> mockCpuContext;

  @Mock
  private MetricContext<String, Double> mockMemoryContext;

  @Mock
  private MetricContext<String, Double> mockDiskContext;

  private SystemWatchNotificationManager manager;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    manager = new SystemWatchNotificationManager(mockSettingManager, mockEmailNotification) {
      @Override
      protected void initializeMetricContexts() {
        // Do nothing
      }
    };

    when(mockMetricContexts.get("CPU")).thenReturn(mockCpuContext);
    when(mockMetricContexts.get("Memory")).thenReturn(mockMemoryContext);
    when(mockMetricContexts.get("Disk")).thenReturn(mockDiskContext);

    try {
      var field = SystemWatchNotificationManager.class.getDeclaredField("metricContexts");
      field.setAccessible(true);
      field.set(manager, mockMetricContexts);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }

    when(mockSettingManager.getSettings()).thenReturn(mockSpringdogSettings);
    when(mockSpringdogSettings.getSystemWatchSetting()).thenReturn(mockSystemWatchSetting);
    when(mockSpringdogSettings.getNotificationGlobalSetting()).thenReturn(mockNotificationGlobalSetting);
  }

  @Test
  @DisplayName("System metrics are not checked when global notifications are disabled")
  void notificationDisabled() {
    when(mockNotificationGlobalSetting.isEnabled()).thenReturn(false);
    when(mockSystemWatchSetting.isEnabled()).thenReturn(true);

    manager.checkMetrics(70.0, 80.0, 75.0);

    verify(mockNotificationGlobalSetting).isEnabled();
    verifyNoInteractions(mockMetricContexts);
  }

  @Test
  @DisplayName("System metrics are not checked when system watch notifications are disabled")
  void systemWatchDisabled() {
    when(mockNotificationGlobalSetting.isEnabled()).thenReturn(true);
    when(mockSystemWatchSetting.isEnabled()).thenReturn(false);

    manager.checkMetrics(70.0, 80.0, 75.0);

    verify(mockSystemWatchSetting).isEnabled();
    verifyNoInteractions(mockMetricContexts);
  }

  @Test
  @DisplayName("System metrics are checked and compared to thresholds when all settings are enabled")
  void allEnabled() {
    when(mockNotificationGlobalSetting.isEnabled()).thenReturn(true);
    when(mockSystemWatchSetting.isEnabled()).thenReturn(true);
    when(mockSystemWatchSetting.getCpuThreshold()).thenReturn(80.0);
    when(mockSystemWatchSetting.getMemoryThreshold()).thenReturn(90.0);
    when(mockSystemWatchSetting.getDiskThreshold()).thenReturn(85.0);

    manager.checkMetrics(70.0, 80.0, 75.0);

    verify(mockCpuContext).checkMetric(70.0, 80.0);
    verify(mockMemoryContext).checkMetric(80.0, 90.0);
    verify(mockDiskContext).checkMetric(75.0, 85.0);
  }

  @Test
  @DisplayName("System metrics exceeding thresholds are correctly processed when all settings are enabled")
  void metricsExceedThresholds() {
    when(mockNotificationGlobalSetting.isEnabled()).thenReturn(true);
    when(mockSystemWatchSetting.isEnabled()).thenReturn(true);
    when(mockSystemWatchSetting.getCpuThreshold()).thenReturn(80.0);
    when(mockSystemWatchSetting.getMemoryThreshold()).thenReturn(90.0);
    when(mockSystemWatchSetting.getDiskThreshold()).thenReturn(85.0);

    manager.checkMetrics(90.0, 95.0, 95.0);

    verify(mockCpuContext).checkMetric(90.0, 80.0);
    verify(mockMemoryContext).checkMetric(95.0, 90.0);
    verify(mockDiskContext).checkMetric(95.0, 85.0);
  }
}
