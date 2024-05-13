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

import org.easypeelsecurity.springdog.shared.ratelimit.RulesetDto;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

/**
 * Rule cache instance.
 */
@SuppressWarnings("checkstyle:MissingJavadocMethod")
public final class RuleCache {

  private RuleCache() {
  }

  public static Optional<RulesetDto> findRuleByFqcn(String fqcn) {
    RulesetDto temp = getInstance().getIfPresent(fqcn);
    if (temp != null) {
      getInstance().put(fqcn, temp);
      return Optional.of(temp);
    }
    return Optional.empty();
  }

  public static void cachingRule(String fqcn, RulesetDto rule) {
    getInstance().put(fqcn, rule);
  }

  public static void changeRuleCached(String fqcn, RulesetDto newRule) {
    getInstance().put(fqcn, newRule);
  }

  private static Cache<String, RulesetDto> getInstance() {
    return RuleCacheHolder.INSTANCE;
  }

  private static final class RuleCacheHolder {

    private static final Cache<String, RulesetDto> INSTANCE = Caffeine.newBuilder().build();
  }
}
