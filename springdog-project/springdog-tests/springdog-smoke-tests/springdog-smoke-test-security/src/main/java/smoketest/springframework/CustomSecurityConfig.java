package smoketest.springframework;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class CustomSecurityConfig {

  private final String ADMIN_USERNAME = "admin";
  private final String ADMIN_PASSWORD = "password";

  @Bean
//  FIXME: DO NOT ADD @Order(Integer.MIN_VALUE) TO THE CODE
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .securityMatcher("/**")
        .authorizeHttpRequests(authorizeRequests ->
            authorizeRequests
                .requestMatchers("/admin**").hasRole("ADMIN")
                .requestMatchers("/anonymous**").permitAll()
                .anyRequest().permitAll()
        ).formLogin(formLogin -> formLogin
            .loginPage("/login.html")
            .failureUrl("/login.html?failure")
            .loginProcessingUrl("/login.html")
            .defaultSuccessUrl("/admin")
            .permitAll())
        .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/anonymous")
            .permitAll())
        .csrf(AbstractHttpConfigurer::disable);

    return http.build();
  }

  @Bean
  public UserDetailsService userDetailsService() {
    UserDetails user = User.withUsername(ADMIN_USERNAME)
        .password("{noop}" + ADMIN_PASSWORD)
        .roles("ADMIN")
        .build();

    return new InMemoryUserDetailsManager(user);
  }
}
