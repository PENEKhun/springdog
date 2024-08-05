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

package org.easypeelsecurity.springdog.shared.configuration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;

import jakarta.annotation.PostConstruct;

import org.springframework.boot.context.properties.ConfigurationProperties;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class SystemWatchPropertiesTest {
  @Nested
  @DisplayName("When system watch properties are enabled")
  class Enabled {
    @Test
    @DisplayName("Throws exception when enabled without threshold")
    void shouldThrowExceptionWithoutThreshold() {
      // given
      SystemWatchProperties systemWatchProperties = new SystemWatchProperties();
      systemWatchProperties.setEnabled(true);

      // when & then
      assertThrows(IllegalArgumentException.class, systemWatchProperties::init,
          "To enable SystemWatch, you must enter at least one threshold.");
    }

    @Test
    @DisplayName("Does not throw when CPU threshold is set")
    void shouldNotThrowWithCpuThreshold() {
      // given
      SystemWatchProperties systemWatchProperties = new SystemWatchProperties();
      systemWatchProperties.setEnabled(true);
      systemWatchProperties.setCpuThreshold(10.1);

      // when & then
      assertDoesNotThrow(systemWatchProperties::init);
    }

    @Test
    @DisplayName("Does not throw when Disk threshold is set")
    void shouldNotThrowWithDiskThreshold() {
      // given
      SystemWatchProperties systemWatchProperties = new SystemWatchProperties();
      systemWatchProperties.setEnabled(true);
      systemWatchProperties.setDiskThreshold(10.1);

      // when & then
      assertDoesNotThrow(systemWatchProperties::init);
    }

    @Test
    @DisplayName("Does not throw when Memory threshold is set")
    void shouldNotThrowWithMemoryThreshold() {
      // given
      SystemWatchProperties systemWatchProperties = new SystemWatchProperties();
      systemWatchProperties.setEnabled(true);
      systemWatchProperties.setMemoryThreshold(10.1);

      // when & then
      assertDoesNotThrow(systemWatchProperties::init);
    }
  }

  @Nested
  @DisplayName("When system watch properties are disabled")
  class Disabled {
    @Test
    @DisplayName("Does not throw exception when disabled")
    void shouldNotThrowWhenDisabled() {
      // given
      SystemWatchProperties systemWatchProperties = new SystemWatchProperties();
      systemWatchProperties.setEnabled(false);

      // when & then
      assertDoesNotThrow(systemWatchProperties::init);
    }
  }

  @Test
  @DisplayName("Check if init method has @PostConstruct annotation")
  void shouldHavePostConstructAnnotation() throws NoSuchMethodException {
    // given
    Method initMethod = NotificationGmailProperties.class.getDeclaredMethod("init");

    // when
    boolean isPostConstructPresent = initMethod.isAnnotationPresent(PostConstruct.class);

    // then
    assertTrue(isPostConstructPresent, "The init method should be annotated with @PostConstruct");
  }

  @Nested
  @DisplayName("Getter and Setter Tests")
  class GetterAndSetterTests {

    @Test
    @DisplayName("Set and get CPU threshold")
    void setAndGetCpuThreshold() {
      // given
      SystemWatchProperties systemWatchProperties = new SystemWatchProperties();
      double expectedCpuThreshold = 25.0;

      // when
      systemWatchProperties.setCpuThreshold(expectedCpuThreshold);
      double actualCpuThreshold = systemWatchProperties.getCpuThreshold();

      // then
      assertThat(actualCpuThreshold).isEqualTo(25.0);
    }

    @Test
    @DisplayName("Set and get Memory threshold")
    void setAndGetMemoryThreshold() {
      // given
      SystemWatchProperties systemWatchProperties = new SystemWatchProperties();
      double expectedMemoryThreshold = 50.0;

      // when
      systemWatchProperties.setMemoryThreshold(expectedMemoryThreshold);
      double actualMemoryThreshold = systemWatchProperties.getMemoryThreshold();

      // then
      assertThat(actualMemoryThreshold).isEqualTo(50.0);
    }

    @Test
    @DisplayName("Set and get Disk threshold")
    void setAndGetDiskThreshold() {
      // given
      SystemWatchProperties systemWatchProperties = new SystemWatchProperties();
      double expectedDiskThreshold = 75.0;

      // when
      systemWatchProperties.setDiskThreshold(expectedDiskThreshold);
      double actualDiskThreshold = systemWatchProperties.getDiskThreshold();

      // then
      assertThat(actualDiskThreshold).isEqualTo(75.0);
    }

    @Test
    @DisplayName("Set and get enabled status")
    void setAndGetEnabledStatus() {
      // given
      SystemWatchProperties systemWatchProperties = new SystemWatchProperties();
      boolean expectedEnabledStatus = true;

      // when
      systemWatchProperties.setEnabled(expectedEnabledStatus);
      boolean actualEnabledStatus = systemWatchProperties.isEnabled();

      // then
      assertThat(actualEnabledStatus).isTrue();
    }
  }

  @Test
  @DisplayName("Check if class has @ConfigurationProperties annotation")
  void shouldHaveConfigurationPropertiesAnnotation() {
    // when
    boolean isConfigurationPropertiesPresent = SystemWatchProperties.class.isAnnotationPresent(
        ConfigurationProperties.class);
    String prefix = SystemWatchProperties.class.getAnnotation(ConfigurationProperties.class).prefix();

    // then
    assertThat(isConfigurationPropertiesPresent).isTrue();
    assertThat(prefix).isEqualTo("springdog.system-watch");
  }
}
