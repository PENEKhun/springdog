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

/**
 * Utility class for time operations.
 */
public abstract class TimeUtil {

  private static final int SECONDS_IN_DAY = 86400;
  private static final int SECONDS_IN_HOUR = 3600;
  private static final int SECONDS_IN_MINUTE = 60;

  /**
   * Convert hours, minutes and seconds to seconds.
   *
   * @return seconds
   */
  public static int convertToSeconds(int days, int hours, int minutes, int seconds) {
    Assert.isTrue(days >= 0, "Days must be greater than or equal to 0");
    Assert.isTrue(hours >= 0, "Hours must be greater than or equal to 0");
    Assert.isTrue(minutes >= 0, "Minutes must be greater than or equal to 0");

    return days * SECONDS_IN_DAY + hours * SECONDS_IN_HOUR + minutes * SECONDS_IN_MINUTE + seconds;
  }

  /**
   * Convert seconds to hours, minutes and seconds.
   *
   * @return Time object
   */
  public static Time convertToSeconds(int seconds) {
    Assert.isTrue(seconds >= 0, "Seconds must be greater than or equal to 0");

    int days = seconds / SECONDS_IN_DAY;
    seconds -= days * SECONDS_IN_DAY;
    int hours = seconds / SECONDS_IN_HOUR;
    seconds -= hours * SECONDS_IN_HOUR;
    int minutes = seconds / SECONDS_IN_MINUTE;
    seconds -= minutes * SECONDS_IN_MINUTE;
    return new Time(days, hours, minutes, seconds);
  }

  /**
   * Time record.
   */
  private record Time(int days, int hours, int minutes, int seconds) {
  }
}
