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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

/**
 * Rule for parameter.
 */
@Entity
@DiscriminatorValue("parameter")
public class ParameterRule extends Rule {

  @OneToOne
  @JoinColumn(referencedColumnName = "paramHash")
  private EndpointParameter endpointParameter;

  /**
   * Constructor.
   */
  public ParameterRule() {
    super();
  }

  /**
   * Constructor.
   */
  public ParameterRule(Long id, Ruleset ruleset, EndpointParameter endpointParameter) {
    super(id, ruleset);
    this.endpointParameter = endpointParameter;
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
    return endpointParameter.equals(that.endpointParameter);
  }

  @Override
  public int hashCode() {
    return endpointParameter.hashCode();
  }
}
