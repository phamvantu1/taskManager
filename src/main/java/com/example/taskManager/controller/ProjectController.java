package com.example.taskManager.controller;


import com.example.taskManager.common.exception.Response;
import com.example.taskManager.model.DTO.request.ProjectRequest;
import com.example.taskManager.service.ProjectService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Slf4j
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping("/create-project")
    public ResponseEntity<Response<?>> createProject( @RequestBody ProjectRequest projectRequest) {

        return ResponseEntity.ok(Response.success((projectService.createProject(projectRequest))));
    }

    @GetMapping("/get-all-projects")
    public ResponseEntity<Response<?>> getAllProjects(@RequestParam(name = "page", defaultValue = "0") int page,
                                                      @RequestParam(name = "size", defaultValue = "10") int size,
                                                      @RequestParam(name = "departmentId", required = false) Long departmentId,
                                                      @RequestParam(name = "textSearch", required = false) String textSearch,
                                                      @RequestParam(name = "status" , required = false) String status,
                                                      @RequestParam(name = "startTime", required = false) String startTime,
                                                      @RequestParam(name = "endTime", required = false) String endTime) {

        return ResponseEntity.ok(Response.success(projectService.getAllProjects(page, size, departmentId,textSearch, status,startTime , endTime)));
    }

    @GetMapping("/get-info-project")
    public ResponseEntity<Response<?>> getInfoProject(@RequestParam(name = "projectId") Long projectId) {
        return ResponseEntity.ok(Response.success(projectService.getInfoProject(projectId)));
    }

    @GetMapping("/get-user-by-project")
    public ResponseEntity<Response<?>> getUserByProject(@RequestParam(name = "projectId", required = false) Long projectId,
                                                        @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                                        @RequestParam(name = "size",required = false, defaultValue = "10") int size ,
                                                        @RequestParam(name = "textSearch", required = false) String textSearch) {
        return ResponseEntity.ok(Response.success(projectService.getUserByProject(projectId, textSearch, page, size)));
    }

    @PutMapping("/update-project")
    public ResponseEntity<Response<?>> updateProject(@RequestBody ProjectRequest projectRequest) {
        return ResponseEntity.ok(Response.success((projectService.updateProject(projectRequest))));
    }
}
