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

package org.easypeelsecurity.springdogtest.statistics;

import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

import java.util.concurrent.TimeUnit;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.TestPropertySource;

import org.easypeelsecurity.springdog.manager.statistics.SystemMetricsScheduler;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@SpringBootTest
@TestPropertySource(properties = "springdog.systemMetricsScheduler.fixedRate=100")
class SystemMetricsSchedulerTest {

  @SpyBean
  private SystemMetricsScheduler scheduler;

  @Test
  @DisplayName("System metric scheduler runs correctly at specified intervals.")
  void shouldRunSchedulerAtSpecifiedIntervals() {
    Awaitility.await()
        /*
        FIXME: This test is flaky. It fails sometimes because the scheduler is not called at least 5 times. So I set the timeout to 5000, instead of arbitrarily around 500.
         */
        .atMost(5000, TimeUnit.MILLISECONDS)
        .untilAsserted(() -> {
          verify(scheduler, atLeast(5)).storeSystemsMetrics();
        });
  }
}
