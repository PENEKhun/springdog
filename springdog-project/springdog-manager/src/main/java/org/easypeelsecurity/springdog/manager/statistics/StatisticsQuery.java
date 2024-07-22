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

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import org.easypeelsecurity.springdog.shared.ratelimit.model.Endpoint;
import org.easypeelsecurity.springdog.shared.statistics.EndpointMetricDto;
import org.easypeelsecurity.springdog.shared.statistics.SystemMetricConverter;
import org.easypeelsecurity.springdog.shared.statistics.SystemMetricDto;
import org.easypeelsecurity.springdog.shared.statistics.model.EndpointMetric;
import org.easypeelsecurity.springdog.shared.statistics.model.SystemMetric;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.configuration.CayenneRuntime;
import org.apache.cayenne.query.ObjectSelect;

/**
 * Query for statistics.
 *
 * @author PENEKhun
 */
@Service
public class StatisticsQuery {

  private final CayenneRuntime springdogRepository;

  /**
   * Constructor.
   */
  public StatisticsQuery(@Qualifier("springdogRepository") CayenneRuntime springdogRepository) {
    this.springdogRepository = springdogRepository;
  }

  /**
   * Total endpoint count.
   *
   * @return count
   */
  public int totalEndpointCount() {
    ObjectContext context = springdogRepository.newContext();
    return ObjectSelect.query(Endpoint.class)
        .select(context).size();
  }

  /**
   * Total endpoint count by status.
   *
   * @param status status
   * @return count
   */
  public int totalEndpointCountByStatus(String status) {
    ObjectContext context = springdogRepository.newContext();
    return ObjectSelect.query(Endpoint.class)
        .where(Endpoint.RULE_STATUS.eq(status))
        .select(context).size();
  }

  /**
   * Retrieves a list of recent system metrics up to a specified limit.
   *
   * @param limit the maximum number of system metrics to retrieve
   * @return a list of SystemMetricDto objects representing the most recent system metrics
   */
  public List<SystemMetricDto> getRecentSystemMetrics(int limit) {
    ObjectContext context = springdogRepository.newContext();
    return ObjectSelect.query(SystemMetric.class)
        .orderBy(SystemMetric.TIMESTAMP.desc())
        .limit(limit)
        .select(context)
        .stream()
        .map(SystemMetricConverter::convert)
        .toList();
  }

  /**
   * Get endpoint metrics for a specific baseDate.
   */
  public List<EndpointMetricDto> getEndpointMetrics(LocalDate baseDate) {
    ObjectContext context = springdogRepository.newContext();
    return ObjectSelect.query(EndpointMetric.class)
        .where(EndpointMetric.METRIC_DATE.eq(baseDate))
        .select(context)
        .stream()
        .map(metric -> new EndpointMetricDto(
            metric.getEndpoint().getPath(),
            metric.getEndpoint().getHttpMethod(),
            metric.getPageView(),
            metric.getAverageResponseMs(),
            metric.getFailureWithRatelimit(),
            baseDate))
        .toList();
  }
}
