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

import java.util.HashSet;
import java.util.Set;

import org.easypeelsecurity.springdog.shared.ratelimit.model.HttpMethod;
import org.easypeelsecurity.springdog.shared.ratelimit.model.RuleStatus;
import org.easypeelsecurity.springdog.shared.util.Assert;

/**
 * Metadata to group and identify information parsed by the controller.
 *
 * @author PENEKhun
 */
@SuppressWarnings("checkstyle:MissingJavadocMethod")
public class EndpointDto {

  private String hash;
  private String path;
  private String fqcn;
  private HttpMethod httpMethod;
  private Set<EndpointParameterDto> parameters = new HashSet<>();
  private Set<String> parameterNamesToEnable = new HashSet<>();
  private boolean isPatternPath;
  private RuleStatus ruleStatus;
  private boolean ruleIpBased;
  private boolean rulePermanentBan;
  private int ruleRequestLimitCount;
  private int ruleTimeLimitInSeconds;
  private int ruleBanTimeInSeconds;

  public EndpointDto(String hash, String path, String fqcn, HttpMethod httpMethod,
      Set<EndpointParameterDto> parameters, boolean isPatternPath, RuleStatus ruleStatus, boolean ruleIpBased,
      boolean rulePermanentBan, int ruleRequestLimitCount, int ruleTimeLimitInSeconds,
      int ruleBanTimeInSeconds) {
    Assert.hasText(path, "Endpoint must not be null or empty");
    Assert.hasText(fqcn, "FQCN must not be null or empty");
    Assert.notNull(httpMethod, "HttpMethod must not be null");

    this.hash = hash;
    this.path = path;
    this.fqcn = fqcn;
    this.httpMethod = httpMethod;
    if (parameters != null && !parameters.isEmpty()) {
      this.parameters.addAll(parameters);
    }
    this.isPatternPath = isPatternPath;
    this.ruleStatus = ruleStatus;
    this.ruleIpBased = ruleIpBased;
    this.rulePermanentBan = rulePermanentBan;
    this.ruleRequestLimitCount = ruleRequestLimitCount;
    this.ruleTimeLimitInSeconds = ruleTimeLimitInSeconds;
    this.ruleBanTimeInSeconds = ruleBanTimeInSeconds;
  }

  /**
   * Constructor.
   */
  public EndpointDto() {
  }

  public EndpointDto(String path, String fqcn, HttpMethod httpMethod, boolean isPatternPath) {
    this.path = path;
    this.fqcn = fqcn;
    this.httpMethod = httpMethod;
    this.isPatternPath = isPatternPath;
  }

  public void setRuleStatus(RuleStatus ruleStatus) {
    this.ruleStatus = ruleStatus;
  }

  public void setRuleIpBased(boolean ruleIpBased) {
    this.ruleIpBased = ruleIpBased;
  }

  public void setRuleRequestLimitCount(int ruleRequestLimitCount) {
    this.ruleRequestLimitCount = ruleRequestLimitCount;
  }

  public Set<String> getParameterNamesToEnable() {
    return parameterNamesToEnable;
  }

  public void setParameterNamesToEnable(Set<String> parameterNamesToEnable) {
    this.parameterNamesToEnable = parameterNamesToEnable;
  }

  public RuleStatus getRuleStatus() {
    return this.ruleStatus;
  }

  public int timeLimitDays() {
    return this.ruleTimeLimitInSeconds / 86_400;
  }

  public int timeLimitHours() {
    int remainSeconds = this.ruleTimeLimitInSeconds % 86_400;
    return remainSeconds / 3_600;
  }

  public int timeLimitMinutes() {
    int remainSeconds = this.ruleTimeLimitInSeconds % 86_400;
    remainSeconds %= 3_600;
    return remainSeconds / 60;
  }

  public int timeLimitSeconds() {
    return this.ruleTimeLimitInSeconds % 60;
  }

  public int banTimeDays() {
    return this.ruleBanTimeInSeconds / 86_400;
  }

  public int banTimeHours() {
    int remainSeconds = this.ruleBanTimeInSeconds % 86_400;
    return remainSeconds / 3_600;
  }

  public int banTimeMinutes() {
    int remainSeconds = this.ruleBanTimeInSeconds % 86_400;
    remainSeconds %= 3_600;
    return remainSeconds / 60;
  }

  public int banTimeSeconds() {
    int remainSeconds = this.ruleBanTimeInSeconds % 86_400;
    remainSeconds %= 3_600;
    return remainSeconds % 60;
  }

  public boolean isRuleIpBased() {
    return this.ruleIpBased;
  }

  public int getRuleRequestLimitCount() {
    return this.ruleRequestLimitCount;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (object == null || getClass() != object.getClass()) {
      return false;
    }

    EndpointDto that = (EndpointDto) object;

    if (!this.path.equals(that.path)) {
      return false;
    }
    return this.httpMethod == that.httpMethod;
  }

  @Override
  public int hashCode() {
    int result = this.path.hashCode();
    result = 31 * result + this.httpMethod.hashCode();
    return result;
  }

