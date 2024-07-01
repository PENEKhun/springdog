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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EndpointRuleTest {

  @Test
  @DisplayName("Should set default values in field about the rules when endpoint was created")
  void ruleDefaultValue() {
    // given
    Endpoint endpoint = new Endpoint();
    // when & then
    assertAll(
        () -> assertEquals(RuleStatus.NOT_CONFIGURED.name(), endpoint.getRuleStatus()),
        () -> assertEquals(0, endpoint.getRuleRequestLimitCount()),
        () -> assertEquals(0, endpoint.getRuleTimeLimitInSeconds()),
        () -> assertEquals(0, endpoint.getRuleBanTimeInSeconds()),
        () -> assertFalse(endpoint.isRulePermanentBan()),
        () -> assertFalse(endpoint.isRuleIpBased()),
        () -> assertTrue(endpoint.getEndpointparameters().isEmpty()));
  }

  @Test
  @DisplayName("Time limit must be greater than 0")
  void mustBePositiveValue1() {
    // given
    var endpoint = new Endpoint();

    // when & then
    assertThrows(
        IllegalArgumentException.class,
        () -> endpoint.updateRule(
            RuleStatus.ACTIVE,
            true,
            false,
            10,
            -10,  // negative number
            10,
            new HashSet<>()),
        "Time limit must be greater than 0");
  }

  @Test
  @DisplayName("Request limit count must be greater than 0")
  void mustBePositiveValue2() {
    // given
    var endpoint = new Endpoint();

    // when & then
    assertThrows(
        IllegalArgumentException.class,
        () -> endpoint.updateRule(
            RuleStatus.ACTIVE,
            true,
            false,
            -10, // negative number
            10,
            10,
            new HashSet<>()),
        "Request limit count must be greater than 0");
  }

  @Test
  @DisplayName("Ban time must be greater than 0")
  void mustBePositiveValue3() {
    // given
    var endpoint = new Endpoint();

    // when & then
    assertThrows(
        IllegalArgumentException.class,
        () -> endpoint.updateRule(
            RuleStatus.ACTIVE,
            true,
            false,
            10,
            10,
            -10,
            new HashSet<>()), // negative number
        "Ban time must be greater than 0");
  }
}
