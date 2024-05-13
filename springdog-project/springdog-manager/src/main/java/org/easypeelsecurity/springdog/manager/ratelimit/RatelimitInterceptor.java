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

import java.util.HashMap;

import org.easypeelsecurity.springdog.shared.ratelimit.RulesetDto;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.github.benmanes.caffeine.cache.Cache;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Interceptor for ratelimit.
 */
@Service
public class RatelimitInterceptor implements HandlerInterceptor {

  private static final HashMap<String, Cache<String, Integer>> localCache = new HashMap<>();
  private final EndpointQuery endpointQuery;

  /**
   * Constructor.
   */
  public RatelimitInterceptor(EndpointQuery endpointQuery) {
    this.endpointQuery = endpointQuery;

  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {

    if (handler instanceof HandlerMethod handlerMethod) {
      Object controller = handlerMethod.getBean();
      Class<?> controllerClass = controller.getClass();
      String functionName = handlerMethod.getMethod().getName();
      String fqcn = controllerClass.getName() + "." + functionName;
      if (fqcn.startsWith("org.easypeelsecurity.springdog")) {
        return true;
      }

      RulesetDto rule = RuleCache.findRuleByFqcn(fqcn)
          .orElseGet(() -> endpointQuery.getRuleByFqcn(fqcn).orElse(null));
      if (rule == null) {
        return true;
      }
    }

    return true;
  }
}
