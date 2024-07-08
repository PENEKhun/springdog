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

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.logging.Logger;

import org.easypeelsecurity.springdog.shared.configuration.SpringdogProperties;
import org.easypeelsecurity.springdog.shared.ratelimit.EndpointDto;
import org.easypeelsecurity.springdog.shared.ratelimit.EndpointParameterDto;
import org.easypeelsecurity.springdog.shared.ratelimit.model.RuleStatus;
import org.easypeelsecurity.springdog.shared.util.IpAddressUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Interceptor for ratelimit.
 */

@Service
public class RatelimitInterceptor implements HandlerInterceptor {

  private final EndpointQuery endpointQuery;
  private final SpringdogProperties springdogProperties;
  private final Logger logger = Logger.getLogger(RatelimitInterceptor.class.getName());
  private final ObjectMapper objectMapper = new ObjectMapper();

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
    if (!(request instanceof ContentCachingRequestWrapper)) {
      request = new ContentCachingRequestWrapper(request);
    }

    if (handler instanceof HandlerMethod handlerMethod) {
      Object controller = handlerMethod.getBean();
      Class<?> controllerClass = controller.getClass();
      String functionName = handlerMethod.getMethod().getName();
      String fqcn = controllerClass.getName() + "." + functionName;
      if (shouldSkipRequest(request, controllerClass)) {
        return true;
      }

      EndpointDto endpoint = getEndpoint(fqcn)
          .orElseThrow(() -> new IllegalStateException("Endpoint not found"));
      if (!RuleStatus.ACTIVE.equals(endpoint.getRuleStatus())) {
        return true;
      }

      String requestHashed = generateRequestHash(request, endpoint);
      if (RatelimitCache.isBannedRequest(requestHashed, endpoint, LocalDateTime.now())) {
        int banTimeSeconds =
            endpoint.isRulePermanentBan() ? Integer.MAX_VALUE : endpoint.getRuleBanTimeInSeconds();
        applyRatelimitResponse(response, String.valueOf(banTimeSeconds));
        return false;
      }

      // TODO: set retry after
      response.setHeader("X-RateLimit-Remaining", "");
    }

    return true;
  }

  private Optional<EndpointDto> getEndpoint(String fqcn) {
    EndpointDto endpoint = RuleCache.findEndpointByFqcn(fqcn)
        .orElseGet(() -> {
          Optional<EndpointDto> item = endpointQuery.getEndpointByFqcn(fqcn);
          if (item.isEmpty()) {
            return null;
          } else {
            RuleCache.cachingRule(item.get());
            return item.get();
          }
        });
    return Optional.ofNullable(endpoint);
  }

  private boolean shouldSkipRequest(HttpServletRequest request, Class<?> controllerClass) {
    if (controllerClass.equals(
        org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController.class)) {
      return true;
    }
    String requestPath = request.getRequestURI().substring(request.getContextPath().length());
    if (requestPath.startsWith(springdogProperties.computeAbsolutePath(""))) {
      return true;
    }
    return false;
  }

  private void applyRatelimitResponse(HttpServletResponse response, String retryAfter)
      throws IOException {
    response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
    response.getWriter().write("Too many requests");
    response.setHeader("Retry-After", retryAfter);
    response.setHeader("X-RateLimit-Remaining", "0");
  }

  private String generateRequestHash(HttpServletRequest request, EndpointDto endpoint) {
    StringBuilder result = new StringBuilder();
    result.append(endpoint.getFqcn()).append("\n");

    if (endpoint.isRuleIpBased()) {
      result.append(IpAddressUtil.getClientIp(request)).append("\n");
    }

    // to make hash from default type (such as int, String ...)
    endpoint.getParameters().stream()
        .sorted(Comparator.comparing(EndpointParameterDto::getName))
        .filter(EndpointParameterDto::isEnabled)
        .forEach(param -> {
          String value = request.getParameter(param.getName());
          if (value != null) {
            result.append(param.getName()).append("=").append(value).append("\n");
          }
        });
    try {
      // to make hash from class(Request body)
      JsonNode requestBody = getRequestBodyAsJson(request);
      // FIXME: json 말고도 다른 타입도 처리해야함
      // FIXME: 성능을 위해선 일단 Object라면 Object의 FQCN정도는 저장하고... 이를 불러와서 직렬화 할필요가 있어보임.
      String fqmn = endpoint.getFqcn();
      String fqcn = fqmn.substring(0, fqmn.lastIndexOf("."));
      String methodName = fqmn.substring(fqmn.lastIndexOf(".") + 1);
      Arrays.stream(Class.forName(fqcn).getDeclaredMethods())
          .filter(method -> method.getName().equals(methodName))
          .forEach(method -> Arrays.stream(method.getParameters())
              .filter(param -> param.isAnnotationPresent(RequestBody.class))
              .map(Parameter::getType)
              .forEach(obj -> Arrays.stream(obj.getDeclaredFields())
                  .sorted(Comparator.comparing(Field::getName))
                  .forEach(field -> {
                    String value = request.getParameter(field.getName());
                    if (value == null && requestBody != null && requestBody.has(field.getName())) {
                      value = requestBody.get(field.getName()).asText();
                    }
                    if (value != null) {
                      result.append(field.getName()).append("=").append(value).append("\n");
                    }
                  })));
      // TODO: REAL HASHlize
      return result.toString();
    } catch (ClassNotFoundException e) {
      logger.warning("Failed to generate request hash" + e.getMessage());
      throw new RuntimeException(e);
    }
  }

  private JsonNode getRequestBodyAsJson(HttpServletRequest request) {
    try {
      return objectMapper.readTree(request.getReader());
    } catch (IOException e) {
      logger.warning("Failed to read request body as json" + e.getMessage());
      return null;
    }
  }
}
