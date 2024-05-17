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

import java.util.HashSet;
import java.util.Set;

import org.easypeelsecurity.springdog.shared.ratelimit.model.RuleStatus;
import org.easypeelsecurity.springdog.shared.util.Assert;
import org.easypeelsecurity.springdog.shared.util.TimeUtil;
import org.easypeelsecurity.springdog.shared.util.TimeUtil.Time;

/**
 * dto class for ruleset.
 */
@SuppressWarnings("checkstyle:MissingJavadocMethod")
public class RulesetDto {

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

  private Set<String> enabledParameterNames = new HashSet<>();

  /**
   * Constructor.
   */
  public RulesetDto(RuleStatus status, boolean ipBased, boolean permanentBan, int requestLimitCount,
      int timeLimitDays, int timeLimitHours, int timeLimitMinutes, int timeLimitSeconds, int banTimeDays,
      int banTimeHours, int banTimeMinutes, int banTimeSeconds) {
    this.status = status;
    this.ipBased = ipBased;
    this.permanentBan = permanentBan;
    this.requestLimitCount = requestLimitCount;
    this.timeLimitDays = timeLimitDays;
    this.timeLimitHours = timeLimitHours;
    this.timeLimitMinutes = timeLimitMinutes;
    this.timeLimitSeconds = timeLimitSeconds;
    this.banTimeDays = banTimeDays;
    this.banTimeHours = banTimeHours;
    this.banTimeMinutes = banTimeMinutes;
    this.banTimeSeconds = banTimeSeconds;
  }

  public RulesetDto() {
  }

  /**
   * Constructor.
   */
  public RulesetDto(RuleStatus status, boolean ipBased, boolean permanentBan, int requestLimitCount,
      int timeLimitInSecond, int banTimeInSeconds, Set<String> paramsHashes) {
    Assert.notNull(status, "status must not be null");
    if (status == ACTIVE) {
      Assert.isTrue(requestLimitCount > 0, "requestLimitCount must be greater than 0");
      Assert.isTrue(timeLimitInSecond > 0, "timeLimitInSeconds must be greater than 0");
      Assert.isTrue(banTimeInSeconds > 0, "banTimeInSeconds must be greater than 0");
      Assert.isTrue(
          ipBased || (paramsHashes != null && !paramsHashes.isEmpty()),
          "At least rule must be ip-based or parameters-based-ban");
    }

    this.status = status;
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

    if (paramsHashes != null) {
      this.enabledParameterNames = paramsHashes;
    }
  }

  public RuleStatus getStatus() {
    return this.status;
  }

  public void setStatus(RuleStatus status) {
    this.status = status;
  }

  public boolean isIpBased() {
    return this.ipBased;
  }

  public void setIpBased(boolean ipBased) {
    this.ipBased = ipBased;
  }

  public boolean isPermanentBan() {
    return this.permanentBan;
  }

  public void setPermanentBan(boolean permanentBan) {
    this.permanentBan = permanentBan;
  }

  public int getRequestLimitCount() {
    return this.requestLimitCount;
  }

  public void setRequestLimitCount(int requestLimitCount) {
    this.requestLimitCount = requestLimitCount;
  }

  public int getTimeLimitDays() {
    return this.timeLimitDays;
  }

  public void setTimeLimitDays(int timeLimitDays) {
    this.timeLimitDays = timeLimitDays;
  }

  public int getTimeLimitHours() {
    return this.timeLimitHours;
  }

  public void setTimeLimitHours(int timeLimitHours) {
    this.timeLimitHours = timeLimitHours;
  }

  public int getTimeLimitMinutes() {
    return this.timeLimitMinutes;
  }

  public void setTimeLimitMinutes(int timeLimitMinutes) {
    this.timeLimitMinutes = timeLimitMinutes;
  }

  public int getTimeLimitSeconds() {
    return this.timeLimitSeconds;
  }

  public void setTimeLimitSeconds(int timeLimitSeconds) {
    this.timeLimitSeconds = timeLimitSeconds;
  }

  public int getBanTimeDays() {
    return this.banTimeDays;
  }

  public void setBanTimeDays(int banTimeDays) {
    this.banTimeDays = banTimeDays;
  }

  public int getBanTimeHours() {
    return this.banTimeHours;
  }

  public void setBanTimeHours(int banTimeHours) {
    this.banTimeHours = banTimeHours;
  }

  public int getBanTimeMinutes() {
    return this.banTimeMinutes;
  }

  public void setBanTimeMinutes(int banTimeMinutes) {
    this.banTimeMinutes = banTimeMinutes;
  }

  public int getBanTimeSeconds() {
    return this.banTimeSeconds;
  }

  public void setBanTimeSeconds(int banTimeSeconds) {
    this.banTimeSeconds = banTimeSeconds;
  }

  public Set<String> getEnabledParameterNames() {
    return this.enabledParameterNames;
  }

  public void setEnabledParameterNames(Set<String> enabledParameterNames) {
    this.enabledParameterNames = enabledParameterNames;
  }

  @Override
  public String toString() {
    return "RulesetDto{" +
        "status=" + status +
        ", ipBased=" + ipBased +
        ", permanentBan=" + permanentBan +
        ", requestLimitCount=" + requestLimitCount +
        ", timeLimitDays=" + timeLimitDays +
        ", timeLimitHours=" + timeLimitHours +
        ", timeLimitMinutes=" + timeLimitMinutes +
        ", timeLimitSeconds=" + timeLimitSeconds +
        ", banTimeDays=" + banTimeDays +
        ", banTimeHours=" + banTimeHours +
        ", banTimeMinutes=" + banTimeMinutes +
        ", banTimeSeconds=" + banTimeSeconds +
        ", paramHashes=" + enabledParameterNames +
        '}';
  }
}
