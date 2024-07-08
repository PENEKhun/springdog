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
import org.easypeelsecurity.springdog.shared.ratelimit.VersionCompare;
import org.easypeelsecurity.springdog.shared.ratelimit.model.Endpoint;
import org.easypeelsecurity.springdog.shared.ratelimit.model.EndpointVersionControl;

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
   * Compares the provided hashes.
   *
   * @param compareWith The hash string to be compared.
   * @param compareTo   An {@code Optional} containing the latest {@code EndpointVersionControl} instance, which
   *                    holds the version to compare against. If empty, it indicates that there is no existing
   *                    version.
   * @return A {@code VersionCompare} indicating the result of the comparison.
   *     {@code VersionCompare.FIRST_RUN}is returned if {@code compareTo} is empty, suggesting that this is the
   *     initial version check.
   */
  public VersionCompare compareToLatestVersion(String compareWith,
      Optional<EndpointVersionControl> compareTo) {
    if (compareTo.isEmpty()) {
      return VersionCompare.FIRST_RUN;
    }

    String latestVersionHash = compareTo.get().getFullHashOfEndpoints();
    return latestVersionHash.equals(compareWith) ? VersionCompare.SAME : VersionCompare.DIFFERENT;
  }

  /**
   * Find endpoint by hash.
   *
   * @param apiHash hash of the endpoint
   * @return endpoint DTO
   * @throws IllegalArgumentException if endpoint not found
   */
  public EndpointDto findApi(String apiHash) {
    ObjectContext context = springdogRepository.newContext();
    return ObjectSelect.query(Endpoint.class)
        .where(Endpoint.HASH.eq(apiHash))
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
