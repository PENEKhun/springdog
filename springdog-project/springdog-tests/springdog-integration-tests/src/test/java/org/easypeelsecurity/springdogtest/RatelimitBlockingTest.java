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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.easypeelsecurity.springdog.domain.ratelimit.EndpointService;
import org.easypeelsecurity.springdog.domain.ratelimit.converter.EndpointConverter;
import org.easypeelsecurity.springdog.domain.ratelimit.model.Endpoint;
import org.easypeelsecurity.springdog.manager.ratelimit.RatelimitCache;
import org.easypeelsecurity.springdog.shared.dto.EndpointDto;
import org.easypeelsecurity.springdog.shared.enums.RuleStatus;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.configuration.CayenneRuntime;
import org.apache.cayenne.query.ObjectSelect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@SpringBootTest
@AutoConfigureMockMvc
class RatelimitBlockingTest {

  @Autowired
  ExampleController exampleController;
  @Autowired
  EndpointService endpointService;
  @Autowired
  @Qualifier("springdogRepository")
  CayenneRuntime springdogRepository;
  @Autowired
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    RatelimitCache.clearCaches();
    enableRatelimit();
  }

  private void enableRatelimit() {
    // enable ratelimit (1)
    ObjectContext context = springdogRepository.newContext();
    EndpointDto targetApi = EndpointConverter.toDto(
        ObjectSelect.query(Endpoint.class)
            .where(Endpoint.PATH.eq(CASE1.PATH)
                .andExp(Endpoint.HTTP_METHOD.eq(CASE1.HTTP_METHOD)))
            .selectFirst(context));
    targetApi.setParameterNamesToEnable(CASE1.PARAMETER_NAMES_TO_ENABLE);
    targetApi.setRuleRequestLimitCount(CASE1.REQUEST_LIMIT);
    targetApi.setRuleTimeLimitInSeconds(100);
    targetApi.setRuleBanTimeInSeconds(100);
    targetApi.setRuleStatus(RuleStatus.ACTIVE);
    endpointService.updateRule(targetApi);

    // enable ratelimit (2)
    EndpointDto targetApi2 = EndpointConverter.toDto(
        ObjectSelect.query(Endpoint.class)
            .where(Endpoint.PATH.eq(CASE2.PATH)
                .andExp(Endpoint.HTTP_METHOD.eq(CASE2.HTTP_METHOD)))
            .selectFirst(context));
    targetApi2.setRuleRequestLimitCount(CASE2.REQUEST_LIMIT);
    targetApi2.setParameterNamesToEnable(CASE2.PARAMETER_NAMES_TO_ENABLE);
    targetApi2.setRuleTimeLimitInSeconds(100);
    targetApi2.setRuleBanTimeInSeconds(100);
    targetApi2.setRuleStatus(RuleStatus.ACTIVE);
    endpointService.updateRule(targetApi2);

    // enable ratelimit (3)
    EndpointDto targetApi3 = EndpointConverter.toDto(
        ObjectSelect.query(Endpoint.class)
            .where(Endpoint.PATH.eq(CASE3.PATH)
                .andExp(Endpoint.HTTP_METHOD.eq(CASE3.HTTP_METHOD)))
            .selectFirst(context));
    targetApi3.setHeaderNamesToEnable(CASE3.HEADER_NAMES_TO_ENABLE);
    targetApi3.setRuleRequestLimitCount(CASE3.REQUEST_LIMIT);
    targetApi3.setRuleTimeLimitInSeconds(100);
    targetApi3.setRuleBanTimeInSeconds(100);
    targetApi3.setRuleStatus(RuleStatus.ACTIVE);
    endpointService.updateRule(targetApi3);
  }

  @Test
  @DisplayName("Handles only the specified requests successfully even under concurrent requests")
  void ratelimitAccurateTest() throws Exception {
    // given
    ExecutorService executor = Executors.newFixedThreadPool(10);
    int extraRequest = 30;
    AtomicInteger successCount = new AtomicInteger(0);
    CountDownLatch latch = new CountDownLatch(CASE1.REQUEST_LIMIT + extraRequest);

    // when
    for (int i = 0; i < CASE1.REQUEST_LIMIT + extraRequest; i++) {
      executor.submit(() -> {
        try {
          mockMvc.perform(get(CASE1.PATH)
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
    assertThat(successCount.get()).isEqualTo(CASE1.REQUEST_LIMIT);
  }

  @Test
  @DisplayName("Requests are processed successfully if parameter values differ when the parameter rule is enabled in the Endpoint.")
  void differentParamValue() throws Exception {
    // given
    for (int i = 0; i < CASE1.REQUEST_LIMIT; i++) {
      mockMvc.perform(get(CASE1.PATH)
              .param("param1", "same-request"))
          .andExpect(status().isOk());
    }

    // when & then
    mockMvc.perform(get(CASE1.PATH)
            .param("param1", "different-request"))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("Returns isTooManyRequests if too many requests are received within the specified timeframe.")
  void testRateLimitBlockingWithConcurrency() throws Exception {
    // given
    for (int i = 0; i < CASE1.REQUEST_LIMIT; i++) {
      mockMvc.perform(get(CASE1.PATH)
              .param("param1", "test"))
          .andExpect(header().exists("X-RateLimit-Remaining"))
          .andExpect(status().isOk());
    }

    // when & then
    mockMvc.perform(get(CASE1.PATH)
            .param("param1", "test"))
        .andExpect(status().isTooManyRequests());
  }

  @Test
  @DisplayName("Does not work on inactive endpoints.")
  void DoesNotWorkOnInactiveEndpoints() throws Exception {
    // given
    String path = "/api/put?newTitle=hello&newContent=world";
    for (int i = 0; i < 1_000; i++) {
      mockMvc.perform(put(path))
          .andExpect(status().isOk());
    }

    // when & then
    mockMvc.perform(put(path))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("Should work correctly when requestBody contains an Object instead of a Value.")
  void handleRequestBodyWithObject() throws Exception {
    // given
    for (int i = 0; i < CASE2.REQUEST_LIMIT; i++) {
      mockMvc.perform(post(CASE2.PATH)
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {
                    "title": "My first post",
                    "content": "Hello world!"
                  }
                  """))
          .andExpect(status().isOk());
    }

    // when & then
    mockMvc.perform(post(CASE2.PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "title": "My first post",
                  "content": "Hello world!"
                }
                """))
        .andExpect(status().isTooManyRequests());
  }

  @Test
  @DisplayName("Should not be blocked when serialized values in requestBody are different.")
  void allowDifferentSerializedValues() throws Exception {
    // when & then
    for (int i = 0; i < CASE2.REQUEST_LIMIT * 2; i++) {
      mockMvc.perform(post(CASE2.PATH)
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {
                    "title": "different number : %d",
                    "content": "same content"
                  }
                  """.formatted(i)))
          .andExpect(status().isOk());
    }
  }

  @Test
  @DisplayName("Should work correctly when request headers are enabled.")
  void handleRequestHeaders() throws Exception {
    // given
    for (int i = 0; i < CASE3.REQUEST_LIMIT; i++) {
      mockMvc.perform(get(CASE3.PATH)
              .header("token1", "token1")
              .header("token2", "token2"))
          .andExpect(status().isOk());
    }

    // when & then
    mockMvc.perform(get(CASE3.PATH)
            .header("token1", "token1")
            .header("token2", "token2"))
        .andExpect(status().isTooManyRequests());
  }

  private static final class CASE1 {

    static final String PATH = "/api/get";
    static final String HTTP_METHOD = "GET";
    static final int REQUEST_LIMIT = 50;
    static final Set<String> PARAMETER_NAMES_TO_ENABLE = Set.of("param1");
  }

  private static final class CASE2 {

    static final String PATH = "/api/post";
    static final String HTTP_METHOD = "POST";
    static final int REQUEST_LIMIT = 50;
    static final Set<String> PARAMETER_NAMES_TO_ENABLE = Set.of("postRequest");
  }

  private static final class CASE3 {

      static final String PATH = "/api/header";
      static final String HTTP_METHOD = "GET";
      static final int REQUEST_LIMIT = 50;
      static final Set<String> HEADER_NAMES_TO_ENABLE = Set.of("token1", "token2");

  }
}
