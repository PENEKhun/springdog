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
import static org.easypeelsecurity.springdog.shared.ratelimit.model.RuleStatus.NOT_CONFIGURED;

import org.junit.jupiter.api.Test;

/**
 * Endpoint test.
 *
 * @author PENEKhun
 */
class EndpointTest {

  @Test
  void endpointDefaultValue() {
    // given
    Endpoint endpoint = new Endpoint();

    // when & then
    assertThat(endpoint.getRuleStatus()).isEqualTo(NOT_CONFIGURED.name());
    assertThat(endpoint.getRuleRequestLimitCount()).isZero();
    assertThat(endpoint.getRuleBanTimeInSeconds()).isZero();
    assertThat(endpoint.getRuleTimeLimitInSeconds()).isZero();
    assertThat(endpoint.isRuleIpBased()).isFalse();
    assertThat(endpoint.isRulePermanentBan()).isFalse();
    assertThat(endpoint.getEndpointparameters()).isNotNull();
  }

  @Test
  void equalsFalseWhenDiffPath() {
    // given
    Endpoint endpoint1 = new Endpoint();
    endpoint1.setPath("/api/books");
    endpoint1.setFqmn("org.easypeelsecurity.springdogtest.ExampleController.example1");
    endpoint1.setHttpMethod(String.valueOf(HttpMethod.GET));
    endpoint1.setIsPatternPath(false);
    Endpoint endpoint2 = new Endpoint();
    endpoint2.setPath("/diff");
    endpoint2.setFqmn("org.easypeelsecurity.springdogtest.ExampleController.example1");
    endpoint2.setHttpMethod(String.valueOf(HttpMethod.GET));
    endpoint2.setIsPatternPath(false);

    // when & then
    assertThat(endpoint1).isNotEqualTo(endpoint2);
  }

  @Test
  void equalsFalseWhenDiffMethod() {
    // given
    Endpoint endpoint1 = new Endpoint();
    endpoint1.setPath("/api/books");
    endpoint1.setFqmn("org.easypeelsecurity.springdogtest.ExampleController.example1");
    endpoint1.setHttpMethod(String.valueOf(HttpMethod.GET));
    endpoint1.setIsPatternPath(false);
    Endpoint endpoint2 = new Endpoint();
    endpoint2.setPath("/api/books");
    endpoint2.setFqmn("org.easypeelsecurity.springdogtest.ExampleController.example1");
    endpoint2.setHttpMethod(String.valueOf(HttpMethod.POST));
    endpoint2.setIsPatternPath(false);

    // when & then
    assertThat(endpoint1).isNotEqualTo(endpoint2);
  }

  @Test
  void equalsFalseWhenDiffPatternPath() {
    // given
    Endpoint endpoint1 = new Endpoint();
    endpoint1.setPath("/api/books");
    endpoint1.setFqmn("org.easypeelsecurity.springdogtest.ExampleController.example1");
    endpoint1.setHttpMethod(String.valueOf(HttpMethod.GET));
    endpoint1.setIsPatternPath(false);
    Endpoint endpoint2 = new Endpoint();
    endpoint2.setPath("/api/books");
    endpoint2.setFqmn("org.easypeelsecurity.springdogtest.ExampleController.example1");
    endpoint2.setHttpMethod(String.valueOf(HttpMethod.GET));
    endpoint2.setIsPatternPath(true);

    // when & then
    assertThat(endpoint1).isNotEqualTo(endpoint2);
  }

  @Test
  void equalsTrue() {
    // given
    Endpoint endpoint1 = new Endpoint();
    endpoint1.setPath("/api/books");
    endpoint1.setFqmn("org.easypeelsecurity.springdogtest.ExampleController.example1");
    endpoint1.setHttpMethod(String.valueOf(HttpMethod.GET));
    endpoint1.setIsPatternPath(false);
    Endpoint endpoint2 = new Endpoint();
    endpoint2.setPath("/api/books");
    endpoint2.setFqmn("org.easypeelsecurity.springdogtest.ExampleController.example1");
    endpoint2.setHttpMethod(String.valueOf(HttpMethod.GET));
    endpoint2.setIsPatternPath(false);

    // when & then
    assertThat(endpoint1).isEqualTo(endpoint2);
  }
}
