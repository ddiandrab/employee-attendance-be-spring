package com.employeeapp.employeeservice.auth.service;

import com.employeeapp.employeeservice.auth.dto.CurrentUserResponse;
import com.employeeapp.employeeservice.auth.dto.LoginRequest;
import com.employeeapp.employeeservice.auth.dto.LoginResponse;
import com.employeeapp.employeeservice.common.exception.InvalidCredentialsException;
import com.employeeapp.employeeservice.common.exception.ResourceNotFoundException;
import com.employeeapp.employeeservice.security.JwtService;
import com.employeeapp.employeeservice.security.PasswordService;
import com.employeeapp.employeeservice.user.entity.User;
import com.employeeapp.employeeservice.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

        private final UserRepository userRepository;
        private final PasswordService passwordService;
        // private final PasswordEncoder passwordEncoder;
        private final JwtService jwtService;

        public LoginResponse login(LoginRequest request) {
                // Both error messages are the same to prevent user enumeration attacks
                User user = userRepository
                                .findByEmail(request.getEmail())
                                .orElseThrow(() -> {
                                        log.warn("Login failed: user not found for email={}",
                                                        request.getEmail());

                                        return new InvalidCredentialsException(
                                                        "Invalid email or password");
                                });

                boolean passwordMatches = passwordService.matches(
                                request.getPassword(),
                                user.getPasswordHash());

                if (!passwordMatches) {
                        throw new InvalidCredentialsException(
                                        "Invalid email or password");
                }

                String token = jwtService.generateToken(user);

                return LoginResponse.builder()
                                .accessToken(token)
                                .tokenType("Bearer")
                                .build();
        }

        public CurrentUserResponse getCurrentUser(String email) {
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "User not found"));

                return CurrentUserResponse.builder()
                                .id(user.getId())
                                .email(user.getEmail())
                                .role(user.getRole())
                                .build();
        }
}