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

package org.easypeelsecurity.springdog.notification.email.content.systemwatch;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import org.easypeelsecurity.springdog.notification.email.content.ContentParameters;
import org.easypeelsecurity.springdog.notification.email.content.EmailContent;
import org.easypeelsecurity.springdog.notification.email.content.slowresponse.SlowResponseParameters;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SystemWatchEmailContentTest {

  @Test
  @DisplayName("Should generate a complete HTML message for email delivery")
  void generateCompleteHtmlMessage() {
    // given
    EmailContent emailContent = new SystemWatchEmailContent();
    ContentParameters parameters = new SystemWatchParameters("triggerTarget", 0.0);
    emailContent.setParameters(parameters);

    // when
    String emailMessage = emailContent.getMessage();

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
    EmailContent emailContent = new SystemWatchEmailContent();
    ContentParameters parameters = new SystemWatchParameters("PENEKHUN", 10.5);
    emailContent.setParameters(parameters);

    // when
    String emailMessage = emailContent.getMessage();

    // then
    assertThat(emailMessage).contains("PENEKHUN");
    assertThat(emailMessage).contains("10.5");
  }

  @Test
  @DisplayName("Should generate the correct subject line for system watch email content.")
  void generateCorrectSubjectForSystemWatchEmail() {
    // given
    EmailContent emailContent = new SystemWatchEmailContent();
    ContentParameters parameters = new SystemWatchParameters("CPU", 10.5);
    emailContent.setParameters(parameters);

    // when
    String emailSubject = emailContent.getSubject();

    // then
    assertThat(emailSubject).isEqualTo("SystemWatch Alert: CPU was reached 10.5%");
  }

  @Test
  void setParametersError() {
    // given
    SystemWatchEmailContent emailContent = new SystemWatchEmailContent();
    SlowResponseParameters parameters = new SlowResponseParameters("/path", "GET", 123123, 456456);

    // when & then
    assertThrows(ClassCastException.class, () -> emailContent.setParameters(parameters));
  }
}
