package com.employeeapp.employeeservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter
                extends OncePerRequestFilter {

        private final JwtService jwtService;
        private final CustomUserDetailsService userDetailsService;

        @Override
        protected void doFilterInternal(
                        HttpServletRequest request,
                        HttpServletResponse response,
                        FilterChain filterChain) throws ServletException, IOException {

                String authorizationHeader = request.getHeader("Authorization");

                // Request tidak membawa Bearer token.
                // Biarkan Spring Security menentukan apakah endpoint
                // tersebut public atau protected.
                if (authorizationHeader == null
                                || !authorizationHeader.startsWith("Bearer ")) {

                        filterChain.doFilter(request, response);
                        return;
                }

                String token = authorizationHeader.substring(7);

                // JWT invalid / expired.
                if (!jwtService.isTokenValid(token)) {
                        filterChain.doFilter(request, response);
                        return;
                }

                String email = jwtService.extractEmail(token);

                // Jangan replace authentication apabila sebelumnya sudah ada authentication di
                // SecurityContext.
                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        // Load role from database to ensure the role is up-to-date
                        // Can change to use a cache or other mechanism to reduce database calls if
                        // needed
                        UserDetails userDetails = userDetailsService
                                        .loadUserByUsername(email);

                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities());

                        SecurityContextHolder
                                        .getContext()
                                        .setAuthentication(authentication);
                }

                filterChain.doFilter(request, response);
        }
}