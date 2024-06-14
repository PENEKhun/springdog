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

package org.easypeelsecurity.springdog.manager.ratelimit;

import java.util.List;

import org.easypeelsecurity.springdog.shared.ratelimit.model.EndpointChangeLog;
import org.easypeelsecurity.springdog.shared.ratelimit.model.EndpointVersionControl;
import org.springframework.stereotype.Service;

@Service
@SuppressWarnings({"checkstyle:MissingJavadocMethod", "checkstyle:MissingJavadocType"})
public class VersionControlQuery {

  private final VersionControlRepository versionControlRepository;

  public VersionControlQuery(VersionControlRepository versionControlRepository) {
    this.versionControlRepository = versionControlRepository;
  }

  /**
   * Gather all unresolved change logs and fetch them.
   * @return list of unresolved change logs
   */
  public List<EndpointChangeLog> getAllChangeLogsNotResolved() {
    return versionControlRepository.findAll()
        .stream()
        .map(EndpointVersionControl::getChangeLogs)
        .flatMap(List::stream)
        .filter(changeLog -> !changeLog.isResolved())
        .toList();
  }
}
