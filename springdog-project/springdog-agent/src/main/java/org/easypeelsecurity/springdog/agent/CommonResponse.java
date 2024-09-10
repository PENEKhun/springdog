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

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Common response.
 *
 * @param <T> The type of the response detail
 */
public class CommonResponse<T> {

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private final LocalDateTime timestamp = LocalDateTime.now();
  private final T detail;
  private final String message;
  private final ResponseStatus result;

  /**
   * Constructor.
   */
  public CommonResponse(T detail, String message, ResponseStatus result) {
    this.detail = detail;
    this.message = message;
    this.result = result;
  }

  /**
   * Constructor for success response.
   *
   * @param detail The response detail
   */
  public CommonResponse(T detail) {
    this(detail, "The request was successfully processed.", ResponseStatus.SUCCESS);
  }

  /**
   * Create a error response.
   *
   * @param message The error message
   * @return The error response
   */
  public static <T> ResponseEntity<CommonResponse<T>> responseError(String message) {
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(new CommonResponse<>(
            null,
            message,
            ResponseStatus.FAILURE));
  }

  /**
   * Create a error response with exception's message.
   *
   * @param message The error message
   * @param e       The exception to contain the message
   * @return The error response
   */
  public static <T> ResponseEntity<CommonResponse<T>> responseError(String message, Exception e) {
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(new CommonResponse<>(
            null,
            message + " " + e.getLocalizedMessage(),
            ResponseStatus.FAILURE));
  }

  /**
   * Get the timestamp.
   */
  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  /**
   * Get the response detail.
   */
  public T getDetail() {
    return detail;
  }

  /**
   * Get the response message.
   *
   * @return The message
   */
  public String getMessage() {
    return message;
  }

  /**
   * Get the response status.
   *
   * @return The status
   */
  public String getResult() {
    return result.name();
  }
}
