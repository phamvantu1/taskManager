package com.example.taskManager.model.entity;


import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "permissions")
public class Permission {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    private String name ;
}
