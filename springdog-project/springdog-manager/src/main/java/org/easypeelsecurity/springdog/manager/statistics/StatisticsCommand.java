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
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import org.easypeelsecurity.springdog.shared.ratelimit.model.Endpoint;
import org.easypeelsecurity.springdog.shared.statistics.model.EndpointMetric;
import org.easypeelsecurity.springdog.shared.statistics.model.SystemMetric;
import org.easypeelsecurity.springdog.shared.util.Assert;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.configuration.CayenneRuntime;
import org.apache.cayenne.query.ObjectSelect;

/**
 * Command for statistics.
 *
 * @author PENEKhun
 */
@Service
public class StatisticsCommand {
  private final CayenneRuntime springdogRepository;

  /**
   * Constructor.
   */
  public StatisticsCommand(@Qualifier("springdogRepository") CayenneRuntime springdogRepository) {
    this.springdogRepository = springdogRepository;
  }

  /**
   * Store system metrics.
   *
   * @param cpuUsagePercent    cpu usage percentage
   * @param memoryUsagePercent memory usage percentage
   * @param diskUsagePercent   disk usage percentage
   */
  public void storeSystemMetrics(double cpuUsagePercent, double memoryUsagePercent, double diskUsagePercent) {
    if (Double.isNaN(cpuUsagePercent) || Double.isNaN(memoryUsagePercent) || Double.isNaN(diskUsagePercent)) {
      throw new IllegalArgumentException("percentage must be a number");
    }

    if (cpuUsagePercent < 0 || cpuUsagePercent > 100) {
      throw new IllegalArgumentException("CPU usage percentage must be between 0 and 100");
    }

    if (memoryUsagePercent < 0 || memoryUsagePercent > 100) {
      throw new IllegalArgumentException("Memory usage percentage must be between 0 and 100");
    }

    if (diskUsagePercent < 0 || diskUsagePercent > 100) {
      throw new IllegalArgumentException("Disk usage percentage must be between 0 and 100");
    }

    ObjectContext context = springdogRepository.newContext();
    SystemMetric systemMetric = context.newObject(SystemMetric.class);
    systemMetric.setCpuUsagePercent(cpuUsagePercent);
    systemMetric.setMemoryUsagePercent(memoryUsagePercent);
    systemMetric.setDiskUsagePercent(diskUsagePercent);

    context.commitChanges();
  }

  /**
   * Updates or inserts endpoint metrics including page views and response times.
   * <p>
   * This method updates the statistics of an endpoint for the current date.
   * If the endpoint or its metrics do not exist, they will be created.
   * </p>
   *
   * @param fqmn          the fully qualified method name of the endpoint
   * @param responseTimes the array of response times to be included in the statistics
   * @param today         the current date
   * @throws IllegalArgumentException if the parameters are invalid
   */
  public void upsertEndpointMetrics(String fqmn, long[] responseTimes, LocalDate today) {
    Assert.isTrue(Arrays.stream(responseTimes).allMatch(time -> time >= 0),
        "Response times must be non-negative");
    Assert.isTrue(today != null, "Date must not be null");

    ObjectContext context = springdogRepository.newContext();
    Endpoint endpoint = ObjectSelect.query(Endpoint.class)
        .where(Endpoint.FQMN.eq(fqmn))
        .selectOne(context);

    Assert.notNull(endpoint, "Endpoint not found");
    EndpointMetric endpointMetric = ObjectSelect.query(EndpointMetric.class)
        .where(EndpointMetric.ENDPOINT.eq(endpoint)
            .andExp(EndpointMetric.METRIC_DATE.eq(today)))
        .selectOne(context);
    if (endpointMetric == null) {
      endpointMetric = context.newObject(EndpointMetric.class);
      endpointMetric.setEndpoint(endpoint);
      endpointMetric.setMetricDate(today);
    }
    long responseTimeSum = Arrays.stream(responseTimes).sum();
    endpointMetric.updateStatistics(responseTimes.length, responseTimeSum);

    context.commitChanges();
  }
}
