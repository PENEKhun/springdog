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

package org.easypeelsecurity.springdog.autoconfigure.controller.parser;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

class ParameterNameExtractorTest {

  private static class ControllerStub {

    public void simpleMethod(String param1, int param2) {
      // Simple method with basic parameters
    }

    public void annotatedMethod(@RequestParam("paramA") String param1, @RequestParam("paramB") int param2,
        @RequestBody Object paramC) {
      // Method with @RequestParam, @RequestBody annotations
    }

    public void mixedMethod(@RequestParam("paramC") String param1, int param2) {
      // Mixed method with one annotated parameter
    }
  }

  @Test
  void testSimpleMethodParameterNames() throws IOException, NoSuchMethodException {
    // when
    String[] paramNames =
        ParameterNameExtractor.getParameterNames(ControllerStub.class, "simpleMethod", String.class, int.class);

    // then
    assertThat(paramNames).containsExactlyInAnyOrder("param1", "param2");
  }

  @Test
  void testAnnotatedMethodParameterNames() throws IOException, NoSuchMethodException {
    // when
    String[] paramNames =
        ParameterNameExtractor.getParameterNames(ControllerStub.class, "annotatedMethod", String.class,
            int.class, Object.class);

    // then
    assertThat(paramNames).containsExactlyInAnyOrder("paramA", "paramB", "paramC");
  }

  @Test
  void testMixedMethodParameterNames() throws IOException, NoSuchMethodException {
    // when
    String[] paramNames =
        ParameterNameExtractor.getParameterNames(ControllerStub.class, "mixedMethod", String.class, int.class);

    // then
    assertThat(paramNames).containsExactlyInAnyOrder("paramC", "param2");
  }
}
