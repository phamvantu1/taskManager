package com.example.taskManager.mapper;

import com.example.taskManager.model.DTO.response.TaskResponse;
import com.example.taskManager.model.entity.Task;
import com.example.taskManager.model.entity.User;
import java.time.format.DateTimeFormatter;

public class TaskMapper {

    public static TaskResponse toTaskResponse(Task task, User createdByUser) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setStatus(task.getStatus());
        response.setStartTime(task.getStartTime());
        response.setEndTime(task.getEndTime());

        // Gán tên người được giao (nếu có)
        if (task.getAssignedTo() != null) {
            String firstName = task.getAssignedTo().getFirstName() != null ? task.getAssignedTo().getFirstName() : "";
            String lastName = task.getAssignedTo().getLastName() != null ? task.getAssignedTo().getLastName() : "";
            String email = task.getAssignedTo().getEmail() != null ? task.getAssignedTo().getEmail() : "";

            String fullName = (firstName + " " + lastName).trim();
            if (!email.isEmpty()) {
                fullName += " (" + email + ")";
            }

            response.setNameAssignedTo(fullName.trim());
        }


        // Gán tên người tạo (nếu truyền vào)
        if (createdByUser != null) {
            response.setNameCreatedBy(createdByUser.getFirstName() + " " + createdByUser.getLastName());
        }

        response.setLever(task.getLever());
        response.setProcess(task.getProcess());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        response.setCreatedAt(task.getCreatedAt().format(formatter));

        return response;
    }
}