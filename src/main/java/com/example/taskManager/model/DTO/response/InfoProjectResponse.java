package com.example.taskManager.model.DTO.response;

import lombok.Data;
import java.time.LocalDate;


@Data
public class InfoProjectResponse {

    private Long id;

    private String name;

    private String description;

    private String ownerName;

    private Integer numberOfMembers;

    private Integer numberOfTasks;

    private String status;

    private LocalDate startDate;

    private LocalDate endDate;

}
