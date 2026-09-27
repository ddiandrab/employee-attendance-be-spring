package com.employeeapp.employeeservice.attendance.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@Builder
public class AttendanceResponse {

    private Integer id;
    private Integer employeeId;
    private LocalDate attendanceDate;
    private OffsetDateTime checkIn;
    private OffsetDateTime checkOut;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
