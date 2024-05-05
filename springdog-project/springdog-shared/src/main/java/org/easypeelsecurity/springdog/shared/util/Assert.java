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

/**
 * Assertion utility class that assists in validating arguments.
 */
@SuppressWarnings("checkstyle:MissingJavadocMethod")
public abstract class Assert {

  public static void notNull(Object object, String message) {
    if (object == null) {
      throw new IllegalArgumentException(message);
    }
  }

  public static void hasText(String text, String message) {
    if (text == null || text.trim().isEmpty()) {
      throw new IllegalArgumentException(message);
    }
  }

  public static void notEmpty(Object[] array, String message) {
    if (array == null || array.length == 0) {
      throw new IllegalArgumentException(message);
    }
  }

  public static void isTrue(boolean expression, String message) {
    if (!expression) {
      throw new IllegalArgumentException(message);
    }
  }

  public static void isFalse(boolean expression, String message) {
    if (expression) {
      throw new IllegalArgumentException(message);
    }
  }

  public static void notEmpty(String text, String message) {
    if (text == null || text.isEmpty()) {
      throw new IllegalArgumentException(message);
    }
  }

  public static void notEmpty(Iterable<?> iterable, String message) {
    if (iterable == null || !iterable.iterator().hasNext()) {
      throw new IllegalArgumentException(message);
    }
  }

  public static void notEmpty(String text, String message, Object... args) {
    if (text == null || text.isEmpty()) {
      throw new IllegalArgumentException(String.format(message, args));
    }
  }

  public static void notEmpty(Iterable<?> iterable, String message, Object... args) {
    if (iterable == null || !iterable.iterator().hasNext()) {
      throw new IllegalArgumentException(String.format(message, args));
    }
  }

  public static void notEmpty(Object[] array, String message, Object... args) {
    if (array == null || array.length == 0) {
      throw new IllegalArgumentException(String.format(message, args));
    }
  }

  public static void notNull(Object object, String message, Object... args) {
    if (object == null) {
      throw new IllegalArgumentException(String.format(message, args));
    }
  }

  public static void hasText(String text, String message, Object... args) {
    if (text == null || text.trim().isEmpty()) {
      throw new IllegalArgumentException(String.format(message, args));
    }
  }

  public static void isTrue(boolean expression, String message, Object... args) {
    if (!expression) {
      throw new IllegalArgumentException(String.format(message, args));
    }
  }

}
