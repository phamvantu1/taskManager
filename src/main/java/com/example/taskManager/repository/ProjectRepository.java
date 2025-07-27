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
            "where :departmentId is null or p.department_id = :departmentId "
    , nativeQuery = true)
    Page<Project> findAllProject(Pageable pageable,
                                 @Param("departmentId") Long departmentId);

    List<Project> findByDepartmentId(Long departmentId);


}
