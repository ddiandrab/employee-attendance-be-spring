package com.employeeapp.employeeservice.attendance.service;

import com.employeeapp.employeeservice.attendance.entity.AttendanceRecord;
import com.employeeapp.employeeservice.attendance.repository.AttendanceRecordRepository;
import com.employeeapp.employeeservice.common.exception.BadRequestException;
import com.employeeapp.employeeservice.common.exception.ResourceNotFoundException;
import com.employeeapp.employeeservice.employee.entity.Employee;
import com.employeeapp.employeeservice.employee.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AttendanceServiceTest {

    private final AttendanceRecordRepository attendanceRecordRepository = mock(AttendanceRecordRepository.class);
    private final EmployeeRepository employeeRepository = mock(EmployeeRepository.class);
    private final AttendanceService attendanceService = new AttendanceService(attendanceRecordRepository, employeeRepository);
    private final Employee employee = new Employee();
    private final LocalDate today = LocalDate.now(ZoneId.of("Asia/Jakarta"));

    @BeforeEach
    void setUp() {
        employee.setId(17);
        employee.setFirstName("Rani");
        when(employeeRepository.findByUserEmail("rani@example.com")).thenReturn(Optional.of(employee));
    }

    @Test
    void checkInCreatesRecordForCurrentJakartaDate() {
        when(attendanceRecordRepository.findByEmployeeIdAndAttendanceDate(17, today)).thenReturn(Optional.empty());
        when(attendanceRecordRepository.save(any(AttendanceRecord.class))).thenAnswer(invocation -> {
            AttendanceRecord record = invocation.getArgument(0);
            record.setId(5);
            return record;
        });

        var response = attendanceService.checkIn("rani@example.com");

        ArgumentCaptor<AttendanceRecord> recordCaptor = ArgumentCaptor.forClass(AttendanceRecord.class);
        verify(attendanceRecordRepository).save(recordCaptor.capture());
        assertEquals(today, recordCaptor.getValue().getAttendanceDate());
        assertNotNull(recordCaptor.getValue().getCheckIn());
        assertEquals(5, response.getId());
        assertEquals(17, response.getEmployeeId());
    }

    @Test
    void checkInRejectsExistingAttendance() {
        when(attendanceRecordRepository.findByEmployeeIdAndAttendanceDate(eq(17), eq(today)))
                .thenReturn(Optional.of(new AttendanceRecord()));

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> attendanceService.checkIn("rani@example.com"));

        assertEquals("Employee has already checked in today", exception.getMessage());
    }

    @Test
    void checkOutRejectsMissingCheckIn() {
        when(attendanceRecordRepository.findByEmployeeIdAndAttendanceDate(17, today)).thenReturn(Optional.empty());

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> attendanceService.checkOut("rani@example.com"));

        assertEquals("Employee has not checked in today", exception.getMessage());
    }

    @Test
    void historyUsesRequestedInclusiveDateRange() {
        when(attendanceRecordRepository.findByEmployeeIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(
                17, LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30))).thenReturn(java.util.List.of());

        attendanceService.findMyAttendance("rani@example.com", "2026-09-01", "2026-09-30");

        verify(attendanceRecordRepository).findByEmployeeIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(
                17, LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30));
    }

    @Test
    void checkInRejectsUserWithoutEmployeeProfile() {
        when(employeeRepository.findByUserEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> attendanceService.checkIn("missing@example.com"));
    }
}
