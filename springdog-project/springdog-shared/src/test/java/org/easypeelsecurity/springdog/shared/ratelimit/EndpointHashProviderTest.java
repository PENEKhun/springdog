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

package org.easypeelsecurity.springdog.shared.ratelimit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Set;

import org.easypeelsecurity.springdog.shared.ratelimit.model.ApiParameterType;
import org.easypeelsecurity.springdog.shared.ratelimit.model.HttpMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class EndpointHashProviderTest {

  private static final EndpointHashProvider endpointHashProvider = new EndpointHashProvider();

  @ParameterizedTest
  @EnumSource(HttpMethod.class)
  @DisplayName("Should generate identical hashes for identical endpoints")
  void endpointHashEqualTrueTest(HttpMethod httpMethod) {
    // given
    EndpointDto endpointDto1 =
        new EndpointDto("/api/v1/test", "com.example.TestController", httpMethod, false);
    EndpointDto endpointDto2 =
        new EndpointDto("/api/v1/test", "com.example.TestController", httpMethod, false);

    // when
    String hash1 = endpointHashProvider.getHash(endpointDto1);
    String hash2 = endpointHashProvider.getHash(endpointDto2);

    // then
    assertEquals(hash1, hash2);
  }

  @Test
  @DisplayName("Should generate same api hashes for having different order of parameters")
  void endpointHashEqualTrueTest2() {
    // given
    EndpointParameterDto endpointParameterDto1 = new EndpointParameterDto("param1", ApiParameterType.QUERY);
    EndpointParameterDto endpointParameterDto2 = new EndpointParameterDto("param2", ApiParameterType.QUERY);

    EndpointDto endpointDto1 =
        new EndpointDto("/api/v1/test", "com.example.TestController", HttpMethod.GET,
            false, Set.of(endpointParameterDto1, endpointParameterDto2));
    EndpointDto endpointDto2 =
        new EndpointDto("/api/v1/test", "com.example.TestController", HttpMethod.GET,
            false, Set.of(endpointParameterDto2, endpointParameterDto1));

    // when
    String hash1 = endpointHashProvider.getHash(endpointDto1);
    String hash2 = endpointHashProvider.getHash(endpointDto2);

    // then
    assertEquals(hash1, hash2);
  }

  @Test
  @DisplayName("Should generate different hashes for endpoints with different paths")
  void endpointHashEqualFalse1() {
    // given
    EndpointDto endpointDto1 =
        new EndpointDto("/api/v1/test1", "com.example.TestController", HttpMethod.GET, false);
    EndpointDto endpointDto2 =
        new EndpointDto("/api/v1/test2", "com.example.TestController", HttpMethod.GET, false);

    // when
    String hash1 = endpointHashProvider.getHash(endpointDto1);
    String hash2 = endpointHashProvider.getHash(endpointDto2);

    // then
    assertNotEquals(hash1, hash2);
  }

  @Test
  @DisplayName("Should generate different hashes for endpoints with different controller names")
  void endpointHashEqualFalse2() {
    // given
    EndpointDto endpointDto1 =
        new EndpointDto("/api/v1/test", "com.example.TestController1", HttpMethod.GET, false);
    EndpointDto endpointDto2 =
        new EndpointDto("/api/v1/test", "com.example.TestController2", HttpMethod.GET, false);

    // when
    String hash1 = endpointHashProvider.getHash(endpointDto1);
    String hash2 = endpointHashProvider.getHash(endpointDto2);

    // then
    assertNotEquals(hash1, hash2);
  }

  @Test
  @DisplayName("Should generate different hashes for endpoints with different HTTP methods")
  void endpointHashEqualFalse3() {
    // given
    EndpointDto endpointDto1 =
        new EndpointDto("/api/v1/test1", "com.example.TestController", HttpMethod.GET, false);
    EndpointDto endpointDto2 =
        new EndpointDto("/api/v1/test", "com.example.TestController", HttpMethod.POST, false);

    // when
    String hash1 = endpointHashProvider.getHash(endpointDto1);
    String hash2 = endpointHashProvider.getHash(endpointDto2);

    // then
    assertNotEquals(hash1, hash2);
  }

  @ParameterizedTest
  @EnumSource(ApiParameterType.class)
  @DisplayName("Should generate identical hashes for identical parameters")
  void endpointParamHashEqualTrueTest(ApiParameterType apiParameterType) {
    // given
    EndpointDto endpointDto =
        new EndpointDto("/api/v1/test", "com.example.TestController", HttpMethod.GET, false);
    EndpointParameterDto endpointParameterDto1 = new EndpointParameterDto("param1", apiParameterType);
    EndpointParameterDto endpointParameterDto2 = new EndpointParameterDto("param2", apiParameterType);

    // when
    String hash1 = endpointHashProvider.getParamHash(endpointDto, endpointParameterDto1, endpointParameterDto2);
    String hash2 = endpointHashProvider.getParamHash(endpointDto, endpointParameterDto1, endpointParameterDto2);

    // then
    assertEquals(hash1, hash2);
  }

  @Test
  @DisplayName("Should generate different hashes for endpoints with different parameters")
  void endpointParamHashEqualFalse() {
    // given
    EndpointDto endpointDto =
        new EndpointDto("/api/v1/test", "com.example.TestController", HttpMethod.GET, false);
    EndpointParameterDto endpointParameterDto1 = new EndpointParameterDto("param1", ApiParameterType.QUERY);
    EndpointParameterDto endpointParameterDto2 = new EndpointParameterDto("param2", ApiParameterType.QUERY);

    // when
    String hash1 = endpointHashProvider.getParamHash(endpointDto, endpointParameterDto1);
    String hash2 = endpointHashProvider.getParamHash(endpointDto, endpointParameterDto2);

    // then
    assertNotEquals(hash1, hash2);
  }

  @Test
  @DisplayName("Should generate different hashes for endpoints with different parameter counts")
  void endpointParamHashEqualFalse2() {
    // given
    EndpointDto endpointDto =
        new EndpointDto("/api/v1/test", "com.example.TestController", HttpMethod.GET, false);
    EndpointParameterDto endpointParameterDto1 = new EndpointParameterDto("param1", ApiParameterType.QUERY);
    EndpointParameterDto endpointParameterDto2 = new EndpointParameterDto("param2", ApiParameterType.QUERY);

    // when
    String hash1 = endpointHashProvider.getParamHash(endpointDto, endpointParameterDto1);
    String hash2 = endpointHashProvider.getParamHash(endpointDto, endpointParameterDto1, endpointParameterDto2);

    // then
    assertNotEquals(hash1, hash2);
  }

  @Test
  @DisplayName("Should generate different parameter hashes for different endpoints")
  void endpointParamHashEqualFalse3() {
    // given
    EndpointDto endpointDto1 =
        new EndpointDto("/api/v1/test1", "com.example.TestController", HttpMethod.GET, false);
    EndpointDto endpointDto2 =
        new EndpointDto("/api/v1/test2", "com.example.TestController", HttpMethod.GET, false);
    EndpointParameterDto endpointParameterDto = new EndpointParameterDto("param1", ApiParameterType.QUERY);

    // when
    String hash1 = endpointHashProvider.getParamHash(endpointDto1, endpointParameterDto);
    String hash2 = endpointHashProvider.getParamHash(endpointDto2, endpointParameterDto);

    // then
    assertNotEquals(hash1, hash2);
  }
}
