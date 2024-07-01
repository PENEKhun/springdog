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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.configuration.CayenneRuntime;
import org.apache.cayenne.query.ObjectSelect;
import org.easypeelsecurity.springdog.shared.ratelimit.EndpointConverter;
import org.easypeelsecurity.springdog.shared.ratelimit.EndpointDto;
import org.easypeelsecurity.springdog.shared.ratelimit.model.Endpoint;
import org.easypeelsecurity.springdog.shared.ratelimit.model.RuleStatus;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
// LIFE CYCLE
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestInstance(Lifecycle.PER_METHOD)
@AutoConfigureMockMvc
@Disabled
class RatelimitBlockingTest {

  @Autowired
  ExampleController exampleController;
  @Autowired
  @Qualifier("springdogRepository")
  CayenneRuntime springdogRepository;
  @Autowired
  private MockMvc mockMvc;

  @Test
  @DisplayName("If the requestHash is different, the request will not be rejected.")
  void differentParamValue() throws Exception {
    // given
    ObjectContext context = springdogRepository.newContext();
    EndpointDto targetApi = EndpointConverter.toDto(
        ObjectSelect.query(Endpoint.class)
            .where(Endpoint.FQCN.eq("org.easypeelsecurity.springdogtest.ExampleController.example"))
            .selectFirst(context));
    targetApi.setParameterNamesToEnable(Set.of("param1"));
    targetApi.setRuleRequestLimitCount(100);
    targetApi.setRuleTimeLimitInSeconds(100);
    targetApi.setRuleBanTimeInSeconds(100);
    targetApi.setRuleStatus(RuleStatus.ACTIVE);

//    ObjectSelect.query(Endpoint.class)
//            .where(Endpoint.FQCN.eq("org.easypeelsecurity.springdogtest.ExampleController.example"))
//            .selectFirst(context).updateRule(
//            RuleStatus.ACTIVE,
//            false,
//            false,
//            100,
//            100,
//            100,
//        Set.of("param1"));

    int requestLimit = targetApi.getRuleRequestLimitCount();
    ExecutorService executor = Executors.newFixedThreadPool(2);
    CountDownLatch latch = new CountDownLatch(requestLimit);

    // when
    while (latch.getCount() > 0) {
      executor.submit(() -> {
        try {
          mockMvc.perform(get(targetApi.getPath())
                  .param("param1", "ban"))
              .andExpect(status().isOk());
          latch.countDown();
        } catch (Exception e) {
          // DO NOTHING
        }
      });
    }

    // then
    boolean processedOnTime = latch.await(10, java.util.concurrent.TimeUnit.SECONDS);
    executor.shutdown();
    assertThat(processedOnTime).isTrue();
    mockMvc.perform(get(targetApi.getPath())
            .param("param1", "pass"))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("Returns isTooManyRequests if the request limit is exceeded within the allowed time frame.")
  void testRateLimitBlockingWithConcurrency() throws Exception {
    // given
    ObjectContext context = springdogRepository.newContext();
    EndpointDto targetApi = EndpointConverter.toDto(
        ObjectSelect.query(Endpoint.class)
            .where(Endpoint.FQCN.eq("org.easypeelsecurity.springdogtest.ExampleController.example"))
            .selectFirst(context));
    targetApi.setParameterNamesToEnable(Set.of("param1"));
    targetApi.setRuleRequestLimitCount(100);
    targetApi.setRuleTimeLimitInSeconds(100);
    targetApi.setRuleBanTimeInSeconds(100);
    targetApi.setRuleStatus(RuleStatus.ACTIVE);

    int requestLimit = targetApi.getRuleRequestLimitCount();
    ExecutorService executor = Executors.newFixedThreadPool(2);
    CountDownLatch latch = new CountDownLatch(requestLimit);

    // when
    while (latch.getCount() > 0) {
      executor.submit(() -> {
        try {
          mockMvc.perform(get(targetApi.getPath())
                  .param("param1", "test"))
              // TODO: Validate X-RateLimit-Remaining VALUE
              .andExpect(header().exists("X-RateLimit-Remaining"))
              .andExpect(status().isOk());
          latch.countDown();
        } catch (Exception e) {
          // DO NOTHING
        }
      });
    }

    // then
    boolean processedOnTime = latch.await(10, java.util.concurrent.TimeUnit.SECONDS);
    executor.shutdown();
    assertThat(processedOnTime).isTrue();
    mockMvc.perform(get(targetApi.getPath())
            .param("param1", "test"))
        .andExpect(status().isTooManyRequests());
  }

  @Test
  @DisplayName("Does not work on inactive endpoints.")
  void DoesNotWorkOnInactiveEndpoints() throws Exception {
    // given
    ObjectContext context = springdogRepository.newContext();
    EndpointDto targetApi = EndpointConverter.toDto(
        ObjectSelect.query(Endpoint.class)
            .where(Endpoint.FQCN.eq("org.easypeelsecurity.springdogtest.ExampleController.example"))
            .selectFirst(context));
    targetApi.setParameterNamesToEnable(Set.of("param1"));
    targetApi.setRuleRequestLimitCount(100);
    targetApi.setRuleTimeLimitInSeconds(100);
    targetApi.setRuleBanTimeInSeconds(100);
    targetApi.setRuleStatus(RuleStatus.ACTIVE);

    int requestLimit = targetApi.getRuleRequestLimitCount() + 10;
    ExecutorService executor = Executors.newFixedThreadPool(2);
    CountDownLatch latch = new CountDownLatch(requestLimit);

    // when
    while (latch.getCount() > 0) {
      executor.submit(() -> {
        try {
          mockMvc.perform(get(targetApi.getPath())
                  .param("param1", "test"))
              .andExpect(status().isOk());
          latch.countDown();
        } catch (Exception e) {
          // DO NOTHING
        }
      });
    }

    // then
    boolean processedOnTime = latch.await(10, java.util.concurrent.TimeUnit.SECONDS);
    executor.shutdown();
    assertThat(processedOnTime).isTrue();
    mockMvc.perform(get(targetApi.getPath())
            .param("param1", "test"))
        .andExpect(status().isOk());
  }
}
