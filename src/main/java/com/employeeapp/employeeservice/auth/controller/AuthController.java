package com.employeeapp.employeeservice.auth.controller;

import com.employeeapp.employeeservice.auth.dto.CurrentUserResponse;
import com.employeeapp.employeeservice.auth.dto.LoginRequest;
import com.employeeapp.employeeservice.auth.dto.LoginResponse;
import com.employeeapp.employeeservice.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(
            @Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    public CurrentUserResponse me(Authentication authentication) {
        return authService.getCurrentUser(authentication.getName());
    }
}