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
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashSet;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RulesetTest {

  @Test
  @DisplayName("Should set default values when created with default constructor")
  void default_constructor() {
    // given
    Ruleset ruleset = new Ruleset();

    // when & then
    assertAll(
        () -> Assertions.assertEquals(RuleStatus.NOT_CONFIGURED, ruleset.getStatus()),
        () -> Assertions.assertFalse(ruleset.isIpBased()),
        () -> Assertions.assertFalse(ruleset.isPermanentBan()),
        () -> Assertions.assertEquals(0, ruleset.getRequestLimitCount()),
        () -> Assertions.assertEquals(0, ruleset.getTimeLimitInSeconds()),
        () -> Assertions.assertEquals(0, ruleset.getBanTimeInSeconds())
    );
  }

  @Test
  @DisplayName("Time limit must be greater than 0")
  void mustBePositiveValue1() {
    // when & then
    assertThrows(
        IllegalArgumentException.class,
        () -> new Ruleset(
            RuleStatus.ACTIVE,
            true,
            false,
            10,
            -1, // negative number
            0,
            new HashSet<>()),
        "Time limit must be greater than 0");
  }

  @Test
  @DisplayName("Request limit count must be greater than 0")
  void mustBePositiveValue2() {
    // when & then
    assertThrows(
        IllegalArgumentException.class,
        () -> new Ruleset(
            RuleStatus.ACTIVE,
            true,
            false,
            -1, // negative number
            10,
            0,
            new HashSet<>()),
        "Request limit count must be greater than 0");
  }

  @Test
  @DisplayName("Ban time must be greater than 0")
  void mustBePositiveValue3() {
    // when & then
    assertThrows(
        IllegalArgumentException.class,
        () -> new Ruleset(
            RuleStatus.ACTIVE,
            true,
            false,
            3,
            10,
            -10, // negative number
            new HashSet<>()),
        "Ban time must be greater than 0");
  }
}
