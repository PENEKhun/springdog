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

import org.easypeelsecurity.springdog.shared.util.Assert;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

/**
 * Entity class for endpoint parameter.
 *
 * @author PENEKhun
 */
@Entity
public class EndpointParameter {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(unique = true)
  private String paramHash;
  private String name;
  @Enumerated(EnumType.STRING)
  private ApiParameterType type;
  private boolean enabled;

  @ManyToOne(cascade = CascadeType.ALL)
  private Endpoint endpoint;

  /**
   * Constructor.
   *
   * @param paramHash parameter hashed
   * @param name      parameter name
   * @param type      parameter type (query, path, body, model), default is query
   */
  public EndpointParameter(String paramHash, String name, ApiParameterType type) {
    Assert.hasText(paramHash, "Hash must not be null or empty");
    Assert.hasText(name, "Name must not be null or empty");

    this.paramHash = paramHash;
    this.name = name;
    this.type = Objects.requireNonNullElse(type, ApiParameterType.QUERY);
  }

  /**
   * no-arg Constructor.
   */
  public EndpointParameter() {
  }

  /**
   * Constructor.
   */
  public EndpointParameter(String paramHash) {
    this.paramHash = paramHash;
  }

  /**
   * set endpoint.
   *
   * @param endpoint endpoint object
   */
  public void setEndpoint(Endpoint endpoint) {
    Assert.notNull(endpoint, "Endpoint must not be null");
    this.endpoint = endpoint;
  }

  /**
   * Get hashed object.
   *
   * @return hash
   */
  public String getParamHash() {
    return this.paramHash;
  }

  /**
   * Get parameter name.
   *
   * @return parameter name
   */
  public String getName() {
    return this.name;
  }

  /**
   * Get parameter type.
   *
   * @return parameter type
   */
  public ApiParameterType getType() {
    return this.type;
  }

  /**
   * Enable parameter for ratelimit.
   */
  public void enable() {
    this.enabled = true;
  }

  /**
   * Disable parameter for ratelimit.
   */
  public void disable() {
    this.enabled = false;
  }

  /**
   * Getter.
   */
  public boolean isEnabled() {
    return this.enabled;
  }
}
