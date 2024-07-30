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

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.easypeelsecurity.springdog.manager.ratelimit.EndpointQuery;
import org.easypeelsecurity.springdog.shared.ratelimit.EndpointDto;
import org.easypeelsecurity.springdog.shared.ratelimit.EndpointParameterDto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;
import org.junit.jupiter.params.provider.CsvSource;

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
        .extracting(EndpointDto::getPath, EndpointDto::getHttpMethod, EndpointDto::getMethodSignature,
            EndpointDto::isPatternPath)
        .containsExactlyInAnyOrder(
            tuple("/api/get", GET,
                "java.lang.String org.easypeelsecurity.springdogtest.ExampleController.example(java.lang.String)",
                false),
            tuple("/api/post", POST,
                "java.lang.String org.easypeelsecurity.springdogtest.ExampleController.example2(org.easypeelsecurity.springdogtest.ExampleController$PostRequest)",
                false),
            tuple("/api/delete/{id}", DELETE,
                "java.lang.String org.easypeelsecurity.springdogtest.ExampleController.example3(java.lang.Integer)",
                true),
            tuple("/api/put", PUT,
                "java.lang.String org.easypeelsecurity.springdogtest.ExampleController.example4(java.lang.String, java.lang.String)",
                false),
            tuple("/api/get/{id}", GET,
                "java.lang.String org.easypeelsecurity.springdogtest.ExampleController.example5(java.lang.Integer)",
                true)
        );
  }

  @ParameterizedTest
  @DisplayName("The controller's parameters should be parsed well.")
  @CsvSource({
      "'java.lang.String org.easypeelsecurity.springdogtest.ExampleController.example(java.lang.String)', param1",
      "'java.lang.String org.easypeelsecurity.springdogtest.ExampleController.example2(org.easypeelsecurity.springdogtest.ExampleController$PostRequest)', postRequest",
      "'java.lang.String org.easypeelsecurity.springdogtest.ExampleController.example3(java.lang.Integer)', id",
      "'java.lang.String org.easypeelsecurity.springdogtest.ExampleController.example4(java.lang.String, java.lang.String)', newTitle&newContent",
      "'java.lang.String org.easypeelsecurity.springdogtest.ExampleController.example5(java.lang.Integer)', id"
  })
  void parameterParsedWell(String methodSignature, @ConvertWith(StringToArrayConverter.class) String[] params) {
    // given
    var endpoint = endpointQuery.getEndpointByMethodSignature(methodSignature).get();
    Set<EndpointParameterDto> parameters = endpoint.getParameters();

    // when & then
    assertThat(parameters)
        .extracting(EndpointParameterDto::getName)
        .containsExactlyInAnyOrder(params);
  }

  static final class StringToArrayConverter extends SimpleArgumentConverter {

    @Override
    protected Object convert(Object source, Class<?> targetType) throws ArgumentConversionException {
      if (source instanceof String && targetType == String[].class) {
        return ((String) source).split("&");
      }
      throw new ArgumentConversionException("Conversion failed. Expected an array.");
    }
  }
}
