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

import java.util.List;

import org.apache.cayenne.ObjectContext;

/**
 * Repository for {@link ExceptionType}, {@link ExceptionClass} entities.
 */
public interface ExceptionListingRepository {
  /**
   * Find all {@link ExceptionType} entities.
   *
   * @return The list of {@link ExceptionType} entities
   */
  List<ExceptionType> findAllExceptions(ObjectContext context);

  /**
   * Select Exception class by id.
   *
   * @return Exception class
   */
  ExceptionClass findByIdOrNull(ObjectContext context, long exceptionClassId);
}
