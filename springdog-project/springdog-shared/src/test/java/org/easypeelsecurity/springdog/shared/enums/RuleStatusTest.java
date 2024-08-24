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

package org.easypeelsecurity.springdog.shared.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class RuleStatusTest {

  @ParameterizedTest
  @CsvSource({
      "ACTIVE",
      "INACTIVE",
      "NOT_CONFIGURED"
  })
  void ofHappy(String val) {
    RuleStatus status = RuleStatus.of(val);
    assertNotNull(status);
    assertEquals(val, status.name());
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"SAD", "UNKNOWN_VALUE", "HAHA"})
  void ofSad(String val) {
    assertThrows(IllegalArgumentException.class, () -> RuleStatus.of(val));
  }
}

