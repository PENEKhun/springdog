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

package org.easypeelsecurity.springdogtest.errortracing;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.easypeelsecurity.springdog.domain.errortracing.model.ExceptionListingRepository;

import org.apache.cayenne.ObjectContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@SpringBootTest
class ErrorTracingTest {

  @Autowired
  ExceptionListingRepository exceptionListingRepository;
  @Autowired
  ObjectContext context;

  @Test
  @DisplayName("Should parse exception classes from based package")
  void shouldExceptionClassesParsedWell() {
    // when
    var exceptionClasses = exceptionListingRepository.findAllExceptions(context);

    // then
    assertThat(exceptionClasses)
        .extracting("packageType")
        .containsAll(
            List.of(
                "org.easypeelsecurity.springdogtest",
                "java.lang",
                "java.util",
                "java.io",
                "java.net",
                "java.nio",
                "java.sql"
            )
        );
  }
}
