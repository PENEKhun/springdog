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

package org.easypeelsecurity.springdog.shared.dto;

import org.easypeelsecurity.springdog.shared.enums.EndpointParameterType;
import org.easypeelsecurity.springdog.shared.util.Assert;

/**
 * Controller parameter item.
 *
 * @author PENEKhun
 */
public class EndpointParameterDto {

  private Long id;
  private final String name;
  private final EndpointParameterType type;
  private final boolean enabled;

  /**
   * All-arg Constructor.
   *
   * @param name    name of parameter
   * @param type    type of parameter
   * @param enabled enabled or not
   */
  public EndpointParameterDto(String name, EndpointParameterType type, boolean enabled) {
    Assert.hasText(name, "Name must not be null or empty");
    Assert.notNull(type, "Type must not be null");

    this.name = name;
    this.type = type;
    this.enabled = enabled;
  }

  /**
   * Getter.
   */
  public Long getId() {
    return id;
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
  public EndpointParameterType getType() {
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

  @Override
  public final boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof EndpointParameterDto that)) {
      return false;
    }

    return name.equals(that.name) && type == that.type;
  }

  @Override
  public int hashCode() {
    int result = name.hashCode();
    result = 31 * result + type.hashCode();
    return result;
  }
}
