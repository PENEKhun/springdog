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

package org.easypeelsecurity.springdog.agent.security;

import org.easypeelsecurity.springdog.shared.configuration.SpringdogProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Setting up Spring Security for Agent pages.
 */
@Configuration
public class SpringdogSecurityConfig {

  private final SpringdogProperties springdogProperties;

  /**
   * Constructor.
   */
  public SpringdogSecurityConfig(SpringdogProperties springdogProperties) {
    this.springdogProperties = springdogProperties;
  }

  /**
   * Security filter chain for springdog agent.
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    String basePath = springdogProperties.getAgentBasePath();
    http
        .authorizeHttpRequests(authorizeRequests ->
            authorizeRequests
                .requestMatchers(request -> request.getRequestURI().startsWith("/" + basePath + "/login"))
                .permitAll()
                .requestMatchers(request -> request.getRequestURI().startsWith("/" + basePath + "/logout"))
                .permitAll()
                .requestMatchers(request -> request.getRequestURI().startsWith("/" + basePath)).authenticated()
                .anyRequest().permitAll()
        ).formLogin(formLogin -> formLogin
            .loginPage("/" + basePath + "/login")
            .failureUrl("/" + basePath + "/login?error")
            .loginProcessingUrl("/" + basePath + "/login")
            .defaultSuccessUrl("/" + basePath + "/")
            .permitAll())
        .logout(logout -> logout
            .logoutUrl("/" + basePath + "/logout")
        );

    return http.build();
  }

  /**
   * UserDetailsService bean to configure in-memory authentication.
   */
  @Bean
  public UserDetailsService userDetailsService() {
    String username = springdogProperties.getAgentUsername();
    String password = springdogProperties.getAgentPassword();
    UserDetails user = User.withUsername(username)
        .password("{noop}" + password)
        .roles("ADMIN")
        .build();
    return new InMemoryUserDetailsManager(user);
  }
}
