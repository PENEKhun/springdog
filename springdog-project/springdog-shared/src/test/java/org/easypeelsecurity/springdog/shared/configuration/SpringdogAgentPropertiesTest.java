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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SpringdogAgentPropertiesTest {

  @Test
  void basePathTest() {
    // given
    SpringdogAgentProperties springdogAgentProperties = new SpringdogAgentProperties();
    springdogAgentProperties.setBasePath("new-path");

    // when
    String basePath = springdogAgentProperties.getBasePath();

    // then
    assertThat(basePath).isEqualTo("new-path");
  }

  @Test
  void usernameTest() {
    // given
    SpringdogAgentProperties springdogAgentProperties = new SpringdogAgentProperties();
    springdogAgentProperties.setUsername("new-username");

    // when
    String username = springdogAgentProperties.getUsername();

    // then
    assertThat(username).isEqualTo("new-username");
  }

  @Test
  void passwordTest() {
    // given
    SpringdogAgentProperties springdogAgentProperties = new SpringdogAgentProperties();
    springdogAgentProperties.setPassword("new-password");

    // when
    String password = springdogAgentProperties.getPassword();

    // then
    assertThat(password).isEqualTo("new-password");
  }

  @Test
  void externalAccessTest() {
    // given
    SpringdogAgentProperties springdogAgentProperties = new SpringdogAgentProperties();
    springdogAgentProperties.setExternalAccess(true);

    // when
    boolean externalAccess = springdogAgentProperties.isExternalAccess();

    // then
    assertThat(externalAccess).isTrue();
  }
}
