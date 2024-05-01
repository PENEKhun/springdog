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

import java.util.Set;
import java.util.stream.Collectors;

import org.easypeelsecurity.springdog.shared.ratelimit.EndpointConverter;
import org.easypeelsecurity.springdog.shared.ratelimit.EndpointDto;
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

  /**
   * Constructor.
   */
  public EndpointQuery(EndpointRepository endpointRepository) {
    this.endpointRepository = endpointRepository;
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
}
