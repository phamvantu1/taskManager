package com.example.taskManager.model.DTO.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserInforRequest {

    private String email;

    private String phone;

    private String firstName;

    private String lastName;

    private LocalDate DateOfBirth;

    private String  gender;

}
