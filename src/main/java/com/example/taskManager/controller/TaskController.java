package com.example.taskManager.controller;

import com.example.taskManager.common.exception.Response;
import com.example.taskManager.model.DTO.request.TaskRequest;
import com.example.taskManager.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping("/create")
    public ResponseEntity<Response<?>> createTask(@RequestBody TaskRequest taskRequest) {
       return ResponseEntity.ok(Response.success(taskService.createTask(taskRequest)));
    }
}
