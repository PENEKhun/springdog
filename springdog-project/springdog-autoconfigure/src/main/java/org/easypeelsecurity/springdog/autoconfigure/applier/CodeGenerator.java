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

import java.io.IOException;

import javax.annotation.processing.Generated;
import javax.annotation.processing.ProcessingEnvironment;

import org.easypeelsecurity.springdog.shared.util.Assert;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;

/**
 * Base class for generating code.
 */
public abstract class CodeGenerator {

  static final String ANNOTATION_DEFAULT_FIELD = "value";

  /**
   * Returns the {@link Builder} for the generated class.
   *
   * @return the {@link Builder}
   * @throws UnsupportedOperationException if not implemented
   */
  public Builder typeSpec() {
    throw new UnsupportedOperationException("typeSpec() not implemented");
  }

  /**
   * Writes the generated code to a file.
   *
   * @param packageName   the package name
   * @param processingEnv the processing environment
   */
  public final void writeTo(String packageName, ProcessingEnvironment processingEnv) {
    Assert.hasText(packageName, "Package name must not be empty");
    Assert.notNull(processingEnv, "ProcessingEnvironment must not be null");
    Assert.isTrue(this.getClass().getSimpleName().equals(this.typeSpec().build().name),
        "TypeSpec name must match class name. " +
            "Expected %s, but was %s", this.getClass().getSimpleName(), this.typeSpec().build().name);

    try {
      JavaFile.builder(packageName, this.generate())
          .build()
          .writeTo(processingEnv.getFiler());
    } catch (IOException e) {
      processingEnv.getMessager().printMessage(javax.tools.Diagnostic.Kind.ERROR,
          "Error writing " + this.typeSpec() + " to file: " + e.getMessage());
    }
  }

  private TypeSpec generate() {
    for (AnnotationSpec annotation : typeSpec().build().annotations) {
      if (annotation.type.toString().equals(Generated.class.getName())) {
        throw new IllegalStateException("TypeSpec already contains a Generated annotation.");
      }
    }

    return typeSpec()
        .addAnnotation(AnnotationSpec.builder(Generated.class)
            .addMember(ANNOTATION_DEFAULT_FIELD, "$S", SpringDogEnable.class.getName())
            .build())
        .build();
  }
}
