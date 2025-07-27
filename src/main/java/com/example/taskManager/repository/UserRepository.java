package com.example.taskManager.repository;


import com.example.taskManager.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {


    @Query(value = "SELECT u.* FROM users u " +
            "WHERE u.email = :email " +
            " limit 1 "
            , nativeQuery = true)
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query(value = "SELECT distinct u.*  from users u  " +
            " join tasks t on u.id = t.assigned_to " +
            " join projects p on p.id = t.project_id " +
            "where p.id = :projectId "
    , nativeQuery = true)
    List<User> listUserInProject(@Param("projectId") Long projectId);


    @Query(value = "SELECT distinct u.* from users u " +
            "left join department_users du on u.id = du.user_id " +
            "where :departmentId is null OR du.department_id = :departmentId "
    , nativeQuery = true)
    Page<User> findAllUser(@Param("departmentId") Long departmentId,
                           Pageable pageable);


    @Query(value = "SELECT distinct u.* from users u " +
            "join projects p on p.owner_id = u.id "
            , nativeQuery = true)
    List<User> findPM();

    @Query(value = "select distinct  u.* from users u "  +
            "join departments d on d.leader_id = u.id " +
            "where d.status = 'ACTIVE' "
    , nativeQuery = true)
    List<User> findLeaderDepartment();

    @Query(value = "select distinct  u.* from users u " +
            "join departments d on d.leader_id = u.id " +
            "where d.status = 'hah' "
            , nativeQuery = true)
    List<User> findAdmin();

    @Query("SELECT u FROM User u WHERE u.id NOT IN :excludedIds")
    Page<User> findAllExcludeIds(@Param("excludedIds") Set<Long> excludedIds, Pageable pageable);

}