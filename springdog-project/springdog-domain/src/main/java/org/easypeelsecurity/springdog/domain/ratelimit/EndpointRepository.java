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

import java.util.List;

import org.easypeelsecurity.springdog.domain.ratelimit.model.Endpoint;

import org.apache.cayenne.ObjectContext;

/**
 * Repository for {@link Endpoint} entities.
 *
 * @author PENEKhun
 */
public interface EndpointRepository {
  /**
   * Find an {@link Endpoint} by its method signature.
   *
   * @param context         The Cayenne ObjectContext
   * @param methodSignature The method signature
   * @return The {@link Endpoint} or null if not found
   */
  Endpoint findByMethodSignatureOrNull(ObjectContext context, String methodSignature);

  /**
   * Find an {@link Endpoint} by its id.
   *
   * @param context The Cayenne ObjectContext
   * @param id      The id
   * @return The {@link Endpoint} or null if not found
   */
  Endpoint findByIdOrNull(ObjectContext context, Long id);

  /**
   * Find all {@link Endpoint} entities.
   *
   * @param context The Cayenne ObjectContext
   * @return The list of {@link Endpoint} entities
   */
  List<Endpoint> findAll(ObjectContext context);

  /**
   * Find all {@link Endpoint} entities by status.
   *
   * @param context The Cayenne ObjectContext
   * @return The list of {@link Endpoint} entities
   */
  long getEndpointCount(ObjectContext context);

  /**
   * Find all {@link Endpoint} entities by status.
   *
   * @param context The Cayenne ObjectContext
   * @param status  The status
   * @return The counts of {@link Endpoint} entities
   */
  long getEndpointCountByStatus(ObjectContext context, String status);
}
