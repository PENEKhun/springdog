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

package org.easypeelsecurity.springdog.shared.ratelimit;

import org.easypeelsecurity.springdog.shared.ratelimit.model.ApiParameterType;
import org.easypeelsecurity.springdog.shared.util.Assert;

/**
 * Controller parameter item.
 *
 * @author PENEKhun
 */
public class EndpointParameterDto {

  private final String paramHash;
  private final String name;
  private final ApiParameterType type;
  private final boolean enabled;

  /**
   * All-arg Constructor.
   *
   * @param paramHash hashed
   * @param name name of parameter
   * @param type type of parameter
   * @param enabled enabled or not
   */
  public EndpointParameterDto(String paramHash, String name, ApiParameterType type, boolean enabled) {
    Assert.hasText(name, "Name must not be null or empty");
    Assert.notNull(type, "Type must not be null");

    this.paramHash = paramHash;
    this.name = name;
    this.type = type;
    this.enabled = enabled;
  }

  /**
   * Constructor.
   */
  public EndpointParameterDto(String name, ApiParameterType type) {
    this(null, name, type, false);
  }

  /**
   * Get object hashed string.
   *
   * @return hashed string
   */
  public String getParamHash() {
    return this.paramHash;
  }

  /**
   * Get name of parameter.
   *
   * @return name of parameter
   */
  public String getName() {
    return this.name;
  }

  /**
   * Get type of parameter.
   *
   * @return type of parameter
   */
  public ApiParameterType getType() {
    return this.type;
  }

  /**
   * Get enabled or not.
   *
   * @return enabled or not
   */
  public boolean isEnabled() {
    return enabled;
  }
}
