package com.example.taskManager.repository;

import com.example.taskManager.model.DTO.response.ProjectMemberView;
import com.example.taskManager.model.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    Page<Project> findAll(Pageable pageable);

    @Query("""
    SELECT 
        CONCAT(COALESCE(u.firstName, ''), ' ', COALESCE(u.lastName, '')) AS fullName,
        u.email AS email,
        COUNT(t.id) AS totalTasks,
        SUM(CASE WHEN t.status = 'COMPLETED' THEN 1 ELSE 0 END) AS completedTasks,
        SUM(CASE WHEN t.status = 'IN_PROGRESS' THEN 1 ELSE 0 END) AS inProgressTasks,
        SUM(CASE WHEN t.status = 'PENDING' THEN 1 ELSE 0 END) AS pendingTasks,
        SUM(CASE WHEN  t.status = 'OVERDUE' THEN 1 ELSE 0 END) AS overdueTasks
    FROM User u
    LEFT JOIN u.assignedTasks t
    WHERE t.project.id = :projectId
    AND (:textSearch IS NULL OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', COALESCE(:textSearch, ''), '%') )
         OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', COALESCE(:textSearch, ''), '%')) )
    GROUP BY u.id
""")
    Page<ProjectMemberView> findProjectMembersByProjectId(
            @Param("projectId") Long projectId,
            @Param("textSearch") String textSearch,
            Pageable pageable
    );

    @Query(value  = "select  p.* from projects p " +
            "where (:departmentId is null or p.department_id = :departmentId ) " +
            "and (:textSearch is null or p.name ILIKE CONCAT('%', LOWER(:textSearch), '%')  ) " +
            "and (:status is null or :status = p.status ) " +
            " AND (:startTime IS NULL OR p.start_time >= CAST(:startTime AS TIMESTAMP)) " +
            " AND (:endTime IS NULL OR p.end_time <= CAST(:endTime AS TIMESTAMP)) "
    , nativeQuery = true)
    Page<Project> findAllProject(Pageable pageable,
                                 @Param("departmentId") Long departmentId,
                                 @Param("textSearch") String textSearch,
                                 @Param("status") String status,
                                 @Param("startTime") String startTime,
                                 @Param("endTime") String endTime);



    List<Project> findByDepartmentId(Long departmentId);


    @Query(value = " SELECT  count(distinct p.id) from projects p " +
            "where :departmentId is null or p.department_id = :departmentId"
            , nativeQuery = true)
    Long totalAllProjects(Long departmentId);


    @Query(value = " SELECT  count(distinct p.id) from projects p " +
            "where p.status = 'COMPLETED' and (:departmentId is null or p.department_id = :departmentId)"
            , nativeQuery = true)
    Long totalProjectFinished(Long departmentId);

    @Query(value = """
    SELECT
        COUNT(CASE WHEN status = 'COMPLETED' THEN 1 END) AS completed,
        COUNT(CASE WHEN status = 'PROCESSING' THEN 1 END) AS inProgress,
        COUNT(CASE WHEN status = 'PENDING' THEN 1 END) AS pending,
        COUNT(CASE WHEN status = 'OVERDUE' THEN 1 END) AS overdue
    FROM projects p
    WHERE (:departmentId IS NULL OR p.department_id = :departmentId)
      AND (:startTime IS NULL OR p.start_time >= CAST(:startTime AS TIMESTAMP))
      AND (:endTime IS NULL OR p.start_time <= CAST(:endTime AS TIMESTAMP))
    """, nativeQuery = true)
    Object getProjectDashboardData(@Param("departmentId") Long departmentId,
                                     @Param("startTime") String startTime,
                                     @Param("endTime") String endTime);




}
