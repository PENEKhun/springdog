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

package org.easypeelsecurity.springdog.autoconfigure.controller.parser;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.easypeelsecurity.springdog.manager.ratelimit.EndpointRepository;
import org.easypeelsecurity.springdog.shared.ratelimit.ApiParameter;
import org.easypeelsecurity.springdog.shared.ratelimit.ApiParameterType;
import org.easypeelsecurity.springdog.shared.ratelimit.HttpMethod;
import org.easypeelsecurity.springdog.shared.ratelimit.ParsedMetadata;
import org.easypeelsecurity.springdog.shared.ratelimit.model.Endpoint;
import org.easypeelsecurity.springdog.shared.ratelimit.model.EndpointParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;

/**
 * Controller parser component.
 *
 * @author PENEKhun
 */
@Component("springdogControllerParserComponent")
public class ControllerParser {

  private static final HashMap<ParsedMetadata, List<ApiParameter>> RESULT = new HashMap<>();

  private final RequestMappingHandlerMapping handlerMapping;
  private final EndpointRepository endpointRepository;

  /**
   * Constructor.
   */
  public ControllerParser(RequestMappingHandlerMapping handlerMapping, EndpointRepository endpointRepository) {
    this.handlerMapping = handlerMapping;
    this.endpointRepository = endpointRepository;
  }

  /**
   * List all endpoints and parameters.
   */
  @Transactional
  @PostConstruct
  public void listEndpointsAndParameters() {
    Map<RequestMappingInfo, HandlerMethod> handlerMethods = handlerMapping.getHandlerMethods();
    handlerMethods.forEach((info, method) -> {
      String endPoint = info.getDirectPaths().toString();
      Set<RequestMethod> methods = info.getMethodsCondition().getMethods();
      HttpMethod httpMethod = null;
      if (!methods.isEmpty()) {
        httpMethod = HttpMethod.resolve(methods.iterator().next().asHttpMethod());
      }

      if (httpMethod == null) {
        return;
      }

      String fqcn = method.getBeanType() + "." + method.getMethod().getName();
      ParsedMetadata metadata = new ParsedMetadata(endPoint, fqcn, httpMethod);
      List<ApiParameter> parameters = new ArrayList<>();

      for (Parameter parameter : method.getMethod().getParameters()) {
        ApiParameter parameterItem =
            new ApiParameter(parameter.getName(), ApiParameterType.resolve(parameter.getAnnotations()));
        parameters.add(parameterItem);
      }

      RESULT.put(metadata, parameters);
    });

    List<Endpoint> endpoints = new ArrayList<>();
    RESULT.forEach((metadata, parameters) -> {
      List<EndpointParameter> endpointParameters =
          // if parameter.type == null then use ApiParameterType.QUERY
          parameters.stream()
              .map(parameter -> new EndpointParameter(parameter.name(), parameter.type() == null ? ApiParameterType.QUERY : parameter.type()))
              .toList();
      Endpoint endpoint = new Endpoint(metadata.endpoint(), metadata.httpMethod(), endpointParameters);
      endpoints.add(endpoint);
    });

    endpointRepository.saveAll(endpoints);
  }
}
