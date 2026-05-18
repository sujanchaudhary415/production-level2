package com.productionPractice.level2.config.security;

import com.productionPractice.level2.security.jwt.AuthEntryPoint;
import com.productionPractice.level2.security.jwt.AuthTokenFilter;
import com.productionPractice.level2.security.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

  @Autowired
  private UserDetailsServiceImpl userDetailsService;

  @Autowired
  private AuthEntryPoint unauthorizedHandler;

  @Bean
  public AuthTokenFilter authenticationJwtTokenFilter() {
    return new AuthTokenFilter();
  }

  @Bean
  public DaoAuthenticationProvider authenticationProvider() {

    DaoAuthenticationProvider authProvider =
            new DaoAuthenticationProvider();

    authProvider.setUserDetailsService(userDetailsService);

    authProvider.setPasswordEncoder(passwordEncoder());

    return authProvider;
  }

  @Bean
  public AuthenticationManager authenticationManager(
          AuthenticationConfiguration authConfig
  ) throws Exception {

    return authConfig.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http)
          throws Exception {

    http.csrf(AbstractHttpConfigurer::disable)

            .exceptionHandling(exception ->
                    exception.authenticationEntryPoint(
                            unauthorizedHandler))

            .sessionManagement(session ->
                    session.sessionCreationPolicy(
                            SessionCreationPolicy.STATELESS))

            .authorizeHttpRequests(requests ->
                    requests
                            // Public APIs
                            .requestMatchers("/api/auth/**")
                            .permitAll()

                            .requestMatchers("/swagger-ui/**")
                            .permitAll()

                            .requestMatchers("/v3/api-docs/**")
                            .permitAll()

                            .requestMatchers("/h2-console/**")
                            .permitAll()

                            // USER role
                            .requestMatchers("/api/public/**")
                            .hasRole("USER")

                            // ADMIN role
                            .requestMatchers("/api/admin/**")
                            .hasRole("ADMIN")

                            // USER or ADMIN
                            .requestMatchers("/api/common/**")
                            .hasAnyRole("USER", "ADMIN")

                            // Any authenticated user
                            .anyRequest()
                            .authenticated()

            );

    http.authenticationProvider(authenticationProvider());

    http.addFilterBefore(
            authenticationJwtTokenFilter(),
            UsernamePasswordAuthenticationFilter.class
    );

    http.headers(headers ->
            headers.frameOptions(
                    frameOptions ->
                            frameOptions.sameOrigin()
            ));

    return http.build();
  }
}