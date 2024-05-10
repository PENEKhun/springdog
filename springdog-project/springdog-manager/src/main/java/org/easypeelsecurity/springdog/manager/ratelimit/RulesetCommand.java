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

import org.easypeelsecurity.springdog.shared.ratelimit.EndpointConverter;
import org.easypeelsecurity.springdog.shared.ratelimit.RulesetDto;
import org.easypeelsecurity.springdog.shared.ratelimit.model.Endpoint;
import org.easypeelsecurity.springdog.shared.ratelimit.model.Ruleset;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Ruleset command service.
 */
@Service
@Transactional
public class RulesetCommand {

  private final EndpointRepository endpointRepository;

  /**
   * Constructor.
   */
  public RulesetCommand(EndpointRepository endpointRepository) {
    this.endpointRepository = endpointRepository;
  }

  /**
   * Update ruleset.
   *
   * @param apiHash api hash
   * @param changes ruleset dto that include changes
   * @return updated ruleset
   */
  public RulesetDto update(String apiHash, RulesetDto changes) {
    Endpoint endpoint = endpointRepository.findByHash(apiHash)
        .orElseThrow(() -> new IllegalArgumentException("Endpoint not found"));

    Ruleset ruleset = endpoint.getRuleset();
    ruleset.update(changes);

    return EndpointConverter.toDto(ruleset);
  }
}
