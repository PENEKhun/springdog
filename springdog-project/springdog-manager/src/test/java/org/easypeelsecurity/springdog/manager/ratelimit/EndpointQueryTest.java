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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.Optional;

import org.easypeelsecurity.springdog.shared.ratelimit.VersionCompare;
import org.easypeelsecurity.springdog.shared.ratelimit.model.EndpointVersionControl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EndpointQueryTest {

  @Test
  @DisplayName("Returns FIRST_RUN if the latest version does not exist.")
  void firstRunTest() {
    // given
    EndpointQuery endpointQuery = new EndpointQuery(null);
    Optional<EndpointVersionControl> latestVersion = Optional.empty();

    // when
    VersionCompare result = endpointQuery.compareToLatestVersion("hash", latestVersion);

    // then
    assertThat(result).isEqualTo(VersionCompare.FIRST_RUN);
  }

  @Test
  @DisplayName("Returns DIFFERENT if it is different from the latest version hash that exists.")
  void returnDifferent() {
    // given
    EndpointQuery endpointQuery = new EndpointQuery(null);
    EndpointVersionControl temp = new EndpointVersionControl();
    temp.setFullHashOfEndpoints("exist-hash");
    Optional<EndpointVersionControl> latestVersion = Optional.of(temp);
    String newHash = "new-hash";

    // when
    VersionCompare result = endpointQuery.compareToLatestVersion(newHash, latestVersion);

    // then
    assertThat(result).isEqualTo(VersionCompare.DIFFERENT);
  }

  @Test
  @DisplayName("Returns SAME if it is the same as the latest version hash that exists.")
  void returnSame() {
    // given
    EndpointQuery endpointQuery = new EndpointQuery(null);
    EndpointVersionControl temp = new EndpointVersionControl();
    temp.setFullHashOfEndpoints("same-hash");
    Optional<EndpointVersionControl> latestVersion = Optional.of(temp);
    String newHash = "same-hash";

    // when
    VersionCompare result = endpointQuery.compareToLatestVersion(newHash, latestVersion);

    // then
    assertThat(result).isEqualTo(VersionCompare.SAME);
  }
}
