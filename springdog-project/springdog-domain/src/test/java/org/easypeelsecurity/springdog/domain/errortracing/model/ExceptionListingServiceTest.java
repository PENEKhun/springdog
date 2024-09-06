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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.easypeelsecurity.springdog.shared.dto.ExceptionClassesDto;
import org.easypeelsecurity.springdog.shared.dto.ExceptionClassesDto.ExceptionListDto;
import org.easypeelsecurity.springdog.shared.dto.ExceptionClassesDto.ExceptionListDto.ExceptionItemDto;

import org.apache.cayenne.ObjectContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ExceptionListingServiceTest {

  @Mock
  private ObjectContext context;

  @Mock
  private ExceptionListingRepository exceptionRepository;

  private ExceptionListingService exceptionListingService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    exceptionListingService = new ExceptionListingService(context, exceptionRepository);
  }

  @Test
  void saveExceptionListing_shouldUpdateExistingAndAddNewExceptions() {
    // given
    ExceptionType existingType = mock(ExceptionType.class);
    ExceptionClass existingClass = mock(ExceptionClass.class);
    when(existingClass.getExceptionClassName()).thenReturn("IOException");
    when(existingType.getPackageType()).thenReturn("java.io");
    when(existingType.getExceptionClasses()).thenReturn(new ArrayList<>(List.of(existingClass)));
    when(exceptionRepository.findAllExceptions(context)).thenReturn(new ArrayList<>(List.of(existingType)));

    // Mock new objects
    ExceptionClass newClass = mock(ExceptionClass.class);
    ExceptionType newType = mock(ExceptionType.class);
    when(context.newObject(ExceptionClass.class)).thenReturn(newClass);
    when(context.newObject(ExceptionType.class)).thenReturn(newType);

    ExceptionClassesDto parsedException = new ExceptionClassesDto(List.of(
        new ExceptionListDto(1L, "java.io", "I/O Exceptions", List.of(
            new ExceptionItemDto(1L, "IOException", false),
            new ExceptionItemDto(2L, "FileNotFoundException", false)
        )),
        new ExceptionListDto(2L, "java.lang", "Lang Exceptions", List.of(
            new ExceptionItemDto(3L, "NullPointerException", false)
        ))
    ));

    // when
    exceptionListingService.saveExceptionsWithoutDuplicate(parsedException);

    // then
    verify(context, times(2)).newObject(ExceptionClass.class); // new class
    verify(context, times(1)).newObject(ExceptionType.class); // new type
    verify(context, never()).deleteObject(existingType);
    verify(context, never()).deleteObject(existingClass);
    verify(context).commitChanges();
  }

  @Test
  void saveExceptionListing_shouldDeleteRemovedExceptions() {
    // given
    ExceptionType existingType = mock(ExceptionType.class);
    ExceptionClass existingClass = mock(ExceptionClass.class);
    when(existingClass.getExceptionClassName()).thenReturn("IOException");
    when(existingType.getPackageType()).thenReturn("java.io");
    when(existingType.getExceptionClasses()).thenReturn(new ArrayList<>(List.of(existingClass)));
    when(exceptionRepository.findAllExceptions(context)).thenReturn(new ArrayList<>(List.of(existingType)));

    ExceptionClassesDto parsedExceptions = new ExceptionClassesDto(List.of());

    // when
    exceptionListingService.saveExceptionsWithoutDuplicate(parsedExceptions);

    // then
    verify(context).deleteObject(existingType);
    verify(context).deleteObject(existingClass);
    verify(context).commitChanges();
  }

  @Test
  void getExceptionListing_shouldReturnCorrectDto() {
    // given
    ExceptionType type = mock(ExceptionType.class);
    ExceptionClass exceptionClass = mock(ExceptionClass.class);
    when(type.getId()).thenReturn(1L);
    when(type.getPackageType()).thenReturn("java.io");
    when(type.getDescription()).thenReturn("I/O Exceptions");
    when(type.getExceptionClasses()).thenReturn(Collections.singletonList(exceptionClass));
    when(exceptionClass.getId()).thenReturn(1L);
    when(exceptionClass.getExceptionClassName()).thenReturn("IOException");

    when(exceptionRepository.findAllExceptions(context)).thenReturn(List.of(type));

    // when
    ExceptionClassesDto result = exceptionListingService.getExceptionListing();

    // then
    assertThat(result.exceptionList()).hasSize(1);
    ExceptionListDto listDto = result.exceptionList().get(0);
    assertThat(listDto.packageTypeId()).isEqualTo(1L);
    assertThat(listDto.packageType()).isEqualTo("java.io");
    assertThat(listDto.description()).isEqualTo("I/O Exceptions");
    assertThat(listDto.subExceptions()).hasSize(1);
    assertThat(listDto.subExceptions().get(0).exceptionId()).isEqualTo(1L);
    assertThat(listDto.subExceptions().get(0).exceptionName()).isEqualTo("IOException");
  }
}
