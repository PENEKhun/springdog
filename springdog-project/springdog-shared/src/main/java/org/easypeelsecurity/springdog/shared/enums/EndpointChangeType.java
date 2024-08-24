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
 * Enum for endpoint change types.
 *
 * @author PENEKhun
 */
public enum EndpointChangeType {
  API_DELETED("Can’t found endpoints that existed before."),
  API_ADDED("New endpoint added."),
  ACTIVATED("Endpoint activated."),
  DEACTIVATED("Endpoint deactivated."),
  RULE_CHANGED("Endpoint rule changed."),
  ENABLED_ENDPOINT_WAS_DELETED("Endpoints with Ratelimit enabled are no longer found in the code."),
  ENABLED_PARAMETER_WAS_DELETED(
      "With Endpoint Ratelimit enabled, one of the applied parameters was deleted or changed in the code.");

  private final String description;

  EndpointChangeType(String description) {
    this.description = description;
  }

  /**
   * Returns a detailed description of the endpoint change element.
   */
  public String getDescription() {
    return description;
  }
}
