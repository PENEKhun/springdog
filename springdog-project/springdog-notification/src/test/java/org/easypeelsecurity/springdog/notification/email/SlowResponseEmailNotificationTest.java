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

class SlowResponseEmailNotificationTest {

  @Test
  void setCause() {
    // given
    SlowResponseEmailNotification emailNotification = new SlowResponseEmailNotification(null, null);

    // when
    emailNotification.setCause("/test", 1_000L);

    // then
    assertThat(emailNotification.cause)
        .extracting("key", "value")
        .containsExactly("/test", 1_000L);
    assertThat(emailNotification.recovered).isNull();
  }

  @Test
  void setRecovery() {
    // given
    SlowResponseEmailNotification emailNotification = new SlowResponseEmailNotification(null, null);

    // when
    emailNotification.setCause("/test", 5_000L);
    emailNotification.setRecovery("/test", 1_000L);

    // then
    assertThat(emailNotification.cause).isNull();
    assertThat(emailNotification.recovered)
        .extracting("key", "value")
        .containsExactly("/test", 1_000L);
  }
}
