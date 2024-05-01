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
import org.springframework.util.Assert;

/**
 * Controller parameter item.
 *
 * @author PENEKhun
 */
public class EndpointParameterDto {

  private final Long id;
  private final String name;
  private final ApiParameterType type;

  /**
   * All-arg Constructor.
   *
   * @param id   id
   * @param name name of parameter
   * @param type type of parameter
   */
  public EndpointParameterDto(Long id, String name, ApiParameterType type) {
    Assert.hasText(name, "Name must not be null or empty");
    Assert.notNull(type, "Type must not be null");

    this.id = id;
    this.name = name;
    this.type = type;
  }

  /**
   * Constructor.
   */
  public EndpointParameterDto(String name, ApiParameterType type) {
    this(null, name, type);
  }

  /**
   * Get id of parameter.
   *
   * @return id of parameter
   */
  public Long getId() {
    return this.id;
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
}
