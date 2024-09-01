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

import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import org.easypeelsecurity.springdog.shared.configuration.SpringdogProperties;
import org.easypeelsecurity.springdog.shared.util.Assert;

/**
 * Springdog dynamic URL mapping configuration.
 * Enable agent access to the springdog path user determine at runtime.
 */
@Component
public class SpringdogDynamicUrlMappingConfig implements WebMvcRegistrations {

  private final SpringdogProperties springdogProperties;

  /**
   * Constructor.
   */
  public SpringdogDynamicUrlMappingConfig(SpringdogProperties springdogProperties) {
    this.springdogProperties = springdogProperties;
  }

  @Override
  public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
    return new CustomRequestMappingHandlerMapping(springdogProperties);
  }

  static class CustomRequestMappingHandlerMapping extends RequestMappingHandlerMapping {
    private final SpringdogProperties springdogProperties;

    CustomRequestMappingHandlerMapping(SpringdogProperties springdogProperties) {
      this.springdogProperties = springdogProperties;
    }

    @Override
    protected void initHandlerMethods() {
      String basePath = springdogProperties.computeAbsolutePath("");
      Assert.notNull(basePath, "Springdog Agent's base path must not be null");

      setPathPrefixes(
          java.util.Collections.singletonMap(
              basePath,
              HandlerTypePredicate.forAnnotation(SpringdogAgentController.class)
          )
      );
      super.initHandlerMethods();
    }
  }
}
