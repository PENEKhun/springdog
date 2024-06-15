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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.easypeelsecurity.springdog.manager.ratelimit.EndpointCommand;
import org.easypeelsecurity.springdog.manager.ratelimit.EndpointQuery;
import org.easypeelsecurity.springdog.manager.ratelimit.EndpointRepository;
import org.easypeelsecurity.springdog.manager.ratelimit.VersionControlRepository;
import org.easypeelsecurity.springdog.shared.configuration.SpringdogProperties;
import org.easypeelsecurity.springdog.shared.ratelimit.EndpointDto;
import org.easypeelsecurity.springdog.shared.ratelimit.VersionCompare;
import org.easypeelsecurity.springdog.shared.ratelimit.model.EndpointVersionControl;
import org.easypeelsecurity.springdog.shared.ratelimit.model.HttpMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;

class ControllerParserTest {

  @Mock
  private RequestMappingHandlerMapping handlerMapping;

  @Mock
  private EndpointQuery endpointQuery;

  @Mock
  private EndpointCommand endpointCommand;

  @Mock
  private EndpointRepository endpointRepository;

  @Mock
  private VersionControlRepository versionControlRepository;

  @Mock
  private SpringdogProperties properties;

  private ControllerParser controllerParser;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    controllerParser =
        new ControllerParser(handlerMapping, endpointQuery, endpointCommand, endpointRepository, properties,
            versionControlRepository);
  }

  private final class MockHandlerMethod {

    public void method() {
    }
  }

  @Test
  void testListEndpointsAndParameters_NoChanges() {
    // given
    when(handlerMapping.getHandlerMethods()).thenReturn(new HashMap<>());
    when(properties.computeAbsolutePath("/")).thenReturn("/");
    when(endpointQuery.compareToLatestVersion(ArgumentMatchers.anyString(), ArgumentMatchers.any())).thenReturn(
        VersionCompare.SAME);

    // when
    controllerParser.listEndpointsAndParameters();

    // then
    verify(versionControlRepository, Mockito.never()).save(ArgumentMatchers.any(EndpointVersionControl.class));
    verifyNoInteractions(endpointRepository, endpointCommand);
  }

  @Test
  void testListEndpointsAndParameters_FirstRun() {
    // given
    when(handlerMapping.getHandlerMethods()).thenReturn(new HashMap<>());
    when(properties.computeAbsolutePath("/")).thenReturn("/");
    when(endpointQuery.compareToLatestVersion(ArgumentMatchers.anyString(), ArgumentMatchers.any())).thenReturn(
        VersionCompare.FIRST_RUN);

    // when
    controllerParser.listEndpointsAndParameters();

    // then
    verify(endpointRepository, Mockito.only()).saveAll(ArgumentMatchers.anyList());
  }

  @Test
  void testListEndpointsAndParameters_Different() {
    // given
    when(handlerMapping.getHandlerMethods()).thenReturn(new HashMap<>());
    when(properties.computeAbsolutePath("/")).thenReturn("/");
    when(endpointQuery.compareToLatestVersion(ArgumentMatchers.anyString(), ArgumentMatchers.any())).thenReturn(
        VersionCompare.DIFFERENT);

    // when
    controllerParser.listEndpointsAndParameters();

    // then
    verify(endpointCommand, Mockito.only()).applyChanges(ArgumentMatchers.any(), ArgumentMatchers.any(),
        ArgumentMatchers.any());
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
    Set<EndpointDto> result = controllerParser.parseController(handlerMethods, "/exclude");

    // then
    assertThat(result.toArray())
        .extracting("path", "httpMethod", "fqcn")
        .containsExactly(
            tuple("/api/test", HttpMethod.GET,
                "org.easypeelsecurity.springdog.autoconfigure.controller.parser.MockHandlerMethod.method"));
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
    Set<EndpointDto> result = controllerParser.parseController(handlerMethods, "/exclude");

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

  @Test
  @DisplayName("Should have Transactional annotation at listEndpointsAndParameters()")
  void transactionalTest() throws NoSuchMethodException {
    // given
    Class<ControllerParser> clazz = ControllerParser.class;
    Method method = clazz.getDeclaredMethod("listEndpointsAndParameters");

    // when
    boolean isAnnotationPresent = method.isAnnotationPresent(Transactional.class);

    // then
    assertThat(isAnnotationPresent).isTrue();
  }
}
