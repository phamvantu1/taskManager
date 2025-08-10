package com.example.taskManager.repository;

import com.example.taskManager.model.DTO.response.UserTaskDashBoard;
import com.example.taskManager.model.DTO.response.UserTaskDashBoardResponse;
import com.example.taskManager.model.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {


    @Query(value = "SELECT * FROM tasks t " +
            "WHERE (:textSearch IS NULL OR t.title LIKE CONCAT('%', :textSearch, '%') OR t.description LIKE CONCAT('%', :textSearch, '%')) " +
            "AND (:startTime IS NULL OR t.start_time >= CAST(:startTime AS timestamp)) " +
            "AND (:endTime IS NULL OR t.end_time <= CAST(:endTime AS timestamp)) " +
            "AND (:projectId IS NULL OR t.project_id = :projectId) " +
            " AND (:status IS NULL OR t.status = :status) " +
            " and (:type is null or (:type = 0 and t.assigned_to = :userId ) " +
            " or (:type = 1 and t.created_by = :userId )) " +
            " and t.is_deleted = 'false' " +
            " order by t.updated_at desc ",
            nativeQuery = true)
    Page<Task> getAllTasks(
            @Param("textSearch") String textSearch,
            @Param("startTime") String startTime,
            @Param("endTime") String endTime,
            @Param("projectId") Long projectId,
            @Param("status") String status,
            @Param("type") Long type,
            @Param("userId") Long userId,
            Pageable pageable
    );


    @Query(value = "select distinct t.* from tasks t " +
            "left join projects p on t.project_id = p.id " +
            "where (:projectId is null or p.id = :projectId) " +
            " and (:type is null or (:type = 0 and t.assigned_to = :userId ) " +
            " or (:type = 1 and t.created_by = :userId )) " +
            " and t.is_deleted = 'false' ",
            nativeQuery = true)
    List<Task> findAllByProjectId(@Param("projectId") Long projectId,
                                  @Param("type") Long type,
                                  @Param("userId") Long userId);


    @Query(value = "SELECT  count(distinct t.id) from tasks t " +
            "join projects p on p.id = t.project_id " +
            " join departments d on d.id = p.department_id " +
            " where :departmentId is null or d.id = :departmentId "
            , nativeQuery = true)
    Long totalAllTasks(Long departmentId);


    @Query(value = "SELECT  count(distinct t.id) from tasks t " +
            "join projects p on p.id = t.project_id " +
            " join departments d on d.id = p.department_id " +
            " where t.status = 'COMPLETED' and (:departmentId is null or d.id = :departmentId) "
            , nativeQuery = true)
    Long totalTaskCompleted(Long departmentId);

    @Query(value = """
            SELECT
                COUNT(CASE WHEN t.status = 'COMPLETED' THEN 1 END) AS completed,
                COUNT(CASE WHEN t.status = 'PROCESSING' THEN 1 END) AS inProgress,
                COUNT(CASE WHEN t.status = 'PENDING' THEN 1 END) AS pending,
                COUNT(CASE WHEN t.status = 'OVERDUE' THEN 1 END) AS overdue
            FROM tasks t 
            left JOIN projects p ON t.project_id = p.id
            left JOIN departments d on d.id = p.department_id            
            WHERE (:departmentId IS NULL OR p.department_id = :departmentId)
              AND (:projectId IS NULL OR p.id = :projectId)
              AND (:startTime IS NULL OR p.start_time >= CAST(:startTime AS TIMESTAMP))
              AND (:endTime IS NULL OR p.start_time <= CAST(:endTime AS TIMESTAMP))
            """, nativeQuery = true)
    Object getTaskDashboardData(@Param("departmentId") Long departmentId,
                                @Param("projectId") Long projectId,
                                @Param("startTime") String startTime,
                                @Param("endTime") String endTime);


    @Query(value = """
            SELECT distinct on (u.email)
                                           u.email AS name,
                                           d.name AS department_name,
                                           COUNT(*) FILTER (WHERE t.status = 'PROCESSING') AS processing,
                                           COUNT(*) FILTER (WHERE t.status = 'OVERDUE') AS overdue,
                                           COUNT(*) FILTER (WHERE t.status = 'WAIT_COMPLETED') AS wait_completed,
                                           COUNT(*) FILTER (WHERE t.status = 'COMPLETED') AS completed,
                                           COUNT(*) FILTER (WHERE t.status = 'PENDING') AS pending,
                                           COUNT(*) AS total_tasks,
                                       SUM(CASE WHEN ts.score_value > 0 THEN ts.score_value ELSE 0 END) AS plus_point,
                                       SUM(CASE WHEN ts.score_value < 0 THEN ts.score_value ELSE 0 END) AS minus_point,
                                       COALESCE(SUM(ts.score_value),0) AS total_score
                                       FROM users u
                                       left JOIN tasks t ON t.assigned_to = u.id
                                       LEFT JOIN department_users du ON u.id = du.user_id
                                       LEFT JOIN departments d ON du.department_id = d.id
                                       left JOIN scores ts ON t.id = ts.task_id
                                       WHERE (:departmentId IS NULL OR d.id = :departmentId)
                                         AND (:textSearch IS NULL OR u.email ILIKE CONCAT('%', :textSearch, '%'))
                                         AND (:startTime is null or t.start_time >= CAST(:startTime AS TIMESTAMP))
                                       AND (:endTime is null or t.end_time <= CAST(:endTime AS TIMESTAMP))
                                       GROUP BY u.id, u.email, d.name
                                       ORDER BY u.email , total_score desc
            """,
            nativeQuery = true,
            countQuery = """
                    SELECT COUNT(*) FROM (
                        SELECT DISTINCT u.id
                        FROM users u
                        LEFT JOIN tasks t ON t.assigned_to = u.id
                        LEFT JOIN department_users du ON u.id = du.user_id
                        LEFT JOIN departments d ON du.department_id = d.id
                        LEFT JOIN scores ts ON t.id = ts.task_id
                        WHERE (:departmentId IS NULL OR d.id = :departmentId)
                          AND (:textSearch IS NULL OR u.email ILIKE CONCAT('%', :textSearch, '%'))
                          AND (:startTime IS NULL OR t.start_time >= CAST(:startTime AS TIMESTAMP))
                          AND (:endTime IS NULL OR t.end_time <= CAST(:endTime AS TIMESTAMP))
                    ) AS distinct_users
                    """
    )
    Page<Object[]> getDashboardUserTasks(
            @Param("departmentId") Long departmentId,
            @Param("textSearch") String textSearch,
            @Param("startTime") String startTime,
            @Param("endTime") String endTime,
            Pageable pageable
    );


    @Query(value = """
    SELECT distinct on (u.email)
        u.email AS name,
        d.name AS departmentName,
        COUNT(*) FILTER (WHERE t.status = 'PROCESSING') AS processing,
        COUNT(*) FILTER (WHERE t.status = 'OVERDUE') AS overdue,
        COUNT(*) FILTER (WHERE t.status = 'WAIT_COMPLETED') AS waitCompleted,
        COUNT(*) FILTER (WHERE t.status = 'COMPLETED') AS completed,
        COUNT(*) FILTER (WHERE t.status = 'PENDING') AS pending,
        COUNT(*) AS totalTasks,
        SUM(CASE WHEN ts.score_value > 0 THEN ts.score_value ELSE 0 END) AS plusPoint,
        SUM(CASE WHEN ts.score_value < 0 THEN ts.score_value ELSE 0 END) AS minusPoint,
        COALESCE(SUM(ts.score_value),0) AS totalScore
    FROM tasks t
    JOIN users u ON t.assigned_to = u.id
    LEFT JOIN department_users du ON u.id = du.user_id
     LEFT JOIN departments d ON du.department_id = d.id
    LEFT JOIN scores ts ON t.id = ts.task_id
    WHERE (:departmentId IS NULL OR d.id = :departmentId)
      AND (:textSearch IS NULL OR u.email ILIKE CONCAT('%', :textSearch, '%'))
      AND (:startTime is null or t.start_time >= CAST(:startTime AS TIMESTAMP))
      AND (:endTime is null or t.end_time <= CAST(:endTime AS TIMESTAMP))
    GROUP BY u.id, u.email, d.name
    ORDER BY u.email , total_score desc
    """,
            nativeQuery = true)
    List<UserTaskDashBoard> exportDashboardUserTasks(
            @Param("departmentId") Long departmentId,
            @Param("textSearch") String textSearch,
            @Param("startTime") String startTime,
            @Param("endTime") String endTime
    );


    List<Task> findByProjectId(Long projectId);


    List<Task> findByEndTimeBefore(LocalDate date);
}
