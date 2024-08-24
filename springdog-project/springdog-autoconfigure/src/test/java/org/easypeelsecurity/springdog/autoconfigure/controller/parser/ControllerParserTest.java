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

package org.easypeelsecurity.springdog.autoconfigure.controller.parser;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.annotation.PostConstruct;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import org.easypeelsecurity.springdog.domain.ratelimit.VersionControlService;
import org.easypeelsecurity.springdog.shared.configuration.SpringdogProperties;
import org.easypeelsecurity.springdog.shared.dto.EndpointDto;
import org.easypeelsecurity.springdog.shared.enums.HttpMethod;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

class ControllerParserTest {

  @Mock
  RequestMappingHandlerMapping handlerMapping;

  @Mock
  VersionControlService versionControlService;

  @Mock
  SpringdogProperties properties;

  private ControllerParser controllerParser;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    controllerParser =
        new ControllerParser(handlerMapping, versionControlService, properties);
  }

  @Test
  @DisplayName("parseController return endpoints parsed.")
  void controllerParse() throws NoSuchMethodException {
    // given
    RequestMappingInfo requestMappingInfo = Mockito.mock(RequestMappingInfo.class);
    HandlerMethod handlerMethod = new HandlerMethod(new MockHandlerMethod(), "method");
    when(requestMappingInfo.getDirectPaths()).thenReturn(Collections.singleton("/api/test"));
    when(requestMappingInfo.getMethodsCondition()).thenReturn(
        new RequestMethodsRequestCondition(RequestMethod.resolve(org.springframework.http.HttpMethod.GET)));
    Map<RequestMappingInfo, HandlerMethod> handlerMethods = new HashMap<>();
    handlerMethods.put(requestMappingInfo, handlerMethod);

    // when
    List<EndpointDto> result = controllerParser.parseController(handlerMethods, "/exclude");

    // then
    assertThat(result.toArray())
        .extracting("path", "httpMethod", "methodSignature")
        .containsExactly(
            tuple("/api/test", HttpMethod.GET,
                "void org.easypeelsecurity.springdog.autoconfigure.controller.parser.ControllerParserTest$MockHandlerMethod.method()"));
  }

  @Test
  @DisplayName("Endpoints with certain prefixes in the path should not be parsed.")
  void controllerParseExclude() throws NoSuchMethodException {
    // given
    RequestMappingInfo requestMappingInfo = Mockito.mock(RequestMappingInfo.class);
    HandlerMethod handlerMethod = new HandlerMethod(new MockHandlerMethod(), "method");
    when(requestMappingInfo.getDirectPaths()).thenReturn(Collections.singleton("/exclude/api/test"));
    when(requestMappingInfo.getMethodsCondition()).thenReturn(
        new RequestMethodsRequestCondition(RequestMethod.resolve(org.springframework.http.HttpMethod.GET)));
    Map<RequestMappingInfo, HandlerMethod> handlerMethods = new HashMap<>();
    handlerMethods.put(requestMappingInfo, handlerMethod);

    // when
    List<EndpointDto> result = controllerParser.parseController(handlerMethods, "/exclude");

    // then
    assertThat(result.size()).isZero();
  }

  @Test
  @DisplayName("Should have PostConstruct annotation at listEndpointsAndParameters()")
  void postConstructTest() throws NoSuchMethodException {
    // given
    Class<ControllerParser> clazz = ControllerParser.class;
    Method method = clazz.getDeclaredMethod("listEndpointsAndParameters");

    // when
    boolean isAnnotationPresent = method.isAnnotationPresent(PostConstruct.class);

    // then
    assertThat(isAnnotationPresent).isTrue();
  }

  private final class MockHandlerMethod {

    public void method() {
    }
  }
}
