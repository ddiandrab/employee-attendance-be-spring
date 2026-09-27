package com.employeeapp.employeeservice.attendance.repository;

import com.employeeapp.employeeservice.attendance.entity.AttendanceRecord;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Integer> {

    Optional<AttendanceRecord> findByEmployeeIdAndAttendanceDate(Integer employeeId, LocalDate attendanceDate);

    List<AttendanceRecord> findByEmployeeIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(
            Integer employeeId,
            LocalDate from,
            LocalDate to);

    @EntityGraph(attributePaths = { "employee", "employee.department" })
    List<AttendanceRecord> findByAttendanceDateBetweenOrderByAttendanceDateDesc(LocalDate from, LocalDate to);
}
