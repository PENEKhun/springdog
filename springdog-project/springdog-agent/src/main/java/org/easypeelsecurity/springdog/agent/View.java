package org.easypeelsecurity.springdog.agent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.easypeelsecurity.springdog.manager.ratelimit.EndpointCommand;
import org.easypeelsecurity.springdog.manager.ratelimit.EndpointQuery;
import org.easypeelsecurity.springdog.shared.ratelimit.EndpointDto;
import org.easypeelsecurity.springdog.shared.ratelimit.RulesetDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Controller for the agent's view.
 */
@Controller
@SuppressWarnings("checkstyle:MissingJavadocMethod")
public class View {

  private final EndpointQuery rateLimitQuery;
  private final EndpointCommand rateLimitCommand;

  public View(EndpointQuery rateLimitQuery, EndpointCommand rateLimitCommand) {
    this.rateLimitQuery = rateLimitQuery;
    this.rateLimitCommand = rateLimitCommand;
  }

  @GetMapping("/")
  public String home() {
    return "/templates/content/main.html";
  }

  @GetMapping("/login")
  public String login() {
    return "/templates/content/login.html";
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
