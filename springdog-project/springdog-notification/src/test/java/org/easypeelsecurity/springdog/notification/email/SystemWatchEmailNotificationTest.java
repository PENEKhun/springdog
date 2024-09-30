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

package org.easypeelsecurity.springdog.notification.email;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SystemWatchEmailNotificationTest {

  @Test
  void setCause() {
    // given
    SystemWatchEmailNotification emailNotification = new SystemWatchEmailNotification();

    // when
    emailNotification.setCause("CPU", 10.5);

    // then
    assertThat(emailNotification.cause)
        .extracting("key", "value")
        .containsExactly("CPU", 10.5);
    assertThat(emailNotification.recovered).isNull();
  }

  @Test
  void setRecovery() {
    // given
    SystemWatchEmailNotification emailNotification = new SystemWatchEmailNotification();

    // when
    emailNotification.setRecovery("CPU", 10.5);

    // then
    assertThat(emailNotification.recovered)
        .extracting("key", "value")
        .containsExactly("CPU", 10.5);
    assertThat(emailNotification.cause).isNull();
  }
}
