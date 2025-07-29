package com.example.taskManager.repository;

import com.example.taskManager.model.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {


    @Query(value = "SELECT * FROM tasks t " +
            "WHERE (:textSearch IS NULL OR t.title LIKE CONCAT('%', :textSearch, '%') OR t.description LIKE CONCAT('%', :textSearch, '%')) " +
            "AND (:startTime IS NULL OR t.start_time >= CAST(:startTime AS timestamp)) " +
            "AND (:endTime IS NULL OR t.end_time <= CAST(:endTime AS timestamp)) " +
            "AND (:projectId IS NULL OR t.project_id = :projectId) " +
            " AND (:status IS NULL OR t.status = :status) " +
            " order by t.updated_at desc " ,
            nativeQuery = true)
    Page<Task> getAllTasks(
            @Param("textSearch") String textSearch,
            @Param("startTime") String startTime,
            @Param("endTime") String endTime,
            @Param("projectId") Long projectId,
            @Param("status") String status,
            Pageable pageable
    );


    @Query(value = "select t.* from tasks t " +
            "join projects p on t.project_id = p.id " +
            "where :projectId is null or p.id = :projectId " ,
            nativeQuery = true)
    List<Task> findAllByProjectId(@Param("projectId") Long projectId);


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


}
