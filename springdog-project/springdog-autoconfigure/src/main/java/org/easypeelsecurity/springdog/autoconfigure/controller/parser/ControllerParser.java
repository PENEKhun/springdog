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

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import org.easypeelsecurity.springdog.manager.ratelimit.EndpointCommand;
import org.easypeelsecurity.springdog.manager.ratelimit.EndpointQuery;
import org.easypeelsecurity.springdog.shared.configuration.SpringdogProperties;
import org.easypeelsecurity.springdog.shared.ratelimit.EndpointConverter;
import org.easypeelsecurity.springdog.shared.ratelimit.EndpointDto;
import org.easypeelsecurity.springdog.shared.ratelimit.EndpointParameterDto;
import org.easypeelsecurity.springdog.shared.ratelimit.EndpointVersionCompare;
import org.easypeelsecurity.springdog.shared.ratelimit.model.Endpoint;
import org.easypeelsecurity.springdog.shared.ratelimit.model.EndpointChangelog;
import org.easypeelsecurity.springdog.shared.ratelimit.model.EndpointParameterType;
import org.easypeelsecurity.springdog.shared.ratelimit.model.EndpointVersionControl;
import org.easypeelsecurity.springdog.shared.ratelimit.model.HttpMethod;
import org.easypeelsecurity.springdog.shared.ratelimit.model.RuleStatus;
import org.easypeelsecurity.springdog.shared.util.Assert;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.configuration.CayenneRuntime;
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
  private final EndpointQuery endpointQuery;
  private final EndpointCommand endpointCommand;
  private final SpringdogProperties properties;
  private final CayenneRuntime springdogRepository;

  /**
   * Constructor.
   */
  public ControllerParser(RequestMappingHandlerMapping handlerMapping, EndpointQuery endpointQuery,
      EndpointCommand endpointCommand, @Qualifier("springdogRepository") CayenneRuntime springdogRepository,
      SpringdogProperties properties) {
    this.handlerMapping = handlerMapping;
    this.endpointQuery = endpointQuery;
    this.endpointCommand = endpointCommand;
    this.springdogRepository = springdogRepository;
    this.properties = properties;
  }

  private EndpointDto getEndpointDto(HandlerMethod method, String endPoint, HttpMethod httpMethod,
      boolean isPatternPath) {
    String fqmn = method.getBeanType().getPackageName() + "." + method.getBeanType().getSimpleName() + "." +
        method.getMethod().getName();

    EndpointDto api = new EndpointDto.Builder()
        .fqmn(fqmn)
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
        String name =
            paramNames != null && i < paramNames.length ? paramNames[i] : methodParameters[i].getName();
        parameters.add(
            new EndpointParameterDto(name, EndpointParameterType.resolve(
                methodParameters[i].getAnnotations()), false));
      }
    } catch (IOException | NoSuchMethodException e) {
      logger.error("Error while extracting parameter names.", e);
    }
    api.addParameters(parameters);
    return api;
  }

  /**
   * List all endpoints and parameters.
   */
  @PostConstruct
  public void listEndpointsAndParameters() {
    Assert.notNull(handlerMapping, "RequestMappingHandlerMapping is required.");
    Assert.notNull(properties, "SpringdogProperties is required.");

    Set<EndpointDto> parsedEndpointFromController =
        parseController(handlerMapping.getHandlerMethods(), properties.computeAbsolutePath("/"));
    ObjectContext context = springdogRepository.newContext();
    Set<EndpointDto> endpointsOnDatabase = endpointQuery.findAll();

    EndpointVersionCompare versionCompare =
        new EndpointVersionCompare(parsedEndpointFromController.toArray(EndpointDto[]::new),
            endpointsOnDatabase.toArray(EndpointDto[]::new));
    String nowFullHash = versionCompare.getNewVersionHash();
    switch (versionCompare.compare()) {
      case SAME -> logger.info("No changes found.");
      case FIRST_RUN -> {
        logger.info("First run. Registering all endpoints.");

        EndpointVersionControl newVersion = context.newObject(EndpointVersionControl.class);
        newVersion.setDateOfVersion(LocalDateTime.now());
        newVersion.setFullHashOfEndpoints(nowFullHash);
        List<Endpoint> endpointList = parsedEndpointFromController.stream()
            .map(item -> EndpointConverter.toEntity(context, item))
            .toList();
      }
      case DIFFERENT -> {
        logger.info("Changes found. Applying changes.");

        EndpointVersionControl newVersion = context.newObject(EndpointVersionControl.class);
        newVersion.setDateOfVersion(LocalDateTime.now());
        newVersion.setFullHashOfEndpoints(nowFullHash);
        Set<EndpointParameterDto> parametersOnDatabase = endpointQuery.findAllParameters();
        Set<EndpointDto> disappearedEndpoints = endpointsOnDatabase.stream()
            .filter(dbItem -> !parsedEndpointFromController.contains(dbItem))
            .collect(Collectors.toSet());

        disappearedEndpoints.stream()
            .filter(dbItem -> dbItem.getRuleStatus().equals(RuleStatus.ACTIVE))
            .forEach(disappearedActiveEndpoint -> {
              EndpointChangelog changelog = context.newObject(EndpointChangelog.class);
              changelog.setChangeType(ENABLED_ENDPOINT_WAS_DELETED);
              changelog.setTargetFqmn(disappearedActiveEndpoint.getFqmn());
              changelog.setTargetMethod(disappearedActiveEndpoint.getHttpMethod().name());
              changelog.setTargetPath(disappearedActiveEndpoint.getPath());
              changelog.setDetailString(
                  "The associated Ratelimit setting was automatically cancelled.");
              changelog.setIsResolved(false);
              changelog.setReflectedVersion(newVersion);
              newVersion.addToChangelogs(changelog);
            });

        endpointsOnDatabase.stream()
            .flatMap(endpointDto -> endpointDto.getParameters().stream()
                .filter(EndpointParameterDto::isEnabled)
                .filter(param -> !parametersOnDatabase.contains(param)))
            .forEach(disappearParam -> {
              EndpointDto endpoint = endpointsOnDatabase.stream()
                  .filter(item -> item.getParameters().contains(disappearParam))
                  .findFirst()
                  .orElseThrow();

              EndpointChangelog changeLog = context.newObject(EndpointChangelog.class);
              changeLog.setChangeType(ENABLED_PARAMETER_WAS_DELETED);
              changeLog.setTargetFqmn(endpoint.getFqmn());
              changeLog.setTargetMethod(endpoint.getHttpMethod().name());
              changeLog.setTargetPath(endpoint.getPath());
              changeLog.setDetailString(
                  "The Ratelimit operation associated with this endpoint" +
                      " was terminated because no enabled parameter '%s (%s)' was found."
                          .formatted(disappearParam.getName(), disappearParam.getType()));
              changeLog.setIsResolved(false);
              changeLog.setReflectedVersion(newVersion);
              endpoint.setRuleStatus(RuleStatus.INACTIVE);
              newVersion.addToChangelogs(changeLog);
            });

        Set<EndpointDto> added = parsedEndpointFromController.stream()
            .filter(localItem -> !endpointsOnDatabase.contains(localItem))
            .collect(Collectors.toSet());
        endpointCommand.applyChanges(context, added, disappearedEndpoints);
      }
    }

    context.commitChanges();
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
