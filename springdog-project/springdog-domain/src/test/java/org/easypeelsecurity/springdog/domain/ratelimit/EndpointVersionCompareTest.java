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

package org.easypeelsecurity.springdog.domain.ratelimit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.easypeelsecurity.springdog.domain.ratelimit.model.Endpoint;
import org.easypeelsecurity.springdog.shared.dto.EndpointDto;
import org.easypeelsecurity.springdog.shared.dto.EndpointParameterDto;
import org.easypeelsecurity.springdog.shared.enums.EndpointParameterType;
import org.easypeelsecurity.springdog.shared.enums.HttpMethod;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EndpointVersionCompareTest {

  @Test
  @DisplayName("Returns FIRST_RUN if there is no endpoint in the database.")
  void returnsFirstRunIfNoEndpoint() {
    // given
    List<EndpointDto> parsedEndpoints = new ArrayList<>(List.of(
        EndpointDto.builder()
            .path("/api/v1/test")
            .httpMethod(HttpMethod.GET)
            .methodSignature("void org.epsec.app.Test()")
            .parameters(Set.of(new EndpointParameterDto("param", EndpointParameterType.QUERY, true)))
            .build()
    ));

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
    List<EndpointDto> parsedEndpoints = new ArrayList<>(List.of(
        EndpointDto.builder()
            .path("/api/v1/test")
            .httpMethod(HttpMethod.GET)
            .methodSignature("void org.epsec.app.Test()")
            .parameters(Set.of(new EndpointParameterDto("param", EndpointParameterType.QUERY, true)))
            .build()
    ));

    Endpoint endpoint = new Endpoint();
    endpoint.setPath("/api/v1/test");
    endpoint.setHttpMethod(HttpMethod.GET.name());
    endpoint.setMethodSignature("void org.epsec.app.Test()");

    EndpointVersionCompare endpointVersionCompare =
        new EndpointVersionCompare(parsedEndpoints, new ArrayList<>(List.of(endpoint)));

    // when
    EndpointVersionCompareResult result = endpointVersionCompare.compare();

    // then
    assertThat(result).isEqualTo(EndpointVersionCompareResult.DIFFERENT);
  }

  @Test
  @DisplayName("Returns DIFFERENT when endpoint paths do not match.")
  void returnsDifferentWhenEndpointPathsDoNotMatch() {
    // given
    List<EndpointDto> parsedEndpoints = new ArrayList<>(List.of(
        EndpointDto.builder()
            .path("/api/v1/test")
            .httpMethod(HttpMethod.GET)
            .methodSignature("void org.epsec.app.Test()")
            .build()
    ));

    Endpoint endpoint = new Endpoint();
    endpoint.setPath("/api/v2/different-path");
    endpoint.setHttpMethod(HttpMethod.GET.name());
    endpoint.setMethodSignature("void org.epsec.app.Test()");

    EndpointVersionCompare endpointVersionCompare =
        new EndpointVersionCompare(parsedEndpoints, new ArrayList<>(List.of(endpoint)));

    // when
    EndpointVersionCompareResult result = endpointVersionCompare.compare();

    // then
    assertThat(result).isEqualTo(EndpointVersionCompareResult.DIFFERENT);
  }

  @Test
  @DisplayName("Returns DIFFERENT when HTTP method does not match.")
  void returnsDifferentWhenHttpMethodDoesNotMatch() {
    // given
    List<EndpointDto> parsedEndpoints = new ArrayList<>(List.of(
        EndpointDto.builder()
            .path("/api/v1/test")
            .httpMethod(HttpMethod.GET)
            .methodSignature("void org.epsec.app.Test()")
            .build()
    ));

    Endpoint endpoint = new Endpoint();
    endpoint.setPath("/api/v1/test");
    endpoint.setHttpMethod(HttpMethod.POST.name());
    endpoint.setMethodSignature("void org.epsec.app.Test()");

    EndpointVersionCompare endpointVersionCompare =
        new EndpointVersionCompare(parsedEndpoints, new ArrayList<>(List.of(endpoint)));

    // when
    EndpointVersionCompareResult result = endpointVersionCompare.compare();

    // then
    assertThat(result).isEqualTo(EndpointVersionCompareResult.DIFFERENT);
  }

  @Test
  @DisplayName("Returns DIFFERENT when Method Signature does not match.")
  void returnsDifferentWhenMethodSignatureDoesNotMatch() {
    List<EndpointDto> parsedEndpoints = new ArrayList<>(List.of(
        EndpointDto.builder()
            .path("/api/v1/test")
            .httpMethod(HttpMethod.GET)
            .methodSignature("void org.epsec.app.Test()")
            .build()
    ));

    Endpoint endpoint = new Endpoint();
    endpoint.setPath("/api/v1/test");
    endpoint.setHttpMethod(HttpMethod.GET.name());
    endpoint.setMethodSignature("void org.epsec.app.DifferentMethod()");
    List<Endpoint> onDatabase = new ArrayList<>();
    onDatabase.add(endpoint);

    EndpointVersionCompare endpointVersionCompare = new EndpointVersionCompare(parsedEndpoints, onDatabase);

    // when
    EndpointVersionCompareResult result = endpointVersionCompare.compare();

    // then
    assertThat(result).isEqualTo(EndpointVersionCompareResult.DIFFERENT);
  }
}
