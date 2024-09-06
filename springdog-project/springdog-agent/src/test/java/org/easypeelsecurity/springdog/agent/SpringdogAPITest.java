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

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.security.test.context.support.WithMockUser;

import org.easypeelsecurity.springdog.agent.security.SpringdogSecurityConfig;

import org.junit.jupiter.api.Test;

import support.AgentTestSupport;

class SpringdogAPITest extends AgentTestSupport {

  @Test
  @WithMockUser(username = "admin", roles = {SpringdogSecurityConfig.SPRINGDOG_AGENT_ADMIN_ROLE})
  void errorTraceConfigurationEnable() throws Exception {
    doNothing().when(exceptionListingService).changeMonitoringStatus(1L, true);
    mockMvc.perform(get("/springdog/error-tracing/configuration/1?enabled=true"))
        .andExpect(status().isOk());
  }
}
