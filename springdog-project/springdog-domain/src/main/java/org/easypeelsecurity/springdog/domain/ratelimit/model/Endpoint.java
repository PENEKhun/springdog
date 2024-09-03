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

package org.easypeelsecurity.springdog.domain.ratelimit.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.easypeelsecurity.springdog.domain.ratelimit.model.auto._Endpoint;
import org.easypeelsecurity.springdog.shared.enums.RuleStatus;
import org.easypeelsecurity.springdog.shared.util.Assert;

@SuppressWarnings("all")
public class Endpoint extends _Endpoint {

  private static final long serialVersionUID = 1L;

  public Endpoint() {
    super();
    setRuleStatus(RuleStatus.NOT_CONFIGURED.name());
    setRuleRequestLimitCount(0);
    setRuleTimeLimitInSeconds(0);
    setRuleBanTimeInSeconds(0);
    setRulePermanentBan(false);
    setRuleIpBased(false);
    this.endpointParameters = new ArrayList<>();
    this.endpointHeaders = new ArrayList<>();
  }

  @Override
  public void addToEndpointParameters(EndpointParameter obj) {
    Assert.notNull(obj, "EndpointParameter must not be null");
    obj.setEndpoint(this);
    super.addToEndpointParameters(obj);
  }

  @Override
  public void removeFromEndpointParameters(EndpointParameter obj) {
    Assert.notNull(obj, "EndpointParameter must not be null");
    super.removeFromEndpointParameters(obj);
  }

  @Override
  public void addToEndpointHeaders(EndpointHeader obj) {
    Assert.notNull(obj, "EndpointHeader must not be null");
    obj.setEndpoint(this);
    super.addToEndpointHeaders(obj);
  }

  @Override
  public void removeFromEndpointHeaders(EndpointHeader obj) {
    Assert.notNull(obj, "EndpointHeader must not be null");
    super.removeFromEndpointHeaders(obj);
  }

  /**
   * Update rule.
   *
   * @param ruleStatus             rule status
   * @param ruleIpBased            rule ip based
   * @param rulePermanentBan       rule permanent ban
   * @param ruleRequestLimitCount  rule request limit count
   * @param ruleTimeLimitInSeconds rule time limit in seconds
   * @param ruleBanTimeInSeconds   rule ban time in seconds
   */
  public void updateRule(RuleStatus ruleStatus, boolean ruleIpBased, boolean rulePermanentBan,
      int ruleRequestLimitCount, int ruleTimeLimitInSeconds, int ruleBanTimeInSeconds,
      Set<String> enableParamNames, Set<String> enableHeaderNames) {
    Assert.isNotEqual(ruleStatus, RuleStatus.NOT_CONFIGURED, "Couldn't change ruleStatus to NOT_CONFIGURED");
    Assert.isTrue(enableParamNames != null, "enableParamNames must not be null");
    Assert.isTrue(enableHeaderNames != null, "enableHeaderNames must not be null");

    ruleValidate(
        ruleStatus.name(), ruleIpBased, rulePermanentBan, ruleRequestLimitCount,
        ruleTimeLimitInSeconds, ruleBanTimeInSeconds, enableParamNames, enableHeaderNames,
        this.getEndpointParameters(), this.getEndpointHeaders());

    setRuleStatus(ruleStatus.name());
    setRuleIpBased(ruleIpBased);
    setRulePermanentBan(rulePermanentBan);
    setRuleRequestLimitCount(ruleRequestLimitCount);
    setRuleTimeLimitInSeconds(ruleTimeLimitInSeconds);
    setRuleBanTimeInSeconds(ruleBanTimeInSeconds);
    getEndpointParameters().forEach(param -> param.setEnabled(enableParamNames.contains(param.getName())));
    getEndpointHeaders().forEach(header -> header.setEnabled(enableHeaderNames.contains(header.getName())));
  }

  private void ruleValidate(String ruleStatus, boolean ruleIpBased, boolean rulePermanentBan,
      int ruleRequestLimitCount, int ruleTimeLimitInSeconds, int ruleBanTimeInSeconds,
      Set<String> enableParamNames, Set<String> enableHeaderNames,
      List<EndpointParameter> endpointParameters,
      List<EndpointHeader> endpointHeaders) {
    if (RuleStatus.ACTIVE.name().equals(ruleStatus)) {
      if (ruleRequestLimitCount <= 0) {
        throw new IllegalArgumentException("Request limit count must be greater than 0");
      }
      if (ruleTimeLimitInSeconds <= 0) {
        throw new IllegalArgumentException("Time limit must be greater than 0");
      }
      if (ruleBanTimeInSeconds <= 0 && !rulePermanentBan) {
        throw new IllegalArgumentException("Ban time must be greater than 0");
      }
      if (!ruleIpBased && (isParamNotActivated(enableParamNames, endpointParameters)
          && isHeaderNotActivated(enableHeaderNames, endpointHeaders))) {
        throw new IllegalArgumentException("At least one combination must be enabled");
      }
    }
  }

  private static boolean isHeaderNotActivated(Set<String> enableHeaderNames,
      List<EndpointHeader> endpointHeaders) {
    return endpointHeaders.stream()
        .filter(header -> enableHeaderNames.contains(header.getName()))
        .count() == 0;
  }

  private static boolean isParamNotActivated(Set<String> enableParamNames,
      List<EndpointParameter> endpointParameters) {
    return endpointParameters.stream()
        .filter(param -> enableParamNames.contains(param.getName()))
        .count() == 0;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    Endpoint endpoint = (Endpoint) obj;
    return Objects.equals(this.path, endpoint.path) && Objects.equals(this.isPatternPath,
        endpoint.isPatternPath) && Objects.equals(this.httpMethod, endpoint.httpMethod);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.path, this.isPatternPath, this.httpMethod);
  }
}
