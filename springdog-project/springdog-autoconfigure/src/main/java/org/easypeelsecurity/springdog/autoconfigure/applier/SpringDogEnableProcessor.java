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
import java.util.HashMap;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Generated;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

import org.easypeelsecurity.springdog.agent.SpringdogAgentView;
import org.easypeelsecurity.springdog.autoconfigure.PropertiesLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import jakarta.annotation.PostConstruct;

/**
 * Processes the SpringDogEnable annotation to enable configurations necessary for SpringDog functionalities.
 */
public class SpringDogEnableProcessor extends AbstractProcessor {

  private static final String DATABASE_URL =
      "jdbc:derby:springdog-embedded-database;create=true;";
  private static final String BANNER = """
            \s
            ███████ ██████  ██████  ██ ███    ██  ██████  ██████   ██████   ██████ \s
            ██      ██   ██ ██   ██ ██ ████   ██ ██       ██   ██ ██    ██ ██      \s
            ███████ ██████  ██████  ██ ██ ██  ██ ██   ███ ██   ██ ██    ██ ██   ███\s
                 ██ ██      ██   ██ ██ ██  ██ ██ ██    ██ ██   ██ ██    ██ ██    ██\s
            ███████ ██      ██   ██ ██ ██   ████  ██████  ██████   ██████   ██████ \s
      """;
  private static final String ANNOTATION_DEFAULT_FIELD = "value";

