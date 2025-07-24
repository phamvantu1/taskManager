package com.example.taskManager.controller;

import com.example.taskManager.common.exception.Response;
import com.example.taskManager.model.DTO.request.DepartmentRequest;
import com.example.taskManager.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping("/create")
    public ResponseEntity<Response<?>> createDepartment(@RequestBody DepartmentRequest departmentRequest ,
                                                        Authentication authentication) {
        return ResponseEntity.ok(Response.success(departmentService.createDepartment(departmentRequest, authentication)));
    }

}
