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

package org.easypeelsecurity.springdog.agent;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.junit.jupiter.api.Test;

class CommonResponseTest {

  @Test
  void testCommonResponseWithDetailOnly() {
    String detail = "Test Detail";
    CommonResponse<String> response = new CommonResponse<>(detail);

    assertThat(response.getDetail()).isEqualTo(detail);
    assertThat(response.getMessage()).isEqualTo("The request was successfully processed.");
    assertThat(response.getResult()).isEqualTo("SUCCESS");
    assertThat(response.getTimestamp()).isBeforeOrEqualTo(LocalDateTime.now());
  }

  @Test
  void testCommonResponseWithAllParameters() {
    String detail = "Test Detail";
    String message = "Test Message";
    ResponseStatus status = ResponseStatus.FAILURE;

    CommonResponse<String> response = new CommonResponse<>(detail, message, status);

    assertThat(response.getDetail()).isEqualTo(detail);
    assertThat(response.getMessage()).isEqualTo(message);
    assertThat(response.getResult()).isEqualTo("FAILURE");
    assertThat(response.getTimestamp()).isBeforeOrEqualTo(LocalDateTime.now());
  }

  @Test
  void testResponseError() {
    String errorMessage = "Error occurred";
    ResponseEntity<CommonResponse<Object>> responseEntity = CommonResponse.responseError(errorMessage);

    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(responseEntity.getBody()).isNotNull();
    assertThat(responseEntity.getBody().getDetail()).isNull();
    assertThat(responseEntity.getBody().getMessage()).isEqualTo(errorMessage);
    assertThat(responseEntity.getBody().getResult()).isEqualTo("FAILURE");
  }

  @Test
  void testResponseErrorWithException() {
    String errorMessage = "Error occurred";
    Exception exception = new RuntimeException("Test exception");
    ResponseEntity<CommonResponse<Object>> responseEntity =
        CommonResponse.responseError(errorMessage, exception);

    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(responseEntity.getBody()).isNotNull();
    assertThat(responseEntity.getBody().getDetail()).isNull();
    assertThat(responseEntity.getBody().getMessage()).isEqualTo(errorMessage + " Test exception");
    assertThat(responseEntity.getBody().getResult()).isEqualTo("FAILURE");
  }
}

