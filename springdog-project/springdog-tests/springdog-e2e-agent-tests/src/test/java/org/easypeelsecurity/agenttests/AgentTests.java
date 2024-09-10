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
import static org.openqa.selenium.By.cssSelector;
import static org.openqa.selenium.By.xpath;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.RetryingTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import support.SeleniumTestSupport;

class AgentTests extends SeleniumTestSupport {

  @Test
  @DisplayName("Redirect to the login page when accessing the home PATH")
  void should_redirect_to_loginPage() {
    accessPage("/");
    assertThat(currentUrl()).isEqualTo(getAgentPath("/login"));
  }

  @Test
  @DisplayName("Redirect to home page after successful login")
  void login_success() throws InterruptedException {
    withLogin();
    assertThat(currentUrl()).isEqualTo(getAgentPath(""));
    assertThat(pageSource()).contains("Dashboard", "Total Endpoints");
  }

  @Test
  @DisplayName("Display error message when login fails")
  void login_fail() throws InterruptedException {
    withWrongLogin();

    assertThat(currentUrl()).isEqualTo(getAgentPath("/login?error"));
    assertThat(pageSource()).contains("Login Failed.");
  }

  @Test
  void homePage() throws InterruptedException {
    withLogin();

    accessPage("/");
    assertThat(pageSource()).contains("Dashboard", "Total Endpoints");
  }

  @Test
  @RetryingTest(5)
  void ratelimit_detail() throws InterruptedException {
    withLogin();

    accessPage("/rate-limit");
    WebElement analyzeBtn = accessPageUntilXPath("/rate-limit", "(//button[@title='Analyze'])[1]");
    analyzeBtn.click();

    String pageText = getDriver().findElement(By.tagName("body")).getText();
    assertThat(pageText).contains(
        "Endpoint Information\nPath: /api/hello\nEndpoint Method Signature: java.lang.String org.easypeelsecurity.agenttests.ExampleController.hello()\nHTTP Method: GET\nPattern Path: false\nRule Status: NOT_CONFIGURED\nIP Based Rule: false\nPermanent Ban Rule: false\nRequest Limit Count: 0\nTime Limit (seconds): 0\nBan Time (seconds): 0\n");
  }

  @Test
  @RetryingTest(5)
  void ratelimit_read() throws InterruptedException {
    withLogin();

    WebElement table =
        accessPageUntil("/rate-limit", ExpectedConditions.presenceOfElementLocated(By.id("table")));
    WebElement row = table.findElements(By.tagName("tr")).get(1);

    List<WebElement> cells = row.findElements(By.tagName("td"));
    assertThat(cells.get(0).getText()).isEqualTo("/api/hello");
    assertThat(cells.get(1).getText()).isEqualTo(
        "java.lang.String org.easypeelsecurity.agenttests.ExampleController.hello()");
    assertThat(cells.get(2).getText()).isEqualTo("GET");
    assertThat(cells.get(3).getText()).contains("NOT_CONFIGURED");
    assertThat(cells.get(4).findElement(xpath(".//button[@title='Configure']"))).isNotNull();
    assertThat(cells.get(4).findElement(xpath(".//button[@title='Analyze']"))).isNotNull();
  }

  @Test
  @RetryingTest(5)
  void errorTraceConfigurationSearchTest() throws InterruptedException {
    withLogin();

    WebElement searchInput = accessPageUntil("/error-tracing/configuration",
        ExpectedConditions.visibilityOfElementLocated(By.id("searchInput")));
    assertThat(searchInput).isNotNull();

    performSearch("java.io.ObjectStrea", "<mark>java.io.ObjectStrea</mark>mException",
        "java.lang.MatchException");
    performSearch("MatchException", "java.lang.<mark>MatchException</mark>", "java.io.ObjectStreamException");
    performSearch("nullpointer", "java.lang.<mark>NullPointer</mark>Exception",
        "java.io.ObjectStreamException");
  }

  private void performSearch(String searchTerm, String expectedResult, String notExpectedResult) {
    WebElement searchInput = getDriver().findElement(By.id("searchInput"));
    searchInput.clear();
    searchInput.sendKeys(searchTerm);

    getWait().until(ExpectedConditions.attributeToBe(searchInput, "value", searchTerm));
    List<String> visibleItems = getDriver().findElements(
            cssSelector("li.list-group-item.exception-item:not([style*='display: none']) div label"))
        .stream()
        .map(element -> element.getAttribute("innerHTML"))
        .toList();

    assertThat(visibleItems).isNotEmpty();
    assertThat(visibleItems).contains(expectedResult);
    assertThat(visibleItems).doesNotContain(notExpectedResult);
  }

  @Test
  @RetryingTest(5)
  void errorTraceConfigurationEnableTest() throws InterruptedException {
    withLogin();

    WebElement accordionButton =
        accessPageUntilCssSelector("/error-tracing/configuration", "h2[id^='heading'] button[type='button']");
    accordionButton.click();

    getWait().until(
        ExpectedConditions.visibilityOfElementLocated(cssSelector("li.list-group-item.exception-item")));
    WebElement exceptionClasses =
        getDriver().findElements(cssSelector("li.list-group-item.exception-item")).getFirst();
    WebElement checkbox = exceptionClasses.findElement(xpath(".//input[@type='checkbox']"));
    checkbox.click();
    assertThat(checkbox.isSelected()).isFalse();
  }
}
