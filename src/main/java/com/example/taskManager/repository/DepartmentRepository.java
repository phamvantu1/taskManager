package com.example.taskManager.repository;

import com.example.taskManager.model.entity.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.RequestParam;

public interface DepartmentRepository extends JpaRepository<Department, Long> {


    @Query(value = "select * from departments d " +
            "where :textSearch is null OR LOWER(d.name) LIKE CONCAT('%', LOWER(:textSearch), '%') " +
            " and d.status = 'ACTIVE' "
    , nativeQuery = true)
    Page<Department> findAllDepartment(@RequestParam("textSearch") String textSearch,
                                       Pageable pageable);

}
