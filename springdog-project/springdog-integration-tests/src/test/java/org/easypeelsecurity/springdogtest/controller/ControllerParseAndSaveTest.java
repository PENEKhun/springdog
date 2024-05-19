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
import static org.easypeelsecurity.springdog.shared.ratelimit.model.HttpMethod.DELETE;
import static org.easypeelsecurity.springdog.shared.ratelimit.model.HttpMethod.GET;
import static org.easypeelsecurity.springdog.shared.ratelimit.model.HttpMethod.POST;
import static org.easypeelsecurity.springdog.shared.ratelimit.model.HttpMethod.PUT;

import org.easypeelsecurity.springdog.manager.ratelimit.EndpointQuery;
import org.easypeelsecurity.springdog.shared.ratelimit.EndpointDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ControllerParseAndSaveTest {

  @Autowired
  EndpointQuery endpointQuery;

  @Test
  @DisplayName("should parse well for a variety of mappings.")
  void parsingTest() {
    // given & when
    var endpoints = endpointQuery.findAll();

    // then
    assertThat(endpoints)
        .extracting(EndpointDto::getPath, EndpointDto::getHttpMethod, EndpointDto::getFqcn,
            EndpointDto::isPatternPath)
        .containsExactlyInAnyOrder(
            tuple("/api/get", GET,
                "org.easypeelsecurity.springdogtest.ExampleController.example",
                false),
            tuple("/api/post", POST,
                "org.easypeelsecurity.springdogtest.ExampleController.example2",
                false),
            tuple("/api/delete", DELETE,
                "org.easypeelsecurity.springdogtest.ExampleController.example3",
                false),
            tuple("/api/put", PUT,
                "org.easypeelsecurity.springdogtest.ExampleController.example4",
                false),
            tuple("/api/get/{id}", GET,
                "org.easypeelsecurity.springdogtest.ExampleController.example5",
                true)
        );
  }
}
