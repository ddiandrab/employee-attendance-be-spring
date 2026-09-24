package com.employeeapp.employeeservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
// import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.employeeapp.employeeservice.security.CustomAccessDeniedHandler;
import com.employeeapp.employeeservice.security.CustomAuthenticationEntryPoint;
import com.employeeapp.employeeservice.security.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthenticationFilter;
        private final CustomAuthenticationEntryPoint authenticationEntryPoint;
        private final CustomAccessDeniedHandler accessDeniedHandler;

        @Bean
        public SecurityFilterChain securityFilterChain(
                        HttpSecurity http) throws Exception {

                http
                                .csrf(csrf -> csrf.disable())

                                .sessionManagement(session -> session.sessionCreationPolicy(
                                                SessionCreationPolicy.STATELESS))

                                // Exception handling untuk mengatur response ketika terjadi error
                                .exceptionHandling(exception -> exception
                                                .authenticationEntryPoint(authenticationEntryPoint)
                                                .accessDeniedHandler(accessDeniedHandler))

                                .authorizeHttpRequests(auth -> auth
                                                // Login harus bisa dipanggil tanpa JWT
                                                .requestMatchers("/auth/login")
                                                .permitAll()

                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/auth/me")
                                                .authenticated()

                                                // ADMIN dan EMPLOYEE boleh lihat department
                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/departments/**")
                                                .hasAnyRole(
                                                                "ADMIN",
                                                                "EMPLOYEE")

                                                // Hanya ADMIN boleh create, update, delete department
                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/departments/**")
                                                .hasRole("ADMIN")

                                                .requestMatchers(
                                                                HttpMethod.PATCH,
                                                                "/departments/**")
                                                .hasRole("ADMIN")

                                                .requestMatchers(
                                                                HttpMethod.DELETE,
                                                                "/departments/**")
                                                .hasRole("ADMIN")

                                                // Endpoint lainnya harus login
                                                .anyRequest()
                                                .authenticated())
                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        // @Bean
        // public PasswordEncoder passwordEncoder() {
        // return new BCryptPasswordEncoder();
        // }

        // @Bean
        // public PasswordEncoder passwordEncoder() {
        // return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
        // }
}