  /**
   * Registers support for the SpringDogEnable annotation.
   */
  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return Set.of(SpringDogEnable.class.getName());
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    if (!roundEnv.processingOver() && !annotations.isEmpty()) {
      generateConfigurations(roundEnv);
      return true;
    }
    return false;
  }

  private void generateConfigurations(RoundEnvironment roundEnv) {
    roundEnv.getElementsAnnotatedWith(SpringDogEnable.class).forEach(element -> {
      String fullPackageName = element.getEnclosingElement().toString();
      PropertiesLoader propertiesLoader = new PropertiesLoader(processingEnv);
      String agentBasePath = propertiesLoader.getPropertyOrDefault("springdog.agent.basePath", "springdog");
      agentApplier(fullPackageName, SpringdogAgentView.class, agentBasePath);
      generateThymeleafConfig(fullPackageName);
      springdogManagerApplier(fullPackageName);
      springdogAgentSecurityApplier(fullPackageName);
      autoconfigurationBeanApplier(fullPackageName);
      startBannerPrinter(fullPackageName);
    });
  }

  private void agentApplier(String fullPackageName, Class controller, String basePath) {
    TypeSpec agentPathModifier = TypeSpec.classBuilder("SpringdogAgentView")
        .addAnnotation(Controller.class)
        .addModifiers(Modifier.PUBLIC)
        .superclass(controller)
        .addAnnotation(AnnotationSpec.builder(Generated.class)
            .addMember(ANNOTATION_DEFAULT_FIELD, "$S", "org.easypeelsecurity.springdog")
            .addMember("comments", "$S", "Springdog agent path modifier")
            .build())
        .addAnnotation(AnnotationSpec.builder(RequestMapping.class)
            .addMember(ANNOTATION_DEFAULT_FIELD, "$S", basePath)
            .build())
        .build();

    // See comments gh-42
    TypeSpec sharedApplier = TypeSpec.classBuilder("SpringdogSharedApplier")
        .addAnnotation(Configuration.class)
        .addAnnotation(AnnotationSpec.builder(ComponentScan.class)
            .addMember("basePackages", "$S", "org.easypeelsecurity.springdog.shared")
            .build())
        .addModifiers(Modifier.PUBLIC)
        .build();

    try {
      JavaFile.builder(fullPackageName, agentPathModifier)
          .build()
          .writeTo(processingEnv.getFiler());
      JavaFile.builder(fullPackageName, sharedApplier)
          .build()
          .writeTo(processingEnv.getFiler());
    } catch (IOException e) {
      processingEnv.getMessager()
          .printMessage(Kind.ERROR, "Error writing SpringdogAgentEnabler: " + e.getMessage());
    }
  }

  private void startBannerPrinter(String fullPackageName) {
    MethodSpec initMethod = MethodSpec.methodBuilder("init")
        .addAnnotation(PostConstruct.class)
        .addModifiers(Modifier.PUBLIC)
        .addStatement("System.out.println($S)", BANNER)
        .addStatement("logger.info(\"Springdog was fully loaded.\")")
        .build();

    TypeSpec bannerPrinter = TypeSpec.classBuilder("SpringdogBannerPrinter")
        .addAnnotation(AnnotationSpec.builder(Component.class)
            .addMember(ANNOTATION_DEFAULT_FIELD, "$S", "springdogBannerPrinter")
            .build())
        .addAnnotation(AnnotationSpec.builder(Generated.class)
            .addMember(ANNOTATION_DEFAULT_FIELD, "$S", "org.easypeelsecurity.springdog")
            .addMember("comments", "$S", "Springdog banner printer")
            .build())
        .addModifiers(Modifier.PUBLIC)
        .addField(FieldSpec.builder(Logger.class, "logger", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
            .initializer("$T.getLogger($T.class)", LoggerFactory.class, SpringDogEnableProcessor.class)
            .build())
        .addMethod(initMethod)
        .build();

    try {
      JavaFile.builder(fullPackageName, bannerPrinter)
          .build()
          .writeTo(processingEnv.getFiler());
    } catch (IOException e) {
      processingEnv.getMessager()
          .printMessage(Kind.ERROR, "Error writing SpringdogBannerPrinter: " + e.getMessage());
    }
  }

  private void springdogManagerApplier(String fullPackageName) {
    TypeSpec managerApplier = TypeSpec.classBuilder("SpringdogManagerApplier")
        .addAnnotation(Configuration.class)
        .addAnnotation(AnnotationSpec.builder(ComponentScan.class)
            .addMember("basePackages", "$S", "org.easypeelsecurity.springdog.manager")
            .build())
        .addModifiers(Modifier.PUBLIC)
        .build();

    try {
      JavaFile.builder(fullPackageName, managerApplier)
          .build()
          .writeTo(processingEnv.getFiler());
    } catch (IOException e) {
      processingEnv.getMessager()
          .printMessage(Kind.ERROR, "Error writing SpringdogManagerApplier: " + e.getMessage());
    }
  }

  private void springdogAgentSecurityApplier(String fullPackageName) {
    TypeSpec managerApplier = TypeSpec.classBuilder("SpringdogAgentSecurityApplier")
        .addAnnotation(Configuration.class)
        .addAnnotation(AnnotationSpec.builder(ComponentScan.class)
            .addMember("basePackages", "$S", "org.easypeelsecurity.springdog.agent.security")
            .build())
        .addModifiers(Modifier.PUBLIC)
        .build();

    try {
      JavaFile.builder(fullPackageName, managerApplier)
          .build()
          .writeTo(processingEnv.getFiler());
    } catch (IOException e) {
      processingEnv.getMessager()
          .printMessage(Kind.ERROR, "Error writing SpringdogAgentSecurityApplier: " + e.getMessage());
    }
  }

  private void generateThymeleafConfig(String fullPackageName) {
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

    TypeSpec springDogDynamicTemplateResolver = TypeSpec.classBuilder("SpringDogDynamicTemplateResolver")
        .addAnnotation(Configuration.class)
        .addAnnotation(AnnotationSpec.builder(Generated.class)
            .addMember(ANNOTATION_DEFAULT_FIELD, "$S",
                "org.easypeelsecurity.springdog.autoconfigure.applier.SpringDogEnableProcessor")
            .build())
        .addModifiers(Modifier.PUBLIC)
        .addField(FieldSpec.builder(SpringTemplateEngine.class, "templateEngine", Modifier.PRIVATE)
            .addAnnotation(Autowired.class)
            .build())
        .addField(FieldSpec.builder(ApplicationContext.class, "applicationContext", Modifier.PRIVATE)
            .addAnnotation(Autowired.class)
            .build())
        .addMethod(initMethod)
        .build();

    try {
      JavaFile.builder(fullPackageName, springDogDynamicTemplateResolver)
          .build()
          .writeTo(processingEnv.getFiler());
    } catch (IOException e) {
      processingEnv.getMessager()
          .printMessage(javax.tools.Diagnostic.Kind.ERROR,
              "Error writing Thymeleaf configuration: " + e.getMessage());
    }
  }

  private void autoconfigurationBeanApplier(String fullPackageName) {
    TypeSpec autoconfigurationBeanApplier = TypeSpec.classBuilder("SpringdogAutoconfigurationBeanApplier")
        .addAnnotation(Configuration.class)
        .addAnnotation(AnnotationSpec.builder(ComponentScan.class)
            .addMember("basePackages", "$S", "org.easypeelsecurity.springdog.autoconfigure")
            .build())
        .addModifiers(Modifier.PUBLIC)
        .build();

    try {
      JavaFile.builder(fullPackageName, autoconfigurationBeanApplier)
          .build()
          .writeTo(processingEnv.getFiler());
    } catch (IOException e) {
      processingEnv.getMessager()
          .printMessage(Kind.ERROR, "Error writing SpringdogAutoconfigurationBeanApplier: " + e.getMessage());
    }
  }

  /**
   * Defines Hibernate properties.
   */
  private HashMap<String, String> hibernateProperties() {
    HashMap<String, String> properties = new HashMap<>();
    properties.put("hibernate.hbm2ddl.auto", "update");
    properties.put("hibernate.show_sql", "true");
    return properties;
  }
}
