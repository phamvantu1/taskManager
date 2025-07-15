package com.example.taskManager.model.DTO.request;

import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@Data
public class RegisterRequest {

    private String email;

    private String password;

    private String confirmPassword;


}