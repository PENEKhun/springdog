package org.easypeelsecurity.springdog.agent.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class View {

  // example
  @GetMapping("/")
  public String homePage() {
    return "/content/main";
  }

}
