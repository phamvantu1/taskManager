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

    @GetMapping("/get-all")
    public ResponseEntity<Response<?>> getAllDepartments(Authentication authentication,
                                                         @RequestParam(name = "page",required = false, defaultValue = "0") int page,
                                                         @RequestParam(name = "size",required = false, defaultValue = "10") int size,
                                                         @RequestParam(name = "textSearch", required = false) String textSearch) {
        return ResponseEntity.ok(Response.success(departmentService.getAllDepartments(authentication, page, size, textSearch)));
    }

    @GetMapping("/get-common/{departmentId}")
    public ResponseEntity<Response<?>> getCommonDepartment(@PathVariable Long departmentId,
                                                           Authentication authentication) {
        return ResponseEntity.ok(Response.success(departmentService.getCommonDepartment(departmentId, authentication)));
    }

    @GetMapping("/get-dashboard/{departmentId}")


}
