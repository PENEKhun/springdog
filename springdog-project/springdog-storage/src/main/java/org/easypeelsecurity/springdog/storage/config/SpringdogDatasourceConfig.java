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

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuration class for setting up the Springdog datasource and managing database migrations.
 * <p>
 * This configuration ensures that the Apache Derby database is fully initialized before Flyway
 * performs any migration operations. This is crucial to prevent Flyway from attempting to migrate
 * an uninitialized or partially initialized database, which can lead to migration errors.
 * </p>
 * <p>
 * The initialization order is managed by defining dependencies between Spring beans and using
 * {@link CommandLineRunner} to execute Flyway migrations after the Derby datasource is ready.
 * </p>
 *
 * @see <a href="https://flywaydb.org/documentation/">Flyway Documentation</a>
 * @see <a href="https://db.apache.org/derby/">Apache Derby Documentation</a>
 */
@Configuration
public class SpringdogDatasourceConfig {
  Logger logger = LoggerFactory.getLogger(SpringdogDatasourceConfig.class);

  /**
   * Configures and initializes the Springdog datasource using Apache Derby.
   * <p>
   * This bean sets up the {@link ServerRuntime} for the Springdog datasource with the specified
   * JDBC driver and URL. The Derby database is created if it does not already exist.
   * </p>
   *
   * @return a configured {@link ServerRuntime} instance for the Springdog datasource.
   */
  @Bean(name = "springdogRepository")
  public ServerRuntime springdogRepository() {
    logger.info("Springdog datasource configuration started");
    return ServerRuntime.builder()
        .jdbcDriver("org.apache.derby.jdbc.EmbeddedDriver")
        .url("jdbc:derby:springdog-embedded-database;create=true")
        .addConfig("springdog-db/cayenne-before-migration/cayenne-springdog.xml")
        .build();
  }

  /**
   * Provides a prototype-scoped {@link ObjectContext} for interacting with the Springdog datasource.
   * <p>
   * Each call to this bean will create a new {@link ObjectContext}, ensuring thread safety and
   * isolation between different operations.
   * </p>
   *
   * @return a new {@link ObjectContext} instance for the Springdog datasource.
   */
  @Bean(name = "springdogContext")
  @Scope("prototype")
  public ObjectContext springdogContext() {
    return springdogRepository().newContext();
  }

  /**
   * Configures the Flyway instance for managing database migrations.
   * <p>
   * This Flyway bean is configured with the appropriate datasource URL and migration settings.
   * The Flyway instance does not automatically execute migrations upon bean creation to allow
   * for controlled initialization order.
   * </p>
   *
   * @return a configured {@link Flyway} instance ready for migration.
   */
  @Bean(name = "springdogFlyway")
  public Flyway flyway() {
    logger.info("Springdog datasource flyway migration ready");
    FluentConfiguration config = new FluentConfiguration();
    config
        .dataSource("jdbc:derby:springdog-embedded-database", null, null)
        .baselineOnMigrate(true)
        .baselineVersion("0")
        .sqlMigrationPrefix("V")
        .sqlMigrationSeparator("__")
        .locations("classpath:springdog-db/migration")
        .validateMigrationNaming(true);
    return new Flyway(config);
  }

  /**
   * Executes Flyway migrations after the Springdog datasource has been fully initialized.
   * <p>
   * This {@link CommandLineRunner} bean ensures that Flyway's {@code migrate} method is called
   * only after the {@code springdogRepository} bean has been initialized. This prevents Flyway
   * from attempting to migrate the database before Derby has completed its initialization,
   * avoiding potential migration errors.
   * </p>
   *
   * @param flyway the Flyway instance responsible for performing migrations.
   * @param springdogRepository the Springdog datasource repository to ensure Derby is initialized.
   * @return a {@link CommandLineRunner} that triggers Flyway migrations at application startup.
   */
  @Bean(name = "springdogFlywayRunner")
  public CommandLineRunner migrateFlyway(@Qualifier("springdogFlyway") Flyway flyway,
      @Qualifier("springdogRepository") ServerRuntime springdogRepository) {
    return args -> {
      logger.info("Starting Flyway migration for Springdog datasource");
      flyway.migrate();
      logger.info("Flyway migration for Springdog datasource completed");
    };
  }
}
