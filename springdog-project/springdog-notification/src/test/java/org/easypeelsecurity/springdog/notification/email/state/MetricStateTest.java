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

package org.easypeelsecurity.springdog.notification.email.state;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyDouble;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.easypeelsecurity.springdog.notification.email.MetricContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class MetricStateTest {

  @Mock
  private MetricContext<String, Double> mockContext;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Nested
  class NormalStateTest {
    private NormalState<Double> normalState;

    @BeforeEach
    void setUp() {
      normalState = new NormalState<>("TestMetric", mockContext);
    }

    @Test
    void checkThreshold_BelowThreshold() {
      normalState.checkThreshold(75.0, 80.0);

      verify(mockContext, never()).setState(any());
      verify(mockContext, never()).sendWarningNotification(anyString(), anyDouble());
    }

    @Test
    void checkThreshold_AboveThreshold() {
      normalState.checkThreshold(85.0, 80.0);

      verify(mockContext).setState(any(WarningState.class));
      verify(mockContext).sendWarningNotification("TestMetric", 85.0);
    }

    @Test
    void checkThreshold_EqualToThreshold() {
      normalState.checkThreshold(80.0, 80.0);

      verify(mockContext, never()).setState(any());
      verify(mockContext, never()).sendWarningNotification(anyString(), anyDouble());
    }

    @Test
    void getMetricName() {
      assertEquals("TestMetric", normalState.getMetricName());
    }
  }

  @Nested
  class WarningStateTest {
    private WarningState<Double> warningState;

    @BeforeEach
    void setUp() {
      warningState = new WarningState<>("TestMetric", mockContext);
    }

    @Test
    void checkThreshold_AboveThreshold() {
      warningState.checkThreshold(85.0, 80.0);

      verify(mockContext, never()).setState(any());
      verify(mockContext, never()).sendResolvedNotification(anyString(), anyDouble());
    }

    @Test
    void checkThreshold_BelowThreshold() {
      warningState.checkThreshold(75.0, 80.0);

      verify(mockContext).setState(any(NormalState.class));
      verify(mockContext).sendResolvedNotification("TestMetric", 75.0);
    }

    @Test
    void checkThreshold_EqualToThreshold() {
      warningState.checkThreshold(80.0, 80.0);

      verify(mockContext, never()).setState(any());
      verify(mockContext, never()).sendWarningNotification(anyString(), anyDouble());
    }

    @Test
    void getMetricName() {
      assertEquals("TestMetric", warningState.getMetricName());
    }
  }
}
