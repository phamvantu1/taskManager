package com.example.taskManager.controller;

import com.example.taskManager.common.exception.Response;
import com.example.taskManager.model.DTO.request.TaskRequest;
import com.example.taskManager.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
                                                   @RequestParam(name = "projectId", required = false) Long projectId) {
        return ResponseEntity.ok(Response.success(taskService.getAllTasks(page, size, textSearch, startTime, endTime, projectId)));
    }
}
