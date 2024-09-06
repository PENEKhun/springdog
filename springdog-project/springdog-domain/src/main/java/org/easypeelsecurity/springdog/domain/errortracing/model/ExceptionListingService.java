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

package org.easypeelsecurity.springdog.domain.errortracing.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import org.easypeelsecurity.springdog.domain.errortracing.model.converter.ExceptionConverter;
import org.easypeelsecurity.springdog.shared.dto.ExceptionClassesDto;
import org.easypeelsecurity.springdog.shared.dto.ExceptionClassesDto.ExceptionListDto;
import org.easypeelsecurity.springdog.shared.dto.ExceptionClassesDto.ExceptionListDto.ExceptionItemDto;

import org.apache.cayenne.ObjectContext;

/**
 * Service class.
 */
@Service
public class ExceptionListingService {
  private final ObjectContext context;
  private final ExceptionListingRepository exceptionRepository;

  /**
   * Constructor.
   */
  public ExceptionListingService(
      @Qualifier("springdogContext") ObjectContext context, ExceptionListingRepository exceptionRepository) {
    this.context = context;
    this.exceptionRepository = exceptionRepository;
  }

  /**
   * Save exceptions without duplicate.
   *
   * @param parsedList The parsed Exception classes
   */
  public void saveExceptionsWithoutDuplicate(ExceptionClassesDto parsedList) {
    List<ExceptionType> existingTypes = new ArrayList<>(exceptionRepository.findAllExceptions(context));

    for (ExceptionListDto newListDto : parsedList.exceptionList()) {
      ExceptionType existingType = findExistingType(existingTypes, newListDto.packageType());

      if (existingType == null) {
        // Add new ExceptionType
        ExceptionType newType = context.newObject(ExceptionType.class);
        newType.setPackageType(newListDto.packageType());
        newType.setDescription(newListDto.description());
        updateExceptionClasses(newType, newListDto.subExceptions());
      } else {
        // Update existing ExceptionType
        existingType.setDescription(newListDto.description());
        updateExceptionClasses(existingType, newListDto.subExceptions());
        existingTypes.remove(existingType);
      }
    }

    for (ExceptionType typeToDelete : existingTypes) {
      for (ExceptionClass classToDelete : typeToDelete.getExceptionClasses()) {
        context.deleteObject(classToDelete);
      }
      context.deleteObject(typeToDelete);
    }

    context.commitChanges();
  }

  /**
   * Change monitoring status.
   *
   * @param exceptionClassId  Target exception class ID
   * @param isEnableToMonitor The monitoring status to set
   */
  public void changeMonitoringStatus(long exceptionClassId, boolean isEnableToMonitor) {
    ExceptionClass exceptionClass = exceptionRepository.findByIdOrNull(context, exceptionClassId);
    if (exceptionClass == null) {
      throw new IllegalArgumentException("Exception class not found.");
    }

    exceptionClass.setMonitoringEnabled(isEnableToMonitor);
    context.commitChanges();
  }

  private ExceptionType findExistingType(List<ExceptionType> existingTypes, String packageType) {
    return existingTypes.stream()
        .filter(type -> type.getPackageType().equals(packageType))
        .findFirst()
        .orElse(null);
  }

  private void updateExceptionClasses(ExceptionType exceptionType, List<ExceptionItemDto> newExceptions) {
    List<ExceptionClass> existingClasses = new ArrayList<>(exceptionType.getExceptionClasses());

    for (ExceptionItemDto newException : newExceptions) {
      ExceptionClass existingClass = findExistingClass(existingClasses, newException.exceptionName());

      if (existingClass == null) {
        ExceptionConverter.convertDtoToEntity(context, exceptionType, newException);
      } else {
        existingClasses.remove(existingClass);
      }
    }

    for (ExceptionClass classToDelete : existingClasses) {
      context.deleteObject(classToDelete);
    }
  }

  private ExceptionClass findExistingClass(List<ExceptionClass> existingClasses, String exceptionName) {
    return existingClasses.stream()
        .filter(clazz -> clazz.getExceptionClassName().equals(exceptionName))
        .findFirst()
        .orElse(null);
  }

  /**
   * Get the exception listing.
   */
  public ExceptionClassesDto getExceptionListing() {
    return ExceptionConverter.convertEntitiesToDto(exceptionRepository.findAllExceptions(context));
  }
}
