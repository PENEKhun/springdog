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

package org.easypeelsecurity.springdog.shared.settings;

import lombok.Getter;

/**
 * Enum representing different types of templates.
 */
@Getter
@SuppressWarnings("checkstyle:LineLength")
public enum Template {

  SYSTEM_WATCH_SUBJECT("SystemWatch Alert: {{TARGET_DEVICE}} is {{TARGET_STATUS}} ({{USAGE_PERCENT}}%)"),
  SYSTEM_WATCH_BODY("""
        <div class='header'>
            <h2 style='color: #333;'>System Usage Warning</h2>
        </div>
        <div class='content'>
            <p>Hello,</p>
            <p>This email is an alert from <strong>springdog</strong> about an increase(or recovery) in system usage.</p>
            <p>The current system usage of <strong>{{TARGET_DEVICE}}</strong> has {{TARGET_STATUS}} <span class='alert'>{{USAGE_PERCENT}}%</span>.</p>
            <p>If you believe this value is abnormal, it is advisable to promptly conduct a system inspection.</p>
        </div>
      """),
  SLOW_RESPONSE_SUBJECT("Slow Response Alert : [{{ENDPOINT_PATH}}] - {{RESPONSE_MS}}ms"),
  SLOW_RESPONSE_BODY("""
        <div style="border-radius: 8px; border: 1px solid #ccc; background-color: #f9f9f9; color: #333;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); max-width: 600px; margin: 0 auto;
        font-family: Arial, sans-serif;">
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
                  {{ENDPOINT_PATH}}
                </p>
                <p><strong>Recent Response Times:</strong><br>
                  {{RESPONSE_MS}}
                </p>
            </div>
        </div>
      """);

  private final String defaultTemplate;

  Template(String defaultTemplate) {
    this.defaultTemplate = defaultTemplate;
  }
}

