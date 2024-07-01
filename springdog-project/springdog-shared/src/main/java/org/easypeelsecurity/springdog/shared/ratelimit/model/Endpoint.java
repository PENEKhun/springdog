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

package org.easypeelsecurity.springdog.shared.ratelimit.model;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

import org.easypeelsecurity.springdog.shared.ratelimit.model.auto._Endpoint;
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
    this.endpointparameters = new ArrayList<>();
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
      Set<String> enableParamNames) {
    Assert.isNotEqual(ruleStatus, RuleStatus.NOT_CONFIGURED, "Couldn't change ruleStatus to NOT_CONFIGURED");
    Assert.isTrue(enableParamNames != null, "enableParamNames must not be null");
    setRuleStatus(ruleStatus.name());
    setRuleIpBased(ruleIpBased);
    setRulePermanentBan(rulePermanentBan);
    setRuleRequestLimitCount(ruleRequestLimitCount);
    setRuleTimeLimitInSeconds(ruleTimeLimitInSeconds);
    setRuleBanTimeInSeconds(ruleBanTimeInSeconds);
    getEndpointparameters().forEach(param -> param.setEnabled(enableParamNames.contains(param.getName())));
    ruleValidate();
  }

  private void ruleValidate() {
    if (RuleStatus.ACTIVE.name().equals(this.ruleStatus)) {
      if (this.ruleRequestLimitCount <= 0) {
        throw new IllegalArgumentException("Request limit count must be greater than 0");
      }
      if (this.ruleTimeLimitInSeconds <= 0) {
        throw new IllegalArgumentException("Time limit must be greater than 0");
      }
      if (this.ruleBanTimeInSeconds <= 0 && !this.rulePermanentBan) {
        throw new IllegalArgumentException("Ban time must be greater than 0");
      }
      if (!this.ruleIpBased && this.getEndpointparameters().stream().filter(EndpointParameter::isEnabled)
          .collect(Collectors.toSet()).isEmpty()) {
        throw new IllegalArgumentException("At least one combinations must be enabled");
      }
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Endpoint endpoint = (Endpoint) o;
    return hash.equals(endpoint.hash);
  }

  @Override
  public int hashCode() {
    return hash.hashCode();
  }
}
