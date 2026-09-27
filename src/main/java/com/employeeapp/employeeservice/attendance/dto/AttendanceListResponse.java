package com.employeeapp.employeeservice.attendance.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@Builder
public class AttendanceListResponse {

    private Integer id;
    private Integer employeeId;
    private String employeeNumber;
    private String employeeName;
    private Integer departmentId;
    private String position;
    private LocalDate attendanceDate;
    private OffsetDateTime checkIn;
    private OffsetDateTime checkOut;
}
