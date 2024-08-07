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

package org.easypeelsecurity.springdog.notification.email;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class MetricContextTest {

  @Mock
  private SystemWatchEmailNotification mockEmailNotification;

  private MetricContext metricContext;
  private final String metricName = "TestMetric";

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    metricContext = new MetricContext(metricName, mockEmailNotification);
  }

  @Test
  void testInitialState() {
    // Initial state should be Normal
    metricContext.checkMetric(5.0, 10.0);
    verify(mockEmailNotification, never()).setCause(anyString(), anyDouble());
    verify(mockEmailNotification, never()).setRecovery(anyString(), anyDouble());
  }

  @Test
  void testTransitionToWarningState() {
    metricContext.checkMetric(15.0, 10.0);
    verify(mockEmailNotification).setCause(metricName, 15.0);
    verify(mockEmailNotification).send();
  }

  @Test
  void testStayInWarningState() {
    metricContext.checkMetric(15.0, 10.0);
    verify(mockEmailNotification).setCause(metricName, 15.0);
    verify(mockEmailNotification).send();

    reset(mockEmailNotification);

    metricContext.checkMetric(20.0, 10.0);
    verify(mockEmailNotification, never()).setCause(anyString(), anyDouble());
    verify(mockEmailNotification, never()).setRecovery(anyString(), anyDouble());
  }

  @Test
  void testTransitionBackToNormalState() {
    metricContext.checkMetric(15.0, 10.0);
    verify(mockEmailNotification).setCause(metricName, 15.0);
    verify(mockEmailNotification).send();

    reset(mockEmailNotification);

    metricContext.checkMetric(5.0, 10.0);
    verify(mockEmailNotification).setRecovery(metricName, 5.0);
    verify(mockEmailNotification).send();
  }

  @Test
  void testStayInNormalState() {
    metricContext.checkMetric(5.0, 10.0);
    verify(mockEmailNotification, never()).setCause(anyString(), anyDouble());
    verify(mockEmailNotification, never()).setRecovery(anyString(), anyDouble());

    metricContext.checkMetric(7.0, 10.0);
    verify(mockEmailNotification, never()).setCause(anyString(), anyDouble());
    verify(mockEmailNotification, never()).setRecovery(anyString(), anyDouble());
  }
}
