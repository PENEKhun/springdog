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

package org.easypeelsecurity.springdog.agent;

/**
 * List of URIs that map to static web resources in the SpringDog Agent.
 *
 * @author PENEKhun
 */
public enum ViewStructures {

  MAIN("/", "/templates/content/main.html"),
  LOGIN("/login", "/templates/content/login.html");

  /**
   * The relative URL path that maps to the resource.
   */
  private final String urlPath;

  /**
   * The path to the resource.
   */
  private final String resourcePath;

  ViewStructures(String urlPath, String resourcePath) {
    this.urlPath = urlPath;
    this.resourcePath = resourcePath;
  }

  public String getUrlPath() {
    return urlPath;
  }

  public String getResourcePath() {
    return resourcePath;
  }

}
