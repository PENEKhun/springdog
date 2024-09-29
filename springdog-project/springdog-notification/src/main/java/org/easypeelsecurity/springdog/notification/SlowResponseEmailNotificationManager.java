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

package org.easypeelsecurity.springdog.notification;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.easypeelsecurity.springdog.notification.email.MetricContext;
import org.easypeelsecurity.springdog.notification.email.SlowResponseEmailNotification;
import org.easypeelsecurity.springdog.shared.settings.NotificationGlobalSetting;
import org.easypeelsecurity.springdog.shared.settings.SlowResponseSetting;
import org.easypeelsecurity.springdog.shared.settings.SpringdogSettingManager;
import org.easypeelsecurity.springdog.shared.settings.SpringdogSettings;

/**
 * Manages the slow response process.
 */
@Service
public class SlowResponseEmailNotificationManager {
  private final Map<String, MetricContext<String, Long>> metricContexts;
  private final SpringdogSettingManager settings;
  private final SlowResponseEmailNotification emailNotification;

  /**
   * Constructs a new SlowResponseManager.
   *
   * @param settings           The properties containing threshold values.
   * @param emailNotification  The email notification service to use.
   * @param metricContexts     The map of metric contexts to use.
   */
  public SlowResponseEmailNotificationManager(SpringdogSettingManager settings,
      SlowResponseEmailNotification emailNotification,
      Map<String, MetricContext<String, Long>> metricContexts) {
    this.settings = settings;
    this.emailNotification = emailNotification;
    this.metricContexts = metricContexts != null ? metricContexts : new HashMap<>();
  }

  /**
   * Constructs a new SlowResponseManager with a default metric contexts map.
   *
   * @param settings           The properties containing threshold values.
   * @param emailNotification  The email notification service to use.
   */
  @Autowired
  public SlowResponseEmailNotificationManager(SpringdogSettingManager settings,
      SlowResponseEmailNotification emailNotification) {
    this(settings, emailNotification, new HashMap<>());
  }

  /**
   * Checks the current response time against the threshold.
   *
   * @param value The current response time.
   */
  public void checkSlowResponse(SlowResponse value) {
    SpringdogSettings springdogSettings = settings.getSettings();
    SlowResponseSetting slowResponseSetting = springdogSettings.getSlowResponseSetting();
    NotificationGlobalSetting notificationGlobalSetting = springdogSettings.getNotificationGlobalSetting();
    if (!notificationGlobalSetting.isEnabled() || !slowResponseSetting.isEnabled()) {
      return;
    }

    String metricName = "[%s] %s".formatted(value.endpointMethod, value.endpointPath);
    MetricContext<String, Long> context =
        metricContexts.computeIfAbsent(metricName, n -> new MetricContext<>(n, emailNotification));
    context.checkMetric(value.currentResponseTime, slowResponseSetting.getResponseTimeMs());
  }

  /**
   * Represents a slow response.
   */
  public record SlowResponse(String endpointPath, String endpointMethod, long currentResponseTime)
      implements Comparable<SlowResponse> {

    @Override
    public int compareTo(SlowResponse o) {
      return Long.compare(this.currentResponseTime, o.currentResponseTime);
    }
  }
}
