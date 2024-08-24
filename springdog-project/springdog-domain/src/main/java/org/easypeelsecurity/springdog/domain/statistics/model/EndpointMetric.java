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

package org.easypeelsecurity.springdog.domain.statistics.model;

import java.time.LocalDate;

import org.easypeelsecurity.springdog.domain.statistics.model.auto._EndpointMetric;

@SuppressWarnings("all")
public class EndpointMetric extends _EndpointMetric {

  private static final long serialVersionUID = 1L;

  /**
   * Updates the endpoint's statistics for page views and average response times.
   * This method updates the total page views and calculates the new average response time based on the
   * additional page views and the sum of additional response times provided.
   *
   * @param additionalPageView        the number of additional page views to be added
   * @param additionalResponseTimeSum the sum of additional response times to be added
   * @param ratelimitFailureCount     the number of additional rate limit failures to be added
   */
  public void updateStatistics(long additionalPageView, long additionalResponseTimeSum,
      long ratelimitFailureCount) {
    long totalPageView = getPageView() + additionalPageView;
    long existResponseTime = getPageView() * getAverageResponseMs();
    long totalResponseTime = existResponseTime + additionalResponseTimeSum;
    int averageResponseTime = (int) (totalResponseTime / totalPageView);

    setPageView(totalPageView);
    setAverageResponseMs(averageResponseTime);
    setFailureWithRatelimit(getFailureWithRatelimit() + ratelimitFailureCount);
  }

  @Override
  public void setAverageResponseMs(long averageResponseMs) {
    if (averageResponseMs < 0) {
      throw new IllegalArgumentException("average response time must be a positive number");
    }
    super.setAverageResponseMs(averageResponseMs);
  }

  @Override
  public void setPageView(long pageView) {
    if (pageView < 0) {
      throw new IllegalArgumentException("page view must be a positive number");
    }
    super.setPageView(pageView);
  }

  @Override
  public void setMetricDate(LocalDate metricDate) {
    if (metricDate == null) {
      throw new IllegalArgumentException("metric date cannot be null");
    }
    super.setMetricDate(metricDate);
  }

  @Override
  public void setFailureWithRatelimit(long ratelimitFailureCount) {
    if (ratelimitFailureCount < 0) {
      throw new IllegalArgumentException("ratelimit failure count must be a positive number");
    }
    super.setFailureWithRatelimit(ratelimitFailureCount);
  }
}