  /**
   * Get the api path.
   *
   * @return the api path
   */
  public String getPath() {
    return this.path;
  }

  /**
   * Setter.
   */
  public void setPath(String path) {
    this.path = path;
  }

  /**
   * Get the hashed object (id).
   *
   * @return hashed object (id)
   */
  public String getHash() {
    return this.hash;
  }

  /**
   * Setter.
   */
  public void setHash(String hash) {
    this.hash = hash;
  }

  /**
   * Get the fully qualified class name.
   *
   * @return the fully qualified class name
   */
  public String getFqcn() {
    return this.fqcn;
  }

  /**
   * Setter.
   */
  public void setFqcn(String fqcn) {
    this.fqcn = fqcn;
  }

  /**
   * Get the HTTP method.
   *
   * @return the HTTP method
   */
  public HttpMethod getHttpMethod() {
    return this.httpMethod;
  }

  /**
   * Setter.
   */
  public void setHttpMethod(HttpMethod httpMethod) {
    this.httpMethod = httpMethod;
  }

  /**
   * Get the parameters.
   *
   * @return the parameters
   */
  public Set<EndpointParameterDto> getParameters() {
    return this.parameters;
  }

  /**
   * Add a parameter to the metadata.
   *
   * @param parameters the parameters to add
   */
  public void addParameters(Set<EndpointParameterDto> parameters) {
    if (parameters != null && !parameters.isEmpty()) {
      this.parameters.addAll(parameters);
    }
  }

  /**
   * Is the path a pattern path.
   *
   * @return true if the path is a pattern path
   */
  public boolean isPatternPath() {
    return this.isPatternPath;
  }

  public void setPatternPath(boolean patternPath) {
    isPatternPath = patternPath;
  }

  public boolean isRulePermanentBan() {
    return rulePermanentBan;
  }

  public void setRulePermanentBan(boolean rulePermanentBan) {
    this.rulePermanentBan = rulePermanentBan;
  }

  public int getRuleTimeLimitInSeconds() {
    return ruleTimeLimitInSeconds;
  }

  public void setRuleTimeLimitInSeconds(int ruleTimeLimitInSeconds) {
    this.ruleTimeLimitInSeconds = ruleTimeLimitInSeconds;
  }

  public int getRuleBanTimeInSeconds() {
    return ruleBanTimeInSeconds;
  }

  public void setRuleBanTimeInSeconds(int ruleBanTimeInSeconds) {
    this.ruleBanTimeInSeconds = ruleBanTimeInSeconds;
  }

  @SuppressWarnings({"checkstyle:MissingJavadocType", "checkstyle:MissingJavadocMethod"})
  public static class Builder {

    private String hash;
    private String path;
    private String fqcn;
    private HttpMethod httpMethod;
    private Set<EndpointParameterDto> parameters = new HashSet<>();
    private boolean isPatternPath;
    private RuleStatus ruleStatus;
    private boolean ruleIpBased;
    private boolean rulePermanentBan;
    private int ruleRequestLimitCount;
    private int ruleTimeLimitInSeconds;
    private int ruleBanTimeInSeconds;

    public Builder hash(String hash) {
      this.hash = hash;
      return this;
    }

    public Builder path(String path) {
      this.path = path;
      return this;
    }

    public Builder fqcn(String fqcn) {
      this.fqcn = fqcn;
      return this;
    }

    public Builder httpMethod(HttpMethod httpMethod) {
      this.httpMethod = httpMethod;
      return this;
    }

    public Builder parameters(Set<EndpointParameterDto> parameters) {
      this.parameters = parameters;
      return this;
    }

    public Builder isPatternPath(boolean isPatternPath) {
      this.isPatternPath = isPatternPath;
      return this;
    }

    public Builder ruleStatus(RuleStatus ruleStatus) {
      this.ruleStatus = ruleStatus;
      return this;
    }

    public Builder ruleIpBased(boolean ruleIpBased) {
      this.ruleIpBased = ruleIpBased;
      return this;
    }

    public Builder rulePermanentBan(boolean rulePermanentBan) {
      this.rulePermanentBan = rulePermanentBan;
      return this;
    }

    public Builder ruleRequestLimitCount(int ruleRequestLimitCount) {
      this.ruleRequestLimitCount = ruleRequestLimitCount;
      return this;
    }

    public Builder ruleTimeLimitInSeconds(int ruleTimeLimitInSeconds) {
      this.ruleTimeLimitInSeconds = ruleTimeLimitInSeconds;
      return this;
    }

    public Builder ruleBanTimeInSeconds(int ruleBanTimeInSeconds) {
      this.ruleBanTimeInSeconds = ruleBanTimeInSeconds;
      return this;
    }

    public EndpointDto build() {
      return new EndpointDto(hash, path, fqcn, httpMethod, parameters, isPatternPath, ruleStatus, ruleIpBased,
          rulePermanentBan, ruleRequestLimitCount, ruleTimeLimitInSeconds, ruleBanTimeInSeconds);
    }
  }
}
