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

package org.easypeelsecurity.springdog.notification.email.content;

import java.time.Year;

/**
 * Email content.
 */
public abstract class EmailContent {
  String header;
  String footer;

  /**
   * Constructor.
   */
  protected EmailContent() {
    StringBuilder sbHeader = new StringBuilder();
    sbHeader.append("<html><body><h1>Notification</h1>");
    this.header = sbHeader.toString();

    StringBuilder sbFooter = new StringBuilder();
    sbFooter.append("<footer>© ");
    sbFooter.append(Year.now().getValue());
    sbFooter.append(" springdog</footer></body></html>");
    this.footer = sbFooter.toString();
  }

  /**
   * Methods to set body information for different types of emails.
   */
  protected abstract String generateBody();

  /**
   * Method to get the message with header, body, and footer.
   */
  public String getMessage() {
    StringBuilder sbMessage = new StringBuilder();
    sbMessage.append(header);
    sbMessage.append(generateBody());
    sbMessage.append(footer);
    return sbMessage.toString();
  }

  /**
   * Method to set subject from the email.
   */
  public abstract String getSubject();

  /**
   * Method to set parameters for the email.
   */
  public abstract void setParameters(ContentParameters parameters);
}
