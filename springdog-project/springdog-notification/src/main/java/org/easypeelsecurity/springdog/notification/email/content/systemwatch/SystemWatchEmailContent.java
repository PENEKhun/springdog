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

import org.easypeelsecurity.springdog.notification.email.content.ContentParameters;
import org.easypeelsecurity.springdog.notification.email.content.EmailContent;

/**
 * Email content for SystemWatch alerts.
 */
public class SystemWatchEmailContent extends EmailContent {
  private String triggerSystem;
  private double usage;

  @Override
  @SuppressWarnings("checkstyle:LineLength")
  protected String generateBody() {
    StringBuilder sbBody = new StringBuilder();
    sbBody.append("<html>");
    sbBody.append("<head>");
    sbBody.append("<style>");
    sbBody.append("body { font-family: Arial, sans-serif; margin: 20px; }");
    sbBody.append(
        ".header { background-color: #f2f2f2; padding: 10px; border-radius: 5px; text-align: center; }");
    sbBody.append(".content { margin-top: 20px; line-height: 1.6; }");
    sbBody.append(".alert { color: #d9534f; font-weight: bold; }");
    sbBody.append(
        ".footer { margin-top: 30px; font-size: 12px; color: #888888; border-top: 1px solid #ddd; padding-top: 10px; }");
    sbBody.append("</style>");
    sbBody.append("</head>");
    sbBody.append("<body>");
    sbBody.append("<div class='header'>");
    sbBody.append("<h2 style='color: #333;'>System Usage Warning</h2>");
    sbBody.append("</div>");
    sbBody.append("<div class='content'>");
    sbBody.append("<p>Hello,</p>");
    sbBody.append(
        "<p>This email is an alert from <strong>SpringDog SystemWatch</strong> about an increase in system usage.</p>");
    sbBody.append("<p>The current system usage of <strong>");
    sbBody.append(triggerSystem);
    sbBody.append("</strong> has exceeded <span class='alert'>");
    sbBody.append(usage);
    sbBody.append("%</span>. This indicates a risk of system performance degradation and downtime.</p>");
    sbBody.append(
        "<p>Prompt action is required, so please monitor the system usage and take measures such as adding resources as needed.</p>");
    sbBody.append("<p>Thank you.</p>");
    sbBody.append("</div>");
    sbBody.append("<div class='footer'>");
    sbBody.append("<p>If you have any questions, please contact our support team.</p>");
    sbBody.append("<p>Thank you,<br>The SpringDog SystemWatch Team</p>");
    sbBody.append("</div>");
    sbBody.append("</body>");
    sbBody.append("</html>");
    return sbBody.toString();
  }

  @Override
  public String getSubject() {
    return "SystemWatch Alert: " + triggerSystem + " was reached " + usage + "%";
  }

  @Override
  public void setParameters(ContentParameters parameters) {
    if (parameters instanceof SystemWatchParameters cloudWatchParams) {
      this.triggerSystem = cloudWatchParams.triggerTarget();
      this.usage = cloudWatchParams.usage();
    } else {
      throw new ClassCastException("Expected SystemWatchEmailParameters.");
    }
  }
}
