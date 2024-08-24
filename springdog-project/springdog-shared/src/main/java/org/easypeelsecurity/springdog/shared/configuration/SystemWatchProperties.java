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
 * System watch properties.
 */
@Setter
@Getter
@ConfigurationProperties(prefix = "springdog.system-watch")
@SuppressWarnings("checkstyle:MissingJavadocMethod")
public class SystemWatchProperties {
  private double cpuThreshold;
  private double memoryThreshold;
  private double diskThreshold;
  private boolean enabled;

  @PostConstruct
  public void init() {
    if (enabled) {
      String commonMessage = "To enable SystemWatch, you must enter at least one threshold.";
      if (cpuThreshold == 0 && memoryThreshold == 0 && diskThreshold == 0) {
        throw new IllegalArgumentException(commonMessage);
      }
    }
  }
}
