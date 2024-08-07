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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSender;

import org.easypeelsecurity.springdog.shared.configuration.NotificationGmailProperties;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SystemWatchEmailNotificationTest {

  @Mock
  private NotificationGmailProperties gmailProperties;

  @Mock
  private JavaMailSender mailSender;

  @Mock
  private MimeMessage mimeMessage;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    when(gmailProperties.isEnabled()).thenReturn(true);
    when(gmailProperties.getRecipient()).thenReturn("recipient@example.com");
    when(gmailProperties.getUsername()).thenReturn("username");
    when(gmailProperties.getPassword()).thenReturn("password");
    when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
  }

  @Test
  @DisplayName("Should generate a complete HTML message for email delivery")
  void generateCompleteHtmlMessage() {
    // given
    SystemWatchEmailNotification emailNotification =
        new SystemWatchEmailNotification(mailSender, gmailProperties);
    emailNotification.setCause("triggerTarget", 0.0);

    // when
    String emailMessage = emailNotification.getContent();

    // then
    try {
      Document document = Jsoup.parse(emailMessage, "", Parser.htmlParser());
      assertNotNull(document);
      assertFalse(document.getAllElements().isEmpty());
    } catch (Exception e) {
      fail("HTML content is not valid: " + e.getMessage());
    }
  }

  @Test
  @DisplayName("Should include parameters in the HTML message for email content")
  void includeParametersInHtmlMessage() {
    // given
    SystemWatchEmailNotification emailNotification =
        new SystemWatchEmailNotification(mailSender, gmailProperties);
    emailNotification.setCause("PENEKHUN", 10.5);

    // when
    String emailMessage = emailNotification.getContent();

    // then
    assertThat(emailMessage).contains("PENEKHUN", "10.5%");
  }

  @Test
  @DisplayName("Should generate the correct subject line for system watch alert email.")
  void generateCorrectSubjectForSystemWatchAlertEmail() {
    // given
    SystemWatchEmailNotification emailNotification =
        new SystemWatchEmailNotification(mailSender, gmailProperties);
    emailNotification.setCause("CPU", 10.5);

    // when
    String emailSubject = emailNotification.getSubject();

    // then
    assertThat(emailSubject).isEqualTo("SystemWatch Alert: CPU is overused (10.5%)");
  }

  @Test
  @DisplayName("Should generate the correct subject line for system watch recovery email.")
  void generateCorrectSubjectForSystemWatchRecoveryEmail() {
    // given
    SystemWatchEmailNotification emailNotification =
        new SystemWatchEmailNotification(mailSender, gmailProperties);
    emailNotification.setRecovery("CPU", 10.5);

    // when
    String emailSubject = emailNotification.getSubject();

    // then
    assertThat(emailSubject).isEqualTo("SystemWatch Alert: CPU usage is recovered (10.5%)");
  }

  @Test
  void testSendMailSuccess() {
    // Arrange
    SystemWatchEmailNotification emailNotification =
        new SystemWatchEmailNotification(mailSender, gmailProperties);
    emailNotification.setCause("CPU", 10.5);

    // Act
    boolean result = emailNotification.send();

    // Assert
    assertTrue(result);
    verify(mailSender, times(1)).send(mimeMessage);
  }

  @Test
  void testSendMailDisabled() {
    // Arrange
    when(gmailProperties.isEnabled()).thenReturn(false);
    SystemWatchEmailNotification emailNotification =
        new SystemWatchEmailNotification(mailSender, gmailProperties);
    emailNotification.setCause("CPU", 10.5);

    // Act
    boolean result = emailNotification.send();

    // Assert
    assertFalse(result);
    verify(mailSender, never()).send(any(MimeMessage.class));
  }
}
