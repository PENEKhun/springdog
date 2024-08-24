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

package org.easypeelsecurity.springdog.storage.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Repository;

import org.easypeelsecurity.springdog.domain.ratelimit.model.Endpoint;
import org.easypeelsecurity.springdog.domain.statistics.EndpointMetricRepository;
import org.easypeelsecurity.springdog.domain.statistics.model.EndpointMetric;
import org.easypeelsecurity.springdog.shared.vo.DashboardResponse.DailyEndpointMetric;
import org.easypeelsecurity.springdog.shared.vo.DashboardResponse.DailySlowestEndpoint;
import org.easypeelsecurity.springdog.shared.vo.DashboardResponse.DailyTopFailWithRatelimitEndpoint;
import org.easypeelsecurity.springdog.shared.vo.DashboardResponse.DailyTopTrafficEndpoint;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.EJBQLQuery;
import org.apache.cayenne.query.ObjectSelect;

/**
 * Implementation of {@link EndpointMetricRepository}.
 *
 * @author PENEKhun
 */
@Repository
public class EndpointMetricRepositoryImpl implements EndpointMetricRepository {

  @Override
  public EndpointMetric findByEndpointAndMetricDateOrNull(ObjectContext context, Endpoint endpoint,
      LocalDate specificDate) {
    return ObjectSelect.query(EndpointMetric.class)
        .where(EndpointMetric.ENDPOINT.eq(endpoint)
            .andExp(EndpointMetric.METRIC_DATE.eq(specificDate)))
        .selectOne(context);
  }

  @Override
  public List<EndpointMetric> getRecentEndpointMetrics(ObjectContext context, long endpointId, int limit) {
    return ObjectSelect.query(EndpointMetric.class)
        .where(EndpointMetric.ENDPOINT.eqId(endpointId))
        .orderBy(EndpointMetric.METRIC_DATE.desc())
        .limit(limit)
        .select(context);
  }

  @Override
  public List<DailyEndpointMetric> getDailyEndpointMetrics(ObjectContext context, int limitDays) {
    EJBQLQuery query = new EJBQLQuery("""
        SELECT SUM(em.pageView), AVG(em.averageResponseMs), SUM(em.failureWithRatelimit), em.metricDate
        FROM EndpointMetric em
        GROUP BY em.metricDate
        ORDER BY em.metricDate DESC
        """);
    query.setFetchLimit(limitDays);
    List<Object[]> results = context.performQuery(query);
    return results.stream()
        .map(row -> new DailyEndpointMetric(
            ((Number) row[0]).longValue(),
            ((Number) row[1]).longValue(),
            ((Number) row[2]).longValue(),
            (LocalDate) row[3]))
        .toList();
  }

  @Override
  public List<DailyTopTrafficEndpoint> getDailyTopTrafficEndpoints(ObjectContext context, int limitDays,
      LocalDate specificDate) {
    return ObjectSelect.query(EndpointMetric.class)
        .where(EndpointMetric.METRIC_DATE.eq(specificDate))
        .orderBy(EndpointMetric.METRIC_DATE.desc())
        .orderBy(EndpointMetric.PAGE_VIEW.desc())
        .limit(limitDays)
        .select(context)
        .stream()
        .map(metric -> new DailyTopTrafficEndpoint(
            metric.getEndpoint().getPath(),
            metric.getEndpoint().getHttpMethod(),
            metric.getPageView()))
        .toList();
  }

  @Override
  public List<DailySlowestEndpoint> getDailySlowestEndpoints(ObjectContext context, int limitDays,
      LocalDate specificDate) {
    return ObjectSelect.query(EndpointMetric.class)
        .where(EndpointMetric.METRIC_DATE.eq(specificDate))
        .orderBy(EndpointMetric.METRIC_DATE.desc())
        .orderBy(EndpointMetric.AVERAGE_RESPONSE_MS.desc())
        .limit(limitDays)
        .select(context)
        .stream()
        .map(metric -> new DailySlowestEndpoint(
            metric.getEndpoint().getPath(),
            metric.getEndpoint().getHttpMethod(),
            metric.getPageView()))
        .toList();
  }

  @Override
  public List<DailyTopFailWithRatelimitEndpoint> getDailyTopFailWithRatelimitEndpoints(ObjectContext context,
      int limitDays, LocalDate specificDate) {
    return ObjectSelect.query(EndpointMetric.class)
        .where(EndpointMetric.METRIC_DATE.eq(specificDate))
        .orderBy(EndpointMetric.METRIC_DATE.desc())
        .orderBy(EndpointMetric.FAILURE_WITH_RATELIMIT.desc())
        .limit(limitDays)
        .select(context)
        .stream()
        .map(metric -> new DailyTopFailWithRatelimitEndpoint(
            metric.getEndpoint().getPath(),
            metric.getEndpoint().getHttpMethod(),
            metric.getPageView()))
        .toList();
  }
}
