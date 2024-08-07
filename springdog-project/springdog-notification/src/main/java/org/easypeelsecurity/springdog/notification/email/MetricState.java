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
 * Represents the state of a system metric in the SystemWatch.
 * This interface defines the contract for different states a metric can be in.
 */
public interface MetricState {
  /**
   * Checks the current value against the threshold and potentially changes the state.
   *
   * @param currentValue The current value of the metric.
   * @param threshold The threshold value for the metric.
   */
  void checkThreshold(double currentValue, double threshold);

  /**
   * Gets the name of the metric this state is associated with.
   *
   * @return The name of the metric.
   */
  String getMetricName();
}
