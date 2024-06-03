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

import java.time.LocalDateTime;
import java.util.Comparator;

import org.easypeelsecurity.springdog.shared.configuration.SpringdogProperties;
import org.easypeelsecurity.springdog.shared.ratelimit.EndpointDto;
import org.easypeelsecurity.springdog.shared.ratelimit.EndpointParameterDto;
import org.easypeelsecurity.springdog.shared.ratelimit.RulesetDto;
import org.easypeelsecurity.springdog.shared.ratelimit.model.RuleStatus;
import org.easypeelsecurity.springdog.shared.util.IpAddressUtil;
import org.easypeelsecurity.springdog.shared.util.TimeUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Interceptor for ratelimit.
 */
@Service
public class RatelimitInterceptor implements HandlerInterceptor {

  private final EndpointQuery endpointQuery;
  private final SpringdogProperties springdogProperties;

  /**
   * Constructor.
   */
  public RatelimitInterceptor(EndpointQuery endpointQuery, SpringdogProperties springdogProperties) {
    this.endpointQuery = endpointQuery;
    this.springdogProperties = springdogProperties;
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {

    if (handler instanceof HandlerMethod handlerMethod) {
      Object controller = handlerMethod.getBean();
      Class<?> controllerClass = controller.getClass();
      String functionName = handlerMethod.getMethod().getName();
      String fqcn = controllerClass.getName() + "." + functionName;
      if (controllerClass.equals(
          org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController.class)) {
        return true;
      }
      String requestPath = request.getRequestURI().substring(request.getContextPath().length());
      if (requestPath.startsWith(springdogProperties.computeAbsolutePath(""))) {
        return true;
      }

      EndpointDto endpoint = RuleCache.findEndpointByFqcn(fqcn)
          .orElseGet(() -> endpointQuery.getEndpointByFqcn(fqcn).orElse(null));
      if (endpoint == null) {
        return true;
      }

      RulesetDto rule = endpoint.getRuleset();
      if (rule == null) {
        return true;
      }

      if (!RuleStatus.ACTIVE.equals(rule.getStatus())) {
        return true;
      }

      String requestHashed = generateRequestHash(request, fqcn, endpoint);

      LocalDateTime now = LocalDateTime.now();
      LocalDateTime[] timestamps = RatelimitCache.addTimestamp(requestHashed, now);
      int timeFrameSeconds = TimeUtil.convertToSeconds(rule.getTimeLimitDays(), rule.getTimeLimitHours(),
          rule.getTimeLimitMinutes(), rule.getTimeLimitSeconds());

      if (RatelimitCache.checkBan(requestHashed, now)) {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.getWriter().write("Too many requests");
        response.setHeader("Retry-After",
            String.valueOf(TimeUtil.convertToSeconds(rule.getBanTimeDays(), rule.getBanTimeHours(),
                rule.getBanTimeMinutes(), rule.getBanTimeSeconds())));
        response.setHeader("X-RateLimit-Remaining", "0");
        return false;
      }

      int count = 0;
      for (LocalDateTime timestamp : timestamps) {
        if (timestamp.isAfter(now.minusSeconds(timeFrameSeconds))) {
          count++;
        }
      }

      if (count > rule.getRequestLimitCount()) {
        int banTimeSeconds = rule.isPermanentBan() ? Integer.MAX_VALUE
            : TimeUtil.convertToSeconds(rule.getBanTimeDays(), rule.getBanTimeHours(), rule.getBanTimeMinutes(),
                rule.getBanTimeSeconds());
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.getWriter().write("Too many requests");
        response.setHeader("Retry-After", String.valueOf(banTimeSeconds));
        response.setHeader("X-RateLimit-Remaining", "0");
        RatelimitCache.ban(requestHashed, now.plusSeconds(banTimeSeconds));
        return false;
      } else {
        response.setHeader("X-RateLimit-Remaining", String.valueOf(rule.getRequestLimitCount() - count));
      }
    }

    return true;
  }

  private String generateRequestHash(HttpServletRequest request, String fqcn, EndpointDto endpoint) {
    StringBuilder beforeHash = new StringBuilder();
    beforeHash.append(fqcn).append("\n");

    if (endpoint.getRuleset().isIpBased()) {
      beforeHash.append(IpAddressUtil.getClientIp(request)).append("\n");
    }

    endpoint.getParameters().stream()
        .sorted(Comparator.comparing(EndpointParameterDto::getName))
        .filter(EndpointParameterDto::isEnabled)
        .forEach(param -> {
          String value = request.getParameter(param.getName());
          if (value != null) {
            beforeHash.append(param.getName()).append("=").append(value).append("\n");
          }
        });
    return beforeHash.toString();
  }
}
