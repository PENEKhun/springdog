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

package org.easypeelsecurity.springdog.shared.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EndpointMetricDtoTest {

  @Test
  @DisplayName("Should create EndpointMetricDto with given values")
  void createEndpointMetricDtoWithValues() {
    // given
    String path = "/test/path";
    String method = "GET";
    long visitCount = 100;
    long averageResponseMilliseconds = 200;
    long ratelimitFailureCount = 1_000;
    LocalDate baseDate = LocalDate.now();

    // when
    EndpointMetricDto dto =
        new EndpointMetricDto(path, method, visitCount, averageResponseMilliseconds, ratelimitFailureCount,
            baseDate);

    // then
    assertEquals(path, dto.path());
    assertEquals(method, dto.method());
    assertEquals(visitCount, dto.visitCount());
    assertEquals(averageResponseMilliseconds, dto.averageResponseMilliseconds());
    assertEquals(ratelimitFailureCount, dto.ratelimitFailureCount());
    assertEquals(baseDate, dto.baseDate());
  }
}
