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

package org.easypeelsecurity.springdog.storage.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import org.easypeelsecurity.springdog.domain.statistics.SystemMetricRepository;
import org.easypeelsecurity.springdog.domain.statistics.model.SystemMetric;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;

/**
 * Implementation of {@link SystemMetricRepository}.
 *
 * @author PENEKhun
 */
@Repository
public class SystemMetricRepositoryImpl implements SystemMetricRepository {
  @Override
  public List<SystemMetric> getRecentSystemMetrics(ObjectContext context, int limit) {
    return ObjectSelect.query(SystemMetric.class)
        .orderBy(SystemMetric.TIMESTAMP.desc())
        .limit(limit)
        .select(context);
  }
}
