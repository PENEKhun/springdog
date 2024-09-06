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

package org.easypeelsecurity.springdog.manager.util;

import org.easypeelsecurity.springdog.agent.SpringdogAgentController;

/**
 * Utility class for request handler.
 */
public abstract class RequestHandlerUtil {

  /**
   * Check if the request should be skipped.
   * for example, error controller and our agent should be skipped.
   */
  public static boolean shouldSkipRequest(Class<?> controllerClass) {
    return controllerClass.equals(
        org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController.class) ||
        controllerClass.isAnnotationPresent(SpringdogAgentController.class);
  }
}
