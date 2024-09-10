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

import static org.easypeelsecurity.springdog.agent.CommonResponse.responseError;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Springdog API Exception Handler.
 */
abstract class SpringdogAPIExceptionHandler {
  /**
   * If an Exception related invalid input is thrown, return an error message conforming to the Response
   * specification.
   */
  @org.springframework.web.bind.annotation.ResponseStatus(BAD_REQUEST)
  @ExceptionHandler({
      MethodArgumentNotValidException.class,
      IllegalArgumentException.class,
      HttpMessageNotReadableException.class})
  protected ResponseEntity<CommonResponse<Void>> handleInvalidInput(Exception e) {
    return responseError("Invalid input value.", e);
  }

  /**
   * If an IllegalStateException is thrown, return an error message conforming to the Response specification.
   */
  @org.springframework.web.bind.annotation.ResponseStatus(BAD_REQUEST)
  @ExceptionHandler(value = {IllegalStateException.class})
  protected ResponseEntity<CommonResponse<Void>> handleIllegalStateException(IllegalStateException e) {
    return responseError("This is an inappropriate call.", e);
  }
}
