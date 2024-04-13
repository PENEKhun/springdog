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

import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.sql.DataSource;
import javax.tools.Diagnostic.Kind;

import org.easypeelsecurity.springdog.agent.ViewStructures;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.GetMapping;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;
import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;

/**
 * Processes the SpringDogEnable annotation to enable configurations necessary for SpringDog functionalities.
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "org.easypeelsecurity.springdog.agent")
public class SpringDogEnableProcessor extends AbstractProcessor {

  private static final String DATABASE_URL = "jdbc:derby:springdog-embedded-database;create=true";

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
      generateThymeleafConfig(fullPackageName);
      generateJPAConfig(fullPackageName);
      generateController(fullPackageName);
    });
  }

  private void generateJPAConfig(String fullPackageName) {
    MethodSpec springdogEntityManagerFactory = MethodSpec.methodBuilder("springdogEntityManagerFactory")
        .addAnnotation(Bean.class)
        .returns(LocalContainerEntityManagerFactoryBean.class)
        .addModifiers(Modifier.PUBLIC)
        .addStatement("$T em = new $T()", LocalContainerEntityManagerFactoryBean.class,
            LocalContainerEntityManagerFactoryBean.class)
        .addStatement("em.setDataSource(dataSource())")
        .addStatement("em.setPackagesToScan($S)", "org.easypeelsecurity.springdog.agent")
        .addStatement("$T vendorAdapter = new $T()", JpaVendorAdapter.class, HibernateJpaVendorAdapter.class)
        .addStatement("em.setJpaVendorAdapter(vendorAdapter)")
        .addStatement("em.setJpaProperties(additionalProperties())")
        .addStatement("return em")
        .build();

    MethodSpec dataSource = MethodSpec.methodBuilder("dataSource")
        .addAnnotation(Bean.class)
        .returns(DataSource.class)
        .addModifiers(Modifier.PUBLIC)
        .addStatement("$T dataSource = new $T()", DriverManagerDataSource.class, DriverManagerDataSource.class)
        .addStatement("dataSource.setDriverClassName($S)", "org.apache.derby.jdbc.EmbeddedDriver")
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

    TypeSpec jpaConfig = TypeSpec.classBuilder("JPAConfiguration")
        .addAnnotation(Configuration.class)
        .addAnnotation(AnnotationSpec.builder(EnableJpaRepositories.class)
            .addMember("basePackages", "$S", "org.easypeelsecurity.springdog.agent")
            .addMember("entityManagerFactoryRef", "$S", "springdogEntityManagerFactory")
            .addMember("transactionManagerRef", "$S", "springdogTransactionManager")
            .build())
        .addModifiers(Modifier.PUBLIC)
        .addMethod(springdogEntityManagerFactory)
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

  private void generateController(String fullPackageName) {
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
    TypeSpec controllerSpec = controllerClass.build();
    try {
      JavaFile.builder(fullPackageName, controllerSpec)
          .build()
          .writeTo(processingEnv.getFiler());
    } catch (IOException e) {
      processingEnv.getMessager().printMessage(Kind.ERROR, "Error writing controller class: " + e.getMessage());
    }
  }

  /**
   * Defines Hibernate properties.
   */
  private HashMap<String, String> hibernateProperties() {
    HashMap<String, String> properties = new HashMap<>();
    properties.put("hibernate.hbm2ddl.auto", "create");
    properties.put("hibernate.dialect", "org.hibernate.dialect.DerbyDialect");
    return properties;
  }
}
