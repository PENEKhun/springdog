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

import javax.lang.model.element.Modifier;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import org.easypeelsecurity.springdog.agent.SpringdogAgentView;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;

/**
 * A class that generates code to enable component scanning for a specific package.
 */
public class SpringdogAgentApplier extends CodeGenerator {

  private final String basePath;

  @SuppressWarnings("checkstyle:MissingJavadocMethod")
  public SpringdogAgentApplier(String basePath) {
    this.basePath = basePath;
  }

  @Override
  public Builder typeSpec() {
    return TypeSpec.classBuilder("SpringdogAgentApplier")
        .addAnnotation(Controller.class)
        .addModifiers(Modifier.PUBLIC)
        .superclass(SpringdogAgentView.class)
        .addAnnotation(AnnotationSpec.builder(RequestMapping.class)
            .addMember(ANNOTATION_DEFAULT_FIELD, "$S", basePath)
            .build());
  }
}
