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

import java.util.ArrayList;
import java.util.List;

import org.easypeelsecurity.springdog.shared.ratelimit.HttpMethod;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
  private String path;
  @Enumerated(EnumType.STRING)
  private HttpMethod httpMethod;
  @OneToMany(cascade = CascadeType.PERSIST)
  @JoinColumn(name = "endpoint_id")
  private List<EndpointParameter> parameters = new ArrayList<>();

  /**
   * Constructor.
   *
   * @param path       api path
   * @param httpMethod http method
   * @param parameters api parameter list
   */
  public Endpoint(String path, HttpMethod httpMethod, List<EndpointParameter> parameters) {
    this.path = path;
    this.httpMethod = httpMethod;

    parameters.forEach(parameter -> parameter.setEndpoint(this));
    this.parameters = parameters;
  }

  /**
   * no-arg Constructor.
   */
  public Endpoint() {
  }
}
