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
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.logging.Logger;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;

import org.easypeelsecurity.springdog.domain.ratelimit.EndpointService;
import org.easypeelsecurity.springdog.domain.ratelimit.RuleCache;
import org.easypeelsecurity.springdog.manager.statistics.EndpointMetricCacheManager;
import org.easypeelsecurity.springdog.shared.configuration.SpringdogProperties;
import org.easypeelsecurity.springdog.shared.dto.EndpointDto;
import org.easypeelsecurity.springdog.shared.dto.EndpointParameterDto;
import org.easypeelsecurity.springdog.shared.enums.RuleStatus;
import org.easypeelsecurity.springdog.shared.util.IpAddressUtil;
import org.easypeelsecurity.springdog.shared.util.MethodSignatureParser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Interceptor for ratelimit.
 *
 * @author PENEKhun
 */

@Service
public class RatelimitInterceptor implements HandlerInterceptor {

  private final EndpointService endpointService;
  private final SpringdogProperties springdogProperties;
  private final Logger logger = Logger.getLogger(RatelimitInterceptor.class.getName());
  private final ObjectMapper objectMapper = new ObjectMapper();

  /**
   * Constructor.
   */

  public RatelimitInterceptor(EndpointService endpointService, SpringdogProperties springdogProperties) {
    this.endpointService = endpointService;
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
      String methodSignature = MethodSignatureParser.parse(handlerMethod);
      if (shouldSkipRequest(request, controllerClass)) {
        return true;
      }

      Optional<EndpointDto> optionalEndpoint = getValidEndpoint(methodSignature);
      if (optionalEndpoint.isEmpty()) {
        return true;
      }
      EndpointDto endpoint = optionalEndpoint.get();

      if (!RuleStatus.ACTIVE.equals(endpoint.getRuleStatus())) {
        return true;
      }

      String requestHashed = generateRequestHash(request, endpoint);
      if (RatelimitCache.isBannedRequest(requestHashed, endpoint, LocalDateTime.now())) {
        int banTimeSeconds =
            endpoint.isRulePermanentBan() ? Integer.MAX_VALUE : endpoint.getRuleBanTimeInSeconds();
        applyRatelimitResponse(response, String.valueOf(banTimeSeconds));
        EndpointMetricCacheManager.incrementFailureCount(methodSignature);
        return false;
      }

      // TODO: set retry after
      response.setHeader("X-RateLimit-Remaining", "");
    }

    return true;
  }

  private Optional<EndpointDto> getValidEndpoint(String methodSignature) {
    EndpointDto endpoint = RuleCache.findEndpointByMethodSignature(methodSignature)
        .orElseGet(() -> {
          EndpointDto item = endpointService.getEndpointByMethodSignature(methodSignature);
          if (item == null) {
            return null;
          } else {
            RuleCache.cachingRule(item);
            return item;
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
    return requestPath.startsWith(springdogProperties.computeAbsolutePath(""));
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
    result.append(endpoint.getMethodSignature()).append("\n");

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

      Class<?> clazz = Class.forName(endpoint.getFqcn());
      Method[] methods = clazz.getDeclaredMethods();

      for (Method method : methods) {
        // TODO: 성능 개선 포인트
        if (!method.toString().contains(endpoint.getMethodSignature())) {
          continue;
        }

        Parameter[] parameters = method.getParameters();

        for (Parameter parameter : parameters) {
          if (!parameter.isAnnotationPresent(RequestBody.class)) {
            continue;
          }

          Class<?> paramType = parameter.getType();
          Field[] fields = paramType.getDeclaredFields();
          Arrays.sort(fields, Comparator.comparing(Field::getName));

          processFields(fields, request, requestBody, result);
        }
      }

      // TODO: REAL HASHlize
      return result.toString();
    } catch (ClassNotFoundException e) {
      logger.warning("Failed to generate request hash. " + e.getMessage());
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

  private void processFields(Field[] fields, HttpServletRequest request, JsonNode requestBody,
      StringBuilder result) {
    for (Field field : fields) {
      String value = request.getParameter(field.getName());

      if (value == null && requestBody != null && requestBody.has(field.getName())) {
        value = requestBody.get(field.getName()).asText();
      }

      if (value != null) {
        result.append(field.getName()).append("=").append(value).append("\n");
      }
    }
  }
}
