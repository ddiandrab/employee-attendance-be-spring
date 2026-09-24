package com.employeeapp.employeeservice.department.repository;

import com.employeeapp.employeeservice.department.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {
        boolean existsByName(String name);

        boolean existsByNameAndIdNot(
                        String name,
                        Integer id);
}