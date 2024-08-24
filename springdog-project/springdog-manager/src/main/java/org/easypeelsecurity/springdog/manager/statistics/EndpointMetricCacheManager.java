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
import java.util.Map.Entry;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

/**
 * Manages the cache for endpoint metrics.
 *
 * @author PENEKhun
 */
public abstract class EndpointMetricCacheManager {

  private static final Cache<String, EndpointMetricCachedValue> CACHE_INSTANCE;

  static {
    CACHE_INSTANCE = Caffeine.newBuilder().build();
  }

  /**
   * Adds a response time for a specific endpoint identified by its path and method.
   *
   * @param methodSignature the method signature of the endpoint
   * @param responseTime    the response time to be added
   */
  public static void addResponseTime(String methodSignature, long responseTime) {
    CACHE_INSTANCE.get(methodSignature, k -> new EndpointMetricCachedValue()).addResponseTime(responseTime);
  }

  /**
   * Increments the failure count for a specific endpoint identified by method signature.
   *
   * @param methodSignature the fully qualified method name of the endpoint
   */
  public static void incrementFailureCount(String methodSignature) {
    CACHE_INSTANCE.get(methodSignature, k -> new EndpointMetricCachedValue()).incrementFailureCount();
  }

  /**
   * Gets all cache data.
   *
   * @return a list of all cache data
   */
  public static List<EndpointMetricCached> getAllData() {
    List<EndpointMetricCached> result = new ArrayList<>();
    for (Entry<String, EndpointMetricCachedValue> entry : CACHE_INSTANCE.asMap().entrySet()) {
      EndpointMetricCachedValue value = entry.getValue();
      result.add(
          new EndpointMetricCached(entry.getKey(),
              value.getResponseTimes().stream().mapToLong(Long::longValue).toArray(),
              value.getFailureCount()));
    }
    return result;
  }

  /**
   * Invalidates the cache for the specified key.
   */
  public static void invalidateByMethodSignature(String methodSignature) {
    CACHE_INSTANCE.invalidate(methodSignature);
  }

  /**
   * Clears the cache.
   */
  public static void clear() {
    CACHE_INSTANCE.invalidateAll();
  }

  static class EndpointMetricCachedValue {
    private final ArrayList<Long> responseTimes = new ArrayList<>();
    private int failureCount;

    public synchronized void addResponseTime(long responseTime) {
      responseTimes.add(responseTime);
    }

    public synchronized void incrementFailureCount() {
      failureCount++;
    }

    public List<Long> getResponseTimes() {
      return responseTimes;
    }

    public int getFailureCount() {
      return failureCount;
    }
  }
}
