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

package org.easypeelsecurity.springdog.domain.ratelimit;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import org.easypeelsecurity.springdog.domain.ratelimit.converter.EndpointConverter;
import org.easypeelsecurity.springdog.domain.ratelimit.converter.VersionControlConverter;
import org.easypeelsecurity.springdog.domain.ratelimit.model.Endpoint;
import org.easypeelsecurity.springdog.shared.dto.EndpointChangelogDto;
import org.easypeelsecurity.springdog.shared.dto.EndpointDto;

import org.apache.cayenne.ObjectContext;

/**
 * The Use-Case service for endpoint.
 *
 * @author PENEKhun
 */
@Service
public class EndpointService {
  private final ObjectContext context;
  private final EndpointRepository endpointRepository;
  private final VersionControlRepository versionControlRepository;

  /**
   * Constructor.
   */
  public EndpointService(
      @Qualifier("springdogContext") ObjectContext context, EndpointRepository endpointRepository,
      VersionControlRepository versionControlRepository) {
    this.context = context;
    this.endpointRepository = endpointRepository;
    this.versionControlRepository = versionControlRepository;
  }

  /**
   * Find an {@link Endpoint} by its id.
   *
   * @param endpointId The id
   * @return The {@link EndpointDto}
   */
  public EndpointDto findEndpoint(long endpointId) {
    var endpoint = endpointRepository.findByIdOrNull(context, endpointId);
    if (endpoint == null) {
      throw new IllegalArgumentException("Endpoint not found");
    }
    return EndpointConverter.toDto(endpoint);
  }

  /**
   * Update the rule of an endpoint.
   *
   * @param endpointDto The endpoint DTO
   */
  public void updateRule(EndpointDto endpointDto) {
    Endpoint endpoint = endpointRepository.findByIdOrNull(context, endpointDto.getId());
    if (endpoint == null) {
      throw new IllegalArgumentException("Endpoint not found");
    }

    // TODO: change method sig (too many... args)
    endpoint.updateRule(endpointDto.getRuleStatus(), endpointDto.isRuleIpBased(),
        endpointDto.isRulePermanentBan(), endpointDto.getRuleRequestLimitCount(),
        endpointDto.getRuleTimeLimitInSeconds(), endpointDto.getRuleBanTimeInSeconds(),
        endpointDto.getParameterNamesToEnable(), endpointDto.getHeaderNamesToEnable());

    RuleCache.changeRuleCached(endpoint.getMethodSignature(), EndpointConverter.toDto(endpoint));
    context.commitChanges();
  }

  /**
   * Find all {@link Endpoint} entities.
   *
   * @return The list of {@link EndpointDto} entities
   */
  public List<EndpointDto> findAllEndpoints() {
    return endpointRepository.findAll(context)
        .stream()
        .map(EndpointConverter::toDto)
        .toList();
  }

  /**
   * Check if an endpoint exists.
   *
   * @param methodSignature The method signature
   * @return True if the endpoint exists
   */
  public boolean isEndpointExist(String methodSignature) {
    return endpointRepository.findByMethodSignatureOrNull(context, methodSignature) != null;
  }

  /**
   * Find {@link Endpoint} entity by method signature.
   *
   * @param methodSignature The <strong>valid</strong> method signature
   * @return The {@link EndpointDto}
   */
  public EndpointDto getEndpointByMethodSignature(String methodSignature) {
    return EndpointConverter.toDto(endpointRepository.findByMethodSignatureOrNull(context, methodSignature));
  }

  /**
   * Get all change logs that are not resolved.
   */
  public List<EndpointChangelogDto> getAllChangeLogsNotResolved() {
    var a = versionControlRepository.findAllChangeLogsByResolved(context, false);
    return VersionControlConverter.toChangelogDto(a);
  }
}
