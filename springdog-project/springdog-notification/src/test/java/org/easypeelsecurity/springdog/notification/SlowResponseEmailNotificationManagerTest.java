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

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.easypeelsecurity.springdog.notification.SlowResponseEmailNotificationManager.SlowResponse;
import org.easypeelsecurity.springdog.notification.email.MetricContext;
import org.easypeelsecurity.springdog.notification.email.SlowResponseEmailNotification;
import org.easypeelsecurity.springdog.shared.settings.NotificationGlobalSetting;
import org.easypeelsecurity.springdog.shared.settings.SlowResponseSetting;
import org.easypeelsecurity.springdog.shared.settings.SpringdogSettingManager;
import org.easypeelsecurity.springdog.shared.settings.SpringdogSettings;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SlowResponseEmailNotificationManagerTest {

  @Mock
  private SpringdogSettingManager mockSettingManager;

  @Mock
  private SlowResponseEmailNotification mockEmailNotification;

  @Mock
  private SpringdogSettings mockSpringdogSettings;

  @Mock
  private SlowResponseSetting mockSlowResponseSetting;

  @Mock
  private NotificationGlobalSetting mockNotificationGlobalSetting;

  @Mock
  private Map<String, MetricContext<String, Long>> mockMetricContexts;

  @Mock
  private MetricContext<String, Long> mockMetricContext;
  private SlowResponseEmailNotificationManager manager;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    manager =
        new SlowResponseEmailNotificationManager(mockSettingManager, mockEmailNotification, mockMetricContexts);

    when(mockSettingManager.getSettings()).thenReturn(mockSpringdogSettings);
    when(mockSpringdogSettings.getSlowResponseSetting()).thenReturn(mockSlowResponseSetting);
    when(mockSpringdogSettings.getNotificationGlobalSetting()).thenReturn(mockNotificationGlobalSetting);
  }

  @Test
  @DisplayName("Given global notifications are disabled, when checking slow response, then no interactions should occur with metric contexts")
  void notificationDisabled() {
    when(mockNotificationGlobalSetting.isEnabled()).thenReturn(false);
    when(mockSlowResponseSetting.isEnabled()).thenReturn(true);

    SlowResponse slowResponse = new SlowResponse("/test", "GET", 1000L);
    manager.checkSlowResponse(slowResponse);

    verifyNoInteractions(mockMetricContexts);
  }

  @Test
  @DisplayName("Given slow response notifications are disabled, when checking slow response, then no interactions should occur with metric contexts")
  void slowResponseDisabled() {
    when(mockNotificationGlobalSetting.isEnabled()).thenReturn(true);
    when(mockSlowResponseSetting.isEnabled()).thenReturn(false);

    SlowResponse slowResponse = new SlowResponse("/test", "GET", 1000L);
    manager.checkSlowResponse(slowResponse);

    verifyNoInteractions(mockMetricContexts);
  }

  @Test
  @DisplayName("Given response time exceeds threshold, when checking slow response, then metric context should be updated accordingly")
  void responseTimeExceedsThreshold() {
    when(mockNotificationGlobalSetting.isEnabled()).thenReturn(true);
    when(mockSlowResponseSetting.isEnabled()).thenReturn(true);
    when(mockSlowResponseSetting.getResponseTimeMs()).thenReturn(500L);

    String metricName = "[GET] /test";
    when(mockMetricContexts.computeIfAbsent(eq(metricName), any()))
        .thenReturn(mockMetricContext);

    SlowResponse slowResponse = new SlowResponse("/test", "GET", 1000L);
    manager.checkSlowResponse(slowResponse);

    verify(mockMetricContext).checkMetric(1000L, 500L);
    verify(mockMetricContexts).computeIfAbsent(eq(metricName), any());
  }

  @Test
  @DisplayName("Given response time is within threshold, when checking slow response, then metric context should be updated accordingly")
  void responseTimeWithinThreshold() {
    when(mockNotificationGlobalSetting.isEnabled()).thenReturn(true);
    when(mockSlowResponseSetting.isEnabled()).thenReturn(true);
    when(mockSlowResponseSetting.getResponseTimeMs()).thenReturn(1500L);

    String metricName = "[GET] /test";
    when(mockMetricContexts.computeIfAbsent(eq(metricName), any()))
        .thenReturn(mockMetricContext);

    SlowResponse slowResponse = new SlowResponse("/test", "GET", 1000L);
    manager.checkSlowResponse(slowResponse);

    verify(mockMetricContext).checkMetric(1000L, 1500L);
    verify(mockMetricContexts).computeIfAbsent(eq(metricName), any());
  }
}
