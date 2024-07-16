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
import java.util.Map.Entry;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * This class is responsible for scheduling tasks to calculate the average response times of HTTP requests.
 * It retrieves response times from {@link RequestTimingInterceptor} and processes them at regular intervals.
 *
 * @author PENEKhun
 */
@Component

@EnableScheduling
public class EndpointMetricScheduler {
  private final StatisticsCommand statisticsCommand;

  /**
   * Constructor.
   */
  public EndpointMetricScheduler(StatisticsCommand statisticsCommand) {
    this.statisticsCommand = statisticsCommand;
  }

  /**
   * Scheduled task that calculates the average response times of HTTP requests and stores them in the database.
   */
  @Scheduled(fixedRateString = "${springdog.endpointMetricScheduler.fixedRate:300000}")
  public void calculateAverageResponseTimes() {
    List<Entry<EndpointCacheKey, long[]>> allResponseTimes = EndpointMetricCache.getAllResponseTimes();

    for (Entry<EndpointCacheKey, long[]> entry : allResponseTimes) {
      String path = entry.getKey().path();
      String method = entry.getKey().method();
      statisticsCommand.upsertEndpointMetrics(path, method, entry.getValue(), LocalDate.now());
      EndpointMetricCache.clear(entry.getKey());
    }
  }
}
