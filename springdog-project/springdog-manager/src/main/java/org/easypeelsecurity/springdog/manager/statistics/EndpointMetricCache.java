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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Manages caching and retrieval of response times for various endpoints.
 * Provides methods to add response times, retrieve all response times,
 * and clear specific cache entries based on endpoint path and method.
 */
public abstract class EndpointMetricCache {

  private static final EndpointVisitCacheManager CACHE_MANAGER = new EndpointVisitCacheManager();

  /**
   * Adds a response time for a specific endpoint identified by its path and method.
   *
   * @param path         the path of the endpoint
   * @param method       the HTTP method of the endpoint
   * @param responseTime the response time to be added
   */
  public static synchronized void addResponseTime(String path, String method, long responseTime) {
    CACHE_MANAGER.getOrCreateCache(new EndpointCacheKey(path, method)).add(responseTime);
  }

  /**
   * Retrieves all stored response times for all endpoints.
   *
   * @return a list of entries where each entry contains an endpoint key and an array of response times
   */
  public static List<Entry<EndpointCacheKey, long[]>> getAllResponseTimes() {
    List<Entry<EndpointCacheKey, long[]>> result = new ArrayList<>(CACHE_MANAGER.getCacheKeys().length);
    for (var key : CACHE_MANAGER.getCacheKeys()) {
      var responseTimes = CACHE_MANAGER.getOrCreateCache(key).stream().mapToLong(Long::longValue).toArray();
      result.add(Map.entry(key, responseTimes));
    }
    return result;
  }

  /**
   * Clears the cached response times for a specific endpoint identified by its path and method.
   */
  public static void clear(EndpointCacheKey key) {
    CACHE_MANAGER.invalidateCache(key);
  }
}
