package com.example.taskManager.model.DTO.response;


import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DashBoardOverView {

    private String title;

    private Long value;

    private String subtitle;

}
