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

package org.easypeelsecurity.springdog.agent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;

import org.easypeelsecurity.springdog.agent.security.SpringdogSecurityConfig;
import org.easypeelsecurity.springdog.shared.dto.ErrorTracingDto;

import org.junit.jupiter.api.Test;

import support.AgentTestSupport;

class SpringdogAPITest extends AgentTestSupport {

  @Autowired
  SpringdogAPI springdogAPI;

  @Test
  @WithMockUser(username = "admin", roles = {SpringdogSecurityConfig.SPRINGDOG_AGENT_ADMIN_ROLE})
  void errorTraceConfigurationEnable() throws Exception {
    doNothing().when(exceptionListingService).changeMonitoringStatus(1L, true);
    mockMvc.perform(get("/springdog/error-tracing/configuration/1?enabled=true"))
        .andExpect(status().isNoContent());
  }

  @Test
  @WithMockUser(username = "admin", roles = {SpringdogSecurityConfig.SPRINGDOG_AGENT_ADMIN_ROLE})
  void errorTraceListing() throws Exception {
    when(exceptionListingService.getErrorTrace(1L))
        .thenReturn(ErrorTracingDto.builder()
            .id(1L)
            .className("java.lang.IllegalArgumentException")
            .fileName("TestClass.java")
            .methodName("testMethod")
            .lineNumber(10)
            .timestamp(LocalDateTime.now())
            .build()
        );

    mockMvc.perform(get("/springdog/error-tracing/1"))
        .andExpect(status().isOk())
        .andExpect(mvcResult -> {
          String responseBody = mvcResult.getResponse().getContentAsString();
          assertThat(responseBody).contains("java.lang.IllegalArgumentException");
          assertThat(responseBody).contains("TestClass.java");
          assertThat(responseBody).contains("testMethod");
          assertThat(responseBody).contains("10");
        });
  }

  @Test
  @WithMockUser(username = "admin", roles = {SpringdogSecurityConfig.SPRINGDOG_AGENT_ADMIN_ROLE})
  void systemWatchMemo() throws Exception {
    doNothing().when(statisticsService).changeMemo(1L, "test");
    mockMvc.perform(post("/springdog/system-watch/1/memo?description=test"))
        .andExpect(status().isNoContent());
  }

  @Test
  void handleException() {
    // given
    Exception iae = new IllegalArgumentException("id field is required");

    // when
    ResponseEntity<CommonResponse<Void>> response = springdogAPI.handleInvalidInput(iae);

    // then
    assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    assertThat(response.getBody().getMessage()).isEqualTo(
        "Invalid input value. id field is required");
    assertThat(response.getBody().getTimestamp()).isBeforeOrEqualTo(LocalDateTime.now());
    assertThat(response.getBody().getResult()).isEqualTo("FAILURE");
  }
}
