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

import java.util.Objects;

import lombok.Getter;

/**
 * Endpoint's request header DTO.
 */
@Getter
public class EndpointHeaderDto {
  private final String name;
  private final boolean enabled;
  private Long id;

  /**
   * Constructor.
   */
  public EndpointHeaderDto(String name, boolean enabled) {
    this.name = name;
    this.enabled = enabled;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    EndpointHeaderDto that = (EndpointHeaderDto) o;
    return name.equals(that.name) && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    int result = name.hashCode();
    result = 31 * result + Objects.hashCode(id);
    return result;
  }
}
