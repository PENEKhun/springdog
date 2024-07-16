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

import java.util.logging.Logger;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import org.easypeelsecurity.springdog.shared.configuration.SpringdogProperties;

/**
 * Interceptor to measure and record the response times of HTTP requests.
 * This interceptor stores the duration of each request, categorized by the request method and URI.
 * The recorded times can be retrieved for monitoring and analysis purposes.
 *
 * @author PENEKhun
 */
@Component
public class RequestTimingInterceptor implements HandlerInterceptor {

  private final SpringdogProperties springdogProperties;
  private final Logger logger = Logger.getLogger(RequestTimingInterceptor.class.getName());
  private static final String SKIP_REQUEST_TIMING_CALC = "SKIP_REQUEST_TIMING_CALC";
  private static final String START_TIME_REQUEST_TIMING = "START_TIME_REQUEST_TIMING";

  /**
   * Constructor.
   */
  public RequestTimingInterceptor(SpringdogProperties springdogProperties) {
    this.springdogProperties = springdogProperties;
  }

  /**
   * Records the start time of the request.
   */
  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    request.setAttribute(SKIP_REQUEST_TIMING_CALC, true);
    request.setAttribute(START_TIME_REQUEST_TIMING, System.currentTimeMillis());

    if (handler instanceof HandlerMethod handlerMethod) {
      Object controller = handlerMethod.getBean();
      Class<?> controllerClass = controller.getClass();
      if (!shouldSkipRequest(request, controllerClass)) {
        request.setAttribute(SKIP_REQUEST_TIMING_CALC, false);
        return true;
      }
    }
    return true;
  }

  /**
   * Calculates the duration of the request and records it.
   */
  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
      Exception ex) {
    boolean shouldSkip = (boolean) request.getAttribute(SKIP_REQUEST_TIMING_CALC);
    if (shouldSkip) {
      return;
    }

    long startTime = (Long) request.getAttribute(START_TIME_REQUEST_TIMING);
    long endTime = System.currentTimeMillis();
    long duration = endTime - startTime;

    HandlerMethod handlerMethod = (HandlerMethod) handler;
    String fqmn = handlerMethod.getBeanType().getName() + "." + handlerMethod.getMethod().getName();
    EndpointMetricCache.addResponseTime(fqmn, duration);
    System.out.println(fqmn);
  }

  private boolean shouldSkipRequest(HttpServletRequest request, Class<?> controllerClass) {
    if (controllerClass.equals(
        org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController.class)) {
      return true;
    }
    String requestPath = request.getRequestURI().substring(request.getContextPath().length());
    return requestPath.startsWith(springdogProperties.computeAbsolutePath(""));
  }
}
