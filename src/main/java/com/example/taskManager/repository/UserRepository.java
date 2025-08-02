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
            "where (:departmentId is null OR (du.department_id = :departmentId and du.is_deleted = false)) "
            , nativeQuery = true)
    Page<User> findAllUser(@Param("departmentId") Long departmentId,
                           Pageable pageable);


    @Query(value = "SELECT distinct u.* from users u " +
            "join projects p on p.owner_id = u.id " +
            "left join department_users du on u.id = du.user_id " +
            "left join departments d on d.id = du.department_id " +
            "where d.status = 'ACTIVE' " +
            " and (:departmentId is null or d.id = :departmentId  ) " +
            " and (:textSearch is null or u.first_name ILIKE concat('%', lower(:textSearch), '%' )  " +
            "  or u.last_name ILIKE concat('%', lower(:textSearch), '%' ) " +
            "  or u.email ILIKE concat('%', lower(:textSearch), '%' )  ) " +
            " and u.is_active = true "
            , nativeQuery = true)
    List<User> findPM(@Param("departmentId") Long departmentId,
                      @Param("textSearch") String textSearch);

    @Query(value = "select distinct  u.* from users u " +
            "join department_users du on du.user_id = u.id " +
            " join departments d on d.id = du.department_id " +
            "where d.status = 'ACTIVE'  " +
            " and du.role = 'LEADER' " +
            " and du.is_deleted = false " +
            " and (:departmentId is null or d.id = :departmentId  )" +
            " and (:textSearch is null or u.first_name ILIKE concat('%', lower(:textSearch), '%' )  " +
            "  or u.last_name ILIKE concat('%', lower(:textSearch), '%' ) " +
            "  or u.email ILIKE concat('%', lower(:textSearch), '%' )  ) " +
            " and u.is_active = true "
            , nativeQuery = true)
    List<User> findLeaderDepartment(@Param("departmentId") Long departmentId,
                                    @Param("textSearch") String textSearch);

    @Query(value = "select distinct  u.* from users u " +
            "left join department_users du on u.id = du.user_id " +
            "left join departments d on d.id = du.department_id " +
            "join user_roles ru on ru.user_id = u.id " +
            "join roles r on r.id = ru.role_id " +
            "where u.is_active = 'true'" +
            "and r.name = 'ADMIN' " +
            "and (:departmentId IS NULL OR du.department_id = :departmentId) " +
            " and (:textSearch is null or u.first_name ILIKE concat('%', lower(:textSearch), '%' )  " +
            "  or u.last_name ILIKE concat('%', lower(:textSearch), '%' ) " +
            "  or u.email ILIKE concat('%', lower(:textSearch), '%' )  ) "
            , nativeQuery = true)
    List<User> findAdmin( @Param("departmentId") Long departmentId,
                          @Param("textSearch") String textSearch);

    @Query(value = "SELECT distinct u.* FROM users u " +
            "left join department_users du on u.id = du.user_id " +
            "left join departments d on d.id = du.department_id " +
            "WHERE u.id NOT IN :excludedIds " +
            " and u.is_active = 'true' " +
            " and d.status = 'ACTIVE' " +
            "and (:departmentId IS NULL OR du.department_id = :departmentId) " +
            " and (:textSearch is null or u.first_name ILIKE concat('%', lower(:textSearch), '%' )  " +
            "  or u.last_name ILIKE concat('%', lower(:textSearch), '%' ) " +
            "  or u.email ILIKE concat('%', lower(:textSearch), '%' )  ) "
            , nativeQuery = true)
    Page<User> findAllExcludeIds(@Param("excludedIds") Set<Long> excludedIds,
                                 @Param("departmentId") Long departmentId,
                                 @Param("textSearch") String textSearch,
                                 Pageable pageable);


    @Query(value = "SELECT count(DISTINCT u.id) FROM users u " +
            "LEFT JOIN department_users du ON u.id = du.user_id " +
            "WHERE u.is_active = 'true' " +
            "and (:departmentId IS NULL OR du.department_id = :departmentId) "
            , nativeQuery = true)
    Long totalAllNumber(@Param("departmentId") Long departmentId);


    @Query(value = "SELECT COUNT(DISTINCT u.id) FROM users u " +
            "LEFT JOIN department_users du ON u.id = du.user_id " +
            "WHERE u.is_active = 'true' " +
            "AND (:departmentId IS NULL OR du.department_id = :departmentId) " +
            "AND u.created_at >= CURRENT_DATE - INTERVAL '30 days'"
            , nativeQuery = true)
    Long totalNewUsers(@Param("departmentId") Long departmentId);


    @Query(value = """
            SELECT 
              CONCAT(u.first_name, ' ', u.last_name, ' (', u.email, ')') AS full_name,
              COUNT(CASE WHEN t.status = 'COMPLETED' THEN 1 END)::decimal 
                / NULLIF(COUNT(t.id), 0) AS progress
            FROM users u
            LEFT JOIN tasks t ON u.id = t.assigned_to
            LEFT JOIN projects p ON t.project_id = p.id
            WHERE u.is_active = 'true'
              AND (:departmentId IS NULL OR p.department_id = :departmentId)
              AND (:startTime IS NULL OR t.start_time >= CAST(:startTime AS TIMESTAMP))
              AND (:endTime IS NULL OR t.end_time <= CAST(:endTime AS TIMESTAMP))
            GROUP BY u.id, u.first_name, u.last_name, u.email
            ORDER BY progress DESC
            """,
            nativeQuery = true)
    Page<Object[]> getDashboardUsers(@Param("departmentId") Long departmentId,
                                     @Param("startTime") String startTime,
                                     @Param("endTime") String endTime,
                                     Pageable pageable);


    @Query(value = """
            SELECT 
              CONCAT(u.first_name, ' ', u.last_name, ' (', u.email, ')') AS full_name,
              COUNT(CASE WHEN t.status = 'COMPLETED' THEN 1 END)::decimal 
                / NULLIF(COUNT(t.id), 0) AS progress
            FROM users u
            LEFT JOIN tasks t ON u.id = t.assigned_to
            LEFT JOIN projects p ON t.project_id = p.id
            WHERE u.is_active = 'true'
              AND (:departmentId IS NULL OR p.department_id = :departmentId)
              AND (:startTime IS NULL OR t.start_time >= CAST(:startTime AS TIMESTAMP))
              AND (:endTime IS NULL OR t.end_time <= CAST(:endTime AS TIMESTAMP))
            GROUP BY u.id, u.first_name, u.last_name, u.email
            ORDER BY progress DESC
            """,
            nativeQuery = true)
    List<Object[]> getDashboardUsersOverView(@Param("departmentId") Long departmentId,
                                             @Param("startTime") String startTime,
                                             @Param("endTime") String endTime);


}