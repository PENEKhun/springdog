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

package org.easypeelsecurity.springdog.domain.ratelimit.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;

import org.easypeelsecurity.springdog.domain.ratelimit.model.Endpoint;
import org.easypeelsecurity.springdog.domain.ratelimit.model.EndpointHeader;
import org.easypeelsecurity.springdog.domain.ratelimit.model.EndpointParameter;
import org.easypeelsecurity.springdog.shared.dto.EndpointDto;
import org.easypeelsecurity.springdog.shared.dto.EndpointHeaderDto;
import org.easypeelsecurity.springdog.shared.dto.EndpointParameterDto;
import org.easypeelsecurity.springdog.shared.enums.EndpointParameterType;
import org.easypeelsecurity.springdog.shared.enums.HttpMethod;
import org.easypeelsecurity.springdog.shared.enums.RuleStatus;

import org.apache.cayenne.ObjectContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EndpointConverterTest {

  @Test
  @DisplayName("Should convert EndpointDto to Endpoint entity and match with equals()")
  void convertEndpointDtoToEntity() {
    // given
    ObjectContext context = mock(ObjectContext.class);

    EndpointDto endpointDto = EndpointDto.builder()
        .path("/api/test")
        .methodSignature("com.example.TestController.testMethod(String)")
        .httpMethod(HttpMethod.GET)
        .isPatternPath(false)
        .ruleStatus(RuleStatus.ACTIVE)
        .ruleIpBased(true)
        .rulePermanentBan(false)
        .ruleRequestLimitCount(100)
        .ruleTimeLimitInSeconds(3600)
        .ruleBanTimeInSeconds(600)
        .parameters(Set.of(new EndpointParameterDto("param1", EndpointParameterType.QUERY, true)))
        .headers(Set.of(new EndpointHeaderDto("header1", true)))
        .build();

    Endpoint expectedEndpoint = mock(Endpoint.class);
    EndpointParameter parameter = mock(EndpointParameter.class);
    EndpointHeader header = mock(EndpointHeader.class);

    when(context.newObject(Endpoint.class)).thenReturn(expectedEndpoint);
    when(context.newObject(EndpointParameter.class)).thenReturn(parameter);
    when(context.newObject(EndpointHeader.class)).thenReturn(header);

    when(expectedEndpoint.getPath()).thenReturn("/api/test");
    when(expectedEndpoint.getMethodSignature()).thenReturn("com.example.TestController.testMethod(String)");
    when(expectedEndpoint.getHttpMethod()).thenReturn(HttpMethod.GET.name());
    when(expectedEndpoint.isIsPatternPath()).thenReturn(false);
    when(expectedEndpoint.getRuleStatus()).thenReturn(RuleStatus.ACTIVE.name());
    when(expectedEndpoint.isRuleIpBased()).thenReturn(true);
    when(expectedEndpoint.isRulePermanentBan()).thenReturn(false);
    when(expectedEndpoint.getRuleRequestLimitCount()).thenReturn(100);
    when(expectedEndpoint.getRuleTimeLimitInSeconds()).thenReturn(3600);
    when(expectedEndpoint.getRuleBanTimeInSeconds()).thenReturn(600);

    // when
    Endpoint actualEndpoint = EndpointConverter.toEntity(context, endpointDto);

    // then
    assertEquals(expectedEndpoint, actualEndpoint);
  }

  @Test
  @DisplayName("Should convert Endpoint entity to EndpointDto and match with equals()")
  void convertEndpointEntityToDto() {
    // given
    Endpoint endpointEntity = mock(Endpoint.class);

    when(endpointEntity.getPath()).thenReturn("/api/test");
    when(endpointEntity.getMethodSignature()).thenReturn("com.example.TestController.testMethod(String)");
    when(endpointEntity.getHttpMethod()).thenReturn(HttpMethod.GET.name());
    when(endpointEntity.isIsPatternPath()).thenReturn(false);
    when(endpointEntity.getRuleStatus()).thenReturn(RuleStatus.ACTIVE.name());
    when(endpointEntity.isRuleIpBased()).thenReturn(true);
    when(endpointEntity.isRulePermanentBan()).thenReturn(false);
    when(endpointEntity.getRuleRequestLimitCount()).thenReturn(100);
    when(endpointEntity.getRuleTimeLimitInSeconds()).thenReturn(3600);
    when(endpointEntity.getRuleBanTimeInSeconds()).thenReturn(600);

    EndpointParameter parameter = mock(EndpointParameter.class);
    when(parameter.getName()).thenReturn("param1");
    when(parameter.getType()).thenReturn(EndpointParameterType.QUERY);

    EndpointHeader header = mock(EndpointHeader.class);
    when(header.getName()).thenReturn("header1");

    when(endpointEntity.getEndpointParameters()).thenReturn(List.of(parameter));
    when(endpointEntity.getEndpointHeaders()).thenReturn(List.of(header));

    EndpointDto expectedDto = EndpointDto.builder()
        .path("/api/test")
        .methodSignature("com.example.TestController.testMethod(String)")
        .httpMethod(HttpMethod.GET)
        .isPatternPath(false)
        .ruleStatus(RuleStatus.ACTIVE)
        .ruleIpBased(true)
        .rulePermanentBan(false)
        .ruleRequestLimitCount(100)
        .ruleTimeLimitInSeconds(3600)
        .ruleBanTimeInSeconds(600)
        .parameters(Set.of(new EndpointParameterDto("param1", EndpointParameterType.QUERY, true)))
        .headers(Set.of(new EndpointHeaderDto("header1", true)))
        .build();

    // when
    EndpointDto actualDto = EndpointConverter.toDto(endpointEntity);

    // then
    assertEquals(expectedDto, actualDto);
  }
}
