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

package org.easypeelsecurity.springdog.shared.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class TimeUtilTest {

  @ParameterizedTest
  @CsvSource({
      "0, 0, 0, 0, 0",
      "86400, 1, 0, 0, 0",
      "90000, 1, 1, 0, 0",
      "90060, 1, 1, 1, 0",
      "90061, 1, 1, 1, 1"
  })
  @DisplayName("convert to seconds well")
  void convertToSecondsTest(int expected, int days, int hours, int minutes, int seconds) {
    // when
    int actual = TimeUtil.convertToSeconds(days, hours, minutes, seconds);

    // then
    assertEquals(expected, actual);
  }
}
