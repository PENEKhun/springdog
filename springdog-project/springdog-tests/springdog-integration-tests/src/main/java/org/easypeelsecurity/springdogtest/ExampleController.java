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

package org.easypeelsecurity.springdogtest;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ExampleController {

  @GetMapping("/get")
  public String example(@RequestParam("param1") String param1) {
    return "example";
  }

  @PostMapping("/post")
  public String example2(@RequestBody PostRequest postRequest) {
    return "example2";
  }

  @DeleteMapping("/delete/{id}")
  public String example3(@PathVariable Integer id) {
    return "example3";
  }

  @PutMapping("/put")
  public String example4(@RequestParam("newTitle") String newTitle,
      @RequestParam("newContent") String newContent) {
    return "example4";
  }

  @GetMapping("/get/{id}")
  public String example5(@PathVariable Integer id) {
    return "example5";
  }

  @GetMapping("/header")
  public String example6(@RequestHeader String token1, @RequestHeader String token2) {
    return "example6";
  }

  @GetMapping("/exception1")
  public String exception1() {
    throw new IllegalArgumentException("exception1");
  }

  @GetMapping("/exception2")
  public String exception2() {
    throw new TestException("exception2");
  }

  static class PostRequest {

    private String title;
    private String content;

    public PostRequest() {

    }

    public PostRequest(String title, String content) {
      this.title = title;
      this.content = content;
    }

    public String getTitle() {
      return title;
    }

    public void setTitle(String title) {
      this.title = title;
    }

    public String getContent() {
      return content;
    }

    public void setContent(String content) {
      this.content = content;
    }
  }
}
