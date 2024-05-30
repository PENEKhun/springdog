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

package org.easypeelsecurity.springdogtest.configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.easypeelsecurity.springdog.shared.configuration.SpringdogProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ConfigurationTests {

  @Autowired
  private SpringdogProperties properties;
  @Autowired
  private MockMvc mockMvc;

  @Test
  @DisplayName("Should SpringdogProperties.agent init from application.yml")
  void agentPropertiesInitHappy() {
    // given in application.yml

    // then
    assertThat(properties.getAgentBasePath()).isEqualTo("custom-base-path");
    assertThat(properties.getAgentUsername()).isEqualTo("custom-username");
    assertThat(properties.getAgentPassword()).isEqualTo("custom-password");
  }

  @Test
  @DisplayName("Agent's connection path should changes according to the set basePath.")
  void agentBasePathShouldBeChanged() throws Exception {
    // given
    String basePath = properties.getAgentBasePath();
    String connectionPath = properties.computeAbsolutePath("/login");
    int expectedStatus = HttpStatus.OK.value();

    // when
    int status = mockMvc.perform(get(connectionPath)).andReturn().getResponse().getStatus();

    // then
    assertThat(status).isEqualTo(expectedStatus);
  }

  @Test
  @DisplayName("should be able to login as set the username and password.")
  void formLoginTest() throws Exception {
    // given
    String loginPath = properties.computeAbsolutePath("/login");
    String username = properties.getAgentUsername();
    String password = properties.getAgentPassword();

    // when & then
    mockMvc.perform(post(loginPath)
            .with(csrf())
            .param("username", username)
            .param("password", password))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/" + properties.getAgentBasePath() + "/"))
        .andExpect(authenticated().withUsername(username));
  }

  @Test
  void logoutTest() throws Exception {
    // given
    String loginPath = properties.computeAbsolutePath("/login");
    String logoutPath = properties.computeAbsolutePath("/logout");
    String username = properties.getAgentUsername();
    String password = properties.getAgentPassword();

    // when & then
    mockMvc.perform(post(loginPath)
            .with(csrf())
            .param("username", username)
            .param("password", password))
        .andDo(result -> mockMvc.perform(get(logoutPath))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl(loginPath + "?logout"))
            .andExpect(unauthenticated()));
  }
}
