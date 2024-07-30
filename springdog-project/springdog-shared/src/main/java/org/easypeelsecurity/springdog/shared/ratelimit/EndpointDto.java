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
import java.util.Objects;
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

  private final Set<EndpointParameterDto> parameters = new HashSet<>();
  private long id;
  private String path;
  private String methodSignature;
  private HttpMethod httpMethod;
  private Set<String> parameterNamesToEnable = new HashSet<>();
  private boolean isPatternPath;
  private RuleStatus ruleStatus;
  private boolean ruleIpBased;
  private boolean rulePermanentBan;
  private int ruleRequestLimitCount;
  private int ruleTimeLimitInSeconds;
  private int ruleBanTimeInSeconds;

  public EndpointDto(long id, String path, String methodSignature, HttpMethod httpMethod,
      Set<EndpointParameterDto> parameters, boolean isPatternPath, RuleStatus ruleStatus, boolean ruleIpBased,
      boolean rulePermanentBan, int ruleRequestLimitCount, int ruleTimeLimitInSeconds,
      int ruleBanTimeInSeconds) {
    Assert.hasText(path, "Endpoint must not be null or empty");
    Assert.hasText(methodSignature, "Method signature must not be null or empty");
    Assert.notNull(httpMethod, "HttpMethod must not be null");

    this.id = id;
    this.path = path;
    this.methodSignature = methodSignature;
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

  public EndpointDto(String path, String methodSignature, HttpMethod httpMethod, boolean isPatternPath) {
    this.path = path;
    this.methodSignature = methodSignature;
    this.httpMethod = httpMethod;
    this.isPatternPath = isPatternPath;
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

  public void setRuleStatus(RuleStatus ruleStatus) {
    this.ruleStatus = ruleStatus;
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

  public void setRuleIpBased(boolean ruleIpBased) {
    this.ruleIpBased = ruleIpBased;
  }

  public int getRuleRequestLimitCount() {
    return this.ruleRequestLimitCount;
  }

  public void setRuleRequestLimitCount(int ruleRequestLimitCount) {
    this.ruleRequestLimitCount = ruleRequestLimitCount;
  }

  @Override
  public final boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof EndpointDto that)) {
      return false;
    }

    return path.equals(that.path) &&
        methodSignature.equals(that.methodSignature) && httpMethod == that.httpMethod &&
        Objects.equals(parameters, that.parameters);
  }

  @Override
  public int hashCode() {
    int result = path.hashCode();
    result = 31 * result + methodSignature.hashCode();
    result = 31 * result + httpMethod.hashCode();
    result = 31 * result + Objects.hashCode(parameters);
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

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Setter.
   */
  public void setPath(String path) {
    this.path = path;
  }

  /**
   * Get the method signature.
   * (e.g) java.lang.String org.easypeelsecurity.springdogtest.ExampleController.example(java.lang.String)
   *
   * @return the fully qualified method name
   */
  public String getMethodSignature() {
    return this.methodSignature;
  }

  /**
   * Get the fully qualified method name.
   */
  public String getFqmn() {
    Assert.isTrue(this.methodSignature.contains(".") && this.methodSignature.contains("("),
        "May not be a valid method signature");

    int firstSpaceIndex = this.methodSignature.indexOf(" ");
    return this.methodSignature.substring(firstSpaceIndex + 1);
  }

  /**
   * Get the fully qualified class name.
   */
  public String getFqcn() {
    Assert.isTrue(this.methodSignature.contains(".") && this.methodSignature.contains("("),
        "May not be a valid method signature");

    String fqmn = this.getFqmn();
    String onlyMethod = fqmn.substring(0, fqmn.indexOf("("));
    return onlyMethod.substring(0, onlyMethod.lastIndexOf("."));
  }

  /**
   * Get the method name.
   * (ex) parseController
   *
   * @return the method name
   */
  public String getMethodName() {
    String fqmn = this.getFqmn();
    String fqcn = this.getFqcn();
    return fqmn.replace(fqcn, "").substring(1);
  }

  /**
   * Setter.
   */
  public void setMethodSignature(String methodSignature) {
    this.methodSignature = methodSignature;
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

    private long id;
    private String path;
    private String methodSignature;
    private HttpMethod httpMethod;
    private Set<EndpointParameterDto> parameters = new HashSet<>();
    private boolean isPatternPath;
    private RuleStatus ruleStatus = RuleStatus.NOT_CONFIGURED;
    private boolean ruleIpBased;
    private boolean rulePermanentBan;
    private int ruleRequestLimitCount;
    private int ruleTimeLimitInSeconds;
    private int ruleBanTimeInSeconds;

    public Builder id(long id) {
      this.id = id;
      return this;
    }

    public Builder path(String path) {
      this.path = path;
      return this;
    }

    public Builder methodSignature(String methodSignature) {
      this.methodSignature = methodSignature;
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
      return new EndpointDto(id, path, methodSignature, httpMethod, parameters, isPatternPath, ruleStatus,
          ruleIpBased,
          rulePermanentBan, ruleRequestLimitCount, ruleTimeLimitInSeconds, ruleBanTimeInSeconds);
    }
  }
}
