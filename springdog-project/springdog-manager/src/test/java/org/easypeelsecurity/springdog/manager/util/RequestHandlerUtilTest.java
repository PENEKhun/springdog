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

package org.easypeelsecurity.springdog.manager.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;

import org.easypeelsecurity.springdog.agent.SpringdogAgentView;

import org.junit.jupiter.api.Test;

class RequestHandlerUtilTest {

  @Test
  void shouldSkipRequest_BasicErrorController_ReturnsTrue() {
    boolean result = RequestHandlerUtil.shouldSkipRequest(BasicErrorController.class);
    assertThat(result).isTrue();
  }

  @Test
  @SuppressWarnings("checkstyle:RegexpSingleline")
  void shouldSkipRequest_SpringdogAgentViewSubclass_ReturnsTrue() {
    class TestSpringdogAgentView extends SpringdogAgentView {
    }

    boolean result = RequestHandlerUtil.shouldSkipRequest(TestSpringdogAgentView.class);
    assertThat(result).isTrue();
  }

  @Test
  @SuppressWarnings("checkstyle:RegexpSingleline")
  void shouldSkipRequest_RegularController_ReturnsFalse() {
    class RegularController {
    }

    boolean result = RequestHandlerUtil.shouldSkipRequest(RegularController.class);
    assertThat(result).isFalse();
  }
}

