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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * EndpointMetricTest.
 *
 * @author PENEKhun
 */
class EndpointMetricTest {

  @Test
  @DisplayName("Update statistics")
  void shouldUpdateStatisticsCorrectly() {
    // given
    EndpointMetric endpointMetric = new EndpointMetric();
    long initialPageView = 100;
    int initialAverageResponseMs = 200;
    long initialRateLimitFailureCount = 1_000;
    endpointMetric.setPageView(initialPageView);
    endpointMetric.setAverageResponseMs(initialAverageResponseMs);
    endpointMetric.setFailureWithRatelimit(initialRateLimitFailureCount);

    // when
    endpointMetric.updateStatistics(100, 750_000, 1_000);

    // then
    assertEquals(200, endpointMetric.getPageView());
    assertEquals(3_850, endpointMetric.getAverageResponseMs());
    assertEquals(2_000, endpointMetric.getFailureWithRatelimit());
  }

  @Test
  @DisplayName("Should throw exception when page view count is negative")
  void throwExceptionForNegativePageViewCount() {
    // given
    EndpointMetric endpointMetric = new EndpointMetric();

    // when & then
    assertThrows(IllegalArgumentException.class,
        () -> endpointMetric.setPageView(-10),
        "Page view count cannot be negative.");
  }

  @Test
  @DisplayName("Should throw exception when average response time is negative")
  void throwExceptionForNegativeAverageResponseTime() {
    // given
    EndpointMetric endpointMetric = new EndpointMetric();

    // when & then
    assertThrows(IllegalArgumentException.class,
        () -> endpointMetric.setAverageResponseMs(-10),
        "Average response time must be a positive number.");
  }

  @Test
  @DisplayName("Should throw exception when failureCount is negative")
  void throwExceptionForNegativeFailureCount() {
    // given
    EndpointMetric endpointMetric = new EndpointMetric();

    // when & then
    assertThrows(IllegalArgumentException.class,
        () -> endpointMetric.setFailureWithRatelimit(-10),
        "Ratelimit failure count must be a positive number");
  }

  @Test
  @DisplayName("Should set and return average response milliseconds")
  void setAndReturnAverageResponseMilliseconds() {
    // given
    EndpointMetric endpointMetric = new EndpointMetric();

    // when
    endpointMetric.setAverageResponseMs(1_000);

    // then
    assertEquals(1_000, endpointMetric.getAverageResponseMs());
  }

  @Test
  @DisplayName("Should set and return page view count")
  void setAndReturnPageViewCount() {
    // given
    EndpointMetric endpointMetric = new EndpointMetric();

    // when
    endpointMetric.setPageView(1_000);

    // then
    assertEquals(1_000, endpointMetric.getPageView());
  }

  @Test
  @DisplayName("Should throw exception when metric date is null")
  void throwExceptionForNullMetricDate() {
    // given
    EndpointMetric endpointMetric = new EndpointMetric();

    // when & then
    assertThrows(IllegalArgumentException.class,
        () -> endpointMetric.setMetricDate(null),
        "Metric date cannot be null.");
  }

  @Test
  @DisplayName("Should set and return metric date")
  void setAndReturnMetricDate() {
    // given
    EndpointMetric endpointMetric = new EndpointMetric();

    // when
    endpointMetric.setMetricDate(LocalDate.now());

    // then
    assertEquals(LocalDate.now(), endpointMetric.getMetricDate());
  }
}

