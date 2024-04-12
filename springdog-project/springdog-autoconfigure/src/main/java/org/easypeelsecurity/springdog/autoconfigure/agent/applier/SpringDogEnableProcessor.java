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

package org.easypeelsecurity.springdog.autoconfigure.agent.applier;

import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.tools.Diagnostic.Kind.ERROR;

import java.io.IOException;
import java.util.Set;

import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

import org.easypeelsecurity.springdog.agent.ViewStructures;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;
import jakarta.annotation.Generated;


/**
 * &#064;SpringDogEnable  Annotation Processor.
 *
 * @author PENEKhun
 */
public class SpringDogEnableProcessor extends AbstractProcessor {

  private boolean isAlreadyProcessed = false;

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return Set.of(SpringDogEnable.class.getName());
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    if (processingEnv.getSourceVersion().compareTo(SourceVersion.RELEASE_17) < 0) {
      processingEnv.getMessager().printMessage(ERROR, "Springdog is only supported in Java 17 or higher.");
    }

    return SourceVersion.latestSupported();
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(SpringDogEnable.class);
    for (Element ignored : elements) {
      if (isAlreadyProcessed) {
        super.processingEnv.getMessager()
            .printMessage(Kind.MANDATORY_WARNING, "@SpringDogEnable annotation is already processed.");
        return false;
      }

      Builder thymeleafResolver = generateThymeleafResolver();
      Builder accessibleControllerCode = generateControllerCode();
      String fullPackageName = elements.iterator().next().toString();
      super.processingEnv.getMessager()
              .printMessage(Kind.NOTE, fullPackageName);

      saveJavaFile(fullPackageName, thymeleafResolver);
      saveJavaFile(fullPackageName, accessibleControllerCode);
      super.processingEnv.getMessager()
          .printMessage(Kind.NOTE, "Processed @SpringDogEnable annotation successfully.");

      // TODO: 추가 필요한 구성들 동작 (DATABASE 설정, 등등...)이 런타임에 이뤄질 수 있도록
      isAlreadyProcessed = true;
    }

    return true;
  }

  private void saveJavaFile(String fullPackageName, Builder classSpec) {
    assert fullPackageName != null && !fullPackageName.isEmpty() : "package name is null or empty";
    assert classSpec != null : "classSpec is null";

    Filer filer = processingEnv.getFiler();
    String originalPackageName = fullPackageName.substring(0, fullPackageName.lastIndexOf("."));
    AnnotationSpec generatedAnnotation = AnnotationSpec.builder(Generated.class)
        .addMember("value", "$S", SpringDogEnable.class.getName()).build();
    try {
      JavaFile.builder(originalPackageName, classSpec.addAnnotation(generatedAnnotation).build())
          .build()
          .writeTo(filer);
    } catch (IOException e) {
      processingEnv.getMessager().printMessage(ERROR, "Fatal error, Can't save java file. "  + e.getMessage());
    }
  }

  private Builder generateControllerCode() {
    Builder controllerClass = TypeSpec.classBuilder("View")
        .addAnnotation(Controller.class)
        .addModifiers(PUBLIC);

    for (ViewStructures value : ViewStructures.values()) {
      MethodSpec method = MethodSpec.methodBuilder(value.name())
          .addAnnotation(
              AnnotationSpec.builder(GetMapping.class)
                  .addMember("value", "$S", value.getUrlPath())
                  .build())
          .addModifiers(PUBLIC)
          .returns(String.class)
          .addStatement("return $S", value.getResourcePath())
          .build();

      controllerClass.addMethod(method);
    }
    assert !controllerClass.methodSpecs.isEmpty() : "view method wasn't generated";
    return controllerClass;
  }

  private Builder generateThymeleafResolver() {
    MethodSpec templateEngineMethod = MethodSpec.methodBuilder("templateEngine")
        .addAnnotation(Bean.class)
        .returns(SpringTemplateEngine.class)
        .addModifiers(Modifier.PUBLIC)
        .addStatement("$T templateEngine = new $T()", SpringTemplateEngine.class,
            SpringTemplateEngine.class)
        .addStatement("templateEngine.setTemplateResolver(templateResolver())")
        .addStatement("templateEngine.addDialect(new $T())", LayoutDialect.class)
        .addStatement("return templateEngine")
        .build();

    MethodSpec templateResolverMethod = MethodSpec.methodBuilder("templateResolver")
        .addAnnotation(Bean.class)
        .returns(SpringResourceTemplateResolver.class)
        .addModifiers(Modifier.PUBLIC)
        .addStatement("$T templateResolver = new $T()", SpringResourceTemplateResolver.class,
            SpringResourceTemplateResolver.class)
        .addStatement("templateResolver.setPrefix(\"classpath:\")")
        .addStatement("templateResolver.setTemplateMode(\"HTML\")")
        .addStatement("return templateResolver")
        .build();

    return TypeSpec.classBuilder("SpringDogDashboardEnabler")
        .addAnnotation(Configuration.class)
        .addModifiers(Modifier.PUBLIC)
        .addMethod(templateEngineMethod)
        .addMethod(templateResolverMethod);
  }

}
