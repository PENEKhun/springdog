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

import java.util.Arrays;

/**
 * EndpointMetricCached.
 *
 * @param methodSignature       methodSignature
 * @param responseTimes         response times
 * @param ratelimitFailureCount failure count of the endpoint by ratelimit
 */
public record EndpointMetricCached(String methodSignature, long[] responseTimes, int ratelimitFailureCount) {

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof EndpointMetricCached that)) {
      return false;
    }

    return methodSignature.equals(that.methodSignature);
  }

  @Override
  public int hashCode() {
    return methodSignature.hashCode();
  }

  @Override
  public String toString() {
    return "EndpointMetricCached{" +
        "methodSignature='" + methodSignature + '\'' +
        ", responseTimes=" + Arrays.toString(responseTimes) +
        ", ratelimitFailureCount=" + ratelimitFailureCount +
        '}';
  }
}
