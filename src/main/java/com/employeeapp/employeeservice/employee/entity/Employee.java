package com.employeeapp.employeeservice.employee.entity;

import com.employeeapp.employeeservice.department.entity.Department;
import com.employeeapp.employeeservice.user.entity.User;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "employee")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "\"employeeNumber\"", nullable = false, unique = true)
    private String employeeNumber;

    @Column(name = "\"firstName\"", nullable = false)
    private String firstName;

    @Column(name = "\"lastName\"")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "\"photoUrl\"")
    private String photoUrl;

    @Column(name = "position")
    private String position;

    @Column(name = "\"joinDate\"")
    private LocalDate joinDate;

    @Column(name = "\"isActive\"", nullable = false)
    private Boolean isActive;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"userId\"", nullable = false, unique = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"departmentId\"")
    private Department department;

    @Column(name = "\"createdAt\"", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "\"updatedAt\"", nullable = false)
    private OffsetDateTime updatedAt;
}