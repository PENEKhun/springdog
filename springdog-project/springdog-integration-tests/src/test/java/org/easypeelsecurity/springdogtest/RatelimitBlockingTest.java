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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.easypeelsecurity.springdog.agent.SpringdogAgentView;
import org.easypeelsecurity.springdog.manager.ratelimit.EndpointCommand;
import org.easypeelsecurity.springdog.manager.ratelimit.EndpointRepository;
import org.easypeelsecurity.springdog.shared.ratelimit.EndpointHash;
import org.easypeelsecurity.springdog.shared.ratelimit.EndpointHashProvider;
import org.easypeelsecurity.springdog.shared.ratelimit.model.Endpoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class RatelimitBlockingTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  ExampleController exampleController;

  @Autowired
  EndpointRepository endpointRepository;

  @Test
  @DisplayName("Test ratelimit blocking with concurrent requests")
  @Sql("/Initial_data/predefined-rulesets.sql")
  void testRateLimitBlockingWithConcurrency() throws Exception {
    // given
    Endpoint targetApi = endpointRepository.findByFqcn("org.easypeelsecurity.springdogtest.ExampleController.example").get();
    int requestLimit = targetApi.getRuleRequestLimitCount();
    ExecutorService executorService = Executors.newFixedThreadPool(requestLimit);
    CountDownLatch latch = new CountDownLatch(requestLimit);

    // when & then
    for (int i = 0; i < requestLimit; i++) {
      executorService.submit(() -> {
        try {
          mockMvc.perform(get(targetApi.getPath())
                  .param("param1", "test"))
              .andExpect(status().isOk());
        } catch (Exception e) {
          e.printStackTrace();
        } finally {
          latch.countDown();
        }
      });
    }

    latch.await(30, TimeUnit.SECONDS);
    mockMvc.perform(get(targetApi.getPath())
            .param("param1", "test"))
        .andExpect(status().isTooManyRequests());
    executorService.shutdown();

  }
}
