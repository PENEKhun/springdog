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

package org.easypeelsecurity.springdog.shared.enums;

/**
 * Enum for Rule status.
 *
 * @author PENEKhun
 */
public enum RuleStatus {
  NOT_CONFIGURED,
  ACTIVE,
  INACTIVE;

  /**
   * String to Enum Object.
   *
   * @param value String value
   * @return RuleStatus
   */
  public static RuleStatus of(String value) {
    if (value == null || value.isEmpty()) {
      throw new IllegalArgumentException("Value cannot be null or empty");
    }

    for (RuleStatus status : RuleStatus.values()) {
      if (status.name().equalsIgnoreCase(value)) {
        return status;
      }
    }

    throw new IllegalArgumentException("Unknown RuleStatus: " + value);
  }
}
