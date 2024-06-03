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

package org.easypeelsecurity.springdog.manager.agent;

import org.easypeelsecurity.springdog.shared.configuration.SpringdogProperties;
import org.easypeelsecurity.springdog.shared.util.IpAddressUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * A pre-handle for filtering agent connections.
 */
@Service
public class AgentExternalAccessInterceptor implements HandlerInterceptor {

  private final SpringdogProperties properties;

  /**
   * Constructor.
   */
  public AgentExternalAccessInterceptor(SpringdogProperties properties) {
    this.properties = properties;
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    if (!isAgentRequest(request)) {
      return true;
    }

    if (properties.enableExternalAccess()) {
      System.out.println("ENABLED JUST PASS".repeat(100));
      return true;
    }

    String ipAddress = IpAddressUtil.getClientIp(request);
    if (!IpAddressUtil.isLocal(ipAddress)) {
      response.setStatus(HttpServletResponse.SC_FORBIDDEN);
      return false;
    }
    return true;
  }

  private boolean isAgentRequest(HttpServletRequest request) {
    return request.getRequestURI().startsWith(properties.computeAbsolutePath(""));
  }
}
