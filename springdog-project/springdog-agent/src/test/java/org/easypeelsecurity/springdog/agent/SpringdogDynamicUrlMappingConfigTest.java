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

package org.easypeelsecurity.springdog.agent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import org.easypeelsecurity.springdog.agent.SpringdogDynamicUrlMappingConfig.CustomRequestMappingHandlerMapping;
import org.easypeelsecurity.springdog.shared.configuration.SpringdogProperties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SpringdogDynamicUrlMappingConfigTest {

  private SpringdogProperties springdogProperties;
  private SpringdogDynamicUrlMappingConfig config;

  @BeforeEach
  void setUp() {
    springdogProperties = mock(SpringdogProperties.class);
    config = new SpringdogDynamicUrlMappingConfig(springdogProperties);
  }

  @Test
  void testNullBasePath() {
    when(springdogProperties.computeAbsolutePath("")).thenReturn(null);

    RequestMappingHandlerMapping handlerMapping = config.getRequestMappingHandlerMapping();
    CustomRequestMappingHandlerMapping customMapping =
        (CustomRequestMappingHandlerMapping) handlerMapping;

    assertThat(customMapping)
        .isNotNull()
        .satisfies(mapping -> {
          assertThrows(IllegalArgumentException.class, mapping::initHandlerMethods);
        });
  }
}
