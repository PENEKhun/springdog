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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@SpringBootTest(properties = "springdog.agent.externalAccess=true")
@AutoConfigureMockMvc
class EnableExternalAccessTest {

  @Autowired
  private MockMvc mockMvc;

  @ParameterizedTest
  @CsvSource({
      "127.0.0.1",
      "00:00:00:00:00:00:00:01",
  })
  @DisplayName("Internal access is possible external access was enabled.")
  void internalAccessTest(String publicIpAddress) throws Exception {
    // given
    MockHttpServletRequestBuilder requestBuilder = get("/custom-base-path/login")
        .header("Content-Type", "text/html")
        .with(request -> {
          request.setRemoteAddr(publicIpAddress);
          return request;
        });

    // when & then
    mockMvc.perform(requestBuilder)
        .andExpect(status().isOk());
  }

}
