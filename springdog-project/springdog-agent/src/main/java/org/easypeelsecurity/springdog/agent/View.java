package org.easypeelsecurity.springdog.agent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.easypeelsecurity.springdog.manager.ratelimit.EndpointQuery;
import org.easypeelsecurity.springdog.shared.ratelimit.EndpointDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Controller for the agent's view.
 */
@Controller
public class View {

  private final EndpointQuery rateLimitQuery;

  /**
   * Constructor.
   */
  public View(EndpointQuery rateLimitQuery) {
    this.rateLimitQuery = rateLimitQuery;
  }

  @GetMapping("/")
  public String home() {
    return "/templates/content/main.html";
  }

  @GetMapping("/login")
  public String login() {
    return "/templates/content/login.html";
  }

  @GetMapping("/rate-limit/manage")
  public String rateLimitManage(Model model) {
    Set<EndpointDto> endpoints = rateLimitQuery.findAll();
    model.addAttribute("endpoints", endpoints);
    return "/templates/content/rate-limit/manage.html";
  }

  @GetMapping("/rate-limit/manage/{apiHash}")
  public String rateLimitManage(@PathVariable(name = "apiHash") String apiHash, Model model) {
    EndpointDto endpointDto = rateLimitQuery.findApi(apiHash);
    model.addAttribute("api", endpointDto);
    List<String> headers = new ArrayList<>();
    headers.add("X-Auth-Token");
    model.addAttribute("headerItems", headers);

    return "/templates/content/rate-limit/actions/setting.html";
  }

  @GetMapping("/service/change-pw")
  public String changePW() {
    return "/templates/content/service/change-pw.html";
  }
}
