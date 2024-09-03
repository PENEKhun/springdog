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

package org.easypeelsecurity.springdog.shared.dto;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.easypeelsecurity.springdog.shared.enums.HttpMethod;
import org.easypeelsecurity.springdog.shared.enums.RuleStatus;
import org.easypeelsecurity.springdog.shared.util.Assert;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Metadata to group and identify information parsed by the controller.
 *
 * @author PENEKhun
 */
@SuppressWarnings("checkstyle:MissingJavadocMethod")
@Getter
@Setter
@NoArgsConstructor
public class EndpointDto {

  private final Set<EndpointParameterDto> parameters = new HashSet<>();
  private final Set<EndpointHeaderDto> headers = new HashSet<>();
  private long id;
  private String path;
  private String methodSignature;
  private HttpMethod httpMethod;
  private Set<String> parameterNamesToEnable = new HashSet<>();
  private Set<String> headerNamesToEnable = new HashSet<>();
  private boolean isPatternPath;
  private RuleStatus ruleStatus;
  private boolean ruleIpBased;
  private boolean rulePermanentBan;
  private int ruleRequestLimitCount;
  private int ruleTimeLimitInSeconds;
  private int ruleBanTimeInSeconds;

  @Builder
  public EndpointDto(long id, String path, String methodSignature, HttpMethod httpMethod,
      Set<EndpointParameterDto> parameters, Set<EndpointHeaderDto> headers, boolean isPatternPath,
      RuleStatus ruleStatus, boolean ruleIpBased,
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
    if (headers != null && !headers.isEmpty()) {
      this.headers.addAll(headers);
    }
    this.isPatternPath = isPatternPath;
    this.ruleStatus = ruleStatus;
    this.ruleIpBased = ruleIpBased;
    this.rulePermanentBan = rulePermanentBan;
    this.ruleRequestLimitCount = ruleRequestLimitCount;
    this.ruleTimeLimitInSeconds = ruleTimeLimitInSeconds;
    this.ruleBanTimeInSeconds = ruleBanTimeInSeconds;
  }

  public EndpointDto(String path, String methodSignature, HttpMethod httpMethod, boolean isPatternPath) {
    this.path = path;
    this.methodSignature = methodSignature;
    this.httpMethod = httpMethod;
    this.isPatternPath = isPatternPath;
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
        Objects.equals(parameters, that.parameters) && Objects.equals(headers, that.headers);
  }

  @Override
  public int hashCode() {
    int result = path.hashCode();
    result = 31 * result + methodSignature.hashCode();
    result = 31 * result + httpMethod.hashCode();
    result = 31 * result + Objects.hashCode(parameters);
    result = 31 * result + Objects.hashCode(headers);
    return result;
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
   * Add a parameters.
   *
   * @param parameters the parameters to add
   */
  public void addParameters(Set<EndpointParameterDto> parameters) {
    if (parameters != null && !parameters.isEmpty()) {
      this.parameters.addAll(parameters);
    }
  }

  /**
   * Add a headers.
   *
   * @param headers the headers to add
   */
  public void addHeaders(Set<EndpointHeaderDto> headers) {
    if (headers != null && !headers.isEmpty()) {
      this.headers.addAll(headers);
    }
  }
}
