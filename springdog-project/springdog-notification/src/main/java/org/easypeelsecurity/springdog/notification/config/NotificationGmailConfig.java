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

package org.easypeelsecurity.springdog.notification.config;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import org.easypeelsecurity.springdog.shared.settings.NotificationGlobalSetting;
import org.easypeelsecurity.springdog.shared.settings.SpringdogSettingManagerImpl;

/**
 * Configuration for Gmail notifications.
 * TODO : 패키지 위치 변경 -> autoconfigure 같은 곳으로
 */
@Configuration
public class NotificationGmailConfig {
  private final SpringdogSettingManagerImpl settingManager;

  /**
   * Constructor for GmailNotificationConfig.
   */
  public NotificationGmailConfig(SpringdogSettingManagerImpl settingManager) {
    this.settingManager = settingManager;
  }

  /**
   * Create a JavaMailSender for Gmail.
   *
   * @return JavaMailSender
   */
  @Bean(name = "gmailNotificationSender")
  public JavaMailSender gmailSender() {
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
    mailSender.setHost("smtp.gmail.com");
    mailSender.setPort(587);

    NotificationGlobalSetting notificationSetting = settingManager.getSettings().getNotificationGlobalSetting();
    mailSender.setUsername(notificationSetting.getUsername());
    mailSender.setPassword(notificationSetting.getPassword());

    Properties props = mailSender.getJavaMailProperties();
    props.put("mail.transport.protocol", "smtp");
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.timeout", "5000");

    return mailSender;
  }
}
