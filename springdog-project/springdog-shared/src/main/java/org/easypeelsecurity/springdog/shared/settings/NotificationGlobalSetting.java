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

import java.util.EnumMap;
import java.util.Map;

import org.springframework.util.StringUtils;

import lombok.Getter;
import lombok.Setter;

/**
 * Notification global setting.
 */
@Getter
@Setter
public class NotificationGlobalSetting {
  private static final String EMAIL_REGEX =
      "^(?=.{1,64}@)[A-Za-z0-9_\\-]+(\\.[A-Za-z0-9_\\-]+)*@" +
      "[^-][A-Za-z0-9\\-]+(\\.[A-Za-z0-9\\-]+)*(\\.[A-Za-z]{2,})$";

  private boolean enabled;
  private String recipient;
  private String username;
  private String password;

  private Map<Template, String> templates = new EnumMap<>(Template.class);

  /**
   * Constructor.
   */
  protected NotificationGlobalSetting() {
    initializeDefaultTemplates();
  }

  /**
   * Constructor.
   */
  protected NotificationGlobalSetting(boolean enabled, String recipient, String username, String password) {
    this.enabled = enabled;
    this.recipient = recipient;
    this.username = username;
    this.password = password;
    initializeDefaultTemplates();
  }

  /**
   * Check if mail can be sent.
   * @return true if mail can be sent
   */
  public boolean canSendNotification() {
    return (this.enabled && StringUtils.hasText(this.recipient) && StringUtils.hasText(this.username) &&
            StringUtils.hasText(this.password));
  }

  /**
   * Get template.
   * @param templateType template type
   * @return Notification template
   */
  public String getTemplate(Template templateType) {
    return templates.get(templateType);
  }

  /**
   * Generate mail template.
   */
  public String generateMailTemplate(Template templateType, Map<Placeholder, String> replacements) {
    String template = templates.get(templateType);
    if (template == null) {
      throw new IllegalArgumentException("Template with key '" + templateType + "' does not exist.");
    }

    String result = template;
    for (Map.Entry<Placeholder, String> entry : replacements.entrySet()) {
      String placeholder = entry.getKey().getPlaceholderName();
      result = result.replace(placeholder, entry.getValue());
    }

    return result;
  }

  /**
   * Initialize default templates.
   */
  private void initializeDefaultTemplates() {
    for (Template template : Template.values()) {
      templates.put(template, template.getDefaultTemplate());
    }
  }

  /**
   * Validate properties.
   */
  protected void validate() {
    if (enabled) {
      if (recipient == null || !recipient.matches(EMAIL_REGEX)) {
        throw new IllegalArgumentException("Recipient must be a valid email address. " + EMAIL_REGEX);
      }
      if (username == null || username.isEmpty()) {
        throw new IllegalArgumentException("Username must not be null or empty");
      }
      if (password == null || password.isEmpty()) {
        throw new IllegalArgumentException("Password must not be null or empty");
      }

      templates.values()
          .forEach(template -> {
            if (template == null || template.isEmpty()) {
              throw new IllegalArgumentException("Templates must not be null or empty");
            }
          });
    }
  }
}
