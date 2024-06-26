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

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.easypeelsecurity.springdog.shared.ratelimit.model.Endpoint;
import org.easypeelsecurity.springdog.shared.ratelimit.model.EndpointParameter;

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
  public static Endpoint toEntity(EndpointHash hashProvider, EndpointDto endpointDto) {
    Set<EndpointParameter> parameters = new HashSet<>();
    for (EndpointParameterDto param : endpointDto.getParameters()) {
      EndpointParameter parameter = toEntity(hashProvider, endpointDto, param);
      parameters.add(parameter);
    }

    String hash = hashProvider.getHash(endpointDto);
    return new Endpoint(hash, endpointDto.getPath(), endpointDto.getFqcn(),
        endpointDto.getHttpMethod(), parameters, endpointDto.isPatternPath());
  }

  private static EndpointParameter toEntity(EndpointHash hashProvider, EndpointDto apiInfo,
      EndpointParameterDto endpointParameterDto) {
    return new EndpointParameter(hashProvider.getParamHash(apiInfo, endpointParameterDto),
        endpointParameterDto.getName(),
        endpointParameterDto.getType());
  }

  /**
   * Resolve a given DTO {@code EndpointParameterDto} as DAO {@code EndpointParameter}.
   *
   * @param endpointEntity target Entity instance to transform to DTO
   * @return dto object
   */
  public static EndpointDto toDto(Endpoint endpointEntity) {
    return new EndpointDto.Builder()
        .hash(endpointEntity.getHash())
        .path(endpointEntity.getPath())
        .fqcn(endpointEntity.getFqcn())
        .httpMethod(endpointEntity.getHttpMethod())
        .parameters(
            endpointEntity.getParameters().stream().map(EndpointConverter::toDto).collect(Collectors.toSet()))
        .isPatternPath(endpointEntity.isPatternPath())
        .ruleStatus(endpointEntity.getRuleStatus())
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
    return new EndpointParameterDto(endpointParameterEntity.getParamHash(), endpointParameterEntity.getName(),
        endpointParameterEntity.getType(), endpointParameterEntity.isEnabled());
  }

  private static Set<EndpointParameterDto> toDto(Set<EndpointParameter> endpointParameters) {
    return endpointParameters
        .stream()
        .map(EndpointConverter::toDto)
        .collect(Collectors.toSet());
  }
}

