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

package org.easypeelsecurity.springdog.shared.configuration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;

import jakarta.annotation.PostConstruct;

import org.springframework.boot.context.properties.ConfigurationProperties;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class NotificationGmailPropertiesTest {
  @Nested
  @DisplayName("When Gmail notification properties are enabled")
  class Enabled {

    @Test
    @DisplayName("Throws exception when enabled without recipient")
    void shouldThrowExceptionWithoutRecipient() {
      // given
      NotificationGmailProperties properties = new NotificationGmailProperties();
      properties.setEnabled(true);

      // when & then
      assertThrows(IllegalArgumentException.class, properties::init,
          "The gmail notification is enabled, but no details are entered.\nRecipient is required");
    }

    @ParameterizedTest
    @CsvSource({
        "penekhun!gmail.com",
        "@gmail.com",
        "penekhun@gmail",
        "penekhun@gmail.",
        "penekhun@gmail.c",
        "a@a.qe",
        "user@192.168.0.1",
        "username.@domain.com",
        "user-name@domain.com.",
        "username@.com"
    })
    @DisplayName("Throws exception when enabled with invalid recipient email address")
    void shouldThrowExceptionForInvalidRecipientEmail(String recipient) {
      // given
      NotificationGmailProperties properties = new NotificationGmailProperties();
      properties.setEnabled(true);
      properties.setRecipient(recipient);

      // when & then
      assertThrows(IllegalArgumentException.class, properties::init,
          "The gmail notification is enabled, but no details are entered.\nRecipient is not a valid email address");
    }

    @ParameterizedTest
    @CsvSource({
        "user-name@domain.com",
        "username@domain.co.in",
        "user_name@domain.com",
        "user.name@domain.com"
    })
    @DisplayName("Does not throw exception when enabled with valid recipient email address")
    void shouldNotThrowForValidRecipientEmail(String recipient) {
      // given
      NotificationGmailProperties properties = new NotificationGmailProperties();
      properties.setEnabled(true);
      properties.setRecipient(recipient);

      // when & then
      assertDoesNotThrow(properties::init);
    }

    @Test
    @DisplayName("Throws exception when enabled without username")
    void shouldThrowExceptionWithoutUsername() {
      // given
      NotificationGmailProperties properties = new NotificationGmailProperties();
      properties.setEnabled(true);
      properties.setRecipient("test@example.com");
      properties.setUsername("");

      // when & then
      assertThrows(IllegalArgumentException.class, properties::init,
          "The gmail notification is enabled, but no details are entered.\nUsername is required");
    }

    @Test
    @DisplayName("Throws exception when enabled without password")
    void shouldThrowExceptionWithoutPassword() {
      // given
      NotificationGmailProperties properties = new NotificationGmailProperties();
      properties.setEnabled(true);
      properties.setRecipient("test@example.com");
      properties.setUsername("username");
      properties.setPassword("");

      // when & then
      assertThrows(IllegalArgumentException.class, properties::init,
          "The gmail notification is enabled, but no details are entered.\nPassword is required");
    }

    @Test
    @DisplayName("Does not throw when all details are provided")
    void shouldNotThrowWithAllDetails() {
      // given
      NotificationGmailProperties properties = new NotificationGmailProperties();
      properties.setEnabled(true);
      properties.setRecipient("test@example.com");
      properties.setUsername("username");
      properties.setPassword("password");

      // when & then
      assertDoesNotThrow(properties::init);
    }
  }

  @Nested
  @DisplayName("When Gmail notification properties are disabled")
  class Disabled {

    @Test
    @DisplayName("Does not throw exception when disabled")
    void shouldNotThrowWhenDisabled() {
      // given
      NotificationGmailProperties properties = new NotificationGmailProperties();
      properties.setEnabled(false);

      // when & then
      assertDoesNotThrow(properties::init);
    }
  }

  @Test
  @DisplayName("Check if init method has @PostConstruct annotation")
  void shouldHavePostConstructAnnotation() throws NoSuchMethodException {
    // given
    Method initMethod = NotificationGmailProperties.class.getDeclaredMethod("init");

    // when
    boolean isPostConstructPresent = initMethod.isAnnotationPresent(PostConstruct.class);

    // then
    assertTrue(isPostConstructPresent, "The init method should be annotated with @PostConstruct");
  }

  @Test
  @DisplayName("Check if class has @ConfigurationProperties annotation")
  void shouldHaveConfigurationPropertiesAnnotation() {
    // when
    boolean isConfigurationPropertiesPresent = NotificationGmailProperties.class.isAnnotationPresent(
        ConfigurationProperties.class);
    String prefix = NotificationGmailProperties.class.getAnnotation(ConfigurationProperties.class).prefix();

    // then
    assertThat(isConfigurationPropertiesPresent).isTrue();
    assertThat(prefix).isEqualTo("springdog.notification.gmail");
  }

  @Nested
  @DisplayName("Getter and Setter Tests")
  class GetterAndSetterTests {

    @Test
    @DisplayName("Test recipient getter and setter")
    void testRecipientGetterAndSetter() {
      // given
      NotificationGmailProperties properties = new NotificationGmailProperties();
      String recipient = "recipient@example.com";

      // when
      properties.setRecipient(recipient);

      // then
      assertEquals("recipient@example.com", properties.getRecipient(), "The recipient should be set correctly");
    }

    @Test
    @DisplayName("Test username getter and setter")
    void testUsernameGetterAndSetter() {
      // given
      NotificationGmailProperties properties = new NotificationGmailProperties();
      String username = "testuser";

      // when
      properties.setUsername(username);

      // then
      assertEquals("testuser", properties.getUsername(), "The username should be set correctly");
    }

    @Test
    @DisplayName("Test password getter and setter")
    void testPasswordGetterAndSetter() {
      // given
      NotificationGmailProperties properties = new NotificationGmailProperties();
      String password = "testpass";

      // when
      properties.setPassword(password);

      // then
      assertEquals("testpass", properties.getPassword(), "The password should be set correctly");
    }

    @Test
    @DisplayName("Test enabled getter and setter")
    void testEnabledGetterAndSetter() {
      // given
      NotificationGmailProperties properties = new NotificationGmailProperties();
      boolean enabled = true;

      // when
      properties.setEnabled(enabled);

      // then
      assertEquals(enabled, properties.isEnabled(), "The enabled status should be set correctly");
    }
  }
}
