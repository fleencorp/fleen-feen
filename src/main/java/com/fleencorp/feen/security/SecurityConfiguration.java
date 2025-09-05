package com.fleencorp.feen.security;

import com.fleencorp.feen.common.filter.JwtAuthenticationFilter;
import com.fleencorp.feen.security.provider.CustomAuthenticationProvider;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@ComponentScan(basePackages = {
  "com.fleencorp",
  "com.fleencorp.base"
})
@EnableWebSecurity
@EnableMethodSecurity(
  securedEnabled = true,
  jsr250Enabled = true
)
@AllArgsConstructor
public class SecurityConfiguration {

  private final JwtAuthenticationFilter authenticationFilter;
  private final CustomAuthenticationProvider authenticationProvider;

  protected static final String[] WHITELIST = {
    "/auth/**",
    "/v2/api-docs",
    "/swagger-resources",
    "/swagger-resources/**",
    "/swagger-ui.html",
    "/v3/api-docs/**",
    "/swagger-ui/**",
    "/actuator/health",
    "/**"
  };

  protected static final String[] WHITELIST_RESOURCES = {
    "/resources/**"
  };

  @Bean
  public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {

    http
      .authorizeHttpRequests((requests) ->
        requests
        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
        .requestMatchers(WHITELIST_RESOURCES).permitAll()
        .requestMatchers(WHITELIST).permitAll()
        .anyRequest().authenticated()
      )
      .httpBasic(AbstractHttpConfigurer::disable)
      .formLogin(AbstractHttpConfigurer::disable)
      .cors(AbstractHttpConfigurer::disable)
      .csrf(AbstractHttpConfigurer::disable)
      .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(STATELESS))
      .headers((headers) -> headers.frameOptions((HeadersConfigurer.FrameOptionsConfig::sameOrigin)))
      .addFilterBefore(authenticationFilter,
        UsernamePasswordAuthenticationFilter.class)
      .logout(LogoutConfigurer::permitAll);

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManagerBean(final HttpSecurity http) throws Exception {
    final AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
    authenticationManagerBuilder.authenticationProvider(authenticationProvider);

    return authenticationManagerBuilder.build();
  }

}
