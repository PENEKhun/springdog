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

/**
 * Manages the context for a single metric in the SystemWatch.
 * This class handles state transitions and notifications for a metric.
 */
public class MetricContext {
  private MetricState state;
  private final SystemWatchEmailNotification emailNotification;

  /**
   * Constructs a new MetricContext.
   *
   * @param metricName The name of the metric.
   * @param emailNotification The email notification service to use.
   */
  public MetricContext(String metricName, SystemWatchEmailNotification emailNotification) {
    this.state = new NormalState(metricName, this);
    this.emailNotification = emailNotification;
  }

  /**
   * Sets the current state of the metric.
   *
   * @param state The new state to set.
   */
  public void setState(MetricState state) {
    this.state = state;
  }

  /**
   * Checks the current metric value against the threshold.
   *
   * @param currentValue The current value of the metric.
   * @param threshold The threshold value for the metric.
   */
  public void checkMetric(double currentValue, double threshold) {
    state.checkThreshold(currentValue, threshold);
  }

  /**
   * Sends a warning notification for the metric.
   *
   * @param metricName The name of the metric.
   * @param value The current value of the metric.
   */
  public void sendWarningNotification(String metricName, double value) {
    emailNotification.setCause(metricName, value);
    emailNotification.send();
  }

  /**
   * Sends a resolved notification for the metric.
   *
   * @param metricName The name of the metric.
   * @param value The current value of the metric.
   */
  public void sendResolvedNotification(String metricName, double value) {
    emailNotification.setRecovery(metricName, value);
    emailNotification.send();
  }
}
