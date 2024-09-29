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

package support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.openqa.selenium.By.name;
import static org.openqa.selenium.By.xpath;

import java.time.Duration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.By;
import org.openqa.selenium.By.ByCssSelector;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.WebDriverManager;

/**
 * SeleniumTestSupport.
 * For functionality not provided here, access it directly via getWait and getDriver.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class SeleniumTestSupport {
  final String AGENT_USERNAME = "username";
  final String AGENT_PASSWORD = "password";

  @LocalServerPort
  private int port;

  private WebDriver driver;
  private WebDriverWait wait;

  @BeforeEach
  public void setUp() {
    ChromeOptions options = new ChromeOptions();
    options.addArguments("--headless");
    options.addArguments("--window-size=1920,1080"); // HACK for testing on GitHub actions
    options.addArguments("--disable-gpu");
    options.addArguments("--no-sandbox");
    options.setExperimentalOption("excludeSwitches", new String[] {"enable-automation"});
    this.driver = WebDriverManager.chromedriver().capabilities(options).create();
    this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
  }

  @AfterEach
  public void tearDown() {
    this.driver.quit();
  }

  public void accessPage(String path) {
    this.driver.get(getAgentPath(path));
  }

  public WebElement accessPageUntil(String path, ExpectedCondition<WebElement> condition) {
    this.driver.get(getAgentPath(path));
    return this.wait.until(condition);
  }

  public WebElement accessPageUntilCssSelector(String path, String cssSelector) {
    this.driver.get(getAgentPath(path));
    return this.wait.until(ExpectedConditions.presenceOfElementLocated(ByCssSelector.cssSelector(cssSelector)));
  }

  public WebElement accessPageUntilXPath(String path, String xpathElement) {
    this.driver.get(getAgentPath(path));
    return this.wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpathElement)));
  }

  public WebElement accessPageUntilId(String path, String id) {
    this.driver.get(getAgentPath(path));
    return this.wait.until(ExpectedConditions.presenceOfElementLocated(By.id(id)));
  }

  public void accessNotSpringdogPage(String path) {
    assertThat(path).startsWith("/");
    this.driver.get("http://localhost:" + port + path);
  }

  public void withLogin() throws InterruptedException {
    this.login(AGENT_USERNAME, AGENT_PASSWORD);
  }

  public void withWrongLogin() throws InterruptedException {
    this.login("wrong_username", "wrong_password");
  }

  public String pageSource() {
    return this.driver.getPageSource();
  }

  public String currentUrl() {
    return this.driver.getCurrentUrl();
  }

  private void login(String username, String password) throws InterruptedException {
    this.driver.get(getAgentPath("login"));

    WebElement loginForm = this.wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("form")));
    WebElement usernameInput = loginForm.findElement(name("username"));
    WebElement passwordInput = loginForm.findElement(name("password"));
    WebElement submitButton = loginForm.findElement(xpath("//button[@type='submit']"));

    usernameInput.sendKeys(username);
    passwordInput.sendKeys(password);
    submitButton.click();

    if (username.equals(AGENT_USERNAME) && password.equals(AGENT_PASSWORD)) {
      this.wait.until(
          ExpectedConditions.visibilityOfElementLocated(xpath("//h1[normalize-space()='Dashboard']")));
    }
  }

  public String getAgentPath(String path) {
    if (path.startsWith("/")) {
      path = path.substring(1);
    }
    return "http://localhost:" + port + "/springdog/" + path;
  }

  public WebDriver getDriver() {
    return this.driver;
  }

  public WebDriverWait getWait() {
    return this.wait;
  }
}
