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

package org.easypeelsecurity.springdog.shared.ratelimit.model;

import org.springframework.util.Assert;

/**
 * Enum for HTTP methods.
 *
 * @author PENEKhun
 */
public enum HttpMethod {
  GET,
  HEAD,
  POST,
  PUT,
  PATCH,
  DELETE,
  OPTIONS,
  TRACE;

  /**
   * Resolve a given Spring-dependent method as springdog's {@code HttpMethod}.
   *
   * @param httpMethod the Spring method to resolve
   * @return the corresponding springdog {@code HttpMethod}, or {@code null} if not found
   */
  public static HttpMethod resolve(org.springframework.http.HttpMethod httpMethod) {
    Assert.notNull(httpMethod, "HttpMethod must not be null");

    if (httpMethod.equals(org.springframework.http.HttpMethod.GET)) {
      return GET;
    } else if (httpMethod.equals(org.springframework.http.HttpMethod.HEAD)) {
      return HEAD;
    } else if (httpMethod.equals(org.springframework.http.HttpMethod.POST)) {
      return POST;
    } else if (httpMethod.equals(org.springframework.http.HttpMethod.PUT)) {
      return PUT;
    } else if (httpMethod.equals(org.springframework.http.HttpMethod.PATCH)) {
      return PATCH;
    } else if (httpMethod.equals(org.springframework.http.HttpMethod.DELETE)) {
      return DELETE;
    } else if (httpMethod.equals(org.springframework.http.HttpMethod.OPTIONS)) {
      return OPTIONS;
    } else if (httpMethod.equals(org.springframework.http.HttpMethod.TRACE)) {
      return TRACE;
    }
    return null;
  }
}
