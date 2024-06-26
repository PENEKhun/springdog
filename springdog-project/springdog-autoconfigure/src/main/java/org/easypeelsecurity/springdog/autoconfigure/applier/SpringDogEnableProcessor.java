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
import java.util.Properties;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Generated;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.sql.DataSource;
import javax.tools.Diagnostic.Kind;

import org.easypeelsecurity.springdog.agent.SpringdogAgentView;
import org.easypeelsecurity.springdog.autoconfigure.PropertiesLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import jakarta.annotation.PostConstruct;
import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;

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
      generateJPAConfig(fullPackageName);
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

  private void generateJPAConfig(String fullPackageName) {
    MethodSpec springdogEntityManagerFactory = MethodSpec.methodBuilder("springdogEntityManagerFactory")
        .addAnnotation(Bean.class)
        .returns(LocalContainerEntityManagerFactoryBean.class)
        .addModifiers(Modifier.PUBLIC)
        .addStatement("$T em = new $T()", LocalContainerEntityManagerFactoryBean.class,
            LocalContainerEntityManagerFactoryBean.class)
        .addStatement("em.setDataSource(dataSource())")
        .addStatement("em.setPackagesToScan($S)", "org.easypeelsecurity.springdog.shared")
        .addStatement("$T vendorAdapter = new $T()", JpaVendorAdapter.class, HibernateJpaVendorAdapter.class)
        .addStatement("em.setJpaVendorAdapter(vendorAdapter)")
        .addStatement("em.setJpaProperties(additionalProperties())") // TODO: reference to MethodSpec
        .addStatement("return em")
        .build();

    MethodSpec dataSource = MethodSpec.methodBuilder("dataSource")
        .addAnnotation(Bean.class)
        .returns(DataSource.class)
        .addModifiers(Modifier.PUBLIC)
        .addStatement("$T dataSource = new $T()", DriverManagerDataSource.class, DriverManagerDataSource.class)
        .addStatement("dataSource.setDriverClassName($S)", "org.apache.derby.jdbc.EmbeddedDriver")
        // ddl-auto: create
        .addStatement("dataSource.setUrl($S)", DATABASE_URL)
        .addStatement("return dataSource")
        .build();

    MethodSpec.Builder tempAdditionalProperties = MethodSpec.methodBuilder("additionalProperties")
        .addModifiers(Modifier.PUBLIC)
        .returns(Properties.class)
        .addStatement("$T properties = new $T()", Properties.class, Properties.class);
    hibernateProperties().forEach(
        (key, value) -> tempAdditionalProperties.addStatement("properties.setProperty($S, $S)", key, value));
    MethodSpec additionalProperties = tempAdditionalProperties.addStatement("return properties").build();

    MethodSpec springdogTransactionManager = MethodSpec.methodBuilder("springdogTransactionManager")
        .addAnnotation(Bean.class)
        .returns(PlatformTransactionManager.class)
        .addModifiers(Modifier.PUBLIC)
        .addParameter(LocalContainerEntityManagerFactoryBean.class, "entityManagerFactory")
        .addStatement("$T jpaTransactionManager = new $T()", JpaTransactionManager.class,
            JpaTransactionManager.class)
        .addStatement("jpaTransactionManager.setEntityManagerFactory(entityManagerFactory.getObject())")
        .addStatement("return jpaTransactionManager")
        .build();

    TypeSpec jpaConfig = TypeSpec.classBuilder("JPAConfiguration")
        .addAnnotation(Configuration.class)
        .addAnnotation(AnnotationSpec.builder(EnableJpaRepositories.class)
            .addMember("basePackages", "$S", "org.easypeelsecurity.springdog.manager")
            .addMember("entityManagerFactoryRef", "$S", "springdogEntityManagerFactory")
            .addMember("transactionManagerRef", "$S", "springdogTransactionManager")
            .build())
        .addModifiers(Modifier.PUBLIC)
        .addMethod(springdogEntityManagerFactory)
        .addMethod(springdogTransactionManager)
        .addMethod(dataSource)
        .addMethod(additionalProperties)
        .build();

    try {
      JavaFile.builder(fullPackageName, jpaConfig)
          .build()
          .writeTo(processingEnv.getFiler());
    } catch (IOException e) {
      processingEnv.getMessager()
          .printMessage(Kind.ERROR, "Error writing datasource init codes : " + e.getMessage());
    }
  }

  private void generateThymeleafConfig(String fullPackageName) {
    MethodSpec templateEngineMethod = MethodSpec.methodBuilder("templateEngine")
        .addAnnotation(Bean.class)
        .returns(SpringTemplateEngine.class)
        .addModifiers(Modifier.PUBLIC)
        .addStatement("$T templateEngine = new $T()", SpringTemplateEngine.class,
            SpringTemplateEngine.class)
        .addStatement("templateEngine.setTemplateResolver(templateResolver())")
        .addStatement("templateEngine.addDialect(new $T())", LayoutDialect.class)
        .addStatement("templateEngine.setEnableSpringELCompiler(true)")
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

    TypeSpec agentEnabler = TypeSpec.classBuilder("SpringDogAgentEnabler")
        .addAnnotation(Configuration.class)
        .addModifiers(Modifier.PUBLIC)
        .addMethod(templateEngineMethod)
        .addMethod(templateResolverMethod)
        .build();

    try {
      JavaFile.builder(fullPackageName, agentEnabler)
          .build()
          .writeTo(processingEnv.getFiler());
    } catch (IOException e) {
      processingEnv.getMessager()
          .printMessage(Kind.ERROR, "Error writing Thymeleaf configuration: " + e.getMessage());
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
