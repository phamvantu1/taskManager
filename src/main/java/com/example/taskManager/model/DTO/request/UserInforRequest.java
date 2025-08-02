package com.example.taskManager.model.DTO.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserInforRequest {

    private Long id;

    private String email;

    private String phone;

    private String firstName;

    private String lastName;

    private LocalDate DateOfBirth;

    private String  gender;

    private Long departmentId;

    private String role;

}
