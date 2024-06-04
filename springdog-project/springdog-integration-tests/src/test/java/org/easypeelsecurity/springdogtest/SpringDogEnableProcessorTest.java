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

package org.easypeelsecurity.springdogtest;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
class SpringDogEnableProcessorTest {

  @Autowired
  private ApplicationContext applicationContext;

  @ParameterizedTest
  @ValueSource(strings = {"JPAConfiguration", "springdogAgentView", "springdogAgentSecurityApplier", "templateEngine", "templateResolver",
      "springdogAutoconfigurationBeanApplier", "springdogBannerPrinter", "springdogManagerApplier", "springdogProperties"})
  @DisplayName("Should register of beans generated by annotation processing")
  void checkGeneratedClassAppliedAtSpringContainer(String targetBean) {
    // when
    boolean beanExist = applicationContext.containsBean(targetBean);

    // then
    assertThat(beanExist).isTrue();
  }

  @Test
  void agentExternalAccessInterceptorBeanApply() {
    // given
    String beanName = "agentExternalAccessInterceptor";

    // when
    boolean beanExist = applicationContext.containsBean(beanName);

    // then
    assertThat(beanExist).isTrue();
  }

  @Test
  @DisplayName("Should register of beans generated by annotation processing | gh-42 see comments")
  void springdogPropertiesBeanApplierTest() {
    // given
    String beanName = "springdogProperties";

    // when
    boolean beanExist = applicationContext.containsBean(beanName);

    // then
    assertThat(beanExist).isTrue();
  }
}