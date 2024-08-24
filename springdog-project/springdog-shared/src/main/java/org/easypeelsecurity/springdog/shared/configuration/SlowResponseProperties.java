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

package org.easypeelsecurity.springdog.shared.configuration;

import jakarta.annotation.PostConstruct;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * Slow response properties.
 */
@Setter
@Getter
@ConfigurationProperties(prefix = "springdog.slow-response")
@SuppressWarnings("checkstyle:MissingJavadocMethod")
public class SlowResponseProperties {

  private long thresholdMs;
  private boolean enabled;

  @PostConstruct
  public void init() {
    if (enabled) {
      if (thresholdMs <= 0) {
        throw new IllegalArgumentException("To enable SlowResponse, you must enter a threshold.");
      }
    }
  }
}
