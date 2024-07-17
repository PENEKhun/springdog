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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.Set;

import org.easypeelsecurity.springdog.shared.ratelimit.model.EndpointParameterType;
import org.easypeelsecurity.springdog.shared.ratelimit.model.HttpMethod;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EndpointVersionCompareTest {

  @Test
  @DisplayName("Returns FIRST_RUN if there is no endpoint in the database.")
  void returnsFirstRunIfNoEndpoint() {
    // given
    EndpointDto[] parsedEndpoints = new EndpointDto[] {
        new EndpointDto.Builder()
            .path("/api/v1/test")
            .httpMethod(HttpMethod.GET)
            .fqmn("org.epsec.app.Test")
            .build()
    };
    EndpointVersionCompare endpointVersionCompare = new EndpointVersionCompare(parsedEndpoints, null);

    // when
    EndpointVersionCompareResult result = endpointVersionCompare.compare();

    // then
    assertThat(result).isEqualTo(EndpointVersionCompareResult.FIRST_RUN);
  }

  @Test
  @DisplayName("Returns DIFFERENT when endpoint parameters do not match.")
  void returnsDifferentWhenEndpointParametersDoNotMatch() {
    // given
    EndpointDto[] parsedEndpoints = new EndpointDto[] {
        new EndpointDto.Builder()
            .path("/api/v1/test")
            .httpMethod(HttpMethod.GET)
            .fqmn("org.epsec.app.Test")
            .parameters(Set.of(new EndpointParameterDto("param", EndpointParameterType.QUERY, true)))
            .build()
    };

    EndpointDto[] onDatabase = new EndpointDto[] {
        new EndpointDto.Builder()
            .path("/api/v1/test")
            .httpMethod(HttpMethod.GET)
            .fqmn("org.epsec.app.Test")
            .parameters(Set.of(new EndpointParameterDto("differentName", EndpointParameterType.QUERY, true)))
            .build()
    };
    EndpointVersionCompare endpointVersionCompare = new EndpointVersionCompare(parsedEndpoints, onDatabase);

    // when
    EndpointVersionCompareResult result = endpointVersionCompare.compare();

    // then
    assertThat(result).isEqualTo(EndpointVersionCompareResult.DIFFERENT);
  }

  @Test
  @DisplayName("Returns DIFFERENT when endpoint paths do not match.")
  void returnsDifferentWhenEndpointPathsDoNotMatch() {
    // given
    EndpointDto[] parsedEndpoints = new EndpointDto[] {
        new EndpointDto.Builder()
            .path("/api/v1/test")
            .httpMethod(HttpMethod.GET)
            .fqmn("org.epsec.app.Test")
            .parameters(Set.of(new EndpointParameterDto("param", EndpointParameterType.QUERY, true)))
            .build()
    };

    EndpointDto[] onDatabase = new EndpointDto[] {
        new EndpointDto.Builder()
            .path("/api/v1/different/path")
            .httpMethod(HttpMethod.GET)
            .fqmn("org.epsec.app.Test")
            .parameters(Set.of(new EndpointParameterDto("param", EndpointParameterType.QUERY, true)))
            .build()
    };
    EndpointVersionCompare endpointVersionCompare = new EndpointVersionCompare(parsedEndpoints, onDatabase);

    // when
    EndpointVersionCompareResult result = endpointVersionCompare.compare();

    // then
    assertThat(result).isEqualTo(EndpointVersionCompareResult.DIFFERENT);
  }

  @Test
  @DisplayName("Returns DIFFERENT when HTTP method does not match.")
  void returnsDifferentWhenHttpMethodDoesNotMatch() {
    // given
    EndpointDto[] parsedEndpoints = new EndpointDto[] {
        new EndpointDto.Builder()
            .path("/api/v1/test")
            .httpMethod(HttpMethod.GET)
            .fqmn("org.epsec.app.Test")
            .parameters(Set.of(new EndpointParameterDto("param", EndpointParameterType.QUERY, true)))
            .build()
    };

    EndpointDto[] onDatabase = new EndpointDto[] {
        new EndpointDto.Builder()
            .path("/api/v1/test")
            .httpMethod(HttpMethod.POST)
            .fqmn("org.epsec.app.Test")
            .parameters(Set.of(new EndpointParameterDto("param", EndpointParameterType.QUERY, true)))
            .build()
    };
    EndpointVersionCompare endpointVersionCompare = new EndpointVersionCompare(parsedEndpoints, onDatabase);

    // when
    EndpointVersionCompareResult result = endpointVersionCompare.compare();

    // then
    assertThat(result).isEqualTo(EndpointVersionCompareResult.DIFFERENT);
  }

  @Test
  @DisplayName("Returns DIFFERENT when fully qualified method name (FQMN) does not match.")
  void returnsDifferentWhenFqmnDoesNotMatch() {
    // given
    EndpointDto[] parsedEndpoints = new EndpointDto[] {
        new EndpointDto.Builder()
            .path("/api/v1/test")
            .httpMethod(HttpMethod.GET)
            .fqmn("org.epsec.app.Test")
            .parameters(Set.of(new EndpointParameterDto("param", EndpointParameterType.QUERY, true)))
            .build()
    };

    EndpointDto[] onDatabase = new EndpointDto[] {
        new EndpointDto.Builder()
            .path("/api/v1/test")
            .httpMethod(HttpMethod.GET)
            .fqmn("org.epsec.app.AnotherTest")
            .parameters(Set.of(new EndpointParameterDto("param", EndpointParameterType.QUERY, true)))
            .build()
    };
    EndpointVersionCompare endpointVersionCompare = new EndpointVersionCompare(parsedEndpoints, onDatabase);

    // when
    EndpointVersionCompareResult result = endpointVersionCompare.compare();

    // then
    assertThat(result).isEqualTo(EndpointVersionCompareResult.DIFFERENT);
  }
}
