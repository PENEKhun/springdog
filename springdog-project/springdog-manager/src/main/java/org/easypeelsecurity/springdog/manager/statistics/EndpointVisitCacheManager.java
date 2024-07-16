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

import java.util.List;
import java.util.Vector;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

/**
 * Manages the Caffeine cache instance.
 * Provides methods to interact with the cache.
 */
public class EndpointVisitCacheManager {

  private static final Cache<EndpointCacheKey, Vector<Long>> CACHE_INSTANCE;

  static {
    CACHE_INSTANCE = Caffeine.newBuilder().build();
  }

  /**
   * Gets the cache for the specified key or creates it if it doesn't exist.
   *
   * @return the cache for the specified key
   */
  public List<Long> getOrCreateCache(EndpointCacheKey key) {
    return CACHE_INSTANCE.get(key, k -> new Vector<>());
  }

  /**
   * Gets all cache keys.
   *
   * @return an array of all cache keys
   */
  public EndpointCacheKey[] getCacheKeys() {
    return CACHE_INSTANCE.asMap().keySet().toArray(new EndpointCacheKey[0]);
  }

  /**
   * Invalidates the cache for the specified key.
   */
  public void invalidateCache(EndpointCacheKey key) {
    CACHE_INSTANCE.invalidate(key);
  }
}
