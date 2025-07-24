package com.example.taskManager.model.DTO.request;

import lombok.Data;
import java.util.Set;

@Data
public class DepartmentRequest {

    private Long id;

    private String name;

    private String description;

    private Long leader_id;

    private Set<Long> users;

}
