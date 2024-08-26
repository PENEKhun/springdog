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

import org.easypeelsecurity.springdog.domain.ratelimit.model.EndpointChangelog;

import org.apache.cayenne.ObjectContext;

/**
 * Repository for {@link EndpointChangelog} entities.
 *
 * @author PENEKhun
 */
public interface VersionControlRepository {

  /**
   * Find all {@link EndpointChangelog} entities by resolved status.
   *
   * @param context    The Cayenne ObjectContext
   * @param isResolved The resolved status
   * @return The list of {@link EndpointChangelog} entities
   */
  List<EndpointChangelog> findAllChangeLogsByResolved(ObjectContext context, boolean isResolved);
}
