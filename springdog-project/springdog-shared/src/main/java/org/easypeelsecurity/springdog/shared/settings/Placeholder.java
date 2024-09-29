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
 * Interface representing a placeholder in a template.
 */
public interface Placeholder {

  /**
   * Returns the key of the placeholder.
   */
  String getKey();

  /**
   * Returns the placeholder name as it appears in the template (e.g., "{{TARGET_DEVICE}}").
   * @return the placeholder name
   */
  default String getPlaceholderName() {
    return "{{%s}}".formatted(getKey());
  }

  /**
   * Returns a description of the placeholder.
   *
   * @return the placeholder description
   */
  String getDescription();
}
