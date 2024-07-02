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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.configuration.CayenneRuntime;
import org.apache.cayenne.query.ObjectSelect;
import org.easypeelsecurity.springdog.manager.ratelimit.EndpointCommand;
import org.easypeelsecurity.springdog.shared.ratelimit.EndpointConverter;
import org.easypeelsecurity.springdog.shared.ratelimit.EndpointDto;
import org.easypeelsecurity.springdog.shared.ratelimit.model.Endpoint;
import org.easypeelsecurity.springdog.shared.ratelimit.model.RuleStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@DirtiesContext
@AutoConfigureMockMvc
class RatelimitBlockingTest {

  private final String PATH = "/api/get";
  private final String HTTP_METHOD = "GET";
  private final int REQUEST_LIMIT = 50;

  @Autowired
  ExampleController exampleController;
  @Autowired
  EndpointCommand endpointCommand;
  @Autowired
  @Qualifier("springdogRepository")
  CayenneRuntime springdogRepository;
  @Autowired
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    ObjectContext context = springdogRepository.newContext();
    EndpointDto targetApi = EndpointConverter.toDto(
        ObjectSelect.query(Endpoint.class)
            .where(Endpoint.PATH.eq(PATH)
                .andExp(Endpoint.HTTP_METHOD.eq(HTTP_METHOD)))
            .selectFirst(context));
    targetApi.setParameterNamesToEnable(Set.of("param1"));
    targetApi.setRuleRequestLimitCount(REQUEST_LIMIT);
    targetApi.setRuleTimeLimitInSeconds(100);
    targetApi.setRuleBanTimeInSeconds(100);
    targetApi.setRuleStatus(RuleStatus.ACTIVE);
    endpointCommand.updateRule(targetApi);
  }

  @Test
  @DisplayName("Handles only the specified requests successfully even under concurrent requests")
  void ratelimitAccurateTest() throws Exception {
    // given
    ExecutorService executor = Executors.newFixedThreadPool(10);
    int extraRequest = 30;
    AtomicInteger successCount = new AtomicInteger(0);
    CountDownLatch latch = new CountDownLatch(REQUEST_LIMIT + extraRequest);

    // when
    for (int i = 0; i < REQUEST_LIMIT + extraRequest; i++) {
      executor.submit(() -> {
        try {
          mockMvc.perform(get(PATH)
                  .param("param1", "ban"))
              .andExpect(status().isOk());
          successCount.incrementAndGet();
        } catch (Exception e) {
          // DO NOTHING
        } finally {
          latch.countDown();
        }
      });
    }

    // then
    boolean processedOnTime = latch.await(30, java.util.concurrent.TimeUnit.SECONDS);
    executor.shutdown();
    assertThat(processedOnTime).isTrue();
    assertThat(successCount.get()).isEqualTo(REQUEST_LIMIT);
  }

  @Test
  @DisplayName("Requests are processed successfully if parameter values differ when the parameter rule is enabled in the Endpoint.")
  void differentParamValue() throws Exception {
    // given
    for (int i = 0; i < REQUEST_LIMIT; i++) {
      mockMvc.perform(get(PATH)
              .param("param1", "same-request"))
          .andExpect(status().isOk());
    }

    // when & then
    mockMvc.perform(get(PATH)
            .param("param1", "different-request"))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("Returns isTooManyRequests if too many requests are received within the specified timeframe.")
  void testRateLimitBlockingWithConcurrency() throws Exception {
    // given
    for (int i = 0; i < REQUEST_LIMIT; i++) {
      mockMvc.perform(get(PATH)
              .param("param1", "test"))
          .andExpect(header().exists("X-RateLimit-Remaining"))
          .andExpect(status().isOk());
    }

    // when & then
    mockMvc.perform(get(PATH)
            .param("param1", "test"))
        .andExpect(status().isTooManyRequests());
  }

  @Test
  @DisplayName("Does not work on inactive endpoints.")
  void DoesNotWorkOnInactiveEndpoints() throws Exception {
    // given
    for (int i = 0; i < 1_000; i++) {
      mockMvc.perform(post("/api/post")
              .contentType(MediaType.APPLICATION_JSON)
              .content("{\"title\":\"title\", \"content\": \"content\"}"))
          .andExpect(status().isOk());
    }

    // when & then
    mockMvc.perform(post("/api/post")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"title\":\"title\", \"content\": \"content\"}"))
        .andExpect(status().isOk());
  }
}
