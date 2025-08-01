package com.example.taskManager.controller;

import com.example.taskManager.common.exception.Response;
import com.example.taskManager.model.DTO.request.TaskRequest;
import com.example.taskManager.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping("/create")
    public ResponseEntity<Response<?>> createTask(@RequestBody TaskRequest taskRequest) {
       return ResponseEntity.ok(Response.success(taskService.createTask(taskRequest)));
    }

    @GetMapping("/getAll")
    public ResponseEntity<Response<?>> getAllTasks(@RequestParam(name = "page" ,required = false, defaultValue = "0") Integer page,
                                                   @RequestParam(name = "size",required = false, defaultValue = "10") Integer size,
                                                   @RequestParam(name = "textSearch", required = false) String textSearch,
                                                   @RequestParam(name = "startTime", required = false) String startTime,
                                                   @RequestParam(name = "endTime", required = false) String endTime,
                                                   @RequestParam(name = "status", required = false) String status,
                                                   @RequestParam(name = "projectId", required = false) Long projectId,
                                                   @RequestParam(name = "type", required = false) Long type) {
        return ResponseEntity.ok(Response.success(taskService.getAllTasks(page, size, textSearch, startTime, endTime, projectId, status, type)));
    }

    @GetMapping("/dashboard-tasks-by-project")
    public ResponseEntity<Response<?>> getDashboardTasksByProject(@RequestParam(name = "projectId", required = false) Long projectId,
                                                                  @RequestParam(name = "type", required = false) Long type ){
        return ResponseEntity.ok(Response.success(taskService.getDashboardTasksByProject(projectId, type)));
    }

    @GetMapping("/get-details/{taskId}")
    public ResponseEntity<Response<?>> getTaskDetails(@PathVariable Long taskId) {
        return ResponseEntity.ok(Response.success(taskService.getTaskDetails(taskId)));
    }

    @PutMapping("/update/{taskId}")
    public ResponseEntity<Response<?>> updateTask(@PathVariable(name = "taskId") Long taskId,
                                                  @RequestBody TaskRequest taskRequest) {
        return ResponseEntity.ok(Response.success(taskService.updateTask(taskId, taskRequest)));
    }

    @DeleteMapping("/delete-task/{taskId}")
    public ResponseEntity<Response<?>> deleteTask(@PathVariable(name = "taskId") Long taskId) {
        return ResponseEntity.ok(Response.success(taskService.deleteTask(taskId)));
    }

    @PutMapping("/mark-finish-task/{taskId}")
    public ResponseEntity<Response<?>> markFinishTask(@PathVariable(name = "taskId") Long taskId,
                                                      Authentication authentication) {
        return ResponseEntity.ok(Response.success(taskService.markFinishTask(taskId, authentication)));

    }

}
