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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.apache.cayenne.configuration.server.ServerRuntime;

/**
 * Configuration for Springdog datasource.
 */
@Configuration
public class SpringdogDatasourceConfig {

  /**
   * Bean for accessing Springdog datasource.
   *
   * @return ServerRuntime for Springdog datasource.
   */
  @Bean(name = "springdogRepository")
  public ServerRuntime springdogRepository() {
    return ServerRuntime.builder()
        .jdbcDriver("org.apache.derby.jdbc.EmbeddedDriver")
        .url("jdbc:derby:springdog-embedded-database;create=true")
        .addConfig("cayenne-springdog.xml")
        .build();
  }
}
