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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.easypeelsecurity.springdog.manager.ratelimit.EndpointQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@SuppressWarnings("ALL")
class RatelimitInterceptTest {

  @Autowired
  EndpointQuery endpointQuery;

  @Test
  @DisplayName("")
  void test() {
    // when
    var endpoints = endpointQuery.findAll();

    // then
    assertEquals(3, endpoints.size());
  }
}
