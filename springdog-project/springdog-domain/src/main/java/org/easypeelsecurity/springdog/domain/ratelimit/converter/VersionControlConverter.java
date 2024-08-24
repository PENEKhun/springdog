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

package org.easypeelsecurity.springdog.domain.ratelimit.converter;

import java.util.List;

import org.easypeelsecurity.springdog.domain.ratelimit.model.EndpointChangelog;
import org.easypeelsecurity.springdog.domain.ratelimit.model.EndpointVersionControl;
import org.easypeelsecurity.springdog.shared.dto.EndpointChangelogDto;
import org.easypeelsecurity.springdog.shared.dto.VersionControlDto;
import org.easypeelsecurity.springdog.shared.enums.EndpointChangeType;

/**
 * Provides methods to convert the classes associated with VersionControl to DTOs and entities, respectively.
 *
 * @author PENEKhun
 */
abstract class VersionControlConverter {

  public static VersionControlDto toVersionControlDto(EndpointVersionControl versionControl) {
    return VersionControlDto.builder()
        .id(versionControl.getId())
        .fullHashOfEndpoints(versionControl.getFullHashOfEndpoints())
        .dateOfVersion(versionControl.getDateOfVersion())
        .changelogs(toChangelogDto(versionControl.getChangelogs()))
        .build();
  }

  public static List<VersionControlDto> toVersionControlDto(List<EndpointVersionControl> versionControls) {
    return versionControls.stream()
        .map(VersionControlConverter::toVersionControlDto)
        .toList();
  }

  public static EndpointChangelogDto toChangelogDto(EndpointChangelog changelog) {
    return EndpointChangelogDto.builder()
        .id(changelog.getId())
        .changeType(EndpointChangeType.valueOf(changelog.getChangeType()))
        .detailString(changelog.getDetailString())
        .isResolved(changelog.isIsResolved())
        .targetMethod(changelog.getTargetMethod())
        .targetMethodSignature(changelog.getTargetMethodSignature())
        .targetPath(changelog.getTargetPath())
        .reflectedVersion(toVersionControlDto(changelog.getReflectedVersion()))
        .build();
  }

  public static List<EndpointChangelogDto> toChangelogDto(List<EndpointChangelog> changelogs) {
    return changelogs.stream()
        .map(VersionControlConverter::toChangelogDto)
        .toList();
  }

  public static EndpointVersionControl toVersionControl(VersionControlDto dto) {
    var versionControl = new EndpointVersionControl();
    versionControl.setDateOfVersion(dto.getDateOfVersion());
    versionControl.setFullHashOfEndpoints(dto.getFullHashOfEndpoints());
    versionControl.setId(dto.getId());
    for (EndpointChangelogDto changelog : dto.getChangelogs()) {
      versionControl.addToChangelogs(toEndpointChangelog(changelog));
    }
    return versionControl;
  }

  private static EndpointChangelog toEndpointChangelog(EndpointChangelogDto changelog) {
    var endpointChangelog = new EndpointChangelog();
    endpointChangelog.setChangeType(changelog.getChangeType().name());
    endpointChangelog.setDetailString(changelog.getDetailString());
    endpointChangelog.setId(changelog.getId());
    endpointChangelog.setIsResolved(changelog.isResolved());
    endpointChangelog.setTargetMethod(changelog.getTargetMethod());
    endpointChangelog.setTargetMethodSignature(changelog.getTargetMethodSignature());
    endpointChangelog.setTargetPath(changelog.getTargetPath());
    endpointChangelog.setReflectedVersion(toVersionControl(changelog.getReflectedVersion()));
    return endpointChangelog;
  }
}
