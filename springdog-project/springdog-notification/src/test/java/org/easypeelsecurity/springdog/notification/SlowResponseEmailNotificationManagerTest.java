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

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.easypeelsecurity.springdog.notification.email.MetricContext;
import org.easypeelsecurity.springdog.notification.email.SlowResponseEmailNotification;
import org.easypeelsecurity.springdog.notification.email.SlowResponseEmailNotification.SlowResponse;
import org.easypeelsecurity.springdog.shared.configuration.SlowResponseProperties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SlowResponseEmailNotificationManagerTest {

  @Mock
  private SlowResponseProperties properties;

  @Mock
  private SlowResponseEmailNotification emailNotification;

  @Mock
  private MetricContext<String, Long> metricContext;

  @InjectMocks
  private SlowResponseEmailNotificationManager manager;

  @BeforeEach
  void setUp() throws NoSuchFieldException, IllegalAccessException {
    MockitoAnnotations.openMocks(this);
    when(properties.getThresholdMs()).thenReturn(1000L);

    manager = new SlowResponseEmailNotificationManager(properties, emailNotification);

    // Use reflection to set metricContexts
    Field metricContextsField = SlowResponseEmailNotificationManager.class.getDeclaredField("metricContexts");
    metricContextsField.setAccessible(true);
    Map<String, MetricContext<String, Long>> contexts = new HashMap<>();
    contexts.put("[GET] /api/test", metricContext);
    metricContextsField.set(manager, contexts);
  }

  @Test
  void checkSlowResponse_BelowThreshold() {
    when(properties.isEnabled()).thenReturn(true);
    SlowResponse slowResponse = new SlowResponse.Builder()
        .endpointMethod("GET")
        .endpointPath("/api/test")
        .currentResponseTime(500L)
        .build();

    manager.checkSlowResponse(slowResponse);

    verify(metricContext).checkMetric(500L, 1000L);
  }

  @Test
  void checkSlowResponse_AboveThreshold() {
    when(properties.isEnabled()).thenReturn(true);
    SlowResponse slowResponse = new SlowResponse.Builder()
        .endpointMethod("GET")
        .endpointPath("/api/test")
        .currentResponseTime(1500L)
        .build();

    manager.checkSlowResponse(slowResponse);

    verify(metricContext).checkMetric(1500L, 1000L);
  }

  @Test
  void checkSlowResponse_Disabled() {
    when(properties.isEnabled()).thenReturn(false);
    SlowResponse slowResponse = new SlowResponse.Builder()
        .endpointMethod("GET")
        .endpointPath("/api/test")
        .currentResponseTime(1500L)
        .build();

    manager.checkSlowResponse(slowResponse);

    verify(metricContext, never()).checkMetric(anyLong(), anyLong());
  }
}
