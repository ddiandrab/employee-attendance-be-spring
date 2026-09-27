package com.employeeapp.employeeservice.employee.repository;

import com.employeeapp.employeeservice.employee.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    boolean existsByEmployeeNumber(String employeeNumber);

    boolean existsByEmployeeNumberAndIdNot(String employeeNumber, Integer id);

    Optional<Employee> findByUserId(Integer userId);

    Optional<Employee> findByUserEmail(String email);

    boolean existsByUserId(Integer userId);
}
