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

package org.easypeelsecurity.springdog.shared.ratelimit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;

import org.easypeelsecurity.springdog.shared.ratelimit.model.HttpMethod;
import org.easypeelsecurity.springdog.shared.ratelimit.model.RuleStatus;
import org.junit.jupiter.api.Test;

class EndpointDtoTest {

  @Test
  void timeLimitDays() {
    // given
    int timeLimitInSeconds = 12_582_035;
    EndpointDto endpointDto = new EndpointDto(
        "hash",
        "path",
        "fqcn",
        HttpMethod.GET,
        new HashSet<>(),
        false,
        RuleStatus.ACTIVE,
        false,
        false,
        0,
        timeLimitInSeconds,
        0
    );

    // when
    int actual = endpointDto.timeLimitDays();

    // then
    assertEquals(145, actual);
  }

  @Test
  void timeLimitHours() {
    // given
    int timeLimitInSeconds = 12_582_035;
    EndpointDto endpointDto = new EndpointDto(
        "hash",
        "path",
        "fqcn",
        HttpMethod.GET,
        new HashSet<>(),
        false,
        RuleStatus.ACTIVE,
        false,
        false,
        0,
        timeLimitInSeconds,
        0
    );

    // when
    int actual = endpointDto.timeLimitHours();

    // then
    assertEquals(15, actual);
  }

  @Test
  void timeLimitMinutes() {
    // given
    int timeLimitInSeconds = 12_582_035;
    EndpointDto endpointDto = new EndpointDto(
        "hash",
        "path",
        "fqcn",
        HttpMethod.GET,
        new HashSet<>(),
        false,
        RuleStatus.ACTIVE,
        false,
        false,
        0,
        timeLimitInSeconds,
        0
    );

    // when
    int actual = endpointDto.timeLimitMinutes();

    // then
    assertEquals(0, actual);
  }

  @Test
  void timeLimitSeconds() {
    // given
    int timeLimitInSeconds = 12_582_035;
    EndpointDto endpointDto = new EndpointDto(
        "hash",
        "path",
        "fqcn",
        HttpMethod.GET,
        new HashSet<>(),
        false,
        RuleStatus.ACTIVE,
        false,
        false,
        0,
        timeLimitInSeconds,
        0
    );

    // when
    int actual = endpointDto.timeLimitSeconds();

    // then
    assertEquals(35, actual);
  }

  @Test
  void banTimeDays() {
    // given
    int banTimeInSeconds = 12_582_035;
    EndpointDto endpointDto = new EndpointDto(
        "hash",
        "path",
        "fqcn",
        HttpMethod.GET,
        new HashSet<>(),
        false,
        RuleStatus.ACTIVE,
        false,
        false,
        0,
        0,
        banTimeInSeconds
    );

    // when
    int actual = endpointDto.banTimeDays();

    // then
    assertEquals(145, actual);
  }

  @Test
  void banTimeHours() {
    // given
    int banTimeInSeconds = 12_582_035;
    EndpointDto endpointDto = new EndpointDto(
        "hash",
        "path",
        "fqcn",
        HttpMethod.GET,
        new HashSet<>(),
        false,
        RuleStatus.ACTIVE,
        false,
        false,
        0,
        0,
        banTimeInSeconds
    );

    // when
    int actual = endpointDto.banTimeHours();

    // then
    assertEquals(15, actual);
  }

  @Test
  void banTimeMinutes() {
    // given
    int banTimeInSeconds = 12_582_035;
    EndpointDto endpointDto = new EndpointDto(
        "hash",
        "path",
        "fqcn",
        HttpMethod.GET,
        new HashSet<>(),
        false,
        RuleStatus.ACTIVE,
        false,
        false,
        0,
        0,
        banTimeInSeconds
    );

    // when
    int actual = endpointDto.banTimeMinutes();

    // then
    assertEquals(0, actual);
  }

  @Test
  void banTimeSeconds() {
    // given
    int banTimeInSeconds = 12_582_035;
    EndpointDto endpointDto = new EndpointDto(
        "hash",
        "path",
        "fqcn",
        HttpMethod.GET,
        new HashSet<>(),
        false,
        RuleStatus.ACTIVE,
        false,
        false,
        0,
        0,
        banTimeInSeconds
    );

    // when
    int actual = endpointDto.banTimeSeconds();

    // then
    assertEquals(35, actual);
  }

  @Test
  void equalsTrueWhenAllInformationAreSame() {
    // given
    EndpointDto endpoint1 = new EndpointDto.Builder()
        .hash("hash")
        .path("/api/books")
        .fqcn("org.easypeelsecurity.springdogtest.ExampleController.example")
        .httpMethod(HttpMethod.GET)
        .parameters(new HashSet<>() {})
        .build();
    EndpointDto endpoint2 = new EndpointDto.Builder()
        .hash("hash")
        .path("/api/books")
        .fqcn("org.easypeelsecurity.springdogtest.ExampleController.example")
        .httpMethod(HttpMethod.GET)
        .parameters(new HashSet<>() {})
        .build();

    // when & then
    assertThat(endpoint1).isEqualTo(endpoint2);
  }

  @Test
  void pathAffectEqual() {
    // given
    EndpointDto endpoint1 = new EndpointDto.Builder()
        .hash("hash")
        .path("/api/books1") // diff
        .fqcn("org.easypeelsecurity.springdogtest.ExampleController.example")
        .httpMethod(HttpMethod.GET)
        .parameters(new HashSet<>() {})
        .build();
    EndpointDto endpoint2 = new EndpointDto.Builder()
        .hash("hash")
        .path("/api/books2") // diff
        .fqcn("org.easypeelsecurity.springdogtest.ExampleController.example")
        .httpMethod(HttpMethod.GET)
        .parameters(new HashSet<>() {})
        .build();

    // when & then
    assertThat(endpoint1).isNotEqualTo(endpoint2);
  }

  @Test
  void fqcnAffectEqual() {
    // given
    EndpointDto endpoint1 = new EndpointDto.Builder()
        .hash("hash")
        .path("/api/books")
        .fqcn("org.easypeelsecurity.springdogtest.ExampleController.example1") // diff
        .httpMethod(HttpMethod.GET)
        .parameters(new HashSet<>() {})
        .build();
    EndpointDto endpoint2 = new EndpointDto.Builder()
        .hash("hash")
        .path("/api/books")
        .fqcn("org.easypeelsecurity.springdogtest.ExampleController.example2") // diff
        .httpMethod(HttpMethod.GET)
        .parameters(new HashSet<>() {})
        .build();

    // when & then
    assertThat(endpoint1).isNotEqualTo(endpoint2);
  }

  @Test
  void httpMethodAffectEqual() {
    // given
    EndpointDto endpoint1 = new EndpointDto.Builder()
        .hash("hash")
        .path("/api/books")
        .fqcn("org.easypeelsecurity.springdogtest.ExampleController.example")
        .httpMethod(HttpMethod.GET)
        .parameters(new HashSet<>() {})
        .build();
    EndpointDto endpoint2 = new EndpointDto.Builder()
        .hash("hash")
        .path("/api/books")
        .fqcn("org.easypeelsecurity.springdogtest.ExampleController.example")
        .httpMethod(HttpMethod.POST) // diff
        .parameters(new HashSet<>() {})
        .build();

    // when & then
    assertThat(endpoint1).isNotEqualTo(endpoint2);
  }
}
