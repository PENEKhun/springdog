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

package org.easypeelsecurity.springdog.notification.email;

import static org.easypeelsecurity.springdog.shared.settings.SlowResponsePlaceholder.ENDPOINT_PATH;
import static org.easypeelsecurity.springdog.shared.settings.SlowResponsePlaceholder.RESPONSE_MS;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import org.easypeelsecurity.springdog.shared.settings.Placeholder;
import org.easypeelsecurity.springdog.shared.settings.SpringdogSettingManagerImpl;
import org.easypeelsecurity.springdog.shared.settings.Template;

/**
 * Slow response email notification.
 */
@Service
public class SlowResponseEmailNotification extends AbstractEmailNotification<String, Long> {

  private final SpringdogSettingManagerImpl settingManager;

  /**
   * Constructor.
   */
  @Autowired
  public SlowResponseEmailNotification(@Qualifier("gmailNotificationSender") JavaMailSender mailSender,
      SpringdogSettingManagerImpl settingManager) {
    super(mailSender, settingManager);
    this.settingManager = settingManager;
  }

  @Override
  protected String generateBody() {
    Map<Placeholder, String> replacement = new HashMap<>();
    replacement.put(ENDPOINT_PATH, this.cause.key());
    replacement.put(RESPONSE_MS, this.cause.value().toString());

    return settingManager.getSettings()
        .getNotificationGlobalSetting()
        .generateMailTemplate(Template.SLOW_RESPONSE_BODY, replacement);
  }

  @Override
  public String getSubject() {
    Map<Placeholder, String> replacement = new HashMap<>();
    replacement.put(ENDPOINT_PATH, this.cause.key());
    replacement.put(RESPONSE_MS, this.cause.value().toString());

    return settingManager.getSettings()
        .getNotificationGlobalSetting()
        .generateMailTemplate(Template.SLOW_RESPONSE_SUBJECT, replacement);
  }
}
