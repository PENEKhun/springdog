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

import org.easypeelsecurity.springdog.shared.util.Assert;

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
    Assert.notNull(hash, "endpoint hash must not be null");

    this.hash = hash;
  }

  /**
   * Constructor.
   */
  public Ruleset(String hash, RuleStatus status, boolean ipBased, boolean permanentBan,
      int requestLimitCount,
      int timeLimitInSeconds, int banTimeInSeconds) {
    Assert.notNull(hash, "endpoint hash must not be null");

    this.hash = hash;
    this.status = status;
    this.ipBased = ipBased;
    this.permanentBan = permanentBan;
    this.requestLimitCount = requestLimitCount;
    this.timeLimitInSeconds = timeLimitInSeconds;
    this.banTimeInSeconds = banTimeInSeconds;
  }

  /**
   * Change status of the rule.
   *
   * @param status new status
   */
  public void changeStatus(RuleStatus status) {
    this.status = status;
  }

  /**
   * setter.
   */
  public void setPermanentBan(boolean permanentBan) {
    this.permanentBan = permanentBan;
  }

  /**
   * setter.
   */
  public void setRequestLimitCount(int requestLimitCount) {
    this.requestLimitCount = requestLimitCount;
  }

  /**
   * setter.
   */
  public void setTimeLimitInSeconds(int timeLimitInSeconds) {
    this.timeLimitInSeconds = timeLimitInSeconds;
  }

  /**
   * setter.
   */
  public void setBanTimeInSeconds(int banTimeInSeconds) {
    this.banTimeInSeconds = banTimeInSeconds;
  }

  /**
   * setter.
   */
  public void setIpBased(boolean ipBased) {
    this.ipBased = ipBased;
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
}
