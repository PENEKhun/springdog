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

package org.easypeelsecurity.springdog.manager.statistics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EndpointVisitCacheManagerTest {

  private EndpointVisitCacheManager cacheManager;
  static final String TEST_FQMN = "org.easypeelsecurity.springdog.controller.ExampleController";

  @BeforeEach
  void setUp() {
    cacheManager = new EndpointVisitCacheManager();
    cacheManager.invalidateCache(TEST_FQMN);
  }

  @Test
  void testGetOrCreateCache() {
    List<Long> cache = cacheManager.getOrCreateCache(TEST_FQMN);
    assertNotNull(cache, "Cache should not be null");
    assertTrue(cache.isEmpty(), "Cache should be empty initially");

    cache.add(123L);
    List<Long> cacheAgain = cacheManager.getOrCreateCache(TEST_FQMN);
    assertEquals(1, cacheAgain.size(), "Cache should contain one item");
    assertEquals(123L, cacheAgain.get(0), "Cache item should be 123L");
  }

  @Test
  void testGetCacheKeys() {
    String[] keys = cacheManager.getCacheKeys();
    assertEquals(0, keys.length, "No keys should be present initially");

    cacheManager.getOrCreateCache(TEST_FQMN);
    keys = cacheManager.getCacheKeys();
    assertEquals(1, keys.length, "One key should be present");
    assertEquals(TEST_FQMN, keys[0], "The key should match the testKey");
  }

  @Test
  void testInvalidateCache() {
    List<Long> cache = cacheManager.getOrCreateCache(TEST_FQMN);
    cache.add(123L);

    cacheManager.invalidateCache(TEST_FQMN);
    List<Long> invalidatedCache = cacheManager.getOrCreateCache(TEST_FQMN);
    assertTrue(invalidatedCache.isEmpty(), "Cache should be empty after invalidation");
  }
}
