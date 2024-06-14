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

import jakarta.persistence.CascadeType;
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

  private String targetPath;
  private HttpMethod targetMethod;
  private String targetFqcn;

  @ManyToOne(cascade = CascadeType.PERSIST, optional = false)
  @JoinColumn(referencedColumnName = "dateOfVersion")
  private EndpointVersionControl version;

  @Enumerated(EnumType.STRING)
  private EndpointChangeType changeType;

  private String detailString;
  private boolean isResolved;

  /**
   * Constructor.
   *
   * @param changeType   change type
   * @param detailString change history
   * @param isResolved   is resolved
   */
  public EndpointChangeLog(String targetPath, HttpMethod targetMethod, String targetFqcn,
      EndpointChangeType changeType, String detailString, boolean isResolved) {
    this.targetPath = targetPath;
    this.targetMethod = targetMethod;
    this.targetFqcn = targetFqcn;
    this.changeType = changeType;
    this.detailString = detailString;
    this.isResolved = isResolved;
  }

  /**
   * No-arg constructor.
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
  public String getDetailString() {
    return detailString;
  }

  /**
   * Getter.
   */
  public EndpointChangeType getChangeType() {
    return changeType;
  }

  /**
   * Getter.
   */
  public boolean isResolved() {
    return isResolved;
  }

  /**
   * Getter.
   */
  public Long getId() {
    return id;
  }

  /**
   * Getter.
   */
  public String getTargetPath() {
    return targetPath;
  }

  /**
   * Getter.
   */
  public HttpMethod getTargetMethod() {
    return targetMethod;
  }
}
