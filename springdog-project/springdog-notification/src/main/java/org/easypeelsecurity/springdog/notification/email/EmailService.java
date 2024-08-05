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

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import org.easypeelsecurity.springdog.notification.email.content.EmailContent;
import org.easypeelsecurity.springdog.shared.configuration.NotificationGmailProperties;

/**
 * Service for sending emails.
 */
@Service
public class EmailService {
  private final NotificationGmailProperties gmailProperties;
  private final JavaMailSender mailSender;

  /**
   * Constructor for EmailService.
   */
  public EmailService(NotificationGmailProperties gmailProperties,
      @Qualifier("gmailNotificationSender") JavaMailSender mailSender) {
    this.gmailProperties = gmailProperties;
    this.mailSender = mailSender;
  }

  /**
   * Send multiple emails.
   */
  public boolean sendMails(EmailContent... emailMessages) {
    boolean result = true;
    for (EmailContent emailMessage : emailMessages) {
      result &= sendMail(emailMessage);
    }
    return result;
  }

  /**
   * Send email.
   *
   * @param emailMessage Email message.
   * @return True if email was sent, false otherwise.
   */
  public boolean sendMail(EmailContent emailMessage) {
    MimeMessage mimeMessage = mailSender.createMimeMessage();

    if (!gmailProperties.isEnabled()) {
      return false;
    }

    try {
      MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
      mimeMessageHelper.setTo(gmailProperties.getRecipient());
      mimeMessageHelper.setSubject("[springdog] %s".formatted(emailMessage.getSubject()));
      mimeMessageHelper.setText(emailMessage.getMessage(), true);
      mailSender.send(mimeMessage);
    } catch (MessagingException e) {
      return false;
    }
    return true;
  }
}
