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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.easypeelsecurity.springdog.manager.ratelimit.EndpointCommand;
import org.easypeelsecurity.springdog.manager.ratelimit.EndpointQuery;
import org.easypeelsecurity.springdog.shared.ratelimit.EndpointDto;
import org.easypeelsecurity.springdog.shared.ratelimit.RulesetDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Controller for the agent's view.
 */
@SuppressWarnings("checkstyle:MissingJavadocMethod")
public class SpringdogAgentView {

  @Autowired
  private EndpointQuery rateLimitQuery;
  @Autowired
  private EndpointCommand rateLimitCommand;

  @GetMapping("/login")
  public String login() {
    return "/templates/content/login.html";
  }

  @GetMapping("/")
  public String home() {
    return "/templates/content/main.html";
  }

  @GetMapping("/rate-limit")
  public String rateLimitManage(Model model) {
    Set<EndpointDto> endpoints = rateLimitQuery.findAll();
    model.addAttribute("endpoints", endpoints);
    return "/templates/content/rate-limit/manage.html";
  }

  @GetMapping("/rate-limit/{apiHash}")
  public String viewRateLimitSpecific(@PathVariable(name = "apiHash") String apiHash, Model model) {
    EndpointDto endpointDto = rateLimitQuery.findApi(apiHash);
    model.addAttribute("api", endpointDto);
    List<String> headers = new ArrayList<>();
    headers.add("X-Auth-Token");
    model.addAttribute("headerItems", headers);

    return "/templates/content/rate-limit/actions/setting.html";
  }

  @PostMapping("/rate-limit/{apiHash}")
  public String modifyRateLimit(@PathVariable(name = "apiHash") String apiHash,
      @ModelAttribute("api") EndpointDto endpointDto, Model model) {
    try {
      RulesetDto changes = endpointDto.getRuleset();
      rateLimitCommand.updateRule(endpointDto.getFqcn(), apiHash, changes);
    } catch (Exception e) {
      model.addAttribute("result", false);
      model.addAttribute("message", e.getMessage());
      return viewRateLimitSpecific(apiHash, model);
    }

    model.addAttribute("result", true);
    model.addAttribute("message", "Successfully updated");
    return viewRateLimitSpecific(apiHash, model);
  }

  @GetMapping("/service/change-pw")
  public String changePW() {
    return "/templates/content/service/change-pw.html";
  }
}
