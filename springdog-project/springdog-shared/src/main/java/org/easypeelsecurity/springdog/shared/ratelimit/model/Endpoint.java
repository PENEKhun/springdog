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

import java.util.HashSet;
import java.util.Set;

import org.springframework.util.Assert;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

/**
 * Entity class for endpoint.
 *
 * @author PENEKhun
 */
@Entity
public class Endpoint {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(length = 64, unique = true, nullable = false)
  private String hash;
  private String fqcn;
  private String path;
  @Enumerated(EnumType.STRING)
  private HttpMethod httpMethod;
  @OneToMany(cascade = {CascadeType.ALL})
  private Set<EndpointParameter> parameters = new HashSet<>();
  private boolean isPatternPath;

  /**
   * Constructor.
   *
   * @param hash       endpoint hashed String
   * @param path       api path e.g. /api/v1/user
   * @param fqcn       fully qualified class name e.g. org.penekhun.controller.UserController.method1
   * @param httpMethod http method
   * @param parameters api parameter list
   * @param isPatternPath boolean of path has pattern or not
   */
  public Endpoint(String hash, String path, String fqcn, HttpMethod httpMethod,
      Set<EndpointParameter> parameters, boolean isPatternPath) {
    Assert.hasText(hash, "Hash must not be null or empty");

    this.hash = hash;
    this.path = path;
    this.fqcn = fqcn;
    this.httpMethod = httpMethod;
    parameters.forEach(parameter -> parameter.setEndpoint(this));
    this.parameters = parameters;
    this.isPatternPath = isPatternPath;
  }

  /**
   * no-arg Constructor.
   */
  public Endpoint() {
  }

  /**
   * Get endpoint hash id.
   *
   * @return endpoint hash id
   */
  public String getHash() {
    return this.hash;
  }

  /**
   * Get fully qualified class name.
   *
   * @return fully qualified class name
   */
  public String getFqcn() {
    return fqcn;
  }

  /**
   * Get endpoint path.
   *
   * @return endpoint path
   */
  public String getPath() {
    return path;
  }

  /**
   * Get http method.
   *
   * @return http method
   */
  public HttpMethod getHttpMethod() {
    return httpMethod;
  }

  /**
   * Get endpoint parameters.
   *
   * @return endpoint parameters
   */
  public Set<EndpointParameter> getParameters() {
    return parameters;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Endpoint endpoint = (Endpoint) o;
    return hash.equals(endpoint.hash);
  }

  @Override
  public int hashCode() {
    return hash.hashCode();
  }

  /**
   * Check if path is pattern path.
   * @return true if path is pattern path
   */
  public boolean isPatternPath() {
    return this.isPatternPath;
  }
}
