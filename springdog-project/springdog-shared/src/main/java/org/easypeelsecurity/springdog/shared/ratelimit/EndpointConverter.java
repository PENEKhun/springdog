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

package org.easypeelsecurity.springdog.shared.ratelimit;

import java.util.Set;
import java.util.stream.Collectors;

import org.easypeelsecurity.springdog.shared.ratelimit.model.Endpoint;
import org.easypeelsecurity.springdog.shared.ratelimit.model.EndpointParameter;
import org.easypeelsecurity.springdog.shared.ratelimit.model.HttpMethod;
import org.easypeelsecurity.springdog.shared.ratelimit.model.RuleStatus;

import org.apache.cayenne.ObjectContext;

/**
 * Converter for rate limit. between DTO and Entity.
 *
 * @author PENEKhun
 */
@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
public class EndpointConverter {

  /**
   * Resolve a given DTO {@code EndpointDto} as Entity {@code Endpoint}.
   *
   * @param endpointDto target DTO instance to transform to Entity
   * @return entity object
   */
  public static Endpoint toEntity(ObjectContext context, EndpointDto endpointDto) {
    Endpoint endpoint = context.newObject(Endpoint.class);
    endpoint.setPath(endpointDto.getPath());
    endpoint.setMethodSignature(endpointDto.getMethodSignature());
    endpoint.setHttpMethod(endpointDto.getHttpMethod().name());
    endpoint.setIsPatternPath(endpointDto.isPatternPath());
    endpoint.setRuleStatus(endpointDto.getRuleStatus() != null ? endpointDto.getRuleStatus().name()
        : RuleStatus.NOT_CONFIGURED.name());
    endpoint.setRuleIpBased(endpointDto.isRuleIpBased());
    endpoint.setRulePermanentBan(endpointDto.isRulePermanentBan());
    endpoint.setRuleRequestLimitCount(endpointDto.getRuleRequestLimitCount());
    endpoint.setRuleTimeLimitInSeconds(endpointDto.getRuleTimeLimitInSeconds());
    endpoint.setRuleBanTimeInSeconds(endpointDto.getRuleBanTimeInSeconds());

    for (EndpointParameterDto param : endpointDto.getParameters()) {
      EndpointParameter parameter = toEntity(context,  param);
      parameter.setEndpoint(endpoint);
      endpoint.addToEndpointparameters(parameter);
    }
    return endpoint;
  }

  private static EndpointParameter toEntity(ObjectContext context, EndpointParameterDto endpointParameterDto) {
    EndpointParameter endpointParameter = context.newObject(EndpointParameter.class);
    endpointParameter.setName(endpointParameterDto.getName());
    endpointParameter.setType(endpointParameterDto.getType());
    return endpointParameter;
  }

  /**
   * Resolve a given DTO {@code EndpointParameterDto} as DAO {@code EndpointParameter}.
   *
   * @param endpointEntity target Entity instance to transform to DTO
   * @return dto object
   */
  public static EndpointDto toDto(Endpoint endpointEntity) {
    return new EndpointDto.Builder()
        .id(endpointEntity.getId())
        .path(endpointEntity.getPath())
        .methodSignature(endpointEntity.getMethodSignature())
        .httpMethod(HttpMethod.valueOf(endpointEntity.getHttpMethod()))
        .parameters(endpointEntity.getEndpointparameters().stream().map(EndpointConverter::toDto)
            .collect(Collectors.toSet()))
        .isPatternPath(endpointEntity.isIsPatternPath())
        .ruleStatus(RuleStatus.of(endpointEntity.getRuleStatus()))
        .ruleIpBased(endpointEntity.isRuleIpBased())
        .rulePermanentBan(endpointEntity.isRulePermanentBan())
        .ruleRequestLimitCount(endpointEntity.getRuleRequestLimitCount())
        .ruleTimeLimitInSeconds(endpointEntity.getRuleTimeLimitInSeconds())
        .ruleBanTimeInSeconds(endpointEntity.getRuleBanTimeInSeconds())
        .build();
  }

  /**
   * Resolve a given Entity {@code EndpointParameter} as DTO {@code EndpointParameterDto}.
   *
   * @param endpointParameterEntity target Entity instance to transform to DTO
   * @return dto object
   */
  public static EndpointParameterDto toDto(EndpointParameter endpointParameterEntity) {
    return new EndpointParameterDto(endpointParameterEntity.getName(),
        endpointParameterEntity.getType(), endpointParameterEntity.isEnabled());
  }

  private static Set<EndpointParameterDto> toDto(Set<EndpointParameter> endpointParameters) {
    return endpointParameters
        .stream()
        .map(EndpointConverter::toDto)
        .collect(Collectors.toSet());
  }
}

