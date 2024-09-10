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

package org.easypeelsecurity.springdog.manager.errortracing;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.easypeelsecurity.springdog.domain.errortracing.model.ExceptionListingService;
import org.easypeelsecurity.springdog.shared.dto.ErrorTracingDto;

/**
 * Springdog exception collector.
 */
@ControllerAdvice
public class SpringdogExceptionCollector {
  private final ExceptionListingService exceptionListingService;

  /**
   * Constructor.
   */
  public SpringdogExceptionCollector(ExceptionListingService exceptionListingService) {
    this.exceptionListingService = exceptionListingService;
  }

  @SuppressWarnings("checkstyle:MissingJavadocMethod")
  @ExceptionHandler(Exception.class)
  public void collectException(Exception e) {
    String exceptionClassName = e.getClass().getName();
    if (!exceptionListingService.isExceptionEnabled(exceptionClassName)) {
      return;
    }

    ErrorTracingDto errorChain = buildErrorChain(e);
    exceptionListingService.saveException(errorChain);
  }

  private ErrorTracingDto buildErrorChain(Throwable throwable) {
    ErrorTracingDto currentDto = null;
    ErrorTracingDto rootDto = null;

    while (throwable != null) {
      StackTraceElement[] stackTrace = throwable.getStackTrace();
      StackTraceElement element = stackTrace.length > 0 ? stackTrace[0] : null;

      if (element == null) {
        break;
      }
      ErrorTracingDto newDto = ErrorTracingDto.builder()
          .message(throwable.getMessage())
          .fileName(element.getFileName())
          .className(element.getClassName())
          .methodName(element.getMethodName())
          .lineNumber(element.getLineNumber())
          .build();

      if (currentDto == null) {
        rootDto = newDto;
      } else {
        currentDto.setNext(newDto);
      }
      currentDto = newDto;

      throwable = throwable.getCause();
    }

    return rootDto;
  }
}
