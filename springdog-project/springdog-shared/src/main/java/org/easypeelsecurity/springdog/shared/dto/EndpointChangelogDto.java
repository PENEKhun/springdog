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

import org.easypeelsecurity.springdog.shared.enums.EndpointChangeType;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO for endpoint changelog.
 */
@Getter
public class EndpointChangelogDto {
  private Long id;
  private EndpointChangeType changeType;
  private String detailString;
  private boolean isResolved;
  private String targetMethod;
  private String targetMethodSignature;
  private String targetPath;
  private VersionControlDto reflectedVersion;

  /**
   * Constructor.
   */
  @Builder
  public EndpointChangelogDto(Long id, EndpointChangeType changeType, String detailString, boolean isResolved,
      String targetMethod, String targetMethodSignature, String targetPath,
      VersionControlDto reflectedVersion) {
    this.id = id;
    this.changeType = changeType;
    this.detailString = detailString;
    this.isResolved = isResolved;
    this.targetMethod = targetMethod;
    this.targetMethodSignature = targetMethodSignature;
    this.targetPath = targetPath;
    this.reflectedVersion = reflectedVersion;
  }
}
