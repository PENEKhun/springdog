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

package org.easypeelsecurity.springdog.storage.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuration for Springdog datasource.
 */
@Configuration
public class SpringdogDatasourceConfig {
  Logger logger = LoggerFactory.getLogger(SpringdogDatasourceConfig.class);

  /**
   * Bean for accessing Springdog datasource.
   *
   * @return ServerRuntime for Springdog datasource.
   */
  @Bean(name = "springdogRepository")
  public ServerRuntime springdogRepository() {
    logger.info("Springdog datasource configuration started");
    return ServerRuntime.builder()
        .jdbcDriver("org.apache.derby.jdbc.EmbeddedDriver")
        .url("jdbc:derby:springdog-embedded-database;create=true")
        .addConfig("cayenne-springdog.xml")
        .build();
  }

  /**
   * Bean for accessing Springdog ObjectContext.
   */
  @Bean(name = "springdogContext")
  @Scope("prototype")
  public ObjectContext springdogContext() {
    return springdogRepository().newContext();
  }

  /**
   * Bean for migrating Springdog database.
   */
  @Bean(name = "springdogFlyway", initMethod = "migrate")
  @DependsOn("springdogRepository")
  public Flyway flyway() {
    logger.info("Springdog datasource flyway migration started");
    FluentConfiguration config = new FluentConfiguration();
    config
        .dataSource("jdbc:derby:springdog-embedded-database;create=true", null, null)
        .baselineOnMigrate(true)
        .baselineVersion("0")
        .sqlMigrationPrefix("V")
        .sqlMigrationSeparator("__")
        .locations("classpath:springdog-db/migration")
        .validateMigrationNaming(true);
    return new Flyway(config);
  }
}
