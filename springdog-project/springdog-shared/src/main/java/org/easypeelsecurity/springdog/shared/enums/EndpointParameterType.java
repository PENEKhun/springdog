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

package org.easypeelsecurity.springdog.shared.enums;

import java.lang.annotation.Annotation;

import jakarta.websocket.server.PathParam;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Endpoint parameter type.
 *
 * @author PENEKhun
 */
public enum EndpointParameterType {
  BODY,
  QUERY,
  MODEL,
  PATH;

  /**
   * Resolve parameter type from annotation. MUST BE USED IN CONTROLLER PARSER.
   *
   * @param annotation annotation
   * @return parameter type or {@code QUERY} if not found
   */
  public static EndpointParameterType resolve(Annotation[] annotation) {
    for (Annotation a : annotation) {
      if (a instanceof RequestBody) {
        return BODY;
      } else if (a instanceof RequestParam) {
        return QUERY;
      } else if (a instanceof PathVariable) {
        return PATH;
      } else if (a instanceof PathParam) {
        return PATH;
      } else if (a instanceof ModelAttribute) {
        return MODEL;
      }
    }
    return QUERY;
  }
}
