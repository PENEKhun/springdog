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

package org.easypeelsecurity.springdog.shared.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for error tracing.
 */
@Getter
public class ErrorTracingDto {
  private final Long id;
  private final String message;
  private final String fileName;
  private final String className;
  private final String methodName;
  private final int lineNumber;
  @Setter
  private Long parentTraceId;
  @Setter
  private ErrorTracingDto next;
  private final LocalDateTime timestamp;

  /**
   * Constructor.
   */
  @Builder
  public ErrorTracingDto(Long id, String message, String fileName, String className, String methodName,
      int lineNumber, Long parentTraceId, ErrorTracingDto next, LocalDateTime timestamp) {
    this.id = id;
    if (message == null) {
      message = "Unknown message";
    }
    this.message = message;
    if (fileName == null) {
      fileName = "Unknown file name";
    }
    this.fileName = fileName;
    if (className == null) {
      className = "Unknown class name";
    }
    this.className = className;
    if (methodName == null) {
      methodName = "Unknown method name";
    }
    this.methodName = methodName;
    this.lineNumber = lineNumber;
    this.parentTraceId = parentTraceId;
    this.next = next;
    this.timestamp = timestamp;
  }

  @Override
  public String toString() {
    return "ErrorTracingDto{" +
        "id=" + id +
        ", message='" + message + '\'' +
        ", fileName='" + fileName + '\'' +
        ", className='" + className + '\'' +
        ", methodName='" + methodName + '\'' +
        ", lineNumber=" + lineNumber +
        ", next=" + next +
        ", timestamp=" + timestamp +
        '}';
  }
}
