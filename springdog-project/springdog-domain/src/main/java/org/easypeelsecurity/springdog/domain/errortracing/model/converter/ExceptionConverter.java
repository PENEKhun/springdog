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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.easypeelsecurity.springdog.domain.errortracing.model.ExceptionCause;
import org.easypeelsecurity.springdog.domain.errortracing.model.ExceptionClass;
import org.easypeelsecurity.springdog.domain.errortracing.model.ExceptionType;
import org.easypeelsecurity.springdog.shared.dto.ErrorTracingDto;
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
   * Converts an ExceptionCause entity to an ErrorTracingDto.
   *
   * @param exceptionCause The ExceptionCause entity to convert
   * @return The converted ErrorTracingDto
   */
  public static ErrorTracingDto entityToErrorTracingDto(ExceptionCause exceptionCause) {
    if (exceptionCause == null) {
      return null;
    }

    ErrorTracingDto result = ErrorTracingDto.builder()
        .id(exceptionCause.getId())
        .message(exceptionCause.getMessage())
        .fileName(exceptionCause.getFileName())
        .className(exceptionCause.getClassName())
        .methodName(exceptionCause.getMethodName())
        .lineNumber(exceptionCause.getLine())
        .timestamp(exceptionCause.getTimestamp())
        .build();

    if (exceptionCause.getNextException() != null) {
      var next = entityToErrorTracingDto(exceptionCause.getNextException());
      next.setParentTraceId(result.getId());
      result.setNext(next);
    }

    return result;
  }

  /**
   * Converts a list of ExceptionCause entities to ErrorTracingDto.
   *
   * @param exceptionCauses The list of ExceptionCause entities to convert
   * @return The converted ErrorTracingDto
   */
  public static List<ErrorTracingDto> entityToErrorTracingDto(List<ExceptionCause> exceptionCauses) {
    if (exceptionCauses == null || exceptionCauses.isEmpty()) {
      return new ArrayList<>();
    }

    List<ErrorTracingDto> result = new ArrayList<>();
    for (ExceptionCause exceptionCause : exceptionCauses) {
      result.add(entityToErrorTracingDto(exceptionCause));
    }
    return result;
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

  /**
   * Converts an ErrorTracingDto to an ExceptionCause entity.
   *
   * @param context    The ObjectContext to create the entity in
   * @param errorChain The ErrorTracingDto to convert
   * @return The converted ExceptionCause entity
   */
  public static ExceptionCause convertDtoToEntity(ObjectContext context, ErrorTracingDto errorChain) {
    if (errorChain == null) {
      return null;
    }
    ExceptionCause rootCause = context.newObject(ExceptionCause.class);
    return convertRecursive(context, errorChain, rootCause);
  }

  private static ExceptionCause convertRecursive(ObjectContext context, ErrorTracingDto errorChain,
      ExceptionCause currentCause) {
    if (errorChain == null) {
      return null;
    }

    currentCause.setMessage(errorChain.getMessage());
    currentCause.setFileName(errorChain.getFileName());
    currentCause.setClassName(errorChain.getClassName());
    currentCause.setMethodName(errorChain.getMethodName());
    currentCause.setLine(errorChain.getLineNumber());
    currentCause.setTimestamp(LocalDateTime.now());

    if (errorChain.getNext() != null) {
      ExceptionCause nextCause = context.newObject(ExceptionCause.class);
      nextCause.setParentExceptionId(currentCause.getId());
      currentCause.setNextException(nextCause);
      convertRecursive(context, errorChain.getNext(), nextCause);
    }

    return currentCause;
  }
}
