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

import java.util.Objects;

import org.easypeelsecurity.springdog.shared.ratelimit.model.auto._EndpointParameter;

@SuppressWarnings("all")
public class EndpointParameter extends _EndpointParameter {

  private static final long serialVersionUID = 1L;

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    EndpointParameter that = (EndpointParameter) obj;
    return this.getName().equals(that.getName()) && this.getType().equals(that.getType())
        && Objects.equals(this.getEndpoint(), that.getEndpoint());
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.getName(), this.getType(), this.getEndpoint());
  }
}
