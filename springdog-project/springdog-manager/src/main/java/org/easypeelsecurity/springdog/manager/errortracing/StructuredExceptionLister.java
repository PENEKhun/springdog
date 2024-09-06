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

package org.easypeelsecurity.springdog.manager.errortracing;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.annotation.PostConstruct;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import org.easypeelsecurity.springdog.domain.errortracing.model.ExceptionListingService;
import org.easypeelsecurity.springdog.shared.dto.ExceptionClassesDto;
import org.easypeelsecurity.springdog.shared.dto.ExceptionClassesDto.ExceptionListDto;
import org.easypeelsecurity.springdog.shared.dto.ExceptionClassesDto.ExceptionListDto.ExceptionItemDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;

/**
 * This class is responsible for listing and categorizing exception classes in a structured manner.
 * It scans for exception classes in both Java standard libraries and the application's base package.
 */
@Component
public class StructuredExceptionLister {
  private static final Logger logger = LoggerFactory.getLogger(StructuredExceptionLister.class);

  private final ApplicationContext applicationContext;
  private final Environment environment;
  private final ExceptionListingService exceptionListingService;

  /**
   * A map of Java exception packages and their descriptions.
   */
  private static final Map<String, String> JAVA_EXCEPTION_PACKAGES = new LinkedHashMap<>();

  static {
    JAVA_EXCEPTION_PACKAGES.put("java.lang", "Core Java classes including basic exceptions");
    JAVA_EXCEPTION_PACKAGES.put("java.io", "Input/Output related exceptions");
    JAVA_EXCEPTION_PACKAGES.put("java.nio", "New I/O related exceptions");
    JAVA_EXCEPTION_PACKAGES.put("java.net", "Networking related exceptions");
    JAVA_EXCEPTION_PACKAGES.put("java.util", "Utility class related exceptions");
    JAVA_EXCEPTION_PACKAGES.put("java.sql", "SQL and database related exceptions");
    JAVA_EXCEPTION_PACKAGES.put("java.security", "Security related exceptions");
    JAVA_EXCEPTION_PACKAGES.put("java.time", "Date and time handling related exceptions");
    JAVA_EXCEPTION_PACKAGES.put("javax.crypto", "Cryptography related exceptions");
  }

  /**
   * Constructs a new StructuredExceptionLister.
   *
   * @param applicationContext the Spring application context
   * @param environment        the Spring environment
   */
  public StructuredExceptionLister(ApplicationContext applicationContext, Environment environment,
      ExceptionListingService exceptionListingService) {
    this.applicationContext = applicationContext;
    this.environment = environment;
    this.exceptionListingService = exceptionListingService;
  }

  /**
   * Lists all exception classes found in the classpath, including both Java standard exceptions
   * and application-specific exceptions.
   */
  @PostConstruct
  public void listExceptions() {
    var list = scanExistExceptionClasses();
    exceptionListingService.saveExceptionsWithoutDuplicate(list);
  }

  private ExceptionClassesDto scanExistExceptionClasses() {
    String basePackage = findBasePackage();
    Map<String, List<String>> exceptionsByPackage = new LinkedHashMap<>();

    try (ScanResult scanResult = new ClassGraph()
        .enableSystemJarsAndModules()
        .acceptPackages(getAcceptPackages(basePackage))
        .scan()) {

      List<String> exceptionClasses = scanResult.getSubclasses(Exception.class.getName())
          .getNames()
          .stream()
          .filter(this::isLoadableClass)
          .toList();

      for (String className : exceptionClasses) {
        String packageType = getPackageType(className, basePackage);
        exceptionsByPackage.computeIfAbsent(packageType, k -> new ArrayList<>()).add(className);
      }
    } catch (Exception e) {
      logger.error("Error occurred while scanning for exception classes", e);
    }

    return createExceptionClassesDto(exceptionsByPackage);
  }

  /**
   * Creates an ExceptionClassesDto from the map of exceptions by package.
   *
   * @param exceptionsByPackage a map of package names to lists of exception class names
   * @return an ExceptionClassesDto object
   */
  private ExceptionClassesDto createExceptionClassesDto(Map<String, List<String>> exceptionsByPackage) {
    List<ExceptionListDto> exceptionLists = new ArrayList<>();

    for (Map.Entry<String, List<String>> entry : exceptionsByPackage.entrySet()) {
      String packageType = entry.getKey();
      List<String> exceptions = entry.getValue();

      List<ExceptionItemDto> exceptionItems = exceptions.stream()
          .map(exceptionName -> ExceptionItemDto.builder()
              .exceptionName(exceptionName)
              .isEnableToMonitor(true)
              .build())
          .toList();

      ExceptionListDto exceptionList = ExceptionListDto.builder()
          .packageType(packageType)
          .description(JAVA_EXCEPTION_PACKAGES.getOrDefault(packageType, "Application specific exceptions"))
          .subExceptions(exceptionItems)
          .build();

      exceptionLists.add(exceptionList);
    }

    return new ExceptionClassesDto(exceptionLists);
  }

  /**
   * Gets an array of package names to be scanned for exceptions.
   *
   * @param basePackage the base package of the application
   * @return an array of package names to be scanned
   */
  private String[] getAcceptPackages(String basePackage) {
    List<String> packages = new ArrayList<>(JAVA_EXCEPTION_PACKAGES.keySet());
    packages.add(basePackage);
    return packages.toArray(new String[0]);
  }

  /**
   * Determines the package type for a given class name.
   *
   * @param className   the fully qualified class name
   * @param basePackage the base package of the application
   * @return the package type (either a known Java package or the application's base package)
   */
  private String getPackageType(String className, String basePackage) {
    return JAVA_EXCEPTION_PACKAGES.keySet().stream()
        .filter(className::startsWith)
        .findFirst()
        .orElse(basePackage);
  }

  /**
   * Finds the base package of the Spring Boot application.
   *
   * @return the base package name, or an empty string if it cannot be determined
   */
  private String findBasePackage() {
    String[] beanNames = applicationContext.getBeanNamesForAnnotation(SpringBootApplication.class);
    if (beanNames.length > 0) {
      Class<?> mainClass = applicationContext.getBean(beanNames[0]).getClass();
      return mainClass.getPackage().getName();
    }

    String sources = environment.getProperty("spring.main.sources");
    if (sources != null && !sources.isEmpty()) {
      return sources.split(",")[0].trim().replaceAll("\\.[^.]*$", "");
    }

    logger.warn("Unable to determine base package. Using empty string.");
    return "";
  }

  /**
   * Checks if a class is loadable.
   *
   * @param className the fully qualified class name
   * @return true if the class can be loaded, false otherwise
   */
  private boolean isLoadableClass(String className) {
    try {
      Class.forName(className, false, this.getClass().getClassLoader());
      return true;
    } catch (ClassNotFoundException | NoClassDefFoundError e) {
      logger.warn("Unable to load class: {}. Reason: {}", className, e.getMessage());
      return false;
    }
  }
}
