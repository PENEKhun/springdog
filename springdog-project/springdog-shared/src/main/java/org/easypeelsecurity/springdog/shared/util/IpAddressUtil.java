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
package org.easypeelsecurity.springdog.shared.util;

import java.util.Objects;
import java.util.stream.Stream;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Utility class for IP address operations.
 */
public abstract class IpAddressUtil {

  private static final String[] HEADERS_TO_TRY = {
      "X-Forwarded-For",
      "Proxy-Client-IP",
      "WL-Proxy-Client-IP",
      "HTTP_CLIENT_IP",
      "HTTP_X_FORWARDED_FOR"
  };

  /**
   * Get client IP address.
   *
   * @param request HttpServletRequest for getting IP address
   * @return IPv4 String
   */
  public static String getClientIpv4(HttpServletRequest request) {
    return Stream.of(HEADERS_TO_TRY)
        .map(request::getHeader)
        .filter(Objects::nonNull)
        .findFirst()
        .orElse(request.getRemoteAddr());
  }
}
