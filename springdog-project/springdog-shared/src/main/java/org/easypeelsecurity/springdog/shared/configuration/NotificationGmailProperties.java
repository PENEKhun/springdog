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

import jakarta.annotation.PostConstruct;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Springdog email notification properties.
 */
@ConfigurationProperties(prefix = "springdog.notification.gmail")
@SuppressWarnings("checkstyle:MissingJavadocMethod")
public class NotificationGmailProperties {

  private String recipient = "";
  private String username = "username";
  private String password = "password";
  private boolean enabled;

  @PostConstruct
  public void init() {
    if (enabled) {
      String commonMessage = "The gmail notification is enabled, but no details are entered.\n";
      if (recipient.isEmpty()) {
        throw new IllegalArgumentException(commonMessage + "Recipient is required");
      }
      if (!isEmailValid(recipient)) {
        throw new IllegalArgumentException(commonMessage + "Recipient is not a valid email address");
      }
      if (username.isEmpty()) {
        throw new IllegalArgumentException(commonMessage + "Username is required");
      }
      if (password.isEmpty()) {
        throw new IllegalArgumentException(commonMessage + "Password is required");
      }
    }
  }

  private boolean isEmailValid(String email) {
    String regex = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@" +
        "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
    return email.matches(regex);
  }

  public String getRecipient() {
    return recipient;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public void setRecipient(String recipient) {
    this.recipient = recipient;
  }
}
