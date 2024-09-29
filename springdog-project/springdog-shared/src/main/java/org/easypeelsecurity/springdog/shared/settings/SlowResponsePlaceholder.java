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

/**
 * Enum representing placeholders for the Slow Response templates.
 */
public enum SlowResponsePlaceholder implements Placeholder {
  ENDPOINT_PATH("ENDPOINT_PATH", "The API endpoint path"),
  RESPONSE_MS("RESPONSE_MS", "The response time in milliseconds");

  private final String key;
  private final String description;

  SlowResponsePlaceholder(String key, String description) {
    this.key = key;
    this.description = description;
  }

  @Override
  public String getKey() {
    return key;
  }

  @Override
  public String getDescription() {
    return description;
  }
}
