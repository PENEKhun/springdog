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

import org.easypeelsecurity.springdog.shared.ratelimit.RulesetDto;
import org.easypeelsecurity.springdog.shared.util.TimeUtil;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

/**
 * Ruleset entity.
 *
 * @author PENEKhun
 */
@Entity
public class Ruleset {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String hash;
  @Enumerated(EnumType.STRING)
  private RuleStatus status = RuleStatus.NOT_CONFIGURED;

  private boolean ipBased = false;
  private boolean permanentBan = false;
  private int requestLimitCount = 10;
  private int timeLimitInSeconds = 10;
  private int banTimeInSeconds = 10;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "ruleset")
  private Set<ParameterRule> enabledParameters = new HashSet<>();

  //  private LocalDateTime lastUpdate;

  /**
   * Constructor.
   */
  public Ruleset() {
  }

  /**
   * Constructor.
   */
  public Ruleset(String hash) {
    this.hash = hash;

    this.validate();
  }

  /**
   * Constructor.
   */
  public Ruleset(String hash, RuleStatus status, boolean ipBased, boolean permanentBan,
      int requestLimitCount,
      int timeLimitInSeconds, int banTimeInSeconds) {
    this.hash = hash;
    this.status = status;
    this.ipBased = ipBased;
    this.permanentBan = permanentBan;
    this.requestLimitCount = requestLimitCount;
    this.timeLimitInSeconds = timeLimitInSeconds;
    this.banTimeInSeconds = banTimeInSeconds;

    this.validate();
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
   * Update fields.
   */
  public void update(RulesetDto dto) {
    this.status = dto.getStatus();
    this.ipBased = dto.isIpBased();
    this.permanentBan = dto.isPermanentBan();
    this.requestLimitCount = dto.getRequestLimitCount();
    this.timeLimitInSeconds =
        TimeUtil.convertToSeconds(dto.getTimeLimitDays(), dto.getTimeLimitHours(), dto.getTimeLimitMinutes(),
            dto.getTimeLimitSeconds());
    this.banTimeInSeconds =
        TimeUtil.convertToSeconds(dto.getBanTimeDays(), dto.getBanTimeHours(), dto.getBanTimeMinutes(),
            dto.getBanTimeSeconds());

    this.validate();
  }

  private void validate() {
    if (this.hash.isEmpty()) {
      throw new IllegalArgumentException("Hash must not be empty");
    }

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
