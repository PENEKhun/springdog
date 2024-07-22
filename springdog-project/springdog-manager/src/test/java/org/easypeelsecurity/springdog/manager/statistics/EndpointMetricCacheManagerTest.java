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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EndpointMetricCacheManagerTest {

  @BeforeEach
  void setUp() {
    EndpointMetricCacheManager.clear();
    assertThat(EndpointMetricCacheManager.getAllData()).isEmpty();
  }

  @Test
  @DisplayName("Should add response time for a specific endpoint")
  void addResponseTimeSuccessfully() {
    // given
    String fqmn = "com.example.endpoint.method";
    long responseTime = 123L;
    long responseTime2 = 456L;

    // when
    EndpointMetricCacheManager.addResponseTime(fqmn, responseTime);
    EndpointMetricCacheManager.addResponseTime(fqmn, responseTime2);

    // then
    EndpointMetricCached result = EndpointMetricCacheManager.getAllData().stream()
        .filter(cached -> cached.fqmn().equals("com.example.endpoint.method"))
        .findFirst().get();
    assertThat(result).isNotNull();
    assertThat(result.responseTimes()).containsExactlyInAnyOrder(responseTime, responseTime2);
  }

  @Test
  @DisplayName("Should increment failure count for a specific endpoint")
  void incrementFailureCountSuccessfully() {
    // given
    String fqmn = "com.example.endpoint.method";

    // when
    EndpointMetricCacheManager.incrementFailureCount(fqmn);

    // then
    List<EndpointMetricCached> data = EndpointMetricCacheManager.getAllData();
    assertThat(data).anyMatch(cached -> cached.fqmn().equals(fqmn) &&
        cached.ratelimitFailureCount() == 1);
  }

  @Test
  @DisplayName("Should get all cache data")
  void getAllDataSuccessfully() {
    // given
    String fqmn1 = "com.example.endpoint.method1";
    String fqmn2 = "com.example.endpoint.method2";
    EndpointMetricCacheManager.addResponseTime(fqmn1, 100L);
    EndpointMetricCacheManager.incrementFailureCount(fqmn2);

    // when
    List<EndpointMetricCached> data = EndpointMetricCacheManager.getAllData();

    // then
    assertThat(data).hasSize(2);
    assertThat(data).extracting(
            EndpointMetricCached::fqmn, EndpointMetricCached::responseTimes,
            EndpointMetricCached::ratelimitFailureCount)
        .containsExactlyInAnyOrder(
            tuple(fqmn1, new long[] {100L}, 0),
            tuple(fqmn2, new long[] {}, 1)
        );
  }

  @Test
  @DisplayName("Should invalidate cache by fully qualified method name")
  void invalidateByFqmnSuccessfully() {
    // given
    String fqmn = "com.example.endpoint.method";
    EndpointMetricCacheManager.addResponseTime(fqmn, 123L);

    // when
    EndpointMetricCacheManager.invalidateByFqmn(fqmn);

    // then
    List<EndpointMetricCached> data = EndpointMetricCacheManager.getAllData();
    assertThat(data).noneMatch(cached -> cached.fqmn().equals(fqmn));
  }
}
