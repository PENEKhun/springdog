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
import org.easypeelsecurity.springdog.shared.ratelimit.RulesetDto;
import org.easypeelsecurity.springdog.shared.ratelimit.model.Endpoint;
import org.easypeelsecurity.springdog.shared.ratelimit.model.Ruleset;
import org.easypeelsecurity.springdog.shared.util.TimeUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Command class for endpoint.
 */
@Service
@Transactional
public class EndpointCommand {

  private final EndpointRepository endpointRepository;

  /**
   * Constructor.
   */
  public EndpointCommand(EndpointRepository endpointRepository) {
    this.endpointRepository = endpointRepository;
  }

  /**
   * Apply changes.
   *
   * @param hashProvider hash provider
   * @param added        new endpoint
   * @param deleted      deleted endpoint
   */
  public void applyChanges(EndpointHash hashProvider, Set<EndpointDto> added, Set<EndpointDto> deleted) {
    endpointRepository.saveAll(
        added.stream()
            .map(dto -> EndpointConverter.toEntity(hashProvider, dto))
            .toList());
    endpointRepository.deleteAll(
        endpointRepository.findAllByHashIn(deleted.stream()
            .map(EndpointDto::getHash)
            .toList()));
  }

  /**
   * Update ruleset.
   *
   * @param apiHash api hash
   * @param changes ruleset dto that include changes
   * @return updated ruleset
   */
  public RulesetDto updateRule(String apiHash, RulesetDto changes) {
    Endpoint endpoint = endpointRepository.findByHash(apiHash)
        .orElseThrow(() -> new IllegalArgumentException("Endpoint not found"));

    Ruleset newRuleset = new Ruleset(
        changes.getStatus(),
        changes.isIpBased(),
        changes.isPermanentBan(),
        changes.getRequestLimitCount(),
        TimeUtil.convertToSeconds(
            changes.getTimeLimitDays(),
            changes.getTimeLimitHours(),
            changes.getTimeLimitMinutes(),
            changes.getTimeLimitSeconds()),
        TimeUtil.convertToSeconds(
            changes.getBanTimeDays(),
            changes.getBanTimeHours(),
            changes.getBanTimeMinutes(),
            changes.getBanTimeSeconds())
        );

    endpoint.changeRuleset(newRuleset);
    return EndpointConverter.toDto(endpoint.getRuleset());
  }
}
