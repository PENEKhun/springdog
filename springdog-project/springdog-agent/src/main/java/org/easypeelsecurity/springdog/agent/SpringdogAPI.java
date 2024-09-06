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

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.easypeelsecurity.springdog.domain.errortracing.model.ExceptionListingService;

/**
 * RestController for springdog.
 */
@RestController
@SpringdogAgentController
@SuppressWarnings("checkstyle:MissingJavadocMethod")
public class SpringdogAPI {

  private final ExceptionListingService exceptionListingService;

  public SpringdogAPI(ExceptionListingService exceptionListingService) {
    this.exceptionListingService = exceptionListingService;
  }

  @GetMapping("/error-tracing/configuration/{exceptionClassId}")
  public void errorExceptionMonitoringStatus(
      @PathVariable(name = "exceptionClassId") long exceptionClassId,
      @RequestParam("enabled") boolean enabled) {
    exceptionListingService.changeMonitoringStatus(exceptionClassId, enabled);
  }
}
