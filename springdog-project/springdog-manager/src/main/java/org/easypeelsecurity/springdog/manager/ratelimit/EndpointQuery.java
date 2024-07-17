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

package org.easypeelsecurity.springdog.manager.ratelimit;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import org.easypeelsecurity.springdog.shared.ratelimit.EndpointConverter;
import org.easypeelsecurity.springdog.shared.ratelimit.EndpointDto;
import org.easypeelsecurity.springdog.shared.ratelimit.EndpointParameterDto;
import org.easypeelsecurity.springdog.shared.ratelimit.model.Endpoint;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.configuration.CayenneRuntime;
import org.apache.cayenne.query.ObjectSelect;

/**
 * Endpoint query service.
 *
 * @author PENEKhun
 */
@Service
public class EndpointQuery {

  private final CayenneRuntime springdogRepository;

  /**
   * Constructor.
   */
  public EndpointQuery(@Qualifier("springdogRepository") CayenneRuntime springdogRepository) {
    this.springdogRepository = springdogRepository;
  }

  /**
   * Find all endpoints.
   *
   * @return set of endpoint DTOs
   */
  public Set<EndpointDto> findAll() {
    ObjectContext context = springdogRepository.newContext();
    return ObjectSelect.query(Endpoint.class)
        .select(context)
        .stream()
        .map(EndpointConverter::toDto)
        .collect(Collectors.toSet());
  }

  /**
   * Find endpoint by id.
   * @return endpoint DTO
   * @throws IllegalArgumentException if endpoint not found
   */
  public EndpointDto findApi(long id) {
    ObjectContext context = springdogRepository.newContext();
    return ObjectSelect.query(Endpoint.class)
        .where(Endpoint.ID.eq(id))
        .select(context)
        .stream()
        .map(EndpointConverter::toDto)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Endpoint not found"));
  }

  /**
   * Get endpoint by fully qualified method name.
   *
   * @param fqmn fully qualified method name
   * @return Optional EndpointDto
   */
  public Optional<EndpointDto> getEndpointByFqmn(String fqmn) {
    ObjectContext context = springdogRepository.newContext();
    return ObjectSelect.query(Endpoint.class)
        .where(Endpoint.FQMN.eq(fqmn))
        .select(context)
        .stream()
        .map(EndpointConverter::toDto)
        .findFirst();
  }

  /**
   * Find all parameters.
   *
   * @return set of endpoint parameter DTOs
   */
  public Set<EndpointParameterDto> findAllParameters() {
    ObjectContext context = springdogRepository.newContext();
    return ObjectSelect.query(Endpoint.class)
        .select(context)
        .stream()
        .flatMap(endpoint -> endpoint.getEndpointparameters().stream())
        .map(EndpointConverter::toDto)
        .collect(Collectors.toSet());
  }
}
