package com.employeeapp.employeeservice.attendance.service;

import com.employeeapp.employeeservice.attendance.dto.AttendanceListResponse;
import com.employeeapp.employeeservice.attendance.dto.AttendanceResponse;
import com.employeeapp.employeeservice.attendance.entity.AttendanceRecord;
import com.employeeapp.employeeservice.attendance.repository.AttendanceRecordRepository;
import com.employeeapp.employeeservice.common.exception.BadRequestException;
import com.employeeapp.employeeservice.common.exception.ResourceNotFoundException;
import com.employeeapp.employeeservice.employee.entity.Employee;
import com.employeeapp.employeeservice.employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private static final ZoneId BUSINESS_ZONE = ZoneId.of("Asia/Jakarta");

    private final AttendanceRecordRepository attendanceRecordRepository;
    private final EmployeeRepository employeeRepository;

    @Transactional
    public AttendanceResponse checkIn(String email) {
        Employee employee = findEmployeeByEmail(email);
        LocalDate attendanceDate = currentBusinessDate();

        if (attendanceRecordRepository.findByEmployeeIdAndAttendanceDate(employee.getId(), attendanceDate).isPresent()) {
            throw new BadRequestException("Employee has already checked in today");
        }

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        AttendanceRecord record = new AttendanceRecord();
        record.setEmployee(employee);
        record.setAttendanceDate(attendanceDate);
        record.setCheckIn(now);
        record.setCreatedAt(now);
        record.setUpdatedAt(now);

        return toResponse(attendanceRecordRepository.save(record));
    }

    @Transactional
    public AttendanceResponse checkOut(String email) {
        Employee employee = findEmployeeByEmail(email);
        LocalDate attendanceDate = currentBusinessDate();
        AttendanceRecord record = attendanceRecordRepository
                .findByEmployeeIdAndAttendanceDate(employee.getId(), attendanceDate)
                .orElseThrow(() -> new BadRequestException("Employee has not checked in today"));

        if (record.getCheckOut() != null) {
            throw new BadRequestException("Employee has already checked out today");
        }

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        record.setCheckOut(now);
        record.setUpdatedAt(now);
        return toResponse(attendanceRecordRepository.save(record));
    }

    @Transactional(readOnly = true)
    public List<AttendanceResponse> findMyAttendance(String email, String from, String to) {
        Employee employee = findEmployeeByEmail(email);
        DateRange dateRange = dateRange(from, to);

        return attendanceRecordRepository
                .findByEmployeeIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(
                        employee.getId(), dateRange.from(), dateRange.to())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AttendanceListResponse> findAllAttendance(String from, String to) {
        DateRange dateRange = dateRange(from, to);

        return attendanceRecordRepository
                .findByAttendanceDateBetweenOrderByAttendanceDateDesc(dateRange.from(), dateRange.to())
                .stream()
                .map(this::toListResponse)
                .toList();
    }

    private Employee findEmployeeByEmail(String email) {
        return employeeRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Employee profile not found"));
    }

    private DateRange dateRange(String from, String to) {
        LocalDate today = currentBusinessDate();
        LocalDate defaultFrom = today.withDayOfMonth(1);

        return new DateRange(parseDate(from, defaultFrom), parseDate(to, today));
    }

    private LocalDate parseDate(String value, LocalDate defaultValue) {
        if (value == null || value.isBlank()) {
            return defaultValue;
        }

        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException exception) {
            throw new BadRequestException("Date must use the ISO-8601 format (YYYY-MM-DD)");
        }
    }

    private LocalDate currentBusinessDate() {
        return LocalDate.now(BUSINESS_ZONE);
    }

    private AttendanceResponse toResponse(AttendanceRecord record) {
        return AttendanceResponse.builder()
                .id(record.getId())
                .employeeId(record.getEmployee().getId())
                .attendanceDate(record.getAttendanceDate())
                .checkIn(record.getCheckIn())
                .checkOut(record.getCheckOut())
                .createdAt(record.getCreatedAt())
                .updatedAt(record.getUpdatedAt())
                .build();
    }

    private AttendanceListResponse toListResponse(AttendanceRecord record) {
        Employee employee = record.getEmployee();
        String employeeName = Stream.of(employee.getFirstName(), employee.getLastName())
                .filter(name -> name != null && !name.isBlank())
                .reduce((firstName, lastName) -> firstName + " " + lastName)
                .orElse("-");

        return AttendanceListResponse.builder()
                .id(record.getId())
                .employeeId(employee.getId())
                .employeeNumber(employee.getEmployeeNumber())
                .employeeName(employeeName)
                .departmentId(employee.getDepartment() == null ? null : employee.getDepartment().getId())
                .position(employee.getPosition())
                .attendanceDate(record.getAttendanceDate())
                .checkIn(record.getCheckIn())
                .checkOut(record.getCheckOut())
                .build();
    }

    private record DateRange(LocalDate from, LocalDate to) {
    }
}
