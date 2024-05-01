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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.easypeelsecurity.springdog.manager.ratelimit.EndpointRepository;
import org.easypeelsecurity.springdog.shared.ratelimit.EndpointConverter;
import org.easypeelsecurity.springdog.shared.ratelimit.EndpointDto;
import org.easypeelsecurity.springdog.shared.ratelimit.EndpointParameterDto;
import org.easypeelsecurity.springdog.shared.ratelimit.model.ApiParameterType;
import org.easypeelsecurity.springdog.shared.ratelimit.model.HttpMethod;
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

  private static final Set<EndpointDto> RESULT = new HashSet<>();

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
      // TODO : handle multiple paths
      String endPoint = null;
      if (!info.getDirectPaths().isEmpty()) {
        if (info.getDirectPaths().size() > 1) {
          System.out.println(
              "Multiple paths found for " + method.getMethod().getName() + " in " + method.getBeanType() +
                  ". but not supported yet.");
          return;
        }

        endPoint = info.getDirectPaths().iterator().next();
      }

      if (endPoint == null) {
        return;
      }

      Set<RequestMethod> methods = info.getMethodsCondition().getMethods();
      HttpMethod httpMethod = null;
      if (!methods.isEmpty()) {
        httpMethod = HttpMethod.resolve(methods.iterator().next().asHttpMethod());
      }

      if (httpMethod == null) {
        return;
      }

      EndpointDto api = getEndpointDto(method, endPoint, httpMethod);
      if (!api.getFqcn().contains("org.easypeelsecurity.springdog")) {
        RESULT.add(api);
      }
    });

    endpointRepository.saveAll(
        RESULT.stream()
            .map(EndpointConverter::toEntity)
            .toList());
  }

  private static EndpointDto getEndpointDto(HandlerMethod method, String endPoint, HttpMethod httpMethod) {
    String fqcn = method.getBeanType() + "." + method.getMethod().getName();

    EndpointDto api = new EndpointDto(endPoint, fqcn, httpMethod);
    Set<EndpointParameterDto> parameters = new HashSet<>();

    for (Parameter parameter : method.getMethod().getParameters()) {
      EndpointParameterDto parameterItem =
          new EndpointParameterDto(parameter.getName(), ApiParameterType.resolve(parameter.getAnnotations()));
      parameters.add(parameterItem);
    }

    api.addParameters(parameters);
    return api;
  }
}
