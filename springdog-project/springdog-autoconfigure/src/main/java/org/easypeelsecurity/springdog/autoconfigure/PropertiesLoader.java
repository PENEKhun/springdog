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

package org.easypeelsecurity.springdog.autoconfigure;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import org.yaml.snakeyaml.Yaml;

/**
 * Utility class to <b>parse properties</b> from configuration files <b>when running annotation processors</b>.
 */
public class PropertiesLoader {

  private static final String[] PROPERTIES_LOCATIONS = {
      "src/main/resources/application.properties",
      "src/main/resources/config/application.properties",
      "application.properties",
      "config/application.properties",
      "src/main/resources/application.yml",
      "src/main/resources/config/application.yml",
      "application.yml",
      "config/application.yml"
  };
  private final ProcessingEnvironment processingEnv;

  /**
   * Constructor.
   *
   * @param processingEnv the processing environment
   */
  public PropertiesLoader(ProcessingEnvironment processingEnv) {
    this.processingEnv = processingEnv;
  }

  /**
   * Retrieves the property value associated with the specified key. If the property is not found, the default
   * value is returned.
   *
   * @param toFind       the key to find in the properties
   * @param defaultValue the default value to return if the key is not found
   * @return the property value or the default value if the property is not found
   */
  public String getPropertyOrDefault(String toFind, String defaultValue) {
    String value = defaultValue;
    Path projectRoot = findProjectRoot();

    if (projectRoot == null) {
      processingEnv.getMessager()
          .printMessage(Kind.WARNING, msg("Could not find project root. Using default value."));
      return defaultValue;
    }

    for (String location : PROPERTIES_LOCATIONS) {
      Path filePath = projectRoot.resolve(location);
      try {
        if (Files.exists(filePath)) {
          value = loadProperty(filePath, toFind, value);
          if (!defaultValue.equals(value)) {
            break;
          }
        }
      } catch (IOException e) {
        processingEnv.getMessager()
            .printMessage(Kind.WARNING, msg("Error reading " + filePath + ": " + e.getMessage()));
      }
    }

    return value;
  }

  private Path findProjectRoot() {
    try {
      Filer filer = processingEnv.getFiler();
      FileObject resource = filer.getResource(StandardLocation.CLASS_OUTPUT, "", "dummy");
      URI uri = resource.toUri();
      if (!uri.getScheme().equals("file")) {
        return null;
      }
      Path currentPath = Paths.get(uri);

      while (currentPath != null && !Files.exists(currentPath.resolve("src/main"))) {
        currentPath = currentPath.getParent();
      }

      return currentPath;
    } catch (IOException e) {
      processingEnv.getMessager()
          .printMessage(Kind.WARNING, msg("Error obtaining source path: " + e.getMessage()));
      return null;
    }
  }

  private String loadProperty(Path filePath, String toFind, String defaultValue) throws IOException {
    if (filePath.toString().endsWith(".properties")) {
      return loadFromProperties(filePath, toFind, defaultValue);
    } else if (filePath.toString().endsWith(".yml")) {
      return loadFromYaml(filePath, toFind, defaultValue);
    }
    return defaultValue;
  }

  private String loadFromProperties(Path filePath, String toFind, String defaultValue) throws IOException {
    Properties properties = new Properties();
    try (InputStream input = new FileInputStream(filePath.toFile())) {
      properties.load(input);
    }
    return properties.getProperty(toFind, defaultValue);
  }

  private String loadFromYaml(Path filePath, String toFind, String defaultValue) throws IOException {
    try (InputStream yamlStream = new FileInputStream(filePath.toFile())) {
      Yaml yaml = new Yaml();
      Map<String, Object> yamlMap = yaml.load(yamlStream);

      String[] keys = toFind.split("\\.");
      Map<String, Object> currentMap = yamlMap;
      for (String key : keys) {
        Object nextMap = currentMap.get(key);
        if (nextMap instanceof Map) {
          currentMap = (Map<String, Object>) nextMap;
        } else if (nextMap != null) {
          return nextMap.toString();
        } else {
          break;
        }
      }
    }
    return defaultValue;
  }

  private String msg(String msg) {
    return "[Springdog PropertiesLoader] " + msg;
  }
}
