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

import java.lang.reflect.Parameter;

import org.springframework.web.method.HandlerMethod;

/**
 * Utility class for parsing Method Signature from HandlerMethod instances.
 * This class provides functionality to obtain a string representation of a method's full
 * signature including return type, class name, method name, and parameter types.
 *
 * @author PENEKhun
 */
public abstract class MethodSignatureParser {

  /**
   * Parse methodSignature for the given HandlerMethod.
   * (e.g. "void
   * org.easypeelsecurity.springdog.TestController.testMethod(java.lang.String)")
   *
   * @param method the HandlerMethod to parse
   * @return a string representing the fully qualified method name
   */
  public static String parse(HandlerMethod method) {
    String returnType = method.getMethod().getReturnType().getName();
    String fullClassName = method.getBeanType().getName();
    StringBuilder result =
        new StringBuilder(returnType + " " + fullClassName + "." + method.getMethod().getName() + "(");
    StringBuilder parameterPart = new StringBuilder();
    for (Parameter parameter : method.getMethod().getParameters()) {
      parameterPart.append(parameter.getType().getName()).append(", ");
    }

    if (!parameterPart.isEmpty()) {
      result.append(parameterPart);
      result = new StringBuilder(result.substring(0, result.length() - 2));
    }

    result.append(")");
    return String.valueOf(result);
  }
}
