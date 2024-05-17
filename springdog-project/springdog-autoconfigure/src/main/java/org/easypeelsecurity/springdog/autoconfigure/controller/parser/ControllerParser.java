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
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.easypeelsecurity.springdog.manager.ratelimit.EndpointCommand;
import org.easypeelsecurity.springdog.manager.ratelimit.EndpointQuery;
import org.easypeelsecurity.springdog.manager.ratelimit.EndpointRepository;
import org.easypeelsecurity.springdog.manager.ratelimit.EndpointVersionControlRepository;
import org.easypeelsecurity.springdog.manager.ratelimit.VersionControlRepository;
import org.easypeelsecurity.springdog.shared.ratelimit.EndpointConverter;
import org.easypeelsecurity.springdog.shared.ratelimit.EndpointDto;
import org.easypeelsecurity.springdog.shared.ratelimit.EndpointHash;
import org.easypeelsecurity.springdog.shared.ratelimit.EndpointHashProvider;
import org.easypeelsecurity.springdog.shared.ratelimit.EndpointParameterDto;
import org.easypeelsecurity.springdog.shared.ratelimit.VersionCompare;
import org.easypeelsecurity.springdog.shared.ratelimit.model.ApiParameterType;
import org.easypeelsecurity.springdog.shared.ratelimit.model.Endpoint;
import org.easypeelsecurity.springdog.shared.ratelimit.model.EndpointChangeLog;
import org.easypeelsecurity.springdog.shared.ratelimit.model.EndpointChangeType;
import org.easypeelsecurity.springdog.shared.ratelimit.model.EndpointVersionControl;
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
  private final String SPRINGDOG_PACKAGE = "org.easypeelsecurity.springdog";
  private final RequestMappingHandlerMapping handlerMapping;
  //  private final EndpointRepository endpointRepository;
  private final EndpointQuery endpointQuery;
  private final EndpointCommand endpointCommand;
  private final EndpointRepository endpointRepository;
  private final EndpointVersionControlRepository endpointVersionControlRepository;
  private final VersionControlRepository versionControlRepository;

  /**
   * Constructor.
   */
  public ControllerParser(RequestMappingHandlerMapping handlerMapping, EndpointQuery endpointQuery,
      EndpointCommand endpointCommand, EndpointRepository endpointRepository,
      EndpointVersionControlRepository endpointVersionControlRepository,
      VersionControlRepository versionControlRepository) {
    this.handlerMapping = handlerMapping;
    this.endpointQuery = endpointQuery;
    this.endpointCommand = endpointCommand;
    this.endpointRepository = endpointRepository;
    this.endpointVersionControlRepository = endpointVersionControlRepository;
    this.versionControlRepository = versionControlRepository;
  }
  /*public ControllerParser(RequestMappingHandlerMapping handlerMapping, EndpointRepository endpointRepository) {
    this.handlerMapping = handlerMapping;
    this.endpointRepository = endpointRepository;
  }*/

  private static EndpointDto getEndpointDto(HandlerMethod method, String endPoint, HttpMethod httpMethod,
      boolean isPatternPath) {
    String fqcn = method.getBeanType().getPackageName() + "." + method.getBeanType().getSimpleName() + "." +
        method.getMethod().getName();

    EndpointDto api = new EndpointDto(endPoint, fqcn, httpMethod, isPatternPath);
    Set<EndpointParameterDto> parameters = new HashSet<>();

    for (Parameter parameter : method.getMethod().getParameters()) {
      EndpointParameterDto parameterItem =
          new EndpointParameterDto(parameter.getName(), ApiParameterType.resolve(parameter.getAnnotations()));
      parameters.add(parameterItem);
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
    Map<RequestMappingInfo, HandlerMethod> handlerMethods = handlerMapping.getHandlerMethods();
    handlerMethods.forEach((info, method) -> {
      // TODO : handle multiple paths
      String endpoint = null;
      boolean isPatternPath = false;
      // path condition or directPaths
      if (!info.getDirectPaths().isEmpty()) {
        if (info.getDirectPaths().size() > 1) {
          System.out.println(
              "Multiple paths found for " + method.getMethod().getName() + " in " + method.getBeanType() +
                  ". but not supported yet.");
          return;
        }

        endpoint = info.getDirectPaths().iterator().next();
      } else if (!info.getPatternValues().isEmpty()) {
        if (info.getPatternValues().size() > 1) {
          System.out.println(
              "Multiple paths found for " + method.getMethod().getName() + " in " + method.getBeanType() +
                  ". but not supported yet.");
          return;
        }

        isPatternPath = true;
        endpoint = info.getPatternValues().iterator().next();
      }

      if (endpoint == null) {
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

      EndpointDto api = getEndpointDto(method, endpoint, httpMethod, isPatternPath);
      if (!api.getFqcn().startsWith(SPRINGDOG_PACKAGE)) {
        RESULT.add(api);
      }
    });

    EndpointHash hashProvider = new EndpointHashProvider();

    LocalDateTime now = LocalDateTime.now();
    String nowFullHash = hashProvider.getHash(RESULT.toArray(EndpointDto[]::new));
    EndpointVersionControl vc = new EndpointVersionControl(now, nowFullHash);
    // 일단 기존 최신 버전 hash와 비교.
    VersionCompare nowVersion = endpointQuery.compareToLatestVersion(nowFullHash);

    switch (nowVersion) {
      case SAME:
        System.out.println("Same version. Skip.");
        break;
      case FIRST_RUN:
        // 그냥 등록
        System.out.println("First run. Save all.");
        List<Endpoint> endpointList = RESULT.stream()
            .map(item -> EndpointConverter.toEntity(hashProvider, item))
            .toList();

        endpointRepository.saveAll(endpointList);

        versionControlRepository.save(
            new EndpointVersionControl(now, nowFullHash)
        );

        break;
      case DIFFERENT:
        Set<EndpointDto> db = endpointQuery.findAll();

        // db에만 있는 것 (삭제된 것)
        Set<EndpointDto> dbOnly = db.stream()
            .filter(dbItem -> !RESULT.contains(dbItem))
            .collect(Collectors.toSet());

        // RESULT에만 있는 것 (추가된 것)
        Set<EndpointDto> nowOnly = RESULT.stream()
            .filter(localItem -> !db.contains(localItem))
            .collect(Collectors.toSet());
        // TODO: PARAMETER 비교도 포함

        dbOnly.forEach(item -> vc.addChangeLog(new EndpointChangeLog(vc, EndpointChangeType.API_DELETED,
            "[" + item.getHttpMethod() + "] " + item.getPath() + " was deleted")));
        nowOnly.forEach(item -> vc.addChangeLog(new EndpointChangeLog(vc, EndpointChangeType.API_ADDED,
            "[" + item.getHttpMethod() + "] " + item.getPath() + " was added")));

        endpointCommand.applyChanges(hashProvider, nowOnly, dbOnly);
        versionControlRepository.save(vc);
        break;
    }
  }
}
