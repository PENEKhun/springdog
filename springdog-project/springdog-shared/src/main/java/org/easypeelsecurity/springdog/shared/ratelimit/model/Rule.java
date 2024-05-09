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

import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.ManyToOne;

/**
 * items of Ruleset.
 */
@Entity
@DiscriminatorColumn(name = "rule_type")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
abstract class Rule {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  String apiHash;

  @ManyToOne
  private Ruleset ruleset;

  /**
   * Constructor.
   */
  protected Rule() {
  }

  /**
   * Constructor.
   */
  protected Rule(Long id, Ruleset ruleset) {
    this.id = id;
    this.ruleset = ruleset;
  }
}
