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
            "AND (:projectId IS NULL OR t.project_id = :projectId) " ,
            nativeQuery = true)
    Page<Task> getAllTasks(
            @Param("textSearch") String textSearch,
            @Param("startTime") String startTime,
            @Param("endTime") String endTime,
            @Param("projectId") Long projectId,
            Pageable pageable
    );

    List<Task> findAllByProjectId(Long projectId);

}
