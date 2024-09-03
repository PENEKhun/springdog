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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;

import org.easypeelsecurity.springdog.shared.enums.HttpMethod;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class EndpointDtoTest {

  @Test
  void timeLimitDays() {
    // given
    int timeLimitInSeconds = 12_582_035;
    EndpointDto endpointDto = EndpointDto.builder()
        .path("/api/books")
        .methodSignature(
            "java.lang.String org.easypeelsecurity.springdogtest.ExampleController.example(java.lang.String)")
        .httpMethod(HttpMethod.GET)
        .parameters(new HashSet<>() {
        })
        .ruleTimeLimitInSeconds(timeLimitInSeconds)
        .build();

    // when
    int actual = endpointDto.timeLimitDays();

    // then
    assertEquals(145, actual);
  }

  @Test
  void timeLimitHours() {
    // given
    int timeLimitInSeconds = 12_582_035;
    EndpointDto endpointDto = EndpointDto.builder()
        .path("/api/books")
        .methodSignature(
            "java.lang.String org.easypeelsecurity.springdogtest.ExampleController.example(java.lang.String)")
        .httpMethod(HttpMethod.GET)
        .parameters(new HashSet<>() {
        })
        .ruleTimeLimitInSeconds(timeLimitInSeconds)
        .build();

    // when
    int actual = endpointDto.timeLimitHours();

    // then
    assertEquals(15, actual);
  }

  @Test
  void timeLimitMinutes() {
    // given
    int timeLimitInSeconds = 12_582_035;
    EndpointDto endpointDto = EndpointDto.builder()
        .path("/api/books")
        .methodSignature(
            "java.lang.String org.easypeelsecurity.springdogtest.ExampleController.example(java.lang.String)")
        .httpMethod(HttpMethod.GET)
        .parameters(new HashSet<>() {
        })
        .ruleTimeLimitInSeconds(timeLimitInSeconds)
        .build();

    // when
    int actual = endpointDto.timeLimitMinutes();

    // then
    assertEquals(0, actual);
  }

  @Test
  void timeLimitSeconds() {
    // given
    int timeLimitInSeconds = 12_582_035;
    EndpointDto endpointDto = EndpointDto.builder()
        .path("/api/books")
        .methodSignature(
            "java.lang.String org.easypeelsecurity.springdogtest.ExampleController.example(java.lang.String)")
        .httpMethod(HttpMethod.GET)
        .parameters(new HashSet<>() {
        })
        .ruleTimeLimitInSeconds(timeLimitInSeconds)
        .build();

    // when
    int actual = endpointDto.timeLimitSeconds();

    // then
    assertEquals(35, actual);
  }

  @Test
  void banTimeDays() {
    // given
    int banTimeInSeconds = 12_582_035;
    EndpointDto endpointDto = EndpointDto.builder()
        .path("/api/books")
        .methodSignature(
            "java.lang.String org.easypeelsecurity.springdogtest.ExampleController.example(java.lang.String)")
        .httpMethod(HttpMethod.GET)
        .parameters(new HashSet<>() {
        })
        .ruleBanTimeInSeconds(banTimeInSeconds)
        .build();

    // when
    int actual = endpointDto.banTimeDays();

    // then
    assertEquals(145, actual);
  }

  @Test
  void banTimeHours() {
    // given
    int banTimeInSeconds = 12_582_035;
    EndpointDto endpointDto = EndpointDto.builder()
        .path("/api/books")
        .methodSignature(
            "java.lang.String org.easypeelsecurity.springdogtest.ExampleController.example(java.lang.String)")
        .httpMethod(HttpMethod.GET)
        .parameters(new HashSet<>() {
        })
        .ruleBanTimeInSeconds(banTimeInSeconds)
        .build();

    // when
    int actual = endpointDto.banTimeHours();

    // then
    assertEquals(15, actual);
  }

  @Test
  void banTimeMinutes() {
    // given
    int banTimeInSeconds = 12_582_035;
    EndpointDto endpointDto = EndpointDto.builder()
        .path("/api/books")
        .methodSignature(
            "java.lang.String org.easypeelsecurity.springdogtest.ExampleController.example(java.lang.String)")
        .httpMethod(HttpMethod.GET)
        .parameters(new HashSet<>() {
        })
        .ruleBanTimeInSeconds(banTimeInSeconds)
        .build();

    // when
    int actual = endpointDto.banTimeMinutes();

    // then
    assertEquals(0, actual);
  }

  @Test
  void banTimeSeconds() {
    // given
    int banTimeInSeconds = 12_582_035;
    EndpointDto endpointDto = EndpointDto.builder()
        .path("/api/books")
        .methodSignature(
            "java.lang.String org.easypeelsecurity.springdogtest.ExampleController.example(java.lang.String)")
        .httpMethod(HttpMethod.GET)
        .parameters(new HashSet<>() {
        })
        .ruleBanTimeInSeconds(banTimeInSeconds)
        .build();

    // when
    int actual = endpointDto.banTimeSeconds();

    // then
    assertEquals(35, actual);
  }

  @Test
  void equalsTrueWhenAllInformationAreSame() {
    // given
    EndpointDto endpoint1 = EndpointDto.builder()
        .path("/api/books")
        .methodSignature(
            "java.lang.String org.easypeelsecurity.springdogtest.ExampleController.example(java.lang.String)")
        .httpMethod(HttpMethod.GET)
        .parameters(new HashSet<>() {
        })
        .build();
    EndpointDto endpoint2 = EndpointDto.builder()
        .path("/api/books")
        .methodSignature(
            "java.lang.String org.easypeelsecurity.springdogtest.ExampleController.example(java.lang.String)")
        .httpMethod(HttpMethod.GET)
        .parameters(new HashSet<>() {
        })
        .build();

    // when & then
    assertThat(endpoint1).isEqualTo(endpoint2);
  }

  @Test
  void pathAffectEqual() {
    // given
    EndpointDto endpoint1 = EndpointDto.builder()
        .path("/api/books1") // diff
        .methodSignature(
            "java.lang.String org.easypeelsecurity.springdogtest.ExampleController.example(java.lang.String)")
        .httpMethod(HttpMethod.GET)
        .parameters(new HashSet<>() {
        })
        .build();
    EndpointDto endpoint2 = EndpointDto.builder()
        .path("/api/books2") // diff
        .methodSignature(
            "java.lang.String org.easypeelsecurity.springdogtest.ExampleController.example(java.lang.String)")
        .httpMethod(HttpMethod.GET)
        .parameters(new HashSet<>() {
        })
        .build();

    // when & then
    assertThat(endpoint1).isNotEqualTo(endpoint2);
  }

  @Test
  void methodSignatureAffectEqual() {
    // given
    EndpointDto endpoint1 = EndpointDto.builder()
        .path("/api/books")
        .methodSignature(
            "java.lang.String org.easypeelsecurity.springdogtest.ExampleController.example(java.lang.Integer)")
        .httpMethod(HttpMethod.GET)
        .parameters(new HashSet<>() {
        })
        .build();
    EndpointDto endpoint2 = EndpointDto.builder()
        .path("/api/books")
        .methodSignature(
            "java.lang.String org.easypeelsecurity.springdogtest.ExampleController.example(java.lang.String)")
        .httpMethod(HttpMethod.GET)
        .parameters(new HashSet<>() {
        })
        .build();

    // when & then
    assertThat(endpoint1).isNotEqualTo(endpoint2);
  }

  @Test
  void httpMethodAffectEqual() {
    // given
    EndpointDto endpoint1 = EndpointDto.builder()
        .path("/api/books")
        .methodSignature(
            "java.lang.String org.easypeelsecurity.springdogtest.ExampleController.example(java.lang.String)")
        .httpMethod(HttpMethod.GET)
        .parameters(new HashSet<>() {
        })
        .build();
    EndpointDto endpoint2 = EndpointDto.builder()
        .path("/api/books")
        .methodSignature(
            "java.lang.String org.easypeelsecurity.springdogtest.ExampleController.example(java.lang.String)")
        .httpMethod(HttpMethod.POST) // diff
        .parameters(new HashSet<>() {
        })
        .build();

    // when & then
    assertThat(endpoint1).isNotEqualTo(endpoint2);
  }

  @ParameterizedTest
  @CsvSource({
      "java.lang.String org.msh.ExampleController.example(java.lang.String), org.msh.ExampleController",
      "java.lang.String org.qwe.QQ.example(java.lang.Integer), org.qwe.QQ",
      "void kr.epsec.controller.example(java.lang.String), kr.epsec.controller",
      "java.lang.String org.easypeelsecurity.springdogtest.ExampleController.example(), org.easypeelsecurity.springdogtest.ExampleController"
  })
  void getFqcnWork(String givenSignature, String expectedFqcn) {
    // given
    EndpointDto endpointDto = EndpointDto.builder()
        .path("/api/books")
        .httpMethod(HttpMethod.GET)
        .parameters(new HashSet<>() {
        })
        .methodSignature(givenSignature)
        .build();

    // when
    String actual = endpointDto.getFqcn();

    // then
    assertEquals(expectedFqcn, actual);
  }

  @ParameterizedTest
  @CsvSource({
      "java.lang.String org.msh.ExampleController.example(java.lang.String), org.msh.ExampleController.example(java.lang.String)",
      "java.lang.String org.qwe.QQ.example(java.lang.Integer), org.qwe.QQ.example(java.lang.Integer)",
      "void kr.epsec.controller.example(java.lang.String), kr.epsec.controller.example(java.lang.String)",
      "java.lang.String org.easypeelsecurity.springdogtest.ExampleController.example(), org.easypeelsecurity.springdogtest.ExampleController.example()"
  })
  void getFqmnWork(String givenSignature, String expectedFqmn) {
    // given
    EndpointDto endpointDto = EndpointDto.builder()
        .path("/api/books")
        .httpMethod(HttpMethod.GET)
        .parameters(new HashSet<>() {
        })
        .methodSignature(givenSignature)
        .build();

    // when
    String actual = endpointDto.getFqmn();

    // then
    assertEquals(expectedFqmn, actual);
  }

  @ParameterizedTest
  @CsvSource({
      "java.lang.String org.msh.ExampleController.example(java.lang.String), example(java.lang.String)",
      "java.lang.String org.qwe.QQ.example(java.lang.Integer), example(java.lang.Integer)",
      "void kr.epsec.controller.example(java.lang.String), example(java.lang.String)",
      "java.lang.String org.easypeelsecurity.springdogtest.ExampleController.example(), example()"
  })
  void getMethodNameWork(String givenSignature, String expected) {
    // given
    EndpointDto endpointDto = EndpointDto.builder()
        .path("/api/books")
        .httpMethod(HttpMethod.GET)
        .parameters(new HashSet<>() {
        })
        .methodSignature(givenSignature)
        .build();

    // when
    String actual = endpointDto.getMethodName();

    // then
    assertEquals(expected, actual);
  }

  @Test
  void getMethodSignatureWork() {
    // given
    String methodSignature =
        "java.lang.String org.easypeelsecurity.springdogtest.ExampleController.example(java.lang.String)";
    EndpointDto endpointDto = EndpointDto.builder()
        .path("/api/books")
        .httpMethod(HttpMethod.GET)
        .parameters(new HashSet<>() {
        })
        .methodSignature(methodSignature)
        .build();

    // when
    String actual = endpointDto.getMethodSignature();

    // then
    assertEquals(
        "java.lang.String org.easypeelsecurity.springdogtest.ExampleController.example(java.lang.String)",
        actual);
  }
}

