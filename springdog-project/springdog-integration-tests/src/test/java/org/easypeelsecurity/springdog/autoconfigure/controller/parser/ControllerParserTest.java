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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.HashMap;

import org.easypeelsecurity.springdog.manager.ratelimit.EndpointCommand;
import org.easypeelsecurity.springdog.manager.ratelimit.EndpointQuery;
import org.easypeelsecurity.springdog.manager.ratelimit.EndpointRepository;
import org.easypeelsecurity.springdog.manager.ratelimit.VersionControlRepository;
import org.easypeelsecurity.springdog.shared.configuration.SpringdogProperties;
import org.easypeelsecurity.springdog.shared.ratelimit.VersionCompare;
import org.easypeelsecurity.springdog.shared.ratelimit.model.EndpointVersionControl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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

  @Test
  void testListEndpointsAndParameters_NoChanges() {
    // given
    when(handlerMapping.getHandlerMethods()).thenReturn(new HashMap<>());
    when(properties.computeAbsolutePath("/")).thenReturn("/");
    when(endpointQuery.compareToLatestVersion(anyString(), any())).thenReturn(VersionCompare.SAME);

    // when
    controllerParser.listEndpointsAndParameters();

    // then
    verify(versionControlRepository, never()).save(any(EndpointVersionControl.class));
    verifyNoInteractions(endpointRepository, endpointCommand);
  }

  @Test
  void testListEndpointsAndParameters_FirstRun() {
    // given
    when(handlerMapping.getHandlerMethods()).thenReturn(new HashMap<>());
    when(properties.computeAbsolutePath("/")).thenReturn("/");
    when(endpointQuery.compareToLatestVersion(anyString(), any())).thenReturn(VersionCompare.FIRST_RUN);

    // when
    controllerParser.listEndpointsAndParameters();

    // then
    verify(endpointRepository, only()).saveAll(anyList());
  }

  @Test
  void testListEndpointsAndParameters_Different() {
    // given
    when(handlerMapping.getHandlerMethods()).thenReturn(new HashMap<>());
    when(properties.computeAbsolutePath("/")).thenReturn("/");
    when(endpointQuery.compareToLatestVersion(anyString(), any())).thenReturn(VersionCompare.DIFFERENT);

    // when
    controllerParser.listEndpointsAndParameters();

    // then
    verify(endpointCommand, only()).applyChanges(any(), any(), any());
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
