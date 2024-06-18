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

import static org.easypeelsecurity.springdog.shared.ratelimit.model.EndpointChangeType.ENABLED_ENDPOINT_WAS_DELETED;
import static org.easypeelsecurity.springdog.shared.ratelimit.model.EndpointChangeType.ENABLED_PARAMETER_WAS_DELETED;

import java.io.IOException;
import java.lang.reflect.Parameter;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.easypeelsecurity.springdog.manager.ratelimit.EndpointCommand;
import org.easypeelsecurity.springdog.manager.ratelimit.EndpointQuery;
import org.easypeelsecurity.springdog.manager.ratelimit.EndpointRepository;
import org.easypeelsecurity.springdog.manager.ratelimit.VersionControlRepository;
import org.easypeelsecurity.springdog.shared.configuration.SpringdogProperties;
import org.easypeelsecurity.springdog.shared.ratelimit.EndpointConverter;
import org.easypeelsecurity.springdog.shared.ratelimit.EndpointDto;
import org.easypeelsecurity.springdog.shared.ratelimit.EndpointHash;
import org.easypeelsecurity.springdog.shared.ratelimit.EndpointHashProvider;
import org.easypeelsecurity.springdog.shared.ratelimit.EndpointParameterDto;
import org.easypeelsecurity.springdog.shared.ratelimit.model.ApiParameterType;
import org.easypeelsecurity.springdog.shared.ratelimit.model.Endpoint;
import org.easypeelsecurity.springdog.shared.ratelimit.model.EndpointChangeLog;
import org.easypeelsecurity.springdog.shared.ratelimit.model.EndpointVersionControl;
import org.easypeelsecurity.springdog.shared.ratelimit.model.HttpMethod;
import org.easypeelsecurity.springdog.shared.ratelimit.model.RuleStatus;
import org.easypeelsecurity.springdog.shared.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

  private final Logger logger = LoggerFactory.getLogger(ControllerParser.class);
  private final RequestMappingHandlerMapping handlerMapping;
  private final EndpointQuery endpointQuery;
  private final EndpointCommand endpointCommand;
  private final EndpointRepository endpointRepository;
  private final SpringdogProperties properties;
  private final VersionControlRepository versionControlRepository;

  /**
   * Constructor.
   */
  public ControllerParser(RequestMappingHandlerMapping handlerMapping, EndpointQuery endpointQuery,
      EndpointCommand endpointCommand, EndpointRepository endpointRepository, SpringdogProperties properties,
      VersionControlRepository versionControlRepository) {
    this.handlerMapping = handlerMapping;
    this.endpointQuery = endpointQuery;
    this.endpointCommand = endpointCommand;
    this.endpointRepository = endpointRepository;
    this.versionControlRepository = versionControlRepository;
    this.properties = properties;
  }

  private EndpointParameterDto getEndpointParameterDto(Parameter parameter, String[] paramNames,
      int index) {
    String name = paramNames != null && index < paramNames.length ? paramNames[index] : parameter.getName();
    return new EndpointParameterDto(name, ApiParameterType.resolve(parameter.getAnnotations()));
  }

  private EndpointDto getEndpointDto(HandlerMethod method, String endPoint, HttpMethod httpMethod,
      boolean isPatternPath) {
    String fqcn = method.getBeanType().getPackageName() + "." + method.getBeanType().getSimpleName() + "." +
        method.getMethod().getName();

    EndpointDto api = new EndpointDto.Builder()
        .fqcn(fqcn)
        .path(endPoint)
        .httpMethod(httpMethod)
        .isPatternPath(isPatternPath).build();
    Set<EndpointParameterDto> parameters = new HashSet<>();

    try {
      String[] paramNames =
          ParameterNameExtractor.getParameterNames(method.getBeanType(), method.getMethod().getName(),
              method.getMethod().getParameterTypes());

      Parameter[] methodParameters = method.getMethod().getParameters();
      for (int i = 0; i < methodParameters.length; i++) {
        EndpointParameterDto parameterItem = getEndpointParameterDto(methodParameters[i], paramNames, i);
        parameters.add(parameterItem);
      }
    } catch (IOException | NoSuchMethodException e) {
      e.printStackTrace();
    }

    api.addParameters(parameters);
    return api;
  }

  /**
   * List all endpoints and parameters.
   */
  @Transactional
  @PostConstruct
  public void listEndpointsAndParameters() {
    Assert.notNull(handlerMapping, "RequestMappingHandlerMapping is required.");
    Assert.notNull(properties, "SpringdogProperties is required.");

    Set<EndpointDto> parsedEndpointFromController =
        parseController(handlerMapping.getHandlerMethods(), properties.computeAbsolutePath("/"));

    EndpointHash hashProvider = new EndpointHashProvider();
    String nowFullHash = hashProvider.getHash(parsedEndpointFromController.toArray(EndpointDto[]::new));
    EndpointVersionControl newVersion = new EndpointVersionControl(LocalDateTime.now(), nowFullHash);
    switch (endpointQuery.compareToLatestVersion(nowFullHash,
        versionControlRepository.findTopByOrderByDateOfVersionDesc())) {
      case SAME -> logger.info("No changes found.");
      case FIRST_RUN -> {
        logger.info("First run. Registering all endpoints.");
        List<Endpoint> endpointList = parsedEndpointFromController.stream()
            .map(item -> EndpointConverter.toEntity(hashProvider, item))
            .toList();

        endpointRepository.saveAll(endpointList);
        versionControlRepository.save(newVersion);
      }
      case DIFFERENT -> {
        logger.info("Changes found. Applying changes.");
        Set<EndpointDto> endpointsOnDatabase = endpointQuery.findAll();
        Set<EndpointParameterDto> parametersOnDatabase = endpointQuery.findAllParameters();
        Set<EndpointDto> disappearedEndpoints = endpointsOnDatabase.stream()
            .filter(dbItem -> !parsedEndpointFromController.contains(dbItem))
            .collect(Collectors.toSet());

        disappearedEndpoints.stream()
            .filter(dbItem -> dbItem.getRuleStatus().equals(RuleStatus.ACTIVE))
            .forEach(disappearedActiveEndpoint ->
                newVersion.addChangeLog(
                    new EndpointChangeLog(
                        disappearedActiveEndpoint.getPath(),
                        disappearedActiveEndpoint.getHttpMethod(),
                        disappearedActiveEndpoint.getFqcn(),
                        ENABLED_ENDPOINT_WAS_DELETED,
                        "The associated Ratelimit setting was automatically cancelled.",
                        false)));

        Set<EndpointParameterDto> parametersOnParsed = parsedEndpointFromController.stream()
            .map(EndpointDto::getParameters)
            .flatMap(Set::stream)
            .collect(Collectors.toSet());

        parametersOnDatabase.stream()
            .filter(item -> !parametersOnParsed.contains(item))
            .filter(EndpointParameterDto::isEnabled)
            .forEach(disappearedParameter -> {
              EndpointDto endpoint = endpointsOnDatabase.stream()
                  .filter(item -> item.getParameters().contains(disappearedParameter))
                  .findFirst()
                  .orElseThrow();

              newVersion.addChangeLog(new EndpointChangeLog(
                  endpoint.getPath(),
                  endpoint.getHttpMethod(),
                  endpoint.getFqcn(),
                  ENABLED_PARAMETER_WAS_DELETED,
                  ("The Ratelimit operation associated with this endpoint" +
                      " was terminated because no enabled parameter '%s (%s)' was found.")
                      .formatted(disappearedParameter.getName(), disappearedParameter.getType()),
                  false));
            });

        Set<EndpointDto> added = parsedEndpointFromController.stream()
            .filter(localItem -> !endpointsOnDatabase.contains(localItem))
            .collect(Collectors.toSet());
        endpointCommand.applyChanges(hashProvider, added, disappearedEndpoints);
        versionControlRepository.save(newVersion);
      }
    }
  }

  Set<EndpointDto> parseController(Map<RequestMappingInfo, HandlerMethod> handlerMethods,
      String excludePathPrefix) {
    Set<EndpointDto> result = new HashSet<>();
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
