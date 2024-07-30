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
    String methodSignature =
        "java.lang.String org.easypeelsecurity.springdogtest.ExampleController.example(java.lang.String)";
    long responseTime = 123L;
    long responseTime2 = 456L;

    // when
    EndpointMetricCacheManager.addResponseTime(methodSignature, responseTime);
    EndpointMetricCacheManager.addResponseTime(methodSignature, responseTime2);

    // then
    EndpointMetricCached result = EndpointMetricCacheManager.getAllData().stream()
        .filter(cached -> cached.methodSignature().equals(
            "java.lang.String org.easypeelsecurity.springdogtest.ExampleController.example(java.lang.String)"))
        .findFirst().get();
    assertThat(result).isNotNull();
    assertThat(result.responseTimes()).containsExactlyInAnyOrder(responseTime, responseTime2);
  }

  @Test
  @DisplayName("Should increment failure count for a specific endpoint")
  void incrementFailureCountSuccessfully() {
    // given
    String methodSignature =
        "java.lang.String org.easypeelsecurity.springdogtest.ExampleController.example(java.lang.String)";

    // when
    EndpointMetricCacheManager.incrementFailureCount(methodSignature);

    // then
    List<EndpointMetricCached> data = EndpointMetricCacheManager.getAllData();
    assertThat(data).anyMatch(cached -> cached.methodSignature().equals(methodSignature) &&
        cached.ratelimitFailureCount() == 1);
  }

  @Test
  @DisplayName("Should get all cache data")
  void getAllDataSuccessfully() {
    // given
    String methodSignature1 =
        "java.lang.String org.easypeelsecurity.springdogtest.ExampleController.example1(java.lang.String)";
    String methodSignature2 =
        "java.lang.String org.easypeelsecurity.springdogtest.ExampleController.example2(java.lang.String)";
    EndpointMetricCacheManager.addResponseTime(methodSignature1, 100L);
    EndpointMetricCacheManager.incrementFailureCount(methodSignature2);

    // when
    List<EndpointMetricCached> data = EndpointMetricCacheManager.getAllData();

    // then
    assertThat(data).hasSize(2);
    assertThat(data).extracting(
            EndpointMetricCached::methodSignature, EndpointMetricCached::responseTimes,
            EndpointMetricCached::ratelimitFailureCount)
        .containsExactlyInAnyOrder(
            tuple(methodSignature1, new long[] {100L}, 0),
            tuple(methodSignature2, new long[] {}, 1)
        );
  }

  @Test
  @DisplayName("Should invalidate cache by method signature")
  void invalidateByMethodSignatureSuccessfully() {
    // given
    String methodSignature =
        "java.lang.String org.easypeelsecurity.springdogtest.ExampleController.example1(java.lang.String)";
    EndpointMetricCacheManager.addResponseTime(methodSignature, 123L);

    // when
    EndpointMetricCacheManager.invalidateByMethodSignature(methodSignature);

    // then
    List<EndpointMetricCached> data = EndpointMetricCacheManager.getAllData();
    assertThat(data).noneMatch(cached -> cached.methodSignature().equals(methodSignature));
  }
}
