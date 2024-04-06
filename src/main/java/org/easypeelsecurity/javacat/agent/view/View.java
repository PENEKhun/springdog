package org.easypeelsecurity.javacat.agent.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class View {

  @GetMapping("/")
  public String homePage() {
    return "/content/main";
  }

}
