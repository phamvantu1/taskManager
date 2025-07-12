package com.example.taskManager.model.DTO.request;

import lombok.Data;

@Data
public class AuthRequest {
    private String email;
    private String password;
}
