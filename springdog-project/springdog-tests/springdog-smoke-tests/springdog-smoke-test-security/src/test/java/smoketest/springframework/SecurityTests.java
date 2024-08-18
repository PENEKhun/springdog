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

package smoketest.springframework;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityTests {

  @Autowired
  private MockMvc mockMvc;

  @ParameterizedTest
  @CsvSource({
      "/springdog/", "/springdog"
  })
  @DisplayName("Should connect to Springdog agent successfully even with user security configuration")
  void connectToSpringdogAgentWithUserSecurityConfigSuccessfully(String agentPath) throws Exception {
    // when & then
    mockMvc.perform(get(agentPath))
        .andExpect(status().is3xxRedirection())
        .andExpect(result -> result.getResponse().getRedirectedUrl().equals("/springdog/login"));
  }

  @Test
  @DisplayName("Should access pages without app permissions successfully")
  void accessPagesWithoutPermissionsSuccessfully() throws Exception {
    // given
    String path = "/anonymous";

    // when & then
    mockMvc.perform(get(path))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("Should redirect to login page when accessing pages with app permissions")
  void redirectToLoginPageForProtectedPages() throws Exception {
    // given
    String path = "/admin";

    // when & then
    mockMvc.perform(get(path))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("http://localhost/login.html"));
  }


  @Test
  @DisplayName("Should work custom login without conflicting with agent login.")
  void customLoginDoesNotConflictWithAgentLogin() throws Exception {
    // given
    String loginPath = "/login.html";
    String formData = "username=admin&password=password";

    // when & then
    mockMvc.perform(post(loginPath)
            .contentType("application/x-www-form-urlencoded")
            .content(formData))
        .andDo(print())
        .andExpect(redirectedUrl("/admin"))
        .andExpect(request -> assertNotNull(request.getRequest().getSession(false)))
        .andDo(result -> mockMvc.perform(get("/admin")
                .session((MockHttpSession) result.getRequest().getSession(false)))
            .andExpect(status().isOk()))
        .andDo(result -> mockMvc.perform(get("/springdog/login")
                .session((MockHttpSession) result.getRequest().getSession(false)))
            .andExpect(status().isOk()));
  }
}
