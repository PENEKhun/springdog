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

import java.io.IOException;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import org.easypeelsecurity.springdog.domain.ratelimit.VersionControlService;
import org.easypeelsecurity.springdog.shared.configuration.SpringdogProperties;
import org.easypeelsecurity.springdog.shared.dto.EndpointDto;
import org.easypeelsecurity.springdog.shared.dto.EndpointHeaderDto;
import org.easypeelsecurity.springdog.shared.dto.EndpointParameterDto;
import org.easypeelsecurity.springdog.shared.enums.EndpointParameterType;
import org.easypeelsecurity.springdog.shared.enums.HttpMethod;
import org.easypeelsecurity.springdog.shared.util.Assert;
import org.easypeelsecurity.springdog.shared.util.MethodSignatureParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller parser component.
 *
 * @author PENEKhun
 */
@Component("springdogControllerParserComponent")
public class ControllerParser {

  private final Logger logger = LoggerFactory.getLogger(ControllerParser.class);
  private final RequestMappingHandlerMapping handlerMapping;
  private final VersionControlService versionControlService;
  private final SpringdogProperties properties;

  /**
   * Constructor.
   */
  public ControllerParser(
      @Qualifier("requestMappingHandlerMapping") RequestMappingHandlerMapping handlerMapping,
      VersionControlService versionControlService, SpringdogProperties properties) {
    this.handlerMapping = handlerMapping;
    this.versionControlService = versionControlService;
    this.properties = properties;
  }

  private EndpointDto getEndpointDto(HandlerMethod method, String endPoint, HttpMethod httpMethod,
      boolean isPatternPath) {
    String methodSignature =
        MethodSignatureParser.parse(method);

    EndpointDto endpoint = EndpointDto.builder()
        .methodSignature(methodSignature)
        .path(endPoint)
        .httpMethod(httpMethod)
        .isPatternPath(isPatternPath).build();
    Set<EndpointParameterDto> parameters = parseRequestParameters(method);
    Set<EndpointHeaderDto> headers = parseRequestHeaders(method);
    endpoint.addParameters(parameters);
    endpoint.addHeaders(headers);
    return endpoint;
  }

  private Set<EndpointHeaderDto> parseRequestHeaders(HandlerMethod method) {
    Set<EndpointHeaderDto> headers = new HashSet<>();
    Parameter[] parameters = method.getMethod().getParameters();
    for (Parameter parameter : parameters) {
      RequestHeader requestHeaderAnnotation = parameter.getAnnotation(RequestHeader.class);
      if (requestHeaderAnnotation != null) {
        String headerName = requestHeaderAnnotation.value();
        if (headerName.isEmpty()) {
          headerName = parameter.getName();
        }
        headers.add(new EndpointHeaderDto(headerName, false));
      }
    }
    return headers;
  }

  private Set<EndpointParameterDto> parseRequestParameters(HandlerMethod method) {
    Set<EndpointParameterDto> parameters = new HashSet<>();
    try {
      String[] paramNames =
          ParameterNameExtractor.getParameterNames(method.getBeanType(), method.getMethod().getName(),
              method.getMethod().getParameterTypes());
      Parameter[] methodParameters = method.getMethod().getParameters();
      for (int i = 0; i < methodParameters.length; i++) {
        if (methodParameters[i].getAnnotation(RequestHeader.class) != null) {
          continue;
        }
        String name =
            paramNames != null && i < paramNames.length ? paramNames[i] : methodParameters[i].getName();
        parameters.add(
            new EndpointParameterDto(name, EndpointParameterType.resolve(
                methodParameters[i].getAnnotations()), false));
      }
    } catch (IOException | NoSuchMethodException e) {
      logger.error("Error while extracting parameter names.", e);
    }
    return parameters;
  }

  /**
   * List all endpoints and parameters.
   */
  @PostConstruct
  public void listEndpointsAndParameters() {
    Assert.notNull(handlerMapping, "RequestMappingHandlerMapping is required.");
    Assert.notNull(properties, "SpringdogProperties is required.");

    List<EndpointDto> parsedEndpointFromController =
        parseController(handlerMapping.getHandlerMethods(), properties.computeAbsolutePath("/"));
    versionControlService.updateVersion(parsedEndpointFromController);
  }

  List<EndpointDto> parseController(Map<RequestMappingInfo, HandlerMethod> handlerMethods,
      String excludePathPrefix) {
    List<EndpointDto> result = new ArrayList<>();
    for (Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
      RequestMappingInfo info = entry.getKey();
      HandlerMethod method = entry.getValue();

      String endpoint = extractEndpointPath(info, method);
      if (endpoint == null || endpoint.startsWith(excludePathPrefix)) {
        continue;
      }
      HttpMethod httpMethod = extractHttpMethod(info);
      if (httpMethod == null) {
        continue;
      }

      boolean isPatternPath = info.getDirectPaths().isEmpty();
      // Create and add EndpointDto
      result.add(getEndpointDto(method, endpoint, httpMethod, isPatternPath));
    }
    return result;
  }

  private String extractEndpointPath(RequestMappingInfo info, HandlerMethod method) {
    if (!info.getDirectPaths().isEmpty()) {
      if (info.getDirectPaths().size() > 1) {
        logger.warn("Multiple paths found for {} in {} (directPath). but not supported yet.",
            method.getMethod().getName(),
            method.getBeanType());
        return null;
      }
      return info.getDirectPaths().iterator().next();
    } else if (!info.getPatternValues().isEmpty()) {
      if (info.getPatternValues().size() > 1) {
        logger.warn("Multiple paths found for {} in {} (patternUrl). but not supported yet.",
            method.getMethod().getName(),
            method.getBeanType());
        return null;
      }
      return info.getPatternValues().iterator().next();
    }
    return null;
  }

  private HttpMethod extractHttpMethod(RequestMappingInfo info) {
    Set<RequestMethod> methods = info.getMethodsCondition().getMethods();
    if (!methods.isEmpty()) {
      return HttpMethod.resolve(methods.iterator().next().asHttpMethod());
    }
    return null;
  }
}
