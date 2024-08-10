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
import org.apache.cayenne.query.EJBQLQuery;
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
   * Get recent endpoint metrics.
   *
   * @param endpointId the id of the endpoint to retrieve metrics for
   * @param limit      the maximum number of endpoint metrics to retrieve
   * @return a list of EndpointMetricDto objects representing the most recent endpoint metrics
   */
  public List<EndpointMetricDto> getRecentEndpointMetrics(long endpointId, int limit) {
    ObjectContext context = springdogRepository.newContext();
    return ObjectSelect.query(EndpointMetric.class)
        .where(EndpointMetric.ENDPOINT.eqId(endpointId))
        .orderBy(EndpointMetric.METRIC_DATE.desc())
        .limit(limit)
        .select(context)
        .stream()
        // TODO: Refactor this to use a converter
        .map(metric -> new EndpointMetricDto(
            metric.getEndpoint().getPath(),
            metric.getEndpoint().getHttpMethod(),
            metric.getPageView(),
            metric.getAverageResponseMs(),
            metric.getFailureWithRatelimit(),
            metric.getMetricDate()))
        .toList();
  }

  /**
   * Get endpoint metrics by page view.
   *
   * @param limit        the maximum number of endpoint metrics to retrieve
   * @param specificDate the date to retrieve metrics for
   */
  public List<EndpointMetricDto> getEndpointMetricsByPageView(int limit, LocalDate specificDate) {
    ObjectContext context = springdogRepository.newContext();
    return ObjectSelect.query(EndpointMetric.class)
        .where(EndpointMetric.METRIC_DATE.eq(specificDate))
        .orderBy(EndpointMetric.METRIC_DATE.desc().then(EndpointMetric.PAGE_VIEW.desc()))
        .limit(limit)
        .select(context)
        .stream()
        .map(metric -> new EndpointMetricDto(
            metric.getEndpoint().getPath(),
            metric.getEndpoint().getHttpMethod(),
            metric.getPageView(),
            metric.getAverageResponseMs(),
            metric.getFailureWithRatelimit(),
            metric.getMetricDate()))
        .toList();
  }

  /**
   * Get endpoint metrics by failure.
   *
   * @param limit        the maximum number of endpoint metrics to retrieve
   * @param specificDate the date to retrieve metrics for
   */
  public List<EndpointMetricDto> getEndpointMetricsByFailure(int limit, LocalDate specificDate) {
    ObjectContext context = springdogRepository.newContext();
    return ObjectSelect.query(EndpointMetric.class)
        .where(EndpointMetric.METRIC_DATE.eq(specificDate))
        .orderBy(EndpointMetric.METRIC_DATE.desc().then(EndpointMetric.FAILURE_WITH_RATELIMIT.desc()))
        .limit(limit)
        .select(context)
        .stream()
        .map(metric -> new EndpointMetricDto(
            metric.getEndpoint().getPath(),
            metric.getEndpoint().getHttpMethod(),
            metric.getPageView(),
            metric.getAverageResponseMs(),
            metric.getFailureWithRatelimit(),
            metric.getMetricDate()))
        .toList();
  }


  /**
   * Get endpoint metrics by response duration.
   *
   * @param limit        the maximum number of endpoint metrics to retrieve
   * @param specificDate the date to retrieve metrics for
   */
  public List<EndpointMetricDto> getEndpointMetricsByResponseDuration(int limit, LocalDate specificDate) {
    ObjectContext context = springdogRepository.newContext();
    return ObjectSelect.query(EndpointMetric.class)
        .where(EndpointMetric.METRIC_DATE.eq(specificDate))
        .orderBy(EndpointMetric.METRIC_DATE.desc().then(EndpointMetric.AVERAGE_RESPONSE_MS.desc()))
        .limit(limit)
        .select(context)
        .stream()
        .map(metric -> new EndpointMetricDto(
            metric.getEndpoint().getPath(),
            metric.getEndpoint().getHttpMethod(),
            metric.getPageView(),
            metric.getAverageResponseMs(),
            metric.getFailureWithRatelimit(),
            metric.getMetricDate()))
        .toList();
  }

  /**
   * Get endpoint metrics for a recent date.
   */
  public List<EndpointMetricDto> getEndpointMetrics(int limit) {
    ObjectContext context = springdogRepository.newContext();

    EJBQLQuery query = new EJBQLQuery("""
        SELECT SUM(em.pageView), AVG(em.averageResponseMs), SUM(em.failureWithRatelimit), em.metricDate
        FROM EndpointMetric em
        GROUP BY em.metricDate
        ORDER BY em.metricDate DESC
        """);
    query.setFetchLimit(limit);
    List<Object[]> results = context.performQuery(query);
    // TODO: Refactor this to use a converter & Change anothor class to use likes... DailyEndpointMetricDto
    return results.stream()
        .map(row -> new EndpointMetricDto(
            "",
            "",
            ((Number) row[0]).longValue(),
            ((Number) row[1]).longValue(),
            ((Number) row[2]).longValue(),
            (LocalDate) row[3]))
        .toList();
  }
}
