package com.example.taskManager.repository;

import com.example.taskManager.model.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findFirstByName(String name);
}
