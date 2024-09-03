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

import static org.easypeelsecurity.springdog.shared.enums.RuleStatus.ACTIVE;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import org.easypeelsecurity.springdog.domain.ratelimit.EndpointRepository;
import org.easypeelsecurity.springdog.domain.ratelimit.model.Endpoint;
import org.easypeelsecurity.springdog.domain.statistics.converter.SystemMetricConverter;
import org.easypeelsecurity.springdog.domain.statistics.model.EndpointMetric;
import org.easypeelsecurity.springdog.domain.statistics.model.SystemMetric;
import org.easypeelsecurity.springdog.shared.dto.EndpointMetricDto;
import org.easypeelsecurity.springdog.shared.dto.SystemMetricDto;
import org.easypeelsecurity.springdog.shared.util.Assert;
import org.easypeelsecurity.springdog.shared.vo.DashboardResponse;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;

/**
 * Service class for statistics.
 *
 * @author PENEKhun
 */
@Service
public class StatisticsService {
  private final ObjectContext context;
  private final EndpointRepository endpointRepository;
  private final EndpointMetricRepository endpointMetricRepository;
  private final SystemMetricRepository systemMetricRepository;

  /**
   * Constructor.
   */
  public StatisticsService(
      @Qualifier("springdogContext") ObjectContext context, EndpointRepository endpointRepository,
      EndpointMetricRepository endpointMetricRepository, SystemMetricRepository systemMetricRepository) {
    this.context = context;
    this.endpointRepository = endpointRepository;
    this.endpointMetricRepository = endpointMetricRepository;
    this.systemMetricRepository = systemMetricRepository;
  }

  /**
   * Get the dashboard response.
   *
   * @param specificDate Date of view
   * @return The {@link DashboardResponse}
   */
  public DashboardResponse getDashboardResponse(LocalDate specificDate) {
    long totalEndpointCount = endpointRepository.getEndpointCount(context);
    long totalActiveEndpointCount = endpointRepository.getEndpointCountByStatus(context, ACTIVE.name());
    List<SystemMetricDto> recentSystemMetrics = systemMetricRepository.getRecentSystemMetrics(context, 30)
        .stream()
        .map(SystemMetricConverter::convert)
        .toList();

    return new DashboardResponse(
        totalEndpointCount,
        totalActiveEndpointCount,
        totalEndpointCount - totalActiveEndpointCount,
        recentSystemMetrics,
        endpointMetricRepository.getDailyEndpointMetrics(context, 10),
        endpointMetricRepository.getDailyTopTrafficEndpoints(context, 7, specificDate),
        endpointMetricRepository.getDailySlowestEndpoints(context, 7, specificDate),
        endpointMetricRepository.getDailyTopFailWithRatelimitEndpoints(context, 7, specificDate));
  }

  /**
   * Updates or inserts endpoint metrics including page views and response times.
   * <p>
   * This method updates the statistics of an endpoint for the current date.
   * If the endpoint or its metrics do not exist, they will be created.
   * </p>
   *
   * @param methodSignature the method signature of the endpoint
   * @param responseTimes   the array of response times to be included in the statistics
   * @param failureCount    the number of failed rate-limited requests
   * @param today           the current date
   * @throws IllegalArgumentException if the parameters are invalid
   */
  public void upsertEndpointMetrics(String methodSignature, long[] responseTimes, long failureCount,
      LocalDate today) {
    Assert.isTrue(Arrays.stream(responseTimes).allMatch(time -> time >= 0),
        "Response times must be non-negative");
    Assert.isTrue(today != null, "Date must not be null");
    Assert.isTrue(failureCount >= 0, "Failure count must be non-negative");

    Endpoint endpoint = ObjectSelect.query(Endpoint.class)
        .where(Endpoint.METHOD_SIGNATURE.eq(methodSignature))
        .selectOne(context);

    Assert.notNull(endpoint, "Endpoint not found");
    EndpointMetric endpointMetric =
        endpointMetricRepository.findByEndpointAndMetricDateOrNull(context, endpoint, today);
    if (endpointMetric == null) {
      endpointMetric = context.newObject(EndpointMetric.class);
      endpointMetric.setEndpoint(endpoint);
      endpointMetric.setMetricDate(today);
    }
    long responseTimeSum = Arrays.stream(responseTimes).sum();
    endpointMetric.updateStatistics(responseTimes.length, responseTimeSum, failureCount);
    context.commitChanges();
  }

  /**
   * Store system metrics.
   *
   * @param cpuUsagePercent        cpu usage percentage
   * @param memoryUsagePercent     memory usage percentage
   * @param diskUsagePercent       disk usage percentage
   * @param jvmHeapUsagePercent    JVM heap usage percentage
   * @param jvmNonHeapUsagePercent JVM non-heap usage percentage
   * @param jvmTotalMemoryUsed     JVM total memory used
   * @param networkInBytes         network in bytes
   * @param networkOutBytes        network out bytes
   */
  public void storeSystemMetrics(double cpuUsagePercent, double memoryUsagePercent, double diskUsagePercent,
      double jvmHeapUsagePercent, double jvmNonHeapUsagePercent, long jvmTotalMemoryUsed,
      long networkInBytes, long networkOutBytes) {

    if (cpuUsagePercent < 0 || cpuUsagePercent > 100) {
      throw new IllegalArgumentException("CPU usage percentage must be between 0 and 100");
    }

    if (memoryUsagePercent < 0 || memoryUsagePercent > 100) {
      throw new IllegalArgumentException("Memory usage percentage must be between 0 and 100");
    }

    if (diskUsagePercent < 0 || diskUsagePercent > 100) {
      throw new IllegalArgumentException("Disk usage percentage must be between 0 and 100");
    }

    SystemMetric systemMetric = context.newObject(SystemMetric.class);
    systemMetric.setCpuUsagePercent(cpuUsagePercent);
    systemMetric.setMemoryUsagePercent(memoryUsagePercent);
    systemMetric.setDiskUsagePercent(diskUsagePercent);
    systemMetric.setJvmHeapUsagePercent(jvmHeapUsagePercent);
    systemMetric.setJvmNonHeapUsagePercent(jvmNonHeapUsagePercent);
    systemMetric.setJvmTotalMemoryUsed(jvmTotalMemoryUsed);
    systemMetric.setNetworkInBytes(networkInBytes);
    systemMetric.setNetworkOutBytes(networkOutBytes);

    context.commitChanges();
  }

  /**
   * Get the recent endpoint metrics.
   *
   * @param endpointId The endpoint id
   * @param limitDays  Maximum number of import days to include in results
   * @return The list of {@link EndpointMetricDto}
   */
  public List<EndpointMetricDto> getRecentEndpointMetric(long endpointId, int limitDays) {
    return endpointMetricRepository.getRecentEndpointMetrics(context, endpointId, limitDays)
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
}
