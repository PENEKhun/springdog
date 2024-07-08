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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;

import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;

/**
 * Adds configuration to ensure that the Thymeleaf used for the agent does not conflict with the Thymeleaf
 * (optional) used in the user application.
 */
public class SpringdogDynamicTemplateResolver extends CodeGenerator {

  @Override
  public Builder typeSpec() {
    MethodSpec initMethod = MethodSpec.methodBuilder("init")
        .addAnnotation(EventListener.class)
        .addAnnotation(AnnotationSpec.builder(Order.class).addMember(ANNOTATION_DEFAULT_FIELD, "$L", 1).build())
        .addParameter(ContextRefreshedEvent.class, "event")
        .addModifiers(Modifier.PUBLIC)
        .addStatement("$T templateResolver = new $T()",
            SpringResourceTemplateResolver.class, SpringResourceTemplateResolver.class)
        .addStatement("templateResolver.setPrefix(\"classpath:\")")
        .addStatement("templateResolver.setSuffix(\".html\")")
        .addStatement("templateResolver.setTemplateMode($T.HTML)", TemplateMode.class)
        .addStatement("templateResolver.setCharacterEncoding(\"UTF-8\")")
        .addStatement("templateResolver.setOrder($T.MAX_VALUE - 1)", Integer.class)
        .addStatement("templateResolver.setCheckExistence(true)")
        .addStatement("templateResolver.setApplicationContext(applicationContext)")
        .addStatement("templateEngine.addTemplateResolver(templateResolver)")
        .build();

    return TypeSpec.classBuilder("SpringdogDynamicTemplateResolver")
        .addAnnotation(Configuration.class)
        .addModifiers(Modifier.PUBLIC)
        .addField(FieldSpec.builder(SpringTemplateEngine.class, "templateEngine", Modifier.PRIVATE)
            .addAnnotation(Autowired.class)
            .build())
        .addField(FieldSpec.builder(ApplicationContext.class, "applicationContext", Modifier.PRIVATE)
            .addAnnotation(Autowired.class)
            .build())
        .addMethod(initMethod);
  }
}
