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
import org.openqa.selenium.JavascriptExecutor;
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

  @RetryingTest(10)
  void ratelimit_detail() throws InterruptedException {
    withLogin();

    WebElement analyzeBtn = accessPageUntilXPath("/rate-limit", "(//button[@title='Analyze'])[1]");
    analyzeBtn.click();

    String pageText = getDriver().findElement(By.tagName("body")).getText();
    assertThat(pageText).contains(
        "Endpoint Information\nPath: /api/hello\nEndpoint Method Signature: java.lang.String org.easypeelsecurity.agenttests.ExampleController.hello()\nHTTP Method: GET\nPattern Path: false\nRule Status: NOT_CONFIGURED\nIP Based Rule: false\nPermanent Ban Rule: false\nRequest Limit Count: 0\nTime Limit (seconds): 0\nBan Time (seconds): 0\n");
  }

  @RetryingTest(10)
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

  @RetryingTest(10)
  void errorTraceConfigurationSearchTest() throws InterruptedException {
    withLogin();

    WebElement searchInput = accessPageUntil("/error-tracing/configuration",
        ExpectedConditions.visibilityOfElementLocated(By.id("searchInput")));
    assertThat(searchInput).isNotNull();

    performSearch("java.io.ObjectStrea", "<mark>java.io.ObjectStrea</mark>mException",
        "java.lang.MatchException");
    performSearch("lang.NullPo", "java.<mark>lang.NullPo</mark>interException", "java.lang.MatchException");
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

  @RetryingTest(10)
  void errorTraceConfigurationEnableTest() throws InterruptedException {
    withLogin();

    WebElement accordionButton =
        accessPageUntilCssSelector("/error-tracing/configuration", "h2[id^='heading'] button[type='button']");
    accordionButton.click();

    getWait().until(
        ExpectedConditions.visibilityOfElementLocated(cssSelector("li.list-group-item.exception-item")));
    WebElement exceptionClasses =
        getDriver().findElements(cssSelector("li.list-group-item.exception-item")).get(0);
    WebElement checkbox = exceptionClasses.findElement(xpath(".//input[@type='checkbox']"));
    checkbox.click();
    assertThat(checkbox.isSelected()).isFalse();
  }

  @RetryingTest(10)
  void notificationConfigurationTest() throws InterruptedException {
    withLogin();

    accessPage("/notification");
    WebElement submitButton =
        getWait().until(ExpectedConditions.elementToBeClickable(By.id("saveConfigurationButton")));
    WebElement mailConfiguration =
        getDriver().findElement(By.xpath("//h5[normalize-space()='Gmail Configuration']"));
    assertThat(mailConfiguration).isNotNull();
    WebElement emailTemplate = getDriver().findElement(By.xpath("//h5[normalize-space()='Email Templates']"));
    assertThat(emailTemplate).isNotNull();

    WebElement recipient = getDriver().findElement(By.id("recipient"));
    recipient.clear();
    recipient.sendKeys("test@test.com");
    WebElement username = getDriver().findElement(By.id("username"));
    username.clear();
    username.sendKeys("test@test.com");
    WebElement password = getDriver().findElement(By.id("password"));
    password.clear();
    password.sendKeys("test-password");

    ((JavascriptExecutor) getDriver()).executeScript(
        "arguments[0].scrollIntoView({block: 'center', inline: 'nearest'});", submitButton);
    Thread.sleep(500);

    WebElement slowResponseSubject = getDriver().findElement(By.id("slowResponseSubject"));
    slowResponseSubject.clear();
    slowResponseSubject.sendKeys("Test Slow Response Subject");
    WebElement slowResponseBody = getDriver().findElement(By.id("slowResponseBody"));
    slowResponseBody.clear();
    slowResponseBody.sendKeys("Test Slow Response Body");

    submitButton.click();

    getWait().until(
        ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='alert alert-success']")));
    String successMessage = getDriver().findElement(By.xpath("//div[@class='alert alert-success']")).getText();
    assertThat(successMessage).isEqualTo("Successfully updated");

    // changed value should be saved
    assertThat(getDriver().findElement(By.id("recipient")).getAttribute("value")).isEqualTo("test@test.com");
    assertThat(getDriver().findElement(By.id("username")).getAttribute("value")).isEqualTo("test@test.com");
    assertThat(getDriver().findElement(By.id("password")).getAttribute("value")).isEqualTo("test-password");
    assertThat(getDriver().findElement(By.id("slowResponseSubject")).getAttribute("value"))
        .isEqualTo("Test Slow Response Subject");
    assertThat(getDriver().findElement(By.id("slowResponseBody")).getAttribute("value"))
        .isEqualTo("Test Slow Response Body");
  }

  @RetryingTest(10)
  void notificationSystemWatchConfigurationTest() throws InterruptedException {
    withLogin();

    accessPage("/notification/system-watch");
    WebElement submitButton =
        getWait().until(ExpectedConditions.elementToBeClickable(By.id("saveConfigurationButton")));

    WebElement activateCheckbox = getDriver().findElement(By.id("enableSystemWatch"));
    if (!activateCheckbox.isSelected()) {
      activateCheckbox.click();
    }
    WebElement cpuThreshold = getDriver().findElement(By.id("cpuThreshold"));
    cpuThreshold.clear();
    cpuThreshold.sendKeys("10.5");
    WebElement memoryThreshold = getDriver().findElement(By.id("memoryThreshold"));
    memoryThreshold.clear();
    memoryThreshold.sendKeys("10.5");
    WebElement diskThreshold = getDriver().findElement(By.id("diskThreshold"));
    diskThreshold.clear();
    diskThreshold.sendKeys("10.5");

    submitButton.click();

    getWait().until(
        ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='alert alert-success']")));
    String successMessage = getDriver().findElement(By.xpath("//div[@class='alert alert-success']")).getText();
    assertThat(successMessage).isEqualTo("Successfully updated");
    assertThat(getDriver().findElement(By.id("enableSystemWatch")).isSelected()).isTrue();
    assertThat(getDriver().findElement(By.id("cpuThreshold")).getAttribute("value")).isEqualTo("10.5");
    assertThat(getDriver().findElement(By.id("memoryThreshold")).getAttribute("value")).isEqualTo("10.5");
    assertThat(getDriver().findElement(By.id("diskThreshold")).getAttribute("value")).isEqualTo("10.5");
  }

  @RetryingTest(10)
  void notificationSlowResponseConfigurationTest() throws InterruptedException {
    withLogin();

    accessPage("/notification/slow-response");
    WebElement submitButton =
        getWait().until(ExpectedConditions.elementToBeClickable(By.id("saveConfigurationButton")));

    WebElement activateCheckbox = getDriver().findElement(By.id("enableSlowResponse"));
    if (!activateCheckbox.isSelected()) {
      activateCheckbox.click();
    }
    WebElement thresholdMs = getDriver().findElement(By.id("thresholdMs"));
    thresholdMs.clear();
    thresholdMs.sendKeys("5000");

    submitButton.click();
    getWait().until(
        ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='alert alert-success']")));
    String successMessage = getDriver().findElement(By.xpath("//div[@class='alert alert-success']")).getText();
    assertThat(successMessage).isEqualTo("Successfully updated");
    assertThat(getDriver().findElement(By.id("thresholdMs")).getAttribute("value")).isEqualTo("5000");
  }

  @RetryingTest(10)
  void systemWatchView() throws InterruptedException {
    withLogin();

    WebElement systemUsageChart = accessPageUntilXPath("/system-watch", "//canvas[@id='resource-usage-chart']");
    assertThat(systemUsageChart).isNotNull();
    WebElement networkTrafficChart = getDriver().findElement(By.xpath( "//canvas[@id='network-traffic-chart']"));
    assertThat(networkTrafficChart).isNotNull();
    // TODO: chart visual validation and add memo test
  }

  @RetryingTest(10)
  void errorTraceMonitoring() throws InterruptedException {
    accessEndpoint("/api/throw"); // given
    withLogin();

    WebElement target = accessPageUntilXPath("/error-tracing",
        "//h2[@id='heading-0']//span[@class='text-primary'][contains(text(),'org.easypeelsecurity.agenttests.ExampleController.')]");
    assertThat(target).isNotNull();
  }
}
