package com.employeeapp.employeeservice.user.repository;

import com.employeeapp.employeeservice.user.entity.User;
import com.employeeapp.employeeservice.user.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepository
        extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByRoleIn(Collection<Role> roles);
}
