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
 * Slow response email notification.
 */
@Service
public class SlowResponseEmailNotification extends AbstractEmailNotification<String, Long> {

  /**
   * Constructor.
   */
  @Autowired
  public SlowResponseEmailNotification(@Qualifier("gmailNotificationSender") JavaMailSender mailSender,
      NotificationGmailProperties gmailProperties) {
    super(mailSender, gmailProperties);
  }

  SlowResponseEmailNotification() {
  }

  @Override
  @SuppressWarnings({"checkstyle:RegexpSingleline", "checkstyle:LineLength"})
  protected String generateBody() {
    return """
        <div style="border-radius: 8px; border: 1px solid #ccc; background-color: #f9f9f9; color: #333; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); max-width: 600px; margin: 0 auto; font-family: Arial, sans-serif;">
            <div style="padding: 16px;">
                <h3 style="margin: 0; font-size: 24px; font-weight: 600; text-align: center;">
                    Slow Response Alert
                </h3>
            </div>
            <div style="padding: 16px; line-height: 1.5;">
                <p>Hello,</p>
                <p>We wanted to inform you that one of your API endpoints
                 has been experiencing slower than your configured threshold.
                 This issue has been detected by springdog monitoring system.</p>
                <p><strong>Affected Endpoint:</strong><br>
                  %s
                </p>
                <p><strong>Recent Response Times:</strong><br>
                  %s ms
                </p>
            </div>
        </div>
        """.formatted(
        this.cause.key(),
        this.cause.value());
  }

  @Override
  public String getSubject() {
    return "Slow API Response Alert : [%s] - %dms".formatted(
        this.cause.key(), this.cause.value()
    );
  }

  @SuppressWarnings({"checkstyle:MissingJavadocMethod", "checkstyle:MissingJavadocType"})
  public static class SlowResponse implements Comparable<SlowResponse> {
    private final String endpointPath;
    private final String endpointMethod;
    private final long normalResponseTime;
    private final long currentResponseTime;

    SlowResponse(String endpointPath, String endpointMethod, long normalResponseTime,
        long currentResponseTime) {
      this.endpointPath = endpointPath;
      this.endpointMethod = endpointMethod;
      this.normalResponseTime = normalResponseTime;
      this.currentResponseTime = currentResponseTime;
    }

    public String getEndpointPath() {
      return endpointPath;
    }

    public String getEndpointMethod() {
      return endpointMethod;
    }

    public long getNormalResponseTime() {
      return normalResponseTime;
    }

    public long getCurrentResponseTime() {
      return currentResponseTime;
    }

    @Override
    public int compareTo(SlowResponse o) {
      return Long.compare(this.currentResponseTime, o.currentResponseTime);
    }

    public static class Builder {
      private String endpointPath;
      private String endpointMethod;
      private long normalResponseTime;
      private long currentResponseTime;

      public Builder endpointPath(String endpointPath) {
        this.endpointPath = endpointPath;
        return this;
      }

      public Builder endpointMethod(String endpointMethod) {
        this.endpointMethod = endpointMethod;
        return this;
      }

      public Builder normalResponseTime(long normalResponseTime) {
        this.normalResponseTime = normalResponseTime;
        return this;
      }

      public Builder currentResponseTime(long currentResponseTime) {
        this.currentResponseTime = currentResponseTime;
        return this;
      }

      public SlowResponse build() {
        return new SlowResponse(endpointPath, endpointMethod, normalResponseTime, currentResponseTime);
      }
    }
  }
}
