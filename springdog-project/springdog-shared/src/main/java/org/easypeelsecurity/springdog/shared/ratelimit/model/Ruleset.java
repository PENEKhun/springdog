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

import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.Where;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Transient;

/**
 * Ruleset entity.
 *
 * @author PENEKhun
 */
@Embeddable
public class Ruleset {

  @Enumerated(EnumType.STRING)
  private final RuleStatus status;

  private final boolean ipBased;
  private final boolean permanentBan;
  private final int requestLimitCount;
  private final int timeLimitInSeconds;
  private final int banTimeInSeconds;

  @Transient
  @Where(clause = "enabled = true")
  private final Set<EndpointParameter> enabledParameters = new HashSet<>();
  //  private LocalDateTime lastUpdate;

  /**
   * No-arg constructor.
   */
  public Ruleset() {
    this.status = RuleStatus.NOT_CONFIGURED;
    this.ipBased = false;
    this.permanentBan = false;
    this.requestLimitCount = 0;
    this.timeLimitInSeconds = 0;
    this.banTimeInSeconds = 0;

    this.validate();
  }

  /**
   * Constructor.
   */
  public Ruleset(RuleStatus status, boolean ipBased, boolean permanentBan,
      int requestLimitCount,
      int timeLimitInSeconds, int banTimeInSeconds, Set<EndpointParameter> enabledParameters) {
    this.status = status;
    this.ipBased = ipBased;
    this.permanentBan = permanentBan;
    this.requestLimitCount = requestLimitCount;
    this.timeLimitInSeconds = timeLimitInSeconds;
    this.banTimeInSeconds = banTimeInSeconds;
    this.enabledParameters.addAll(enabledParameters);

    this.validate();
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
  public boolean isPermanentBan() {
    return this.permanentBan;
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
  public int getRequestLimitCount() {
    return this.requestLimitCount;
  }

  /**
   * Getter.
   */
  public int getBanTimeInSeconds() {
    return this.banTimeInSeconds;
  }

  /**
   * Getter.
   */
  public int getTimeLimitInSeconds() {
    return this.timeLimitInSeconds;
  }

  /**
   * Getter.
   */
  public Set<EndpointParameter> getEnabledParameters() {
    return enabledParameters;
  }

  private void validate() {
    if (RuleStatus.ACTIVE.equals(this.status)) {
      if (this.requestLimitCount < 0) {
        throw new IllegalArgumentException("Request limit count must be greater than 0");
      }
      if (this.timeLimitInSeconds < 0) {
        throw new IllegalArgumentException("Time limit must be greater than 0");
      }
      if (this.banTimeInSeconds < 0 && !this.permanentBan) {
        throw new IllegalArgumentException("Ban time must be greater than 0");
      }
      if (!ipBased && this.enabledParameters.isEmpty()) {
        throw new IllegalArgumentException("At least one combinations must be enabled");
      }
    }
  }
}
