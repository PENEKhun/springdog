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

import org.easypeelsecurity.springdog.shared.ratelimit.EndpointConverter;
import org.easypeelsecurity.springdog.shared.ratelimit.EndpointDto;
import org.easypeelsecurity.springdog.shared.ratelimit.VersionCompare;
import org.easypeelsecurity.springdog.shared.ratelimit.model.Endpoint;
import org.easypeelsecurity.springdog.shared.ratelimit.model.EndpointVersionControl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Endpoint query service.
 *
 * @author PENEKhun
 */
@Service
@Transactional(readOnly = true)
public class EndpointQuery {

  private final EndpointRepository endpointRepository;
  private final EndpointVersionControlRepository endpointVersionControlRepository;

  /**
   * Constructor.
   */
  public EndpointQuery(EndpointRepository endpointRepository,
      EndpointVersionControlRepository endpointVersionControlRepository) {
    this.endpointRepository = endpointRepository;
    this.endpointVersionControlRepository = endpointVersionControlRepository;
  }

  /**
   * Find all endpoints.
   *
   * @return set of endpoint DTOs
   */
  public Set<EndpointDto> findAll() {
    return endpointRepository.findAll()
        .stream()
        .map(EndpointConverter::toDto)
        .collect(Collectors.toSet());
  }

  /**
   * Compare hash to latest version.
   *
   * @param compareWith hash to compare
   * @return version compare
   */
  public VersionCompare compareToLatestVersion(String compareWith) {
    Optional<EndpointVersionControl> latest =
        endpointVersionControlRepository.findTopByOrderByDateOfVersionDesc();
    if (latest.isEmpty()) {
      return VersionCompare.FIRST_RUN;
    }

    String latestVersionHash = latest.get().getFullHashOfEndpoints();
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
    return endpointRepository.findByHash(apiHash)
        .map(EndpointConverter::toDto)
        .orElseThrow(() -> new IllegalArgumentException("Endpoint not found"));
  }

  /**
   * Get endpoint by fully qualified class name.
   *
   * @param fqcn fully qualified class name
   * @return Optional EndpointDto
   */
  public Optional<EndpointDto> getEndpointByFqcn(String fqcn) {
    Optional<Endpoint> found = endpointRepository.findByFqcn(fqcn);
    return found.map(EndpointConverter::toDto);
  }
}
