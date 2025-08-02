package com.example.taskManager.repository;

import com.example.taskManager.model.entity.DepartmentUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DepartmentUserRepository extends JpaRepository<DepartmentUser, Long> {

    @Query(value = "SELECT count(*) > 0 " +
            "FROM department_users du " +
            "WHERE du.user_id = :userId AND du.department_id = :departmentId " +
            " and du.is_deleted = false ",
            nativeQuery = true)
    Boolean existsByDepartmentIdAndUserId(@Param("departmentId") Long departmentId,
                                          @Param("userId") Long userId);


    @Query(value = "SELECT * from department_users du " +
            "WHERE du.department_id = :departmentId " +
            "and du.is_deleted = false "
            , nativeQuery = true)
    List<DepartmentUser> findByDepartmentId(@Param("departmentId") Long departmentId);


    @Query(value = "select count(*) > 0 from department_users du " +
            "where du.department_id = :departmentId " +
            "and du.role = :role " +
            "and du.is_deleted = false "
            , nativeQuery = true)
    Boolean existsByDepartmentIdAndRole(@Param("departmentId") Long departmentId,
                                        @Param("role") String role);



    @Query(value = "select du.* from department_users du " +
            "where du.department_id = :departmentId " +
            "and du.role = :role " +
            "and du.is_deleted = false "
            , nativeQuery = true)
    DepartmentUser findByDepartmentIdAndRole(@Param("departmentId") Long departmentId,
                                        @Param("role") String role);


    @Query(value = "select du.* from department_users du " +
            "where du.user_id = :userId " +
            "and du.department_id = :departmentId " +
            "and du.is_deleted = false " +
            " limit 1 "
            , nativeQuery = true)
    DepartmentUser findByUserIdAndDepartmentId(@Param("userId") Long userId,
                                               @Param("departmentId") Long departmentId);

    DepartmentUser findFirstByUserId(Long userId);


}
