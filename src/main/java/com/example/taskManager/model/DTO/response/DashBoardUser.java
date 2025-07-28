package com.example.taskManager.model.DTO.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class DashBoardUser {

    private String fullName;

    private Long progress;

}
