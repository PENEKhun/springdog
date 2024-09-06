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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.security.test.context.support.WithMockUser;

import org.easypeelsecurity.springdog.agent.security.SpringdogSecurityConfig;
import org.easypeelsecurity.springdog.shared.dto.EndpointDto;
import org.easypeelsecurity.springdog.shared.dto.ExceptionClassesDto;
import org.easypeelsecurity.springdog.shared.enums.HttpMethod;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import fixture.DashBoardResponseFixture;
import support.AgentTestSupport;

class SpringdogAgentViewTest extends AgentTestSupport {

  @ParameterizedTest
  @CsvSource({
      "/",
      "/aidwjaiwjdawid",
      "/login",
  })
  void invalidPage(String invalidPath) throws Exception {
    mockMvc.perform(get("/login"))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Inaccessible without permission")
  void withoutAuthentication() throws Exception {
    mockMvc.perform(get("/springdog/"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("http://localhost/springdog/login"));
  }

  @Test
  @WithMockUser(username = "admin", roles = {SpringdogSecurityConfig.SPRINGDOG_AGENT_ADMIN_ROLE})
  void testHomePage() throws Exception {
    when(endpointService.getAllChangeLogsNotResolved()).thenReturn(Collections.emptyList());
    when(statisticsService.getDashboardResponse(any())).thenReturn(DashBoardResponseFixture.get());

    mockMvc.perform(get("/springdog/"))
        .andExpect(status().isOk())
        .andExpect(view().name("/templates/content/main.html"))
        .andExpect(model().attributeExists("endpointChangeLog", "totalEndpointCount"));
  }

  @Test
  @WithMockUser(username = "admin", roles = {SpringdogSecurityConfig.SPRINGDOG_AGENT_ADMIN_ROLE})
  void ratelimitList() throws Exception {
    when(endpointService.findAllEndpoints()).thenReturn(List.of());

    mockMvc.perform(get("/springdog/rate-limit"))
        .andExpect(status().isOk())
        .andExpect(view().name("/templates/content/rate-limit/manage.html"))
        .andExpect(model().attributeExists("endpoints"));
  }

  @Test
  @WithMockUser(username = "admin", roles = {SpringdogSecurityConfig.SPRINGDOG_AGENT_ADMIN_ROLE})
  void setRateLimitSpecific() throws Exception {
    long endpointId = 1L;
    when(endpointService.findEndpoint(endpointId)).thenReturn(
        EndpointDto.builder()
            .id(endpointId)
            .path("/test")
            .methodSignature("org.a.b.C.d()")
            .httpMethod(HttpMethod.GET)
            .build()
    );

    mockMvc.perform(get("/springdog/rate-limit/{endpointId}", endpointId))
        .andExpect(status().isOk())
        .andExpect(view().name("/templates/content/rate-limit/actions/setting.html"))
        .andExpect(model().attributeExists("endpoint"));
  }

  @Test
  @WithMockUser(username = "admin", roles = {SpringdogSecurityConfig.SPRINGDOG_AGENT_ADMIN_ROLE})
  void viewRatelimitEndpointAnalytics() throws Exception {
    long endpointId = 1L;
    when(endpointService.findEndpoint(endpointId)).thenReturn(
        EndpointDto.builder()
            .id(endpointId)
            .path("/test")
            .methodSignature("org.a.b.C.d()")
            .httpMethod(HttpMethod.GET)
            .build()
    );

    mockMvc.perform(get("/springdog/rate-limit/{endpointId}/analytics", endpointId))
        .andExpect(status().isOk())
        .andExpect(view().name("/templates/content/rate-limit/actions/analytics.html"))
        .andExpect(model().attributeExists("endpoint"));
  }

  @Test
  void testLogin() throws Exception {
    mockMvc.perform(get("/springdog/rate-limit"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("http://localhost/springdog/login"));
  }

  @Test
  @WithMockUser(username = "admin", roles = {SpringdogSecurityConfig.SPRINGDOG_AGENT_ADMIN_ROLE})
  void testLogout() throws Exception {
    mockMvc.perform(get("/springdog/logout"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/springdog/login?logout"));
  }

  @Test
  @WithMockUser(username = "admin", roles = {SpringdogSecurityConfig.SPRINGDOG_AGENT_ADMIN_ROLE})
  void errorTraceConfiguration() throws Exception {
    when(exceptionListingService.getExceptionListing()).thenReturn(new ExceptionClassesDto(new ArrayList<>()));
    mockMvc.perform(get("/springdog/error-tracing/configuration"))
        .andExpect(status().isOk());
  }
}
