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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import org.easypeelsecurity.springdog.shared.configuration.NotificationGmailProperties;

/**
 * Email content for SystemWatch alerts.
 */
@Service
public class SystemWatchEmailNotification extends AbstractEmailNotification<String, Double> {

  /**
   * Constructor.
   */
  @Autowired
  public SystemWatchEmailNotification(@Qualifier("gmailNotificationSender") JavaMailSender mailSender,
      NotificationGmailProperties gmailProperties) {
    super(mailSender, gmailProperties);
  }

  SystemWatchEmailNotification() {
  }

  @Override
  @SuppressWarnings("checkstyle:LineLength")
  protected String generateBody() {
    StringBuilder sbBody = new StringBuilder();
    sbBody.append("<div class='header'>");
    sbBody.append("<h2 style='color: #333;'>System Usage Warning</h2>");
    sbBody.append("</div>");
    sbBody.append("<div class='content'>");
    sbBody.append("<p>Hello,</p>");
    sbBody.append(
        "<p>This email is an alert from <strong>springdog</strong> about an increase(or recovery) in system usage.</p>");

    if (this.cause != null) {
      sbBody.append("<p>The current system usage of <strong>");
      sbBody.append(cause.key());
      sbBody.append("</strong> has exceeded <span class='alert'>");
      sbBody.append(cause.value()).append("%</span>. ");
      sbBody.append("This indicates a risk of system performance degradation and downtime.</p>");
      sbBody.append(
          "<p>If you believe this value is abnormal, it is advisable to promptly conduct a system inspection.</p>");
      sbBody.append("</div>");
    } else if (this.recovered != null) {
      sbBody.append("<p>The system usage of <strong>");
      sbBody.append(recovered.key());
      sbBody.append("</strong> has returned to normal levels: <span class='alert'>");
      sbBody.append(recovered.value()).append("%</span>. ");
      sbBody.append("We will notify you when usage reaches the threshold again.</p>");
    }
    return sbBody.toString();
  }

  @Override
  public String getSubject() {
    StringBuilder subject = new StringBuilder("SystemWatch Alert: ");

    if (cause != null) {
      subject.append(cause.key()).append(" is overused").append(" (").append(cause.value()).append("%)");
    } else if (recovered != null) {
      subject.append(recovered.key()).append(" usage is recovered").append(" (").append(recovered.value())
          .append("%)");
    } else {
      throw new IllegalStateException("Cause or recovered must be set");
    }
    return subject.toString();
  }
}
