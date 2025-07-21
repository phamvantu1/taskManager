package com.example.taskManager.controller;


import com.example.taskManager.common.exception.Response;
import com.example.taskManager.model.DTO.request.ProjectRequest;
import com.example.taskManager.service.ProjectService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping("/create-project")
    public ResponseEntity<Response<?>> createProject( @RequestBody ProjectRequest projectRequest) {

        return ResponseEntity.ok(Response.success((projectService.createProject(projectRequest))));
    }

    @GetMapping("/get-all-projects")
    public ResponseEntity<Response<?>> getAllProjects(@RequestParam(name = "page", defaultValue = "0") int page,
                                                      @RequestParam(name = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok(Response.success(projectService.getAllProjects(page, size)));
    }

    @GetMapping("/get-info-project")
    public ResponseEntity<Response<?>> getInfoProject(@RequestParam(name = "projectId") Long projectId) {
        return ResponseEntity.ok(Response.success(projectService.getInfoProject(projectId)));
    }
}
