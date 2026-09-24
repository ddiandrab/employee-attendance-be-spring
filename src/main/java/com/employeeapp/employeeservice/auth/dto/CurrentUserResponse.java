package com.employeeapp.employeeservice.auth.dto;

import com.employeeapp.employeeservice.user.entity.Role;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CurrentUserResponse {

    private Integer id;
    private String email;
    private Role role;
}