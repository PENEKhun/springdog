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

package org.easypeelsecurity.springdogtest.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.easypeelsecurity.springdog.shared.enums.EndpointParameterType.BODY;
import static org.easypeelsecurity.springdog.shared.enums.EndpointParameterType.PATH;
import static org.easypeelsecurity.springdog.shared.enums.EndpointParameterType.QUERY;
import static org.easypeelsecurity.springdog.shared.enums.HttpMethod.DELETE;
import static org.easypeelsecurity.springdog.shared.enums.HttpMethod.GET;
import static org.easypeelsecurity.springdog.shared.enums.HttpMethod.POST;
import static org.easypeelsecurity.springdog.shared.enums.HttpMethod.PUT;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.easypeelsecurity.springdog.domain.ratelimit.EndpointService;
import org.easypeelsecurity.springdog.shared.dto.EndpointDto;
import org.easypeelsecurity.springdog.shared.dto.EndpointHeaderDto;
import org.easypeelsecurity.springdog.shared.dto.EndpointParameterDto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@SpringBootTest
class ControllerParseAndSaveTest {

  @Autowired
  EndpointService endpointService;

  @Test
  @DisplayName("should parse well for a variety of mappings.")
  void parsingTest() {
    // given & when
    var endpoints = endpointService.findAllEndpoints();

    // then
    assertThat(endpoints)
        .extracting(EndpointDto::getPath, EndpointDto::getHttpMethod, EndpointDto::getMethodSignature,
            EndpointDto::isPatternPath, EndpointDto::getParameters, EndpointDto::getHeaders)
        .containsExactlyInAnyOrder(
            tuple("/api/get", GET,
                "java.lang.String org.easypeelsecurity.springdogtest.ExampleController.example(java.lang.String)",
                false, Set.of(new EndpointParameterDto("param1", QUERY, false)), Set.of()),
            tuple("/api/post", POST,
                "java.lang.String org.easypeelsecurity.springdogtest.ExampleController.example2(org.easypeelsecurity.springdogtest.ExampleController$PostRequest)",
                false, Set.of(new EndpointParameterDto("postRequest", BODY, false)), Set.of()),
            tuple("/api/delete/{id}", DELETE,
                "java.lang.String org.easypeelsecurity.springdogtest.ExampleController.example3(java.lang.Integer)",
                true, Set.of(new EndpointParameterDto("id", PATH, false)), Set.of()),
            tuple("/api/put", PUT,
                "java.lang.String org.easypeelsecurity.springdogtest.ExampleController.example4(java.lang.String, java.lang.String)",
                false, Set.of(new EndpointParameterDto("newTitle", QUERY, false),
                    new EndpointParameterDto("newContent", QUERY, false)), Set.of()),
            tuple("/api/get/{id}", GET,
                "java.lang.String org.easypeelsecurity.springdogtest.ExampleController.example5(java.lang.Integer)",
                true, Set.of(new EndpointParameterDto("id", PATH, false)), Set.of()),
            tuple("/api/header", GET,
                "java.lang.String org.easypeelsecurity.springdogtest.ExampleController.example6(java.lang.String, java.lang.String)",
                false, Set.of(), Set.of(
                    new EndpointHeaderDto("token1", false),
                    new EndpointHeaderDto("token2", false)
                )),
            tuple("/api/exception1", GET, "java.lang.String org.easypeelsecurity.springdogtest.ExampleController.exception1()",
                false, Set.of(), Set.of()),
            tuple("/api/exception2", GET, "java.lang.String org.easypeelsecurity.springdogtest.ExampleController.exception2()",
                false, Set.of(), Set.of())
        );
  }
}
