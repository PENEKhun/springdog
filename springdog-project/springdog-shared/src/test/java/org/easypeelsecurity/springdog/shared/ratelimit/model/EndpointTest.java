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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.HashSet;

import org.junit.jupiter.api.Test;

class EndpointTest {

  @Test
  void equalsTrueSameHash() {
    // given
    Endpoint endpoint1 = new Endpoint("hash", "path", "fqcn", HttpMethod.GET, new HashSet<>(), false);
    Endpoint endpoint2 = new Endpoint("hash", "path", "fqcn", HttpMethod.GET, new HashSet<>(), false);

    // when & then
    assertThat(endpoint1).isEqualTo(endpoint2);
  }

  @Test
  void equalsFalseDifferentHash() {
    // given
    Endpoint endpoint1 = new Endpoint("hash1", "path", "fqcn", HttpMethod.GET, new HashSet<>(), false);
    Endpoint endpoint2 = new Endpoint("hash2", "path", "fqcn", HttpMethod.GET, new HashSet<>(), false);

    // when & then
    assertThat(endpoint1).isNotEqualTo(endpoint2);
  }
}
