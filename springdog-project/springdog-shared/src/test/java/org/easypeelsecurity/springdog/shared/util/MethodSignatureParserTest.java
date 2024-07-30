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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Method;

import org.springframework.web.method.HandlerMethod;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MethodSignatureParserTest {

  @Test
  @DisplayName("Should generate methodSignature for given HandlerMethod (including params)")
  void generateMethodSignatureForHandlerMethod() throws NoSuchMethodException {
    // given
    TestController1 controller = new TestController1();
    Method method = TestController1.class.getMethod("testMethod", String.class);
    HandlerMethod handlerMethod = new HandlerMethod(controller, method);

    // when
    String methodSignature = MethodSignatureParser.parse(handlerMethod);

    // then
    assertEquals(
        "void org.easypeelsecurity.springdog.shared.util.MethodSignatureParserTest$TestController1.testMethod(java.lang.String)",
        methodSignature);
  }

  @Test
  @DisplayName("Should generate methodSignature for given HandlerMethod (without params)")
  void generateMethodSignatureForHandlerMethodWithoutParams() throws NoSuchMethodException {
    // given
    TestController2 controller = new TestController2();
    Method method = TestController2.class.getMethod("method");
    HandlerMethod handlerMethod = new HandlerMethod(controller, method);

    // when
    String methodSignature = MethodSignatureParser.parse(handlerMethod);

    // then
    assertEquals("void org.easypeelsecurity.springdog.shared.util.MethodSignatureParserTest$TestController2.method()",
        methodSignature);
  }

  static class TestController1 {
    public void testMethod(String param) {
    }
  }

  final class TestController2 {

    public void method() {
    }
  }
}
