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

import java.util.HashSet;
import java.util.Set;

import org.easypeelsecurity.springdog.shared.ratelimit.model.HttpMethod;
import org.easypeelsecurity.springdog.shared.util.Assert;

/**
 * Metadata to group and identify information parsed by the controller.
 *
 * @author PENEKhun
 */
public class EndpointDto {

  private final String hash;
  private final String path;
  private final String fqcn;
  private final HttpMethod httpMethod;
  private final HashSet<EndpointParameterDto> parameters = new HashSet<>();
  private final boolean isPatternPath;

  /**
   * All-arg constructor.
   *
   * @param hash       hash of the endpoint
   * @param path       api path string (e.g. /api/v1/user)
   * @param fqcn       fully qualified class name of the method (e.g.
   *                   org.penekhun.controller.UserController.method1)
   * @param httpMethod HTTP request method (e.g. GET, POST, PUT, DELETE, PATCH)
   * @param isPatternPath is the path a pattern path
   */
  public EndpointDto(String hash, String path, String fqcn, HttpMethod httpMethod, boolean isPatternPath) {
    Assert.hasText(path, "Endpoint must not be null or empty");
    Assert.hasText(fqcn, "FQCN must not be null or empty");
    Assert.notNull(httpMethod, "HttpMethod must not be null");

    this.hash = hash;
    this.path = path;
    this.fqcn = fqcn;
    this.httpMethod = httpMethod;
    this.isPatternPath = isPatternPath;
  }

  /**
   * Constructor.
   */
  public EndpointDto(String path, String fqcn, HttpMethod httpMethod, boolean isPatternPath) {
    this(null, path, fqcn, httpMethod, isPatternPath);
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (object == null || getClass() != object.getClass()) {
      return false;
    }

    EndpointDto that = (EndpointDto) object;

    if (!this.path.equals(that.path)) {
      return false;
    }
    return this.httpMethod == that.httpMethod;
  }

  @Override
  public int hashCode() {
    int result = this.path.hashCode();
    result = 31 * result + this.httpMethod.hashCode();
    return result;
  }

  /**
   * Get the api path.
   *
   * @return the api path
   */
  public String getPath() {
    return this.path;
  }

  /**
   * Get the hashed object (id).
   *
   * @return hashed object (id)
   */
  public String getHash() {
    return this.hash;
  }

  /**
   * Get the fully qualified class name.
   *
   * @return the fully qualified class name
   */
  public String getFqcn() {
    return this.fqcn;
  }

  /**
   * Get the HTTP method.
   *
   * @return the HTTP method
   */
  public HttpMethod getHttpMethod() {
    return this.httpMethod;
  }

  /**
   * Get the parameters.
   *
   * @return the parameters
   */
  public Set<EndpointParameterDto> getParameters() {
    return this.parameters;
  }

  /**
   * Add a parameter to the metadata.
   *
   * @param parameters the parameters to add
   */
  public void addParameters(Set<EndpointParameterDto> parameters) {
    if (parameters != null && !parameters.isEmpty()) {
      this.parameters.addAll(parameters);
    }
  }

  /**
   * Is the path a pattern path.
   * @return true if the path is a pattern path
   */
  public boolean isPatternPath() {
    return this.isPatternPath;
  }
}
