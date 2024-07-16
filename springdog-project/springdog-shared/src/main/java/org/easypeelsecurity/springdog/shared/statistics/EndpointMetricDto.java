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

package org.easypeelsecurity.springdog.shared.statistics;

import java.time.LocalDate;

/**
 * DTO for endpoint metrics.
 *
 * @author PENEKhun
 */
public class EndpointMetricDto {

  private final String path;
  private final String method;
  private final long visitCount;
  private final long averageResponseMilliseconds;
  private final LocalDate baseDate;

  /**
   * Constructor.
   *
   * @param path                        endpoint path
   * @param method                      HTTP method
   * @param visitCount                  number of visits
   * @param averageResponseMilliseconds average response time in milliseconds
   */
  public EndpointMetricDto(String path, String method, long visitCount, long averageResponseMilliseconds,
      LocalDate baseDate) {
    this.path = path;
    this.method = method;
    this.visitCount = visitCount;
    this.averageResponseMilliseconds = averageResponseMilliseconds;
    this.baseDate = baseDate;
  }

  /**
   * Get endpoint path.
   */
  public String getPath() {
    return path;
  }

  /**
   * Get HTTP method.
   */
  public String getMethod() {
    return method;
  }

  /**
   * Get number of visits.
   */
  public long getVisitCount() {
    return visitCount;
  }

  /**
   * Get average response time.
   */
  public long getAverageResponseMilliseconds() {
    return averageResponseMilliseconds;
  }

  /**
   * Returns the base day of the data.
   */
  public LocalDate getDate() {
    return baseDate;
  }
}
