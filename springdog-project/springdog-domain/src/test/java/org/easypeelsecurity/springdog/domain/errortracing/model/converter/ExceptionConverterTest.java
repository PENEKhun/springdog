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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.easypeelsecurity.springdog.domain.errortracing.model.ExceptionCause;
import org.easypeelsecurity.springdog.domain.errortracing.model.ExceptionClass;
import org.easypeelsecurity.springdog.domain.errortracing.model.ExceptionType;
import org.easypeelsecurity.springdog.shared.dto.ErrorTracingDto;
import org.easypeelsecurity.springdog.shared.dto.ExceptionClassesDto;
import org.easypeelsecurity.springdog.shared.dto.ExceptionClassesDto.ExceptionListDto;
import org.easypeelsecurity.springdog.shared.dto.ExceptionClassesDto.ExceptionListDto.ExceptionItemDto;

import org.apache.cayenne.ObjectContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ExceptionConverterTest {

  @Test
  @DisplayName("Should convert ExceptionClassesDto to entities")
  void convertDtoToEntitiesSuccessfully() {
    // given
    ObjectContext context = mock(ObjectContext.class);

    ExceptionItemDto itemDto = ExceptionItemDto.builder()
        .exceptionName("NullPointerException")
        .isEnableToMonitor(true)
        .build();

    ExceptionListDto listDto = ExceptionListDto.builder()
        .packageType("com.example")
        .description("Test exception type")
        .subExceptions(List.of(itemDto))
        .build();

    ExceptionClassesDto dto = new ExceptionClassesDto(List.of(listDto));

    ExceptionType mockExceptionType = new ExceptionType();
    when(context.newObject(ExceptionType.class)).thenReturn(mockExceptionType);

    ExceptionClass mockExceptionClass = mock(ExceptionClass.class);
    doNothing().when(mockExceptionClass).setRootExceptionPackageType(any());
    when(context.newObject(ExceptionClass.class)).thenReturn(mockExceptionClass);
    when(mockExceptionClass.getExceptionClassName()).thenReturn("NullPointerException");

    // when
    List<Object> entities = ExceptionConverter.convertDtoToEntities(context, dto);

    // then
    ExceptionType typeEntity = (ExceptionType) entities.get(0);
    ExceptionClass classEntity = (ExceptionClass) entities.get(1);

    assertThat(typeEntity.getPackageType()).isEqualTo("com.example");
    assertThat(classEntity.getExceptionClassName()).isEqualTo("NullPointerException");
    verify(context, times(1)).newObject(ExceptionType.class);
    verify(context, times(1)).newObject(ExceptionClass.class);
  }

  @Test
  @DisplayName("Should convert ExceptionCause to ErrorTracingDto")
  void convertEntityToErrorTracingDtoSuccessfully() {
    ExceptionCause cause = mock(ExceptionCause.class);
    when(cause.getId()).thenReturn(1L);
    when(cause.getMessage()).thenReturn("NullPointerException occurred");
    when(cause.getFileName()).thenReturn("MyClass.java");
    when(cause.getClassName()).thenReturn("com.example.MyClass");
    when(cause.getMethodName()).thenReturn("myMethod");
    when(cause.getLine()).thenReturn(42);

    // when
    ErrorTracingDto dto = ExceptionConverter.entityToErrorTracingDto(cause);

    // then
    assertThat(dto).isNotNull();
    assertThat(dto.getMessage()).isEqualTo("NullPointerException occurred");
    assertThat(dto.getFileName()).isEqualTo("MyClass.java");
    assertThat(dto.getClassName()).isEqualTo("com.example.MyClass");
    assertThat(dto.getMethodName()).isEqualTo("myMethod");
    assertThat(dto.getLineNumber()).isEqualTo(42);
  }
}

