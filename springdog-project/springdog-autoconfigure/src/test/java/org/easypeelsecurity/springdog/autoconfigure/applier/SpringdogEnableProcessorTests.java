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

package org.easypeelsecurity.springdog.autoconfigure.applier;

import static com.google.testing.compile.Compiler.javac;

import javax.tools.JavaFileObject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.CompilationSubject;
import com.google.testing.compile.JavaFileObjects;

class SpringdogEnableProcessorTests {

  @ParameterizedTest
  @DisplayName("Should generate code correctly with @SpringdogEnable annotation.")
  @CsvSource({
      "test.annotation.springdog.SpringdogAgentApplier",
      "test.annotation.springdog.SpringdogAutoConfigurationApplier",
      "test.annotation.springdog.SpringdogBannerPrinter",
      "test.annotation.springdog.SpringdogDynamicTemplateResolver",
      "test.annotation.springdog.SpringdogManagerApplier",
      "test.annotation.springdog.SpringdogSharedApplier",
  })
  void shouldGenerateCodeWithSpringdogEnable(String qualifiedName) {
    // given
    JavaFileObject src = JavaFileObjects.forResource("WithSpringdogEnableAnnotation.java");

    // when
    Compilation compilation = javac()
        .withProcessors(new SpringDogEnableProcessor())
        .compile(src);

    // then
    CompilationSubject.assertThat(compilation)
        .generatedSourceFile(qualifiedName);
  }
}
