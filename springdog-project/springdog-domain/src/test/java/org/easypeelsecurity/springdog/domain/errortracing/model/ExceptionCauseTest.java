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

package org.easypeelsecurity.springdog.domain.errortracing.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ExceptionCauseTest {

  @Test
  @DisplayName("Should return true when two ExceptionCause objects are equal")
  void equalsWhenObjectsAreEqual() {
    // given
    ExceptionCause cause1 = new ExceptionCause();
    cause1.setClassName("TestException");
    cause1.setFileName("TestFile.java");
    cause1.setLine(42);
    cause1.setMessage("Test message");
    cause1.setMethodName("testMethod");
    cause1.setTimestamp(LocalDateTime.now());
    cause1.setParentExceptionId(1L);
    cause1.setNextException(null);

    ExceptionCause cause2 = new ExceptionCause();
    cause2.setClassName("TestException");
    cause2.setFileName("TestFile.java");
    cause2.setLine(42);
    cause2.setMessage("Test message");
    cause2.setMethodName("testMethod");
    cause2.setTimestamp(cause1.getTimestamp());
    cause2.setParentExceptionId(1L);
    cause2.setNextException(null);

    // when
    boolean isEqual = cause1.equals(cause2);

    // then
    assertTrue(isEqual);
    assertEquals(cause1.hashCode(), cause2.hashCode());
  }

  @Test
  @DisplayName("Should return false when two ExceptionCause objects are not equal")
  void equalsWhenObjectsAreNotEqual() {
    // given
    ExceptionCause cause1 = new ExceptionCause();
    cause1.setClassName("TestException");
    cause1.setFileName("TestFile.java");
    cause1.setLine(42);
    cause1.setMessage("Test message");
    cause1.setMethodName("testMethod");
    cause1.setTimestamp(LocalDateTime.now());
    cause1.setParentExceptionId(1L);
    cause1.setNextException(null);

    ExceptionCause cause2 = new ExceptionCause();
    cause2.setClassName("AnotherException");
    cause2.setFileName("AnotherFile.java");
    cause2.setLine(99);
    cause2.setMessage("Different message");
    cause2.setMethodName("anotherMethod");
    cause2.setTimestamp(LocalDateTime.now());
    cause2.setParentExceptionId(2L);
    cause2.setNextException(null);

    // when
    boolean isEqual = cause1.equals(cause2);

    // then
    assertFalse(isEqual);
    assertNotEquals(cause1.hashCode(), cause2.hashCode());
  }

  @Test
  @DisplayName("Should handle null values and different types in equals()")
  void equalsWithNullAndDifferentTypes() {
    // given
    ExceptionCause cause = new ExceptionCause();
    cause.setClassName("TestException");
    cause.setFileName("TestFile.java");
    cause.setLine(42);
    cause.setMessage("Test message");
    cause.setMethodName("testMethod");
    cause.setTimestamp(LocalDateTime.now());
    cause.setParentExceptionId(1L);
    cause.setNextException(null);

    // when
    boolean isEqualToNull = cause.equals(null);
    boolean isEqualToDifferentType = cause.equals(new Object());

    // then
    assertFalse(isEqualToNull);
    assertFalse(isEqualToDifferentType);
  }

  @Test
  @DisplayName("Should correctly set and get all fields")
  void setAndGetFields() {
    // given
    ExceptionCause cause = new ExceptionCause();
    LocalDateTime timestamp = LocalDateTime.now();

    // when
    cause.setClassName("TestException");
    cause.setFileName("TestFile.java");
    cause.setLine(42);
    cause.setMessage("Test message");
    cause.setMethodName("testMethod");
    cause.setTimestamp(timestamp);
    cause.setParentExceptionId(1L);
    cause.setNextException(null);

    // then
    assertThat(cause.getClassName()).isEqualTo("TestException");
    assertThat(cause.getFileName()).isEqualTo("TestFile.java");
    assertThat(cause.getLine()).isEqualTo(42);
    assertThat(cause.getMessage()).isEqualTo("Test message");
    assertThat(cause.getMethodName()).isEqualTo("testMethod");
    assertThat(cause.getTimestamp()).isEqualTo(timestamp);
    assertThat(cause.getParentExceptionId()).isEqualTo(1L);
    assertThat(cause.getNextException()).isNull();
  }
}

