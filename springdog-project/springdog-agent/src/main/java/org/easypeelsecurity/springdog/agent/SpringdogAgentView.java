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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import org.easypeelsecurity.springdog.domain.errortracing.model.ExceptionListingService;
import org.easypeelsecurity.springdog.domain.ratelimit.EndpointService;
import org.easypeelsecurity.springdog.domain.statistics.StatisticsService;
import org.easypeelsecurity.springdog.shared.configuration.SpringdogProperties;
import org.easypeelsecurity.springdog.shared.dto.EndpointDto;
import org.easypeelsecurity.springdog.shared.dto.ErrorTracingDto;
import org.easypeelsecurity.springdog.shared.settings.NotificationGlobalSetting;
import org.easypeelsecurity.springdog.shared.settings.SlowResponseSetting;
import org.easypeelsecurity.springdog.shared.settings.SpringdogSettingManagerImpl;
import org.easypeelsecurity.springdog.shared.settings.SystemWatchSetting;
import org.easypeelsecurity.springdog.shared.util.Assert;

/**
 * Controller for the agent's view.
 */
@Controller
@SpringdogAgentController
@SuppressWarnings("checkstyle:MissingJavadocMethod")
public class SpringdogAgentView {

  SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
  @Autowired
  private StatisticsService statisticsService;
  @Autowired
  private EndpointService endpointService;
  @Autowired
  private ExceptionListingService exceptionListingService;
  @Autowired
  private SpringdogProperties properties;
  @Autowired
  private SpringdogSettingManagerImpl settingManager;

  @GetMapping("/login")
  public String login() {
    return "/templates/content/login.html";
  }

  /**
   * Created to change logout to GET method.
   */
  @GetMapping("/logout")
  public String performLogout(Authentication authentication, HttpServletRequest request,
      HttpServletResponse response) {
    this.logoutHandler.logout(request, response, authentication);
    return "redirect:" + properties.computeAbsolutePath("/login?logout");
  }

  @GetMapping({"/", ""})
  public String home(Model model) {
    model.addAttribute("endpointChangeLog", endpointService.getAllChangeLogsNotResolved());

    var dashboard = statisticsService.getDashboardResponse(LocalDate.now());
    model.addAttribute("totalEndpointCount", dashboard.totalEndpointCount());
    model.addAttribute("totalEndpointCountActive", dashboard.totalActiveEndpointCount());
    model.addAttribute("totalEndpointCountNotActive", dashboard.totalInactiveEndpointCount());
    model.addAttribute("recentSystemMetrics", dashboard.recentSystemMetrics());
    model.addAttribute("recentEndpointMetrics", dashboard.dailyEndpointMetrics());
    model.addAttribute("dailyTopTraffic", dashboard.dailyTopTrafficEndpoints());
    model.addAttribute("dailyTopFailure", dashboard.dailyTopFailEndpoints());
    model.addAttribute("dailySlowestEndpoints", dashboard.dailySlowestEndpoints());
    return "/templates/content/main.html";
  }

  @GetMapping("/rate-limit")
  public String rateLimitManage(Model model) {
    List<EndpointDto> endpoints = endpointService.findAllEndpoints();
    model.addAttribute("endpoints", endpoints);
    return "/templates/content/rate-limit/manage.html";
  }

  @GetMapping("/rate-limit/{endpointId}")
  public String viewRateLimitSpecific(@PathVariable(name = "endpointId") long endpointId, Model model) {
    EndpointDto endpointDto = endpointService.findEndpoint(endpointId);
    model.addAttribute("endpoint", endpointDto);
    List<String> headers = new ArrayList<>();
    headers.add("X-Auth-Token");
    model.addAttribute("headerItems", headers);

    return "/templates/content/rate-limit/actions/setting.html";
  }

  @GetMapping("/rate-limit/{endpointId}/analytics")
  public String viewRateLimitSpecificAnalytics(@PathVariable(name = "endpointId") long endpointId,
      Model model) {
    EndpointDto endpointDto = endpointService.findEndpoint(endpointId);
    model.addAttribute("endpoint", endpointDto);
    model.addAttribute("statics", statisticsService.getRecentEndpointMetric(endpointId, 10));

    return "/templates/content/rate-limit/actions/analytics.html";
  }

