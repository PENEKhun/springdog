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

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Springdog agent properties.
 */
@ConfigurationProperties(prefix = "springdog.agent")
@SuppressWarnings("checkstyle:MissingJavadocMethod")
class SpringdogAgentProperties {

  private String basePath = "springdog";
  private String username = "admin";
  private String password = "admin";
  private boolean externalAccess;

  public void setBasePath(String basePath) {
    this.basePath = basePath;
  }

  public String getBasePath() {
    return basePath;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public boolean isExternalAccess() {
    return externalAccess;
  }

  public void setExternalAccess(boolean externalAccess) {
    this.externalAccess = externalAccess;
  }
}
