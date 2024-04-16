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

import org.easypeelsecurity.springdog.shared.ratelimit.ApiParameterType;
import org.springframework.util.Assert;

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
  private String name;
  @Enumerated(EnumType.STRING)
  private ApiParameterType type;

  @ManyToOne
  private Endpoint endpoint;

  /**
   * Constructor.
   *
   * @param name parameter name
   * @param type parameter type (query, path, body, model)
   */
  public EndpointParameter(String name, ApiParameterType type) {
    this.name = name;
    this.type = type;
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
   * no-arg Constructor.
   */
  public EndpointParameter() {
  }
}
