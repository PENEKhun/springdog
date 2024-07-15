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

package org.easypeelsecurity.springdog.manager.statistics;

import java.text.DecimalFormat;

/**
 * Interface to define system usage metrics.
 * Provides methods to retrieve various system usage statistics such as CPU usage,
 * heap memory usage, and disk space information.
 *
 * <p>Implementation classes should provide concrete definitions for how to
 * obtain these metrics from the underlying system.
 * </p>
 *
 * <p>This interface also provides a default method for formatting double values
 * to two decimal places.
 * </p>
 *
 * @author PENEKhun
 */
public interface SystemUsageMetrics {
  DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");

  /**
   * Retrieves the current system CPU usage as a percentage.
   *
   * @return the system CPU usage as a percentage, or null if not available
   */
  Double systemCpuUsagePercent();

  /**
   * Retrieves the current heap memory usage as a percentage of the maximum available.
   *
   * @return the used heap memory as a percentage, or -1 if not available
   */
  Double heapMemoryUsagePercent();

  /**
   * Retrieves the current disk usage as a percentage of the total disk space.
   *
   * @return the disk usage as a percentage, or -1 if not available
   */
  Double diskUsagePercent();

  /**
   * Formats a double value to two decimal places.
   *
   * @param value the value to format
   * @return the formatted value as a double
   */
  default Double formatDouble(double value) {
    return Double.parseDouble(DECIMAL_FORMAT.format(value));
  }
}
