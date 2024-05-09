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

import static org.easypeelsecurity.springdog.shared.ratelimit.model.RuleStatus.ACTIVE;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.easypeelsecurity.springdog.shared.ratelimit.model.RuleStatus;
import org.easypeelsecurity.springdog.shared.util.Assert;
import org.easypeelsecurity.springdog.shared.util.TimeUtil;
import org.easypeelsecurity.springdog.shared.util.TimeUtil.Time;

/**
 * dto class for ruleset.
 */
public class RulesetDto {

  private Long id;
  private RuleStatus status;
  private boolean ipBased;
  private boolean permanentBan;
  private int requestLimitCount;
  private int timeLimitDays;
  private int timeLimitHours;
  private int timeLimitMinutes;
  private int timeLimitSeconds;

  private int banTimeDays;
  private int banTimeHours;
  private int banTimeMinutes;
  private int banTimeSeconds;

  private Set<Param> params = new HashSet<>();

  /**
   * Param class.
   */
  public static class Param {

    private String name;
  }

  /**
   * Constructor.
   */
  public RulesetDto(Long id, RuleStatus status, boolean ipBased, boolean permanentBan, int requestLimitCount,
      int timeLimitInSecond, int banTimeInSeconds, Param... params) {
    Assert.notNull(status, "status must not be null");
    if (status == ACTIVE) {
      Assert.isTrue(requestLimitCount > 0, "requestLimitCount must be greater than 0");
      Assert.isTrue(timeLimitInSecond > 0, "timeLimitInSeconds must be greater than 0");
      Assert.isTrue(banTimeInSeconds > 0, "banTimeInSeconds must be greater than 0");
      Assert.isTrue(
          ipBased || (params != null && params.length > 0),
          "At least rule must be ip-based or parameters-based-ban");
    }

    this.id = id;
    this.ipBased = ipBased;
    this.permanentBan = permanentBan;
    this.requestLimitCount = requestLimitCount;
    Time timeLimit = TimeUtil.convertToSeconds(timeLimitInSecond);
    this.timeLimitDays = timeLimit.days();
    this.timeLimitHours = timeLimit.hours();
    this.timeLimitMinutes = timeLimit.minutes();
    this.timeLimitSeconds = timeLimit.seconds();

    Time banTime = TimeUtil.convertToSeconds(banTimeInSeconds);
    this.banTimeDays = banTime.days();
    this.banTimeHours = banTime.hours();
    this.banTimeMinutes = banTime.minutes();
    this.banTimeSeconds = banTime.seconds();

    if (params != null) {
      Collections.addAll(this.params, params);
    }
  }

  /**
   * Getter.
   */
  public Long getId() {
    return this.id;
  }

  /**
   * Getter.
   */
  public RuleStatus getStatus() {
    return this.status;
  }

  /**
   * Getter.
   */
  public boolean isIpBased() {
    return this.ipBased;
  }

  /**
   * Getter.
   */
  public boolean isPermanentBan() {
    return this.permanentBan;
  }

  /**
   * Getter.
   */
  public int getRequestLimitCount() {
    return this.requestLimitCount;
  }

  /**
   * Getter.
   */
  public int getTimeLimitDays() {
    return this.timeLimitDays;
  }

  /**
   * Getter.
   */
  public int getTimeLimitHours() {
    return this.timeLimitHours;
  }

  /**
   * Getter.
   */
  public int getTimeLimitMinutes() {
    return this.timeLimitMinutes;
  }

  /**
   * Getter.
   */
  public int getTimeLimitSeconds() {
    return this.timeLimitSeconds;
  }

  /**
   * Getter.
   */
  public int getBanTimeDays() {
    return this.banTimeDays;
  }

  /**
   * Getter.
   */
  public int getBanTimeHours() {
    return this.banTimeHours;
  }

  /**
   * Getter.
   */
  public int getBanTimeMinutes() {
    return this.banTimeMinutes;
  }

  /**
   * Getter.
   */
  public int getBanTimeSeconds() {
    return this.banTimeSeconds;
  }

  /**
   * Getter.
   */
  public Set<Param> getParams() {
    return this.params;
  }
}
