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

import org.easypeelsecurity.springdog.notification.email.MetricContext;

/**
 * Represents the warning state of a system metric.
 * In this state, the metric is above the threshold.
 */
public class WarningState<T extends Comparable<T>> implements MetricState<T> {
  private final String metricName;
  private final MetricContext<String, T> context;

  /**
   * Constructs a new WarningState.
   *
   * @param metricName The name of the metric.
   * @param context    The context in which this state exists.
   */
  public WarningState(String metricName, MetricContext context) {
    this.metricName = metricName;
    this.context = context;
  }

  /**
   * {@inheritDoc}
   * If the current value falls below or equals the threshold, transitions to NormalState.
   */
  @Override
  public void checkThreshold(T currentValue, T threshold) {
    boolean underThreshold = currentValue.compareTo(threshold) < 0;

    if (underThreshold) {
      context.setState(new NormalState<>(metricName, context));
      context.sendResolvedNotification(metricName, currentValue);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getMetricName() {
    return metricName;
  }
}

