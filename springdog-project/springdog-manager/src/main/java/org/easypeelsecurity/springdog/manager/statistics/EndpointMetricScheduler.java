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

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import org.easypeelsecurity.springdog.domain.statistics.StatisticsService;

/**
 * This class is responsible for scheduling tasks to store endpoint metrics in the database such as average
 * response times, number of failures, etc.
 * It retrieves response times from {@link RequestTimingInterceptor} and processes them at regular intervals.
 *
 * @author PENEKhun
 */
@Component
@EnableScheduling
public class EndpointMetricScheduler {
  private final StatisticsService statisticsService;

  /**
   * Constructor.
   */
  public EndpointMetricScheduler(StatisticsService statisticsService) {
    this.statisticsService = statisticsService;
  }

  /**
   * Scheduled task that saves the endpoint statistics in the database.
   */
  @Scheduled(fixedRateString = "${springdog.endpointMetricScheduler.fixedRate:10000}")
  public void saveEndpointStatistics() {
    List<EndpointMetricCached> cached = EndpointMetricCacheManager.getAllData();

    for (EndpointMetricCached entry : cached) {
      statisticsService.upsertEndpointMetrics(entry.methodSignature(), entry.responseTimes(),
          entry.ratelimitFailureCount(), LocalDate.now());
      EndpointMetricCacheManager.invalidateByMethodSignature(entry.methodSignature());
    }
  }
}
