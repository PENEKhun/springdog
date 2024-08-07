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

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import org.easypeelsecurity.springdog.notification.Notification;
import org.easypeelsecurity.springdog.shared.configuration.NotificationGmailProperties;

/**
 * Email content.
 */
@SuppressWarnings("checkstyle:VisibilityModifier")
abstract class AbstractEmailNotification<K, V> implements Notification {
  private JavaMailSender mailSender;
  private NotificationGmailProperties gmailProperties;
  protected Cause<K, V> cause;
  protected Cause<K, V> recovered;

  /**
   * Constructor.
   */
  AbstractEmailNotification(JavaMailSender mailSender, NotificationGmailProperties gmailProperties) {
    this.mailSender = mailSender;
    this.gmailProperties = gmailProperties;
  }

  AbstractEmailNotification() {
  }

  @Override
  public final boolean send() {
    MimeMessage mimeMessage = mailSender.createMimeMessage();

    if (!gmailProperties.isEnabled()) {
      return false;
    }

    try {
      MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
      mimeMessageHelper.setTo(gmailProperties.getRecipient());
      mimeMessageHelper.setSubject("[springdog] %s".formatted(getSubject()));
      mimeMessageHelper.setText(getContent(), true);
      mailSender.send(mimeMessage);
    } catch (MessagingException e) {
      return false;
    }
    return true;
  }

  @Override
  public String getContent() {
    return """
        <html>
          <head>
            <style>
              body { font-family: Arial, sans-serif; margin: 20px; }
              .header { background-color: #f2f2f2; padding: 10px;
               border-radius: 5px; text-align: center; }
              .content { margin-top: 20px; line-height: 1.6;}
              .alert { color: #d9534f; font-weight: bold; }
              .footer { margin-top: 30px; font-size: 12px; color: #888888;
               border-top: 1px solid #ddd; padding-top: 10px; }
            </style>
          </head>
          <body>
            <h1>Notification</h1>
              %s
            <div class='footer'>
              <p>If you have any questions, please contact our github issue.</p>
              <p>Thank you,<br>The springdog team</p>
            </div>
          </body>
        </html>
        """.formatted(generateBody());
  }

  public void setCause(K key, V value) {
    this.cause = new Cause<>(key, value);
    this.recovered = null;
  }

  public void setRecovery(K key, V value) {
    this.recovered = new Cause<>(key, value);
    this.cause = null;
  }

  /**
   * Methods to set body information for different types of emails.
   */
  protected abstract String generateBody();

  protected record Cause<K, V>(K key, V value) {
  }
}
