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

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

/**
 * Entity class for endpoint change log.
 */
@Entity
public class EndpointChangeLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(referencedColumnName = "dateOfVersion")
  private EndpointVersionControl version;

  @Enumerated(EnumType.STRING)
  private EndpointChangeType changeType;

  private String changeHistory;
  private boolean isResolved;

  /**
   * Constructor.
   *
   * @param version       version control
   * @param changeType    change type
   * @param changeHistory change history
   * @param isResolved    is resolved
   */
  public EndpointChangeLog(EndpointVersionControl version, EndpointChangeType changeType,
      String changeHistory, boolean isResolved) {
    this.version = version;
    this.changeHistory = changeHistory;
    this.changeType = changeType;
    this.isResolved = isResolved;
  }

  /**
   * Constructor.
   *
   * @param version       version control
   * @param changeType    change type
   * @param changeHistory change history
   */
  public EndpointChangeLog(EndpointVersionControl version, EndpointChangeType changeType,
      String changeHistory) {
    this(version, changeType, changeHistory, false);
  }

  /**
   * Constructor.
   */
  public EndpointChangeLog() {
  }

  /**
   * Set version.
   *
   * @param version version
   */
  public void setVersion(EndpointVersionControl version) {
    this.version = version;
  }

  /**
   * Getter.
   */
  public String getChangeHistory() {
    return changeHistory;
  }

  /**
   * Getter.
   */
  public EndpointChangeType getChangeType() {
    return changeType;
  }
}
