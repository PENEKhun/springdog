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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO for version control.
 */
@Getter
public class VersionControlDto {
  private Long id;
  private String fullHashOfEndpoints;
  private LocalDateTime dateOfVersion;
  private List<EndpointChangelogDto> changelogs = new ArrayList<>();

  /**
   * Constructor.
   */
  @Builder
  public VersionControlDto(Long id, String fullHashOfEndpoints, LocalDateTime dateOfVersion,
      List<EndpointChangelogDto> changelogs) {
    this.id = id;
    this.fullHashOfEndpoints = fullHashOfEndpoints;
    this.dateOfVersion = dateOfVersion;
    if (changelogs != null) {
      this.changelogs = changelogs;
    }
  }
}
