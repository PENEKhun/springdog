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

import org.springframework.util.Assert;

/**
 * Metadata to group and identify information parsed by the controller.
 *
 * @author PENEKhun
 */
public record ParsedMetadata(String endpoint, String fqcn, HttpMethod httpMethod) {

  /**
   * Constructor.
   *
   * @param endpoint   api endpoint string (e.g. /api/v1/user)
   * @param fqcn       fully qualified class name of the method (e.g.
   *                   org.penekhun.controller.UserController.method1)
   * @param httpMethod HTTP request method (e.g. GET, POST, PUT, DELETE, PATCH)
   */
  public ParsedMetadata {
    Assert.notNull(endpoint, "Endpoint cannot be null");
    Assert.notNull(fqcn, "FQCN cannot be null");
    Assert.notNull(httpMethod, "Http method type cannot be null");
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (object == null || getClass() != object.getClass()) {
      return false;
    }

    ParsedMetadata that = (ParsedMetadata) object;

    if (!endpoint.equals(that.endpoint)) {
      return false;
    }
    return httpMethod == that.httpMethod;
  }

  @Override
  public int hashCode() {
    int result = endpoint.hashCode();
    result = 31 * result + httpMethod.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "ParsedMetadata{" +
        "endpoint='" + endpoint + '\'' +
        ", fqcn='" + fqcn + '\'' +
        ", httpMethod=" + httpMethod +
        '}';
  }
}
