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

package org.easypeelsecurity.springdog.domain.statistics;

import java.time.LocalDate;
import java.util.List;

import org.easypeelsecurity.springdog.domain.ratelimit.model.Endpoint;
import org.easypeelsecurity.springdog.domain.statistics.model.EndpointMetric;
import org.easypeelsecurity.springdog.shared.vo.DashboardResponse.DailyEndpointMetric;
import org.easypeelsecurity.springdog.shared.vo.DashboardResponse.DailySlowestEndpoint;
import org.easypeelsecurity.springdog.shared.vo.DashboardResponse.DailyTopFailWithRatelimitEndpoint;
import org.easypeelsecurity.springdog.shared.vo.DashboardResponse.DailyTopTrafficEndpoint;

import org.apache.cayenne.ObjectContext;

/**
 * Repository for {@link EndpointMetric} entities.
 *
 * @author PENEKhun
 */
public interface EndpointMetricRepository {
  /**
   * Find an {@link EndpointMetric} by its endpoint and metric date.
   *
   * @param context  The Cayenne ObjectContext
   * @param endpoint The endpoint
   * @param today    The metric date
   * @return The {@link EndpointMetric} or null if not found
   */
  EndpointMetric findByEndpointAndMetricDateOrNull(ObjectContext context, Endpoint endpoint, LocalDate today);

  /**
   * Find recent list {@link EndpointMetric} entities by endpoint.
   *
   * @param context    The Cayenne ObjectContext
   * @param endpointId The endpoint id
   * @param limit      The limit of the result
   * @return The result list of {@link EndpointMetric} entities
   */
  List<EndpointMetric> getRecentEndpointMetrics(ObjectContext context, long endpointId, int limit);

  /**
   * Find daily list {@link EndpointMetric} entities.
   *
   * @param context   The Cayenne ObjectContext
   * @param limitDays Maximum number of import days to include in results
   * @return Daily endpoint matrix
   */
  List<DailyEndpointMetric> getDailyEndpointMetrics(ObjectContext context, int limitDays);

  /**
   * Find daily list {@link EndpointMetric} entities by specific date.
   *
   * @param context      The Cayenne ObjectContext
   * @param limitDays    Maximum number of import days to include in results
   * @param specificDate The specific date
   * @return Daily top traffic endpoints
   */
  List<DailyTopTrafficEndpoint> getDailyTopTrafficEndpoints(ObjectContext context, int limitDays,
      LocalDate specificDate);

  /**
   * Find daily list {@link EndpointMetric} entities by specific date.
   *
   * @param context      The Cayenne ObjectContext
   * @param limitDays    Maximum number of import days to include in results
   * @param specificDate The specific date
   * @return Daily slowest endpoints
   */
  List<DailySlowestEndpoint> getDailySlowestEndpoints(ObjectContext context, int limitDays,
      LocalDate specificDate);

  /**
   * Find daily list {@link EndpointMetric} entities by specific date.
   *
   * @param context      The Cayenne ObjectContext
   * @param limitDays    Maximum number of import days to include in results
   * @param specificDate The specific date
   * @return Daily top fail with ratelimit endpoints
   */
  List<DailyTopFailWithRatelimitEndpoint> getDailyTopFailWithRatelimitEndpoints(ObjectContext context,
      int limitDays, LocalDate specificDate);
}
