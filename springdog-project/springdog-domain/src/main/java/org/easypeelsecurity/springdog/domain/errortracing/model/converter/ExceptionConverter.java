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

package org.easypeelsecurity.springdog.domain.errortracing.model.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.easypeelsecurity.springdog.domain.errortracing.model.ExceptionClass;
import org.easypeelsecurity.springdog.domain.errortracing.model.ExceptionType;
import org.easypeelsecurity.springdog.shared.dto.ExceptionClassesDto;
import org.easypeelsecurity.springdog.shared.dto.ExceptionClassesDto.ExceptionListDto;
import org.easypeelsecurity.springdog.shared.dto.ExceptionClassesDto.ExceptionListDto.ExceptionItemDto;

import org.apache.cayenne.ObjectContext;

/**
 * Converter class for transforming between ExceptionClassesDto and Entity classes (ExceptionClass,
 * ExceptionType).
 */
public abstract class ExceptionConverter {

  /**
   * Converts ExceptionClassesDto to a list of ExceptionType and ExceptionClass entities.
   *
   * @param dto The ExceptionClassesDto to convert
   * @return A list containing ExceptionType and ExceptionClass entities
   */
  public static List<Object> convertDtoToEntities(ObjectContext context, ExceptionClassesDto dto) {
    List<Object> entities = new ArrayList<>();

    for (ExceptionListDto exceptionList : dto.exceptionList()) {
      ExceptionType exceptionType = context.newObject(ExceptionType.class);
      exceptionType.setPackageType(exceptionList.packageType());
      exceptionType.setDescription(exceptionList.description());
      entities.add(exceptionType);

      for (ExceptionItemDto item : exceptionList.subExceptions()) {
        ExceptionClass exceptionClass = context.newObject(ExceptionClass.class);
        exceptionClass.setExceptionClassName(item.exceptionName());
        exceptionClass.setRootExceptionPackageType(exceptionType);
        exceptionClass.setMonitoringEnabled(item.isEnableToMonitor());
        entities.add(exceptionClass);
      }
    }

    return entities;
  }

  /**
   * Converts a list of ExceptionType and ExceptionClass entities to ExceptionClassesDto.
   *
   * @return The converted ExceptionClassesDto
   */
  public static ExceptionClassesDto convertEntitiesToDto(List<ExceptionType> exceptionTypes) {

    List<ExceptionListDto> exceptionLists = exceptionTypes.stream()
        .map(type -> {
          List<ExceptionItemDto> subExceptions = type.getExceptionClasses().stream()
              .map(subException -> ExceptionItemDto.builder()
                  .exceptionId(subException.getId())
                  .exceptionName(subException.getExceptionClassName())
                  .isEnableToMonitor(subException.isMonitoringEnabled())
                  .build())
              .collect(Collectors.toList());

          return ExceptionListDto.builder()
              .packageTypeId(type.getId())
              .packageType(type.getPackageType())
              .description(type.getDescription())
              .subExceptions(subExceptions)
              .build();
        }).toList();

    return new ExceptionClassesDto(exceptionLists);
  }

  /**
   * Converts an ExceptionItemDto to an ExceptionClass entity.
   *
   * @param context           The ObjectContext to create the entity in
   * @param existType         The ExceptionType to add the new ExceptionClass to
   * @param newExceptionClass The ExceptionItemDto to convert
   * @return The converted ExceptionClass entity
   */
  public static ExceptionClass convertDtoToEntity(ObjectContext context, ExceptionType existType,
      ExceptionItemDto newExceptionClass) {
    ExceptionClass exceptionClass = context.newObject(ExceptionClass.class);
    exceptionClass.setExceptionClassName(newExceptionClass.exceptionName());
    exceptionClass.setRootExceptionPackageType(existType);
    existType.addToExceptionClasses(exceptionClass);
    exceptionClass.setMonitoringEnabled(newExceptionClass.isEnableToMonitor());
    return exceptionClass;
  }
}
