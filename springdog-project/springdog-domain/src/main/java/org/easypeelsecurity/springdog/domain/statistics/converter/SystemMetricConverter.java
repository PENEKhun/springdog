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

package org.easypeelsecurity.springdog.domain.statistics.converter;

import org.easypeelsecurity.springdog.domain.statistics.model.SystemMetric;
import org.easypeelsecurity.springdog.shared.dto.SystemMetricDto;

import org.apache.cayenne.ObjectContext;

/**
 * Converter for system metrics.
 *
 * @author PENEKhun
 */
public abstract class SystemMetricConverter {

  /**
   * Convert system metric to DTO.
   *
   * @param systemMetric system metric
   * @return DTO
   */
  public static SystemMetricDto convert(SystemMetric systemMetric) {
    return new SystemMetricDto(systemMetric.getCpuUsagePercent(), systemMetric.getMemoryUsagePercent(),
        systemMetric.getDiskUsagePercent(), systemMetric.getTimestamp());
  }

  /**
   * Convert DTO to system metric.
   *
   * @param objectContext   object context
   * @param systemMetricDto DTO
   * @return system metric
   */
  public static SystemMetric convert(ObjectContext objectContext, SystemMetricDto systemMetricDto) {
    SystemMetric systemMetric = objectContext.newObject(SystemMetric.class);
    systemMetric.setCpuUsagePercent(systemMetricDto.getCpuUsagePercent());
    systemMetric.setMemoryUsagePercent(systemMetricDto.getMemoryUsagePercent());
    systemMetric.setDiskUsagePercent(systemMetricDto.getDiskUsagePercent());
    systemMetric.setTimestamp(systemMetricDto.getTimestamp());
    return systemMetric;
  }
}
