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
    public ResponseEntity<Response<?>> getAllDepartments(@RequestParam(name = "page",required = false, defaultValue = "0") int page,
                                                         @RequestParam(name = "size",required = false, defaultValue = "10") int size,
                                                         @RequestParam(name = "textSearch", required = false) String textSearch) {
        return ResponseEntity.ok(Response.success(departmentService.getAllDepartments( page, size, textSearch)));
    }

    @GetMapping("/get-common/{departmentId}")
    public ResponseEntity<Response<?>> getCommonDepartment(@PathVariable Long departmentId) {
        return ResponseEntity.ok(Response.success(departmentService.getCommonDepartment(departmentId)));
    }

    @GetMapping("/get-dashboard/{departmentId}")
    public ResponseEntity<Response<?>> getDashboardDepartment(@PathVariable Long departmentId) {
        return ResponseEntity.ok(Response.success(departmentService.getDashboardDepartment(departmentId)));
    }

    @DeleteMapping("/delete/{departmentId}")
    public ResponseEntity<Response<?>> deleteDepartment(@PathVariable Long departmentId) {
        return ResponseEntity.ok(Response.success(departmentService.deleteDepartment(departmentId)));
    }

    @PutMapping("/update/{departmentId}")
    public ResponseEntity<Response<?>> updateDepartment(@PathVariable Long departmentId,
                                                        @RequestBody DepartmentRequest departmentRequest,
                                                        Authentication authentication) {
        return ResponseEntity.ok(Response.success(departmentService.updateDepartment(departmentId, departmentRequest, authentication)));
    }

    @PostMapping("/add-user/{departmentId}/{userId}")
    public ResponseEntity<Response<?>> addUserToDepartment(@PathVariable Long departmentId,
                                                           @PathVariable Long userId,
                                                           Authentication authentication) {
        return ResponseEntity.ok(Response.success(departmentService.addUserToDepartment(departmentId, userId, authentication)));
    }


}
