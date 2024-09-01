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

package org.easypeelsecurity.springdog.shared.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class AssertTest {

  @Nested
  class NotNullTests {
    @Test
    void shouldThrowExceptionWhenObjectIsNull() {
      assertThrows(IllegalArgumentException.class, () -> Assert.notNull(null, "Object should not be null"));
    }

    @Test
    void shouldNotThrowExceptionWhenObjectIsNotNull() {
      assertDoesNotThrow(() -> Assert.notNull("Not null", "Object should not be null"));
    }

    @Test
    void shouldThrowExceptionWithFormattedMessage() {
      String expectedMessage = "Object 'test' should not be null";
      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> Assert.notNull(null, "Object '%s' should not be null", "test"));
      assertEquals(expectedMessage, exception.getMessage());
    }
  }

  @Nested
  class HasTextTests {
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "\t", "\n"})
    void shouldThrowExceptionWhenStringIsNullOrEmpty(String input) {
      assertThrows(IllegalArgumentException.class, () -> Assert.hasText(input, "String should have text"));
    }

    @Test
    void shouldNotThrowExceptionWhenStringHasText() {
      assertDoesNotThrow(() -> Assert.hasText("Not empty", "String should have text"));
    }

    @Test
    void shouldThrowExceptionWithFormattedMessage() {
      String expectedMessage = "String 'test' should have text";
      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> Assert.hasText("", "String '%s' should have text", "test"));
      assertEquals(expectedMessage, exception.getMessage());
    }
  }

  @Nested
  class NotEmptyArrayTests {
    @Test
    void shouldThrowExceptionWhenArrayIsNull() {
      assertThrows(IllegalArgumentException.class,
          () -> Assert.notEmpty((Object[]) null, "Array should not be empty"));
    }

    @Test
    void shouldThrowExceptionWhenArrayIsEmpty() {
      assertThrows(IllegalArgumentException.class,
          () -> Assert.notEmpty(new Object[] {}, "Array should not be empty"));
    }

    @Test
    void shouldNotThrowExceptionWhenArrayIsNotEmpty() {
      assertDoesNotThrow(() -> Assert.notEmpty(new Object[] {"Not empty"}, "Array should not be empty"));
    }

    @Test
    void shouldThrowExceptionWithFormattedMessage() {
      String expectedMessage = "Array 'test' should not be empty";
      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> Assert.notEmpty(new Object[] {}, "Array '%s' should not be empty", "test"));
      assertEquals(expectedMessage, exception.getMessage());
    }
  }

  @Nested
  class IsTrueTests {
    @Test
    void shouldThrowExceptionWhenExpressionIsFalse() {
      assertThrows(IllegalArgumentException.class, () -> Assert.isTrue(false, "Expression should be true"));
    }

    @Test
    void shouldNotThrowExceptionWhenExpressionIsTrue() {
      assertDoesNotThrow(() -> Assert.isTrue(true, "Expression should be true"));
    }

    @Test
    void shouldThrowExceptionWithFormattedMessage() {
      String expectedMessage = "Expression 'test' should be true";
      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> Assert.isTrue(false, "Expression '%s' should be true", "test"));
      assertEquals(expectedMessage, exception.getMessage());
    }
  }

  @Nested
  class IsFalseTests {
    @Test
    void shouldThrowExceptionWhenExpressionIsTrue() {
      assertThrows(IllegalArgumentException.class, () -> Assert.isFalse(true, "Expression should be false"));
    }

    @Test
    void shouldNotThrowExceptionWhenExpressionIsFalse() {
      assertDoesNotThrow(() -> Assert.isFalse(false, "Expression should be false"));
    }
  }

  @Nested
  class NotEmptyStringTests {
    @Test
    void shouldThrowExceptionWhenStringIsNull() {
      assertThrows(IllegalArgumentException.class,
          () -> Assert.notEmpty((String) null, "String should not be empty"));
    }

    @Test
    void shouldThrowExceptionWhenStringIsEmpty() {
      assertThrows(IllegalArgumentException.class, () -> Assert.notEmpty("", "String should not be empty"));
    }

    @Test
    void shouldNotThrowExceptionWhenStringIsNotEmpty() {
      assertDoesNotThrow(() -> Assert.notEmpty("Not empty", "String should not be empty"));
    }

    @Test
    void shouldThrowExceptionWithFormattedMessage() {
      String expectedMessage = "String 'test' should not be empty";
      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> Assert.notEmpty("", "String '%s' should not be empty", "test"));
      assertEquals(expectedMessage, exception.getMessage());
    }
  }

  @Nested
  class NotEmptyIterableTests {
    @Test
    void shouldThrowExceptionWhenIterableIsNull() {
      assertThrows(IllegalArgumentException.class,
          () -> Assert.notEmpty((Iterable<?>) null, "Iterable should not be empty"));
    }

    @Test
    void shouldThrowExceptionWhenIterableIsEmpty() {
      assertThrows(IllegalArgumentException.class,
          () -> Assert.notEmpty(Collections.emptyList(), "Iterable should not be empty"));
    }

    @Test
    void shouldNotThrowExceptionWhenIterableIsNotEmpty() {
      assertDoesNotThrow(() -> Assert.notEmpty(Arrays.asList("Not empty"), "Iterable should not be empty"));
    }

    @Test
    void shouldThrowExceptionWithFormattedMessage() {
      String expectedMessage = "Iterable 'test' should not be empty";
      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> Assert.notEmpty(Collections.emptyList(), "Iterable '%s' should not be empty", "test"));
      assertEquals(expectedMessage, exception.getMessage());
    }
  }

  @Nested
  class IsNotEqualTests {
    @Test
    void shouldThrowExceptionWhenObjectsAreEqual() {
      assertThrows(IllegalArgumentException.class,
          () -> Assert.isNotEqual("Same", "Same", "Objects should not be equal"));
    }

    @Test
    void shouldNotThrowExceptionWhenObjectsAreNotEqual() {
      assertDoesNotThrow(() -> Assert.isNotEqual("Different1", "Different2", "Objects should not be equal"));
    }
  }

  @Nested
  class IsEqualTests {
    @Test
    void shouldThrowExceptionWhenObjectsAreNotEqual() {
      assertThrows(IllegalArgumentException.class,
          () -> Assert.isEqual("Different1", "Different2", "Objects should be equal"));
    }

    @Test
    void shouldNotThrowExceptionWhenObjectsAreEqual() {
      assertDoesNotThrow(() -> Assert.isEqual("Same", "Same", "Objects should be equal"));
    }
  }
}
