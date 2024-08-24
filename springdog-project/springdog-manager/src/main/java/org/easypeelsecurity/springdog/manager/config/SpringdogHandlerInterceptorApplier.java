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

package org.easypeelsecurity.springdog.manager.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import org.easypeelsecurity.springdog.domain.ratelimit.EndpointService;
import org.easypeelsecurity.springdog.manager.agent.AgentExternalAccessInterceptor;
import org.easypeelsecurity.springdog.manager.ratelimit.RatelimitInterceptor;
import org.easypeelsecurity.springdog.manager.statistics.RequestTimingInterceptor;
import org.easypeelsecurity.springdog.notification.SlowResponseEmailNotificationManager;
import org.easypeelsecurity.springdog.shared.configuration.SpringdogProperties;

/**
 * Configuration for Springdog handler interceptors.
 *
 * @author PENEKhun
 */
@Configuration
public class SpringdogHandlerInterceptorApplier implements WebMvcConfigurer {

  private final EndpointService endpointService;
  private final SpringdogProperties springdogProperties;
  private final SlowResponseEmailNotificationManager slowResponseEmailNotificationManager;

  /**
   * Constructor.
   */
  public SpringdogHandlerInterceptorApplier(EndpointService endpointService,
      SpringdogProperties springdogProperties,
      SlowResponseEmailNotificationManager slowResponseEmailNotificationManager) {
    this.endpointService = endpointService;
    this.springdogProperties = springdogProperties;
    this.slowResponseEmailNotificationManager = slowResponseEmailNotificationManager;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new RatelimitInterceptor(this.endpointService, this.springdogProperties));
    registry.addInterceptor(new AgentExternalAccessInterceptor(this.springdogProperties));
    registry.addInterceptor(
        new RequestTimingInterceptor(this.springdogProperties, this.slowResponseEmailNotificationManager));
  }
}
