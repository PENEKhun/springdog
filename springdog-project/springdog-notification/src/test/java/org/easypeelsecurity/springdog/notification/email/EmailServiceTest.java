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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSender;

import org.easypeelsecurity.springdog.notification.email.content.EmailContent;
import org.easypeelsecurity.springdog.shared.configuration.NotificationGmailProperties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class EmailServiceTest {

  @Mock
  private NotificationGmailProperties gmailProperties;

  @Mock
  private JavaMailSender mailSender;

  @InjectMocks
  private EmailService emailService;

  @Mock
  private MimeMessage mimeMessage;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    emailService = new EmailService(gmailProperties, mailSender);
    when(gmailProperties.isEnabled()).thenReturn(true);
    when(gmailProperties.getRecipient()).thenReturn("recipient@example.com");
    when(gmailProperties.getUsername()).thenReturn("username");
    when(gmailProperties.getPassword()).thenReturn("password");
    when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
  }

  @Test
  void testSendMailSuccess() {
    // Arrange
    EmailContent emailContent = mock(EmailContent.class);
    when(emailContent.getSubject()).thenReturn("Test Subject");
    when(emailContent.getMessage()).thenReturn("<p>Test Message</p>");

    // Act
    boolean result = emailService.sendMail(emailContent);

    // Assert
    assertTrue(result);
    verify(mailSender, times(1)).send(mimeMessage);

    ArgumentCaptor<MimeMessage> mimeMessageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
    verify(mailSender).send(mimeMessageCaptor.capture());

    MimeMessage sentMessage = mimeMessageCaptor.getValue();
    assertNotNull(sentMessage);
  }

  @Test
  void testSendMailDisabled() {
    // Arrange
    when(gmailProperties.isEnabled()).thenReturn(false);
    EmailContent emailContent = mock(EmailContent.class);

    // Act
    boolean result = emailService.sendMail(emailContent);

    // Assert
    assertFalse(result);
    verify(mailSender, never()).send(any(MimeMessage.class));
  }

  @Test
  void testSendMailsMultiple() {
    // Arrange
    EmailContent emailContent1 = mock(EmailContent.class);
    when(emailContent1.getSubject()).thenReturn("Subject 1");
    when(emailContent1.getMessage()).thenReturn("<p>Message 1</p>");

    EmailContent emailContent2 = mock(EmailContent.class);
    when(emailContent2.getSubject()).thenReturn("Subject 2");
    when(emailContent2.getMessage()).thenReturn("<p>Message 2</p>");

    // Act
    boolean result = emailService.sendMails(emailContent1, emailContent2);

    // Assert
    assertTrue(result);
    verify(mailSender, times(2)).send(any(MimeMessage.class));
  }
}

