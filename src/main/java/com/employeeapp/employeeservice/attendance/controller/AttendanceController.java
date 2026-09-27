package com.employeeapp.employeeservice.attendance.controller;

import com.employeeapp.employeeservice.attendance.dto.AttendanceListResponse;
import com.employeeapp.employeeservice.attendance.dto.AttendanceResponse;
import com.employeeapp.employeeservice.attendance.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/check-in")
    public AttendanceResponse checkIn(Authentication authentication) {
        return attendanceService.checkIn(authentication.getName());
    }

    @PostMapping("/check-out")
    public AttendanceResponse checkOut(Authentication authentication) {
        return attendanceService.checkOut(authentication.getName());
    }

    @GetMapping("/me")
    public List<AttendanceResponse> findMyAttendance(
            Authentication authentication,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        return attendanceService.findMyAttendance(authentication.getName(), from, to);
    }

    @GetMapping
    public List<AttendanceListResponse> findAll(
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        return attendanceService.findAllAttendance(from, to);
    }
}
