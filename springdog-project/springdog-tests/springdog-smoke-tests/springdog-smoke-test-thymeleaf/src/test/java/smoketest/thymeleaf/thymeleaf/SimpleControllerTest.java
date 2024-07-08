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

package smoketest.thymeleaf.thymeleaf;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@SpringBootTest
@AutoConfigureMockMvc
class SimpleControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  @DisplayName("The springdog agent should also connect successfully.")
  void springdogAgent() throws Exception {
    // when & then
    mockMvc.perform(get("/springdog/login"))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("It works correctly when the controller returns the specified HTML path.")
  void specificHtml() throws Exception {
    // given
    String path = "/specifiedHtmlFilePath";
    String expectedHtml =
        "<!DOCTYPE html>\n<html lang=\"en\">\n<head>\n  <meta charset=\"UTF-8\">\n  <title>SimpleThymeleafApplication</title>\n</head>\n<body>\n</body>\n</html>";

    // when & then
    mockMvc.perform(get(path))
        .andExpect(status().isOk())
        .andExpect(content().string(expectedHtml));
  }

  @Test
  @DisplayName("It works correctly when the controller returns the **not** specified HTML path.")
  void notSpecificHtml() throws Exception {
    // given
    String path = "/not/specifiedHtmlFilePath";
    String expectedHtml =
        "<!DOCTYPE html>\n<html lang=\"en\">\n<head>\n  <meta charset=\"UTF-8\">\n  <title>SimpleThymeleafApplication</title>\n</head>\n<body>\n</body>\n</html>";

    // when & then
    mockMvc.perform(get(path))
        .andExpect(status().isOk())
        .andExpect(content().string(expectedHtml));
  }
}