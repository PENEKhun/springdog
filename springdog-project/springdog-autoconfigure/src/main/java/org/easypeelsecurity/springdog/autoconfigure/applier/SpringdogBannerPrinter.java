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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;

import jakarta.annotation.PostConstruct;

/**
 * Displays a banner when the classes required to run Springdog are loaded.
 */
public class SpringdogBannerPrinter extends CodeGenerator {

  private static final String BANNER = """
            \s
            ███████ ██████  ██████  ██ ███    ██  ██████  ██████   ██████   ██████ \s
            ██      ██   ██ ██   ██ ██ ████   ██ ██       ██   ██ ██    ██ ██      \s
            ███████ ██████  ██████  ██ ██ ██  ██ ██   ███ ██   ██ ██    ██ ██   ███\s
                 ██ ██      ██   ██ ██ ██  ██ ██ ██    ██ ██   ██ ██    ██ ██    ██\s
            ███████ ██      ██   ██ ██ ██   ████  ██████  ██████   ██████   ██████ \s
      """;

  @Override
  public Builder typeSpec() {
    MethodSpec initMethod = MethodSpec.methodBuilder("init")
        .addAnnotation(PostConstruct.class)
        .addModifiers(Modifier.PUBLIC)
        .addStatement("System.out.println($S)", BANNER)
        .build();

    return TypeSpec.classBuilder("SpringdogBannerPrinter")
        .addAnnotation(AnnotationSpec.builder(Component.class)
            .addMember(ANNOTATION_DEFAULT_FIELD, "$S", "springdogBannerPrinter")
            .build())

        .addModifiers(Modifier.PUBLIC)
        .addField(FieldSpec.builder(Logger.class, "logger", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
            .initializer("$T.getLogger($T.class)", LoggerFactory.class, SpringDogEnableProcessor.class)
            .build())
        .addMethod(initMethod);
  }
}
