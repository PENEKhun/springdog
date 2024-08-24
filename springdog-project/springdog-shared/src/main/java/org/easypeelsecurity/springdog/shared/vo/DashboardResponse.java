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

package org.easypeelsecurity.springdog.shared.vo;

import java.time.LocalDate;
import java.util.List;

import org.easypeelsecurity.springdog.shared.dto.SystemMetricDto;

/**
 * The response object for the dashboard.
 *
 * @param totalEndpointCount         The total endpoint count
 * @param totalActiveEndpointCount   The total active endpoint count
 * @param totalInactiveEndpointCount The total inactive endpoint count
 * @param recentSystemMetrics        The recent system metrics
 * @param dailyEndpointMetrics       The daily endpoint metrics
 * @param dailyTopTrafficEndpoints   The daily top traffic endpoints
 * @param dailySlowestEndpoints      The daily slowest endpoints
 * @param dailyTopFailEndpoints      The daily top fail endpoints
 */
public record DashboardResponse(
    long totalEndpointCount,
    long totalActiveEndpointCount,
    long totalInactiveEndpointCount,
    List<SystemMetricDto> recentSystemMetrics,
    List<DailyEndpointMetric> dailyEndpointMetrics,
    List<DailyTopTrafficEndpoint> dailyTopTrafficEndpoints,
    List<DailySlowestEndpoint> dailySlowestEndpoints,
    List<DailyTopFailWithRatelimitEndpoint> dailyTopFailEndpoints
) {
  /**
   * The daily endpoint metric.
   *
   * @param totalPageView       The total page view
   * @param averageResponseTime The average response time
   * @param ratelimitFailCount  The ratelimit fail count
   * @param date                The date of the metric
   */
  public record DailyEndpointMetric(
      long totalPageView,
      long averageResponseTime,
      long ratelimitFailCount,
      LocalDate date
  ) {
  }

  /**
   * The daily top traffic endpoint.
   *
   * @param endpoint The endpoint(path)
   * @param method   The http method
   * @param count    The traffic count
   */
  public record DailyTopTrafficEndpoint(
      String endpoint,
      String method,
      long count
  ) {
  }

  /**
   * The daily slowest endpoint.
   *
   * @param endpoint            The endpoint(path)
   * @param method              The http method
   * @param averageResponseTime The average response time (ms)
   */
  public record DailySlowestEndpoint(
      String endpoint,
      String method,
      long averageResponseTime
  ) {
  }

  /**
   * The daily top fail endpoint with ratelimit.
   *
   * @param endpoint The endpoint(path)
   * @param method   The http method
   * @param count    The fail count
   */
  public record DailyTopFailWithRatelimitEndpoint(
      String endpoint,
      String method,
      long count
  ) {
  }
}