  @PostMapping("/rate-limit/{endpointId}")
  public String modifyRateLimit(@PathVariable(name = "endpointId") long endpointId,
      @ModelAttribute("endpoint") EndpointDto endpointDto, Model model) {
    try {
      Assert.isEqual(endpointId, endpointDto.getId(), "Invalid request");
      endpointService.updateRule(endpointDto);
    } catch (Exception e) {
      model.addAttribute("result", false);
      model.addAttribute("message", e.getMessage());
      return viewRateLimitSpecific(endpointId, model);
    }

    model.addAttribute("result", true);
    model.addAttribute("message", "Successfully updated");
    return viewRateLimitSpecific(endpointId, model);
  }

  @GetMapping("/notification")
  public String notificationSettingView(Model model) {
    model.addAttribute("mailConfiguration", settingManager.getSettings().getNotificationGlobalSetting());
    return "/templates/content/notification/configuration.html";
  }

  @PostMapping("/notification")
  public String notificationSettingUpdate(Model model, @ModelAttribute("mailConfiguration")
  NotificationGlobalSetting newSetting) {
    try {
      settingManager.updateNotificationGlobalSetting(newSetting);
    } catch (Exception e) {
      model.addAttribute("result", false);
      model.addAttribute("message", e.getMessage());
      return notificationSettingView(model);
    }

    model.addAttribute("result", true);
    model.addAttribute("message", "Successfully updated");
    return notificationSettingView(model);
  }

  @GetMapping("/notification/system-watch")
  public String notificationSystemWatchView(Model model) {
    model.addAttribute("systemWatchConfiguration", settingManager.getSettings().getSystemWatchSetting());
    return "/templates/content/notification/system-watch.html";
  }

  @PostMapping("/notification/system-watch")
  public String notificationSystemWatchSettingUpdate(Model model, @ModelAttribute("systemWatchConfiguration")
  SystemWatchSetting newSetting) {
    try {
      settingManager.updateSystemWatchSetting(newSetting);
    } catch (Exception e) {
      model.addAttribute("result", false);
      model.addAttribute("message", e.getMessage());
      return notificationSystemWatchView(model);
    }

    model.addAttribute("result", true);
    model.addAttribute("message", "Successfully updated");
    return notificationSystemWatchView(model);
  }

  @GetMapping("/notification/slow-response")
  public String notificationSlowResponseView(Model model) {
    model.addAttribute("slowResponseConfiguration", settingManager.getSettings().getSlowResponseSetting());
    return "/templates/content/notification/slow-response.html";
  }

  @PostMapping("/notification/slow-response")
  public String notificationSlowResponseSettingUpdate(Model model, @ModelAttribute("slowResponseConfiguration")
  SlowResponseSetting newSetting) {
    try {
      settingManager.updateSlowResponseSetting(newSetting);
    } catch (Exception e) {
      model.addAttribute("result", false);
      model.addAttribute("message", e.getMessage());
      return notificationSlowResponseView(model);
    }

    model.addAttribute("result", true);
    model.addAttribute("message", "Successfully updated");
    return notificationSlowResponseView(model);
  }

  @GetMapping("/error-tracing")
  public String errorTracingHome(Model model) {
    List<ErrorTracingDto> causes = exceptionListingService.getAllParentCauses();
    model.addAttribute("causes", causes);
    return "/templates/content/error-tracing/list.html";
  }

  @GetMapping("/error-tracing/configuration")
  public String errorTracingConfiguration(Model model) {
    model.addAttribute("exceptionClasses", exceptionListingService.getExceptionListing());
    return "/templates/content/error-tracing/configuration.html";
  }

  @GetMapping("/system-watch")
  public String systemWatchView(Model model) {
    model.addAttribute("systemMetrics", statisticsService.getRecentSystemMetrics(50));
    return "/templates/content/system-watch/metrics.html";
  }

  @GetMapping("/service/change-pw")
  public String changePW() {
    return "/templates/content/service/change-pw.html";
  }
}
