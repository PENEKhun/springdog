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

package org.easypeelsecurity.springdog.manager.ratelimit;

import java.util.Optional;

import org.easypeelsecurity.springdog.shared.ratelimit.EndpointDto;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

/**
 * Rule cache instance.
 * KEY: fqcn
 * VALUE: EndpointDto
 */
@SuppressWarnings("checkstyle:MissingJavadocMethod")
public final class RuleCache {

  private RuleCache() {
  }

  public static Optional<EndpointDto> findEndpointByFqcn(String fqcn) {
    EndpointDto temp = getInstance().getIfPresent(fqcn);
    return temp == null ? Optional.empty() : Optional.of(temp);
  }

  public static void cachingRule(EndpointDto endpointDto) {
    getInstance().put(endpointDto.getFqcn(), endpointDto);
  }

  public static void changeRuleCached(String fqcn, EndpointDto endpointDto) {
    getInstance().put(fqcn, endpointDto);
  }

  private static Cache<String, EndpointDto> getInstance() {
    return RuleCacheHolder.INSTANCE;
  }

  private static final class RuleCacheHolder {

    private static final Cache<String, EndpointDto> INSTANCE = Caffeine.newBuilder().build();
  }
}
