package com.example.taskManager.repository;

import com.example.taskManager.model.entity.DepartmentUser;
import com.example.taskManager.model.entity.DepartmentUserId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepartmentUserRepository extends JpaRepository<DepartmentUser, DepartmentUserId> {

    boolean existsByDepartmentIdAndUserId(Long departmentId, Long userId);

    List<DepartmentUser> findByDepartmentId(Long departmentId);

    boolean existsByDepartmentIdAndRole(Long departmentId, String role);

    DepartmentUser findByUserIdAndDepartmentId(Long userId, Long departmentId);


}
