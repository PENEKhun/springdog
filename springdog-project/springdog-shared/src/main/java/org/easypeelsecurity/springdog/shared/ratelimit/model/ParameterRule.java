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

package org.easypeelsecurity.springdog.shared.ratelimit.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * Rule for parameter.
 */
@Entity
@DiscriminatorValue("parameter")
public class ParameterRule extends Rule {

  private String paramHash;

  /**
   * Constructor.
   */
  public ParameterRule(String paramHash) {
    this.paramHash = paramHash;
  }

  /**
   * Constructor.
   */
  public ParameterRule() {
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ParameterRule that = (ParameterRule) o;
    return paramHash.equals(that.paramHash);
  }

  @Override
  public int hashCode() {
    return paramHash.hashCode();
  }

  /**
   * Getter.
   */
  public String getParamHash() {
    return paramHash;
  }
}
