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

import org.easypeelsecurity.springdog.shared.ratelimit.EndpointConverter;
import org.easypeelsecurity.springdog.shared.ratelimit.EndpointDto;
import org.easypeelsecurity.springdog.shared.ratelimit.EndpointHash;
import org.easypeelsecurity.springdog.shared.ratelimit.model.Endpoint;
import org.easypeelsecurity.springdog.shared.ratelimit.model.EndpointParameter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Command class for endpoint.
 */
@Service
@Transactional
public class EndpointCommand {

  private final EndpointRepository endpointRepository;
  private final EndpointParameterRepository endpointParameterRepository;

  /**
   * Constructor.
   */
  public EndpointCommand(EndpointRepository endpointRepository,
      EndpointParameterRepository endpointParameterRepository) {
    this.endpointRepository = endpointRepository;
    this.endpointParameterRepository = endpointParameterRepository;
  }

  /**
   * Apply changes.
   *
   * @param hashProvider hash provider
   * @param added        new endpoint
   * @param deleted      deleted endpoint
   */
  public void applyChanges(EndpointHash hashProvider, Set<EndpointDto> added, Set<EndpointDto> deleted) {
    endpointRepository.deleteAll(
        endpointRepository.findAllByHashIn(deleted.stream()
            .map(EndpointDto::getHash)
            .toList()));
    endpointRepository.saveAll(
        added.stream()
            .map(dto -> EndpointConverter.toEntity(hashProvider, dto))
            .toList());
  }

  /**
   * Update ruleset.
   */
  public void updateRule(EndpointDto endpointDto) {
    Endpoint endpoint = endpointRepository.findByHash(endpointDto.getHash())
        .orElseThrow(() -> new IllegalArgumentException("Endpoint not found"));

    Set<EndpointParameter> allParameters = endpoint.getParameters();
    allParameters
        .forEach(parameter -> {
          if (endpointDto.getParameterNamesToEnable().contains(parameter.getName())) {
            parameter.enable();
          } else {
            parameter.disable();
          }
        });
    endpoint.updateRule(
        endpointDto.getRuleStatus(),
        endpointDto.isRuleIpBased(),
        endpointDto.isRulePermanentBan(),
        endpointDto.getRuleRequestLimitCount(),
        endpointDto.getRuleTimeLimitInSeconds(),
        endpointDto.getRuleBanTimeInSeconds());

    RuleCache.changeRuleCached(endpoint.getFqcn(), EndpointConverter.toDto(endpoint));
  }
}
