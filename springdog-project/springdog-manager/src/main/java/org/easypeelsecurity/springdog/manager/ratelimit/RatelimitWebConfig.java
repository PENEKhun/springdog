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

package org.easypeelsecurity.springdog.manager.ratelimit;

import org.easypeelsecurity.springdog.shared.configuration.SpringdogProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Applier for ratelimit interceptor.
 */
@Configuration
public class RatelimitWebConfig implements WebMvcConfigurer {

  private final EndpointQuery endpointQuery;
  private final SpringdogProperties springdogProperties;

  /**
   * Constructor.
   */
  public RatelimitWebConfig(EndpointQuery endpointQuery, SpringdogProperties springdogProperties) {
    this.endpointQuery = endpointQuery;
    this.springdogProperties = springdogProperties;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new RatelimitInterceptor(this.endpointQuery, this.springdogProperties));
  }
}
