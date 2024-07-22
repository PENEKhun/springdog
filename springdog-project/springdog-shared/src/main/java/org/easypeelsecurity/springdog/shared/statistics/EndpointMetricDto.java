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

package org.easypeelsecurity.springdog.shared.statistics;

import java.time.LocalDate;

/**
 * DTO for endpoint metrics.
 *
 * @param path                        endpoint path
 * @param method                      HTTP method
 * @param visitCount                  number of visits
 * @param averageResponseMilliseconds average response time in milliseconds
 * @param ratelimitFailureCount       number of rate limit failures
 */
public record EndpointMetricDto(String path, String method, long visitCount, long averageResponseMilliseconds,
                                long ratelimitFailureCount, LocalDate baseDate) {
}