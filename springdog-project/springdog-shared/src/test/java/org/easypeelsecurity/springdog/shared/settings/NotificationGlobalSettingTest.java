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

package org.easypeelsecurity.springdog.shared.settings;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class NotificationGlobalSettingTest {

  @Nested
  @DisplayName("When Notification is enabled")
  class EnabledTests {

    @ParameterizedTest
    @CsvSource({
        "invalid-email",
        "@gmail.com",
        "user@domain",
        "user@domain.",
        "user@domain.c",
        "user@.com",
        "username.@domain.com",
        "user-name@domain.com.",
        "username@.com"
    })
    @DisplayName("Throws exception when recipient email is invalid")
    void shouldThrowExceptionForInvalidRecipientEmail(String recipient) {
      // Given
      NotificationGlobalSetting setting = new NotificationGlobalSetting(
          true, // enabled
          recipient,
          "validUsername",
          "validPassword"
      );

      // When & Then
      assertThatThrownBy(setting::validate)
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Recipient must be a valid email address");
    }

    @ParameterizedTest
    @CsvSource({
        "user-name@domain.com",
        "username@domain.co.in",
        "user_name@domain.com",
        "user.name@domain.com",
    })
    @DisplayName("Does not throw exception when recipient email is valid")
    void shouldNotThrowForValidRecipientEmail(String recipient) {
      // Given
      NotificationGlobalSetting setting = new NotificationGlobalSetting(
          true, // enabled
          recipient,
          "validUsername",
          "validPassword"
      );

      // When & Then
      assertThatCode(setting::validate).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Throws exception when username is null or empty")
    void shouldThrowExceptionWhenUsernameInvalid(String username) {
      // Given
      NotificationGlobalSetting setting = new NotificationGlobalSetting(
          true, // enabled
          "valid@example.com",
          username,
          "validPassword"
      );

      // When & Then
      assertThatThrownBy(setting::validate)
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Username must not be null or empty");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Throws exception when password is null or empty")
    void shouldThrowExceptionWhenPasswordInvalid(String password) {
      // Given
      NotificationGlobalSetting setting = new NotificationGlobalSetting(
          true, // enabled
          "valid@example.com",
          "validUsername",
          password
      );

      // When & Then
      assertThatThrownBy(setting::validate)
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Password must not be null or empty");
    }

    @Test
    @DisplayName("Does not throw when all details are provided")
    void shouldNotThrowWithAllDetails() {
      // Given
      NotificationGlobalSetting setting = new NotificationGlobalSetting(
          true, // enabled
          "valid@example.com",
          "validUsername",
          "validPassword"
      );

      // When & Then
      assertThatCode(setting::validate).doesNotThrowAnyException();
    }
  }

  @Nested
  @DisplayName("When Notification is disabled")
  class DisabledTests {

    @Test
    @DisplayName("Does not throw exception when disabled")
    void shouldNotThrowWhenDisabled() {
      // Given
      NotificationGlobalSetting setting = new NotificationGlobalSetting(
          false,
          null,
          null,
          null
      );

      // When & Then
      assertThatCode(setting::validate).doesNotThrowAnyException();
    }
  }

  @Test
  @DisplayName("Generates email template correctly for SYSTEM_WATCH_SUBJECT")
  void shouldGenerateTemplateForSystemWatchSubject() {
    // Given
    NotificationGlobalSetting setting = new NotificationGlobalSetting(
        true, // enabled
        "valid@example.com",
        "validUsername",
        "validPassword"
    );

    Map<Placeholder, String> replacements = new HashMap<>();
    replacements.put(SystemWatchPlaceholder.TARGET_DEVICE, "CPU");
    replacements.put(SystemWatchPlaceholder.TARGET_STATUS, "Overloaded");
    replacements.put(SystemWatchPlaceholder.USAGE_PERCENT, "85");

    // When
    String result = setting.generateMailTemplate(Template.SYSTEM_WATCH_SUBJECT, replacements);

    // Then
    assertThat(result).isEqualTo("SystemWatch Alert: CPU is Overloaded (85%)");
  }

  @Test
  @DisplayName("Generates email template correctly for SYSTEM_WATCH_BODY")
  void shouldGenerateTemplateForSystemWatchBody() {
    // Given
    NotificationGlobalSetting setting = new NotificationGlobalSetting(
        true, // enabled
        "valid@example.com",
        "validUsername",
        "validPassword"
    );

    Map<Placeholder, String> replacements = new HashMap<>();
    replacements.put(SystemWatchPlaceholder.TARGET_DEVICE, "CPU");
    replacements.put(SystemWatchPlaceholder.TARGET_STATUS, "Overloaded");
    replacements.put(SystemWatchPlaceholder.USAGE_PERCENT, "85");

    // When
    String result = setting.generateMailTemplate(Template.SYSTEM_WATCH_BODY, replacements);

    // Then
    assertThat(result).contains("CPU")
        .contains("Overloaded")
        .contains("85%");
  }

  @Test
  @DisplayName("Throws exception when template does not exist")
  void shouldThrowExceptionWhenTemplateDoesNotExist() {
    // Given
    NotificationGlobalSetting setting = new NotificationGlobalSetting(
        true, // enabled
        "valid@example.com",
        "validUsername",
        "validPassword"
    );

    // When & Then
    assertThatThrownBy(() -> setting.generateMailTemplate(null, Map.of()))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Template with key");
  }
}
