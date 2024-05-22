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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * Springdog configuration.
 */
@Configuration
@ConfigurationProperties(prefix = "springdog")
@EnableConfigurationProperties(SpringdogAgentProperties.class)
@SuppressWarnings("checkstyle:MissingJavadocMethod")
public class SpringdogProperties {

  private final Logger logger = LoggerFactory.getLogger(SpringdogProperties.class);

  @Autowired
  private SpringdogAgentProperties agentProperties;

  @PostConstruct
  public void init() {
    logger.info("SpringdogProperties was loaded");
    logger.debug("Springdog Agent's base path was set by {}", agentProperties.getBasePath());
    logger.debug("Springdog Agent's username was set by {}", agentProperties.getUsername());
    logger.debug("Springdog Agent's password was set by {}", agentProperties.getPassword());
  }

  public String getAgentBasePath() {
    return agentProperties.getBasePath();
  }

  /**
   * Take a relative path named 'path' and computes it as an absolute path.
   * @param extraPath extra path
   * @return absolute path
   */
  public String computeAbsolutePath(String extraPath) {
    if (getAgentBasePath().startsWith("/")) {
      return getAgentBasePath() + extraPath;
    }

    return "/" + getAgentBasePath() + extraPath;
  }

  public String getAgentUsername() {
    return agentProperties.getUsername();
  }

  public String getAgentPassword() {
    return agentProperties.getPassword();
  }
}
