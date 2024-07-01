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

import java.util.List;
import java.util.Set;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.configuration.CayenneRuntime;
import org.apache.cayenne.query.ObjectSelect;
import org.easypeelsecurity.springdog.shared.ratelimit.EndpointConverter;
import org.easypeelsecurity.springdog.shared.ratelimit.EndpointDto;
import org.easypeelsecurity.springdog.shared.ratelimit.EndpointHash;
import org.easypeelsecurity.springdog.shared.ratelimit.model.Endpoint;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Command class for endpoint.
 */
@Service
public class EndpointCommand {

  private final CayenneRuntime springdogRepository;

  /**
   * Constructor.
   */
  public EndpointCommand(@Qualifier("springdogRepository") CayenneRuntime springdogRepository) {
    this.springdogRepository = springdogRepository;
  }

  /**
   * Apply changes.
   *
   * @param hashProvider hash provider
   * @param added        new endpoint
   * @param deleted      deleted endpoint
   */
  public void applyChanges(EndpointHash hashProvider, Set<EndpointDto> added, Set<EndpointDto> deleted) {
    ObjectContext context = springdogRepository.newContext();
    List<Endpoint> toRemoved = ObjectSelect.query(Endpoint.class)
        .where(Endpoint.HASH.in(deleted.stream().map(hashProvider::getHash).toList()))
        .select(context);
    context.deleteObjects(toRemoved);

    for (EndpointDto endpointDto : added) {
      Endpoint endpoint = EndpointConverter.toEntity(context, hashProvider, endpointDto);
      context.registerNewObject(endpoint);
    }
    context.commitChanges();
  }

  /**
   * Update ruleset.
   */
  public void updateRule(EndpointDto endpointDto) {
    ObjectContext context = springdogRepository.newContext();
    Endpoint endpoint = ObjectSelect.query(Endpoint.class)
        .where(Endpoint.HASH.eq(endpointDto.getHash()))
        .selectOne(context);
    if (endpoint == null) {
      throw new IllegalArgumentException("Endpoint not found");
    }
    endpoint.updateRule(
        endpointDto.getRuleStatus(),
        endpointDto.isRuleIpBased(),
        endpointDto.isRulePermanentBan(),
        endpointDto.getRuleRequestLimitCount(),
        endpointDto.getRuleTimeLimitInSeconds(),
        endpointDto.getRuleBanTimeInSeconds(),
        endpointDto.getParameterNamesToEnable());

    RuleCache.changeRuleCached(endpoint.getFqcn(), EndpointConverter.toDto(endpoint));
    context.commitChanges();
  }
}
