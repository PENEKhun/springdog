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

package org.easypeelsecurity.springdog.domain.ratelimit;

import static org.easypeelsecurity.springdog.shared.enums.RuleStatus.ACTIVE;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import org.easypeelsecurity.springdog.domain.ratelimit.converter.EndpointConverter;
import org.easypeelsecurity.springdog.domain.ratelimit.model.Endpoint;
import org.easypeelsecurity.springdog.domain.ratelimit.model.EndpointChangelog;
import org.easypeelsecurity.springdog.domain.ratelimit.model.EndpointVersionControl;
import org.easypeelsecurity.springdog.shared.dto.EndpointDto;
import org.easypeelsecurity.springdog.shared.enums.EndpointChangeType;

import org.apache.cayenne.ObjectContext;

/**
 * Service class for version control of endpoints.
 *
 * @author PENEKhun
 */
@Service
public class VersionControlService {
  private final ObjectContext context;
  private final EndpointRepository endpointRepository;

  /**
   * Constructor.
   */
  public VersionControlService(
      @Qualifier("springdogContext") ObjectContext context, EndpointRepository endpointRepository) {
    this.context = context;
    this.endpointRepository = endpointRepository;
  }

  /**
   * Updates the version of endpoints based on the parsed endpoint data from the controller.
   * This method compares the parsed endpoints with the existing endpoints in the database,
   * generates a new version control object, and handles different scenarios such as first run,
   * no changes, or differences in endpoints.
   *
   * <p>The method performs the following actions:</p>
   * <ul>
   *   <li>Compares parsed endpoints with database endpoints</li>
   *   <li>Generates a new version control object</li>
   *   <li>Handles scenarios: first run, no changes, or differences</li>
   *   <li>For differences:
   *     <ul>
   *       <li>Identifies and removes disappeared endpoints</li>
   *       <li>Adds new endpoints</li>
   *       <li>Creates changelogs for modifications</li>
   *     </ul>
   *   </li>
   * </ul>
   *
   * @param parsedEndpointFromController A list of <code>EndpointDto</code> objects parsed from the controller
   */
  public void updateVersion(List<EndpointDto> parsedEndpointFromController) {
    var endpointsOnDatabase = endpointRepository.findAll(context);
    EndpointVersionCompare versionCompare =
        new EndpointVersionCompare(parsedEndpointFromController, endpointsOnDatabase);

    var nowFullHash = versionCompare.generateFullHashFromDto(parsedEndpointFromController);
    var newVersion = context.newObject(EndpointVersionControl.class);
    newVersion.setFullHashOfEndpoints(nowFullHash);

    switch (versionCompare.compare()) {
      case FIRST_RUN -> {
        for (EndpointDto endpointDto : parsedEndpointFromController) {
          Endpoint endpoint = EndpointConverter.toEntity(context, endpointDto);
          context.registerNewObject(endpoint);
        }
      }
      case SAME -> {
        // DO NOTHING
      }
      case DIFFERENT -> {
        List<Endpoint> disappearedEndpoints = new ArrayList<>();
        List<EndpointChangelog> changelogs = new ArrayList<>();

        for (Endpoint endpoint : endpointsOnDatabase) {
          if (parsedEndpointFromController.stream()
              .noneMatch(
                  endpointDto -> endpointDto.getMethodSignature().equals(endpoint.getMethodSignature()))) {
            disappearedEndpoints.add(endpoint);
            context.deleteObjects(endpoint);
            var changelog = context.newObject(EndpointChangelog.class);
            changelog.setReflectedVersion(newVersion);
            if (endpoint.getRuleStatus().equals(ACTIVE.name())) {
              changelog.setDetailString(EndpointChangeType.ENABLED_ENDPOINT_WAS_DELETED.getDescription());
              changelog.setChangeType(EndpointChangeType.ENABLED_ENDPOINT_WAS_DELETED.name());
            } else {
              changelog.setDetailString(EndpointChangeType.API_DELETED.getDescription());
              changelog.setChangeType(EndpointChangeType.API_DELETED.name());
            }
            changelog.setTargetMethodSignature(endpoint.getMethodSignature());
            changelog.setTargetPath(endpoint.getPath());
            changelog.setTargetMethod(endpoint.getHttpMethod());
            changelogs.add(changelog);
          }
        }

        /*
        TODO: 파라미터 변경추적
        endpointsOnDatabase.stream()
            .flatMap(endpointDto -> endpointDto.getParameters().stream()
                .filter(EndpointParameterDto::isEnabled)
                .filter(param -> !parametersOnDatabase.contains(param)))
            .forEach(disappearParam -> {
              EndpointDto endpoint = endpointsOnDatabase.stream()
                  .filter(item -> item.getParameters().contains(disappearParam))
                  .findFirst()
                  .orElseThrow();

              EndpointChangelogDto changeLog = EndpointChangelogDto.builder()
                  .changeType(ENABLED_PARAMETER_WAS_DELETED)
                  .targetMethodSignature(endpoint.getMethodSignature())
                  .targetMethod(endpoint.getHttpMethod().name())
                  .targetPath(endpoint.getPath())
                  .detailString(
                      "The Ratelimit operation associated with this endpoint" +
                          " was terminated because no enabled parameter '%s (%s)' was found."
                              .formatted(disappearParam.getName(), disappearParam.getType()))
                  .isResolved(false)
                  .reflectedVersion(newVersion)
                  .build();
              newVersion.addChangelog(changeLog);
            }); */

        List<Endpoint> added = new ArrayList<>();
        for (EndpointDto endpointDto : parsedEndpointFromController) {
          if (endpointsOnDatabase.stream()
              .noneMatch(endpoint -> endpoint.getMethodSignature().equals(endpointDto.getMethodSignature()))) {
            Endpoint endpoint = EndpointConverter.toEntity(context, endpointDto);
            context.registerNewObject(endpoint);
            added.add(endpoint);
            var changelog = context.newObject(EndpointChangelog.class);
            changelog.setReflectedVersion(newVersion);
            changelog.setDetailString(EndpointChangeType.API_ADDED.getDescription());
            changelog.setChangeType(EndpointChangeType.API_ADDED.name());
            changelog.setTargetMethodSignature(endpoint.getMethodSignature());
            changelog.setTargetPath(endpoint.getPath());
            changelog.setTargetMethod(endpoint.getHttpMethod());
            changelogs.add(changelog);
          }
        }

        for (EndpointChangelog changelog : changelogs) {
          newVersion.addToChangelogs(changelog);
        }
      }
    }

    context.commitChanges();
  }
}
