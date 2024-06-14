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
import java.util.stream.Collectors;

import org.easypeelsecurity.springdog.shared.util.Assert;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

/**
 * Entity class for endpoint.
 *
 * @author PENEKhun
 */
@Entity
public class Endpoint {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(length = 64, unique = true, nullable = false)
  private String hash;
  @Column(unique = true, nullable = false)
  private String fqcn;
  @Column(nullable = false)
  private String path;
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private HttpMethod httpMethod;
  @OneToMany(cascade = {CascadeType.ALL}, orphanRemoval = true)
  private Set<EndpointParameter> parameters = new HashSet<>();
  private boolean isPatternPath;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private RuleStatus ruleStatus = RuleStatus.NOT_CONFIGURED;

  private boolean ruleIpBased;
  private boolean rulePermanentBan;
  private int ruleRequestLimitCount;
  private int ruleTimeLimitInSeconds;
  private int ruleBanTimeInSeconds;

  /**
   * Constructor.
   *
   * @param hash          endpoint hashed String
   * @param path          api path e.g. /api/v1/user
   * @param fqcn          fully qualified class name e.g. org.penekhun.controller.UserController.method1
   * @param httpMethod    http method
   * @param parameters    api parameter list
   * @param isPatternPath boolean of path has pattern or not
   */
  public Endpoint(String hash, String path, String fqcn, HttpMethod httpMethod,
      Set<EndpointParameter> parameters, boolean isPatternPath) {
    Assert.hasText(hash, "Hash must not be null or empty");
    this.hash = hash;
    this.path = path;
    this.fqcn = fqcn;
    this.httpMethod = httpMethod;
    this.parameters = parameters;
    this.isPatternPath = isPatternPath;

    // rule
    this.ruleStatus = RuleStatus.NOT_CONFIGURED;
    this.ruleIpBased = false;
    this.rulePermanentBan = false;
    this.ruleRequestLimitCount = 0;
    this.ruleTimeLimitInSeconds = 0;
    this.ruleBanTimeInSeconds = 0;
    parameters.forEach(param -> param.setEndpoint(this));
    ruleValidate();
  }

  /**
   * no-arg Constructor.
   */
  protected Endpoint() {
  }

  /**
   * Get endpoint hash id.
   *
   * @return endpoint hash id
   */
  public String getHash() {
    return this.hash;
  }

  /**
   * Get fully qualified class name.
   *
   * @return fully qualified class name
   */
  public String getFqcn() {
    return fqcn;
  }

  /**
   * Get endpoint path.
   *
   * @return endpoint path
   */
  public String getPath() {
    return path;
  }

  /**
   * Get http method.
   *
   * @return http method
   */
  public HttpMethod getHttpMethod() {
    return httpMethod;
  }

  /**
   * Get endpoint parameters.
   *
   * @return endpoint parameters
   */
  public Set<EndpointParameter> getParameters() {
    return parameters;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Endpoint endpoint = (Endpoint) o;
    return hash.equals(endpoint.hash);
  }

  /**
   * Getter.
   */
  public RuleStatus getRuleStatus() {
    return ruleStatus;
  }

  /**
   * Getter.
   */
  public boolean isRuleIpBased() {
    return ruleIpBased;
  }

  /**
   * Getter.
   */
  public boolean isRulePermanentBan() {
    return rulePermanentBan;
  }

  /**
   * Getter.
   */
  public int getRuleRequestLimitCount() {
    return ruleRequestLimitCount;
  }

  /**
   * Getter.
   */
  public int getRuleTimeLimitInSeconds() {
    return ruleTimeLimitInSeconds;
  }

  /**
   * Getter.
   */
  public int getRuleBanTimeInSeconds() {
    return ruleBanTimeInSeconds;
  }

  @Override
  public int hashCode() {
    return hash.hashCode();
  }

  /**
   * Check if path is pattern path.
   *
   * @return true if path is pattern path
   */
  public boolean isPatternPath() {
    return this.isPatternPath;
  }

  private void ruleValidate() {
    if (RuleStatus.ACTIVE.equals(this.ruleStatus)) {
      if (this.ruleRequestLimitCount <= 0) {
        throw new IllegalArgumentException("Request limit count must be greater than 0");
      }
      if (this.ruleTimeLimitInSeconds <= 0) {
        throw new IllegalArgumentException("Time limit must be greater than 0");
      }
      if (this.ruleBanTimeInSeconds <= 0 && !this.rulePermanentBan) {
        throw new IllegalArgumentException("Ban time must be greater than 0");
      }
      if (!this.ruleIpBased && this.parameters.stream().filter(EndpointParameter::isEnabled)
          .collect(Collectors.toSet()).isEmpty()) {
        throw new IllegalArgumentException("At least one combinations must be enabled");
      }
    }
  }

  /**
   * Update rule.
   *
   * @param ruleStatus             rule status
   * @param ruleIpBased            rule ip based
   * @param rulePermanentBan       rule permanent ban
   * @param ruleRequestLimitCount  rule request limit count
   * @param ruleTimeLimitInSeconds rule time limit in seconds
   * @param ruleBanTimeInSeconds   rule ban time in seconds
   */
  public void updateRule(RuleStatus ruleStatus, boolean ruleIpBased, boolean rulePermanentBan,
      int ruleRequestLimitCount, int ruleTimeLimitInSeconds, int ruleBanTimeInSeconds) {
    Assert.isNotEqual(ruleStatus, RuleStatus.NOT_CONFIGURED, "Couldn't change ruleStatus to NOT_CONFIGURED");
    this.ruleStatus = ruleStatus;
    this.ruleIpBased = ruleIpBased;
    this.rulePermanentBan = rulePermanentBan;
    this.ruleRequestLimitCount = ruleRequestLimitCount;
    this.ruleTimeLimitInSeconds = ruleTimeLimitInSeconds;
    this.ruleBanTimeInSeconds = ruleBanTimeInSeconds;
    ruleValidate();
  }
}
