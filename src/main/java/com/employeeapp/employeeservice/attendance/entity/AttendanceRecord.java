package com.employeeapp.employeeservice.attendance.entity;

import com.employeeapp.employeeservice.employee.entity.Employee;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "\"attendanceRecord\"", uniqueConstraints = @UniqueConstraint(columnNames = {
        "employeeId", "attendanceDate"
}))
public class AttendanceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"employeeId\"", nullable = false)
    private Employee employee;

    @Column(name = "\"attendanceDate\"", nullable = false)
    private LocalDate attendanceDate;

    @Column(name = "\"checkIn\"")
    private OffsetDateTime checkIn;

    @Column(name = "\"checkOut\"")
    private OffsetDateTime checkOut;

    @Column(name = "\"createdAt\"", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "\"updatedAt\"", nullable = false)
    private OffsetDateTime updatedAt;
}
