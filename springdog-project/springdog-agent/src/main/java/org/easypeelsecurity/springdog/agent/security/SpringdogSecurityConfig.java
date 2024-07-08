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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import org.easypeelsecurity.springdog.shared.configuration.SpringdogProperties;

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
  @Order(Integer.MIN_VALUE)
  public SecurityFilterChain springdogSecurityFilterChain(HttpSecurity http) throws Exception {
    String baseAbsolutePath = springdogProperties.computeAbsolutePath("");
    http
        .securityMatcher(baseAbsolutePath + "/**")
        .authorizeHttpRequests(auth -> auth.anyRequest().hasRole("SPRINGDOG_AGENT_ADMIN"))
        .formLogin(formLogin -> formLogin
            .loginPage(springdogProperties.computeAbsolutePath("/login"))
            .failureUrl(springdogProperties.computeAbsolutePath("/login?error"))
            .loginProcessingUrl(springdogProperties.computeAbsolutePath("/login"))
            .defaultSuccessUrl(springdogProperties.computeAbsolutePath("/"))
            .permitAll())
        .logout(logout -> logout
            .logoutUrl(springdogProperties.computeAbsolutePath("/logout"))
            .logoutSuccessUrl(springdogProperties.computeAbsolutePath("/login?logout"))
            .permitAll())
        .csrf(AbstractHttpConfigurer::disable);

    return http.build();
  }

  /**
   * Allows to define duplicate Authentication Manager beans.
   */
  @Bean("springdogAuthenticationManager")
  public AuthenticationManager springdogAuthenticationManager(HttpSecurity http) {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(springdogUserDetailsService());
    return new ProviderManager(provider);
  }

  /**
   * Springdog use in-memory user details service.
   */
  public UserDetailsService springdogUserDetailsService() {
    String username = springdogProperties.getAgentUsername();
    String password = springdogProperties.getAgentPassword();
    UserDetails user = User.withUsername(username)
        .password("{noop}" + password)
        .roles("SPRINGDOG_AGENT_ADMIN")
        .build();

    return new InMemoryUserDetailsManager(user);
  }
}
