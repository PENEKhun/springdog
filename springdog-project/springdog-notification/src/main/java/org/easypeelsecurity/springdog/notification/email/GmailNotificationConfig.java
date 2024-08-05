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

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import org.easypeelsecurity.springdog.shared.configuration.NotificationGmailProperties;

/**
 * Configuration for Gmail notifications.
 */
@Configuration
public class GmailNotificationConfig {
  private final NotificationGmailProperties gmailProperties;

  /**
   * Constructor for GmailNotificationConfig.
   */
  public GmailNotificationConfig(NotificationGmailProperties gmailProperties) {
    this.gmailProperties = gmailProperties;
  }

  /**
   * Create a JavaMailSender for Gmail.
   * @return JavaMailSender
   */
  @Bean(name = "gmailNotificationSender")
  public JavaMailSender gmailSender() {
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
    mailSender.setHost("smtp.gmail.com");
    mailSender.setPort(587);

    mailSender.setUsername(gmailProperties.getUsername());
    mailSender.setPassword(gmailProperties.getPassword());

    Properties props = mailSender.getJavaMailProperties();
    props.put("mail.transport.protocol", "smtp");
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.timeout", "5000");

    return mailSender;
  }
}
