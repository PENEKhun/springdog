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

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import org.easypeelsecurity.springdog.notification.email.MetricContext;
import org.easypeelsecurity.springdog.notification.email.SlowResponseEmailNotification;
import org.easypeelsecurity.springdog.notification.email.SlowResponseEmailNotification.SlowResponse;
import org.easypeelsecurity.springdog.shared.configuration.SlowResponseProperties;

/**
 * Manages the slow response process.
 */
@Service
public class SlowResponseEmailNotificationManager {
  private final Map<String, MetricContext<String, Long>> metricContexts;
  private final SlowResponseProperties properties;
  private final SlowResponseEmailNotification emailNotification;

  /**
   * Constructs a new SlowResponseManager.
   *
   * @param properties        The properties containing threshold values.
   * @param emailNotification The email notification service to use.
   */
  public SlowResponseEmailNotificationManager(SlowResponseProperties properties,
      SlowResponseEmailNotification emailNotification) {
    this.properties = properties;
    this.emailNotification = emailNotification;
    this.metricContexts = new HashMap<>();
  }

  /**
   * Checks the current response time against the threshold.
   *
   * @param value The current response time.
   */
  public void checkSlowResponse(SlowResponse value) {
    if (!properties.isEnabled()) {
      return;
    }

    String metricName = "[%s] %s".formatted(value.getEndpointMethod(), value.getEndpointPath());
    MetricContext<String, Long> context =
        metricContexts.computeIfAbsent(metricName, n -> new MetricContext<>(n, emailNotification));
    context.checkMetric(value.getCurrentResponseTime(), properties.getThresholdMs());
  }
}
