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

package fixture;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.easypeelsecurity.springdog.shared.dto.SystemMetricDto;
import org.easypeelsecurity.springdog.shared.vo.DashboardResponse;
import org.easypeelsecurity.springdog.shared.vo.DashboardResponse.DailyEndpointMetric;
import org.easypeelsecurity.springdog.shared.vo.DashboardResponse.DailySlowestEndpoint;
import org.easypeelsecurity.springdog.shared.vo.DashboardResponse.DailyTopFailWithRatelimitEndpoint;
import org.easypeelsecurity.springdog.shared.vo.DashboardResponse.DailyTopTrafficEndpoint;

@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
public class DashBoardResponseFixture {
  public static DashboardResponse get() {
    SystemMetricDto systemMetric = new SystemMetricDto(10.0, 20.0, 30.0, LocalDateTime.now());
    var dailyEndpointMetric = new DailyEndpointMetric(1, 2, 3, LocalDate.now());
    var dailyTopTrafficEndpoint = new DailyTopTrafficEndpoint("/test", "GET", 1);
    var dailySlowestEndpoint = new DailySlowestEndpoint("/test", "GET", 1);
    var dailyTopFailWithRatelimitEndpoint = new DailyTopFailWithRatelimitEndpoint("/test", "GET", 1);

    return new DashboardResponse(1, 2, 3,
        List.of(systemMetric),
        List.of(dailyEndpointMetric),
        List.of(dailyTopTrafficEndpoint), List.of(dailySlowestEndpoint),
        List.of(dailyTopFailWithRatelimitEndpoint));
  }
}
