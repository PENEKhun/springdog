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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

/**
 * Entity for endpoint version control.
 */
@Entity
public class EndpointVersionControl {

  @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "version")
  private final List<EndpointChangeLog> changeLogs = new ArrayList<>();
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(unique = true)
  private LocalDateTime dateOfVersion;
  @Column(length = 64)
  private String fullHashOfEndpoints;

  /**
   * All-arg constructor.
   *
   * @param dateOfVersion       version name (Date)
   * @param fullHashOfEndpoints apis hash like SHA-256 (not means single api hash)
   */
  public EndpointVersionControl(LocalDateTime dateOfVersion, String fullHashOfEndpoints) {
    this.dateOfVersion = dateOfVersion;
    this.fullHashOfEndpoints = fullHashOfEndpoints;
  }

  /**
   * No-arg constructor.
   */
  public EndpointVersionControl() {
  }

  /**
   * Getter.
   */
  public String getFullHashOfEndpoints() {
    return this.fullHashOfEndpoints;
  }

  /**
   * Getter.
   */
  public LocalDateTime getDateOfVersion() {
    return this.dateOfVersion;
  }

  /**
   * Getter.
   */
  public List<EndpointChangeLog> getChangeLogs() {
    return this.changeLogs;
  }

  /**
   * Add changelog.
   */
  public void addChangeLog(EndpointChangeLog changeLog) {
    this.changeLogs.add(changeLog);
    changeLog.setVersion(this);
  }
}
