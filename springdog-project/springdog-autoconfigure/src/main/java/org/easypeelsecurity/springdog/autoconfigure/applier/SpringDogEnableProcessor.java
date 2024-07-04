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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

import org.easypeelsecurity.springdog.autoconfigure.PropertiesLoader;

/**
 * Processes the SpringDogEnable annotation to enable configurations necessary for SpringDog functionalities.
 */
public class SpringDogEnableProcessor extends AbstractProcessor {

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

      List<CodeGenerator> generatedCodes = Arrays.asList(
          new SpringdogAgentApplier(agentBasePath),
          new SpringdogDynamicTemplateResolver(),
          new SpringdogBannerPrinter(),
          new SpringdogAgentSecurityApplier(),
          new SpringdogManagerApplier(),
          new SpringdogAutoConfigurationApplier(),
          new SpringdogSharedApplier()
      );

      generatedCodes.forEach(generator -> generator.writeTo(fullPackageName, processingEnv));
    });
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
