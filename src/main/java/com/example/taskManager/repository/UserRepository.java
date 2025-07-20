package com.example.taskManager.repository;


import com.example.taskManager.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query(value = "SELECT distinct u.*  from users u  " +
            " join tasks t on u.id = t.assigned_to " +
            " join projects p on p.id = t.project_id " +
            "where p.id = :projectId "
    , nativeQuery = true)
    List<User> listUserInProject(@Param("projectId") Long projectId);
}