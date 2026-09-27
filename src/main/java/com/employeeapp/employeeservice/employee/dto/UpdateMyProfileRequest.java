package com.employeeapp.employeeservice.employee.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateMyProfileRequest {

    private String phone;
    private String photoUrl;
}
