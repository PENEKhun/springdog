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

package org.easypeelsecurity.agenttests;


import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlButton;
import org.htmlunit.html.HtmlForm;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.html.HtmlPasswordInput;
import org.htmlunit.html.HtmlSubmitInput;
import org.htmlunit.html.HtmlTable;
import org.htmlunit.html.HtmlTableRow;
import org.htmlunit.html.HtmlTextInput;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AgentTests {
  final String AGENT_ADMIN_ROLE = "SPRINGDOG_AGENT_ADMIN";
  final String AGENT_USERNAME = "username";
  final String AGENT_PASSWORD = "password";

  @LocalServerPort
  private int port;

  private WebClient webClient;

  @BeforeEach
  public void setUp() {
    webClient = new WebClient();
    webClient.getOptions().setThrowExceptionOnScriptError(false);
    webClient.getOptions().setCssEnabled(false);
    webClient.getOptions().setJavaScriptEnabled(false);
  }

  @AfterEach
  public void tearDown() {
    if (webClient != null) {
      webClient.close();
    }
  }

  @ParameterizedTest
  @CsvSource({
      "/springdog", "/springdog/"
  })
  @DisplayName("Redirect to the login page when accessing the home PATH")
  void should_redirect_to_loginPage(String homePath) throws IOException {
    HtmlPage page = webClient.getPage("http://localhost:" + port + homePath);
    assertThat(page.getUrl()).hasToString(getAgentPath("/login"));
  }


  @Test
  @DisplayName("Redirect to home page after successful login")
  void login_success() throws IOException {
    AgentTestHelper agent = new AgentTestHelper();
    HtmlPage resultPage = agent.login(AGENT_USERNAME, AGENT_PASSWORD);

    assertThat(resultPage.getUrl()).hasToString(getAgentPath(""));
    assertThat(resultPage.asXml()).contains("Dashboard", "Total Endpoints");
  }

  @Test
  @DisplayName("Display error message when login fails")
  void login_fail() throws IOException {
    AgentTestHelper agent = new AgentTestHelper();
    HtmlPage resultPage = agent.login("wrong_username", "wrong_password");

    assertThat(resultPage.getUrl()).hasToString(getAgentPath("/login?error"));
    assertThat(resultPage.asXml()).contains("Login Failed.");
  }

  @Test
  void homePage() throws IOException {
    AgentTestHelper agent = new AgentTestHelper();
    agent.withLogin();

    HtmlPage page = webClient.getPage(getAgentPath("/"));
    assertThat(page.asXml()).contains("Dashboard", "Total Endpoints");
  }

  @Test
  void ratelimit_detail() throws IOException {
    AgentTestHelper agent = new AgentTestHelper();
    agent.withLogin();

    HtmlPage page = webClient.getPage(getAgentPath("/rate-limit"));
    HtmlTable table = page.getFirstByXPath("//table[@id='table']");
    HtmlTableRow row = table.getRow(1); // Skip the header row
    HtmlButton analyzeBtn = row.getCell(4).getFirstByXPath(".//button[@title='Analyze']");
    HtmlPage detailPage = analyzeBtn.click();

    // then
    assertThat(detailPage.asNormalizedText()).contains("""
        Path	Method Signature	HTTP Method	Status	Actions
        /api/hello	java.lang.String org.easypeelsecurity.agenttests.ExampleController.hello()	GET	 NOT_CONFIGURED""");
  }

  @Test
  void ratelimit_read() throws IOException {
    AgentTestHelper agent = new AgentTestHelper();
    agent.withLogin();

    HtmlPage page = webClient.getPage(getAgentPath("/rate-limit"));
    HtmlTable table = page.getFirstByXPath("//table[@id='table']");
    HtmlTableRow row = table.getRow(1); // Skip the header row

    // check row
    assertThat(row.getCell(0).getTextContent()).isEqualTo("/api/hello");
    assertThat(row.getCell(1).getTextContent()).isEqualTo(
        "java.lang.String org.easypeelsecurity.agenttests.ExampleController.hello()");
    assertThat(row.getCell(2).getTextContent()).isEqualTo("GET");
    assertThat(row.getCell(3).getTextContent()).contains("NOT_CONFIGURED");
    HtmlButton configureButton = row.getCell(4).getFirstByXPath(".//button[@title='Configure']");
    assertThat(configureButton).isNotNull();
    HtmlButton analyzeButton = row.getCell(4).getFirstByXPath(".//button[@title='Analyze']");
    assertThat(analyzeButton).isNotNull();
  }

  class AgentTestHelper {
    HtmlPage login(String username, String password) throws IOException {
      HtmlPage loginPage = webClient.getPage(getAgentPath("login"));

      // Get the form
      HtmlForm loginForm = loginPage.getForms().get(0);
      assertThat(loginForm).isNotNull();

      // Fill in the form
      HtmlTextInput usernameInput = loginForm.getInputByName("username");
      HtmlPasswordInput passwordInput = loginForm.getInputByName("password");
      HtmlSubmitInput submitButton = loginForm.getFirstByXPath("//input[@type='submit']");
      usernameInput.type(username);
      passwordInput.type(password);

      // Submit
      return submitButton.click();
    }

    void withLogin() throws IOException {
      login(AGENT_USERNAME, AGENT_PASSWORD);
    }
  }


  private String getAgentPath(String path) {
    if (path.startsWith("/")) {
      path = path.substring(1);
    }
    return "http://localhost:" + port + "/springdog/" + path;
  }
}

