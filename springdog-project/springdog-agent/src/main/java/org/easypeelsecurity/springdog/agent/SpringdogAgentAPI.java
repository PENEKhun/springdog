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

package org.easypeelsecurity.springdog.agent;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import org.easypeelsecurity.springdog.domain.statistics.StatisticsService;
import org.easypeelsecurity.springdog.shared.dto.SystemMetricDto;

/**
 * Controller for the agent's API.
 */
@RestController
@SpringdogAgentController
public class SpringdogAgentAPI {
  private StatisticsService statisticsService;

  /**
   * Constructor.
   */
  public SpringdogAgentAPI(StatisticsService statisticsService) {
    this.statisticsService = statisticsService;
  }

  public SystemMetricDto loadMetric() {
    return statisticsService.loadMetric();
  }

  @PostMapping("/system-watch/{metricId}/memo")
  public void addMemo(@PathVariable Long metricId, String description) {
    statisticsService.addMemo(metricId, description);
  }
}
