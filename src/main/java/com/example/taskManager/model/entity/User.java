package com.example.taskManager.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String phone;

    private String password;

    private String firstName;

    private String lastName;

    private LocalDate DateOfBirth;

    private String  gender;

    @Column(name = "is_active")
    private boolean isActive;

    @JsonIgnore
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Project> ownedProjects;

    @JsonIgnore
    @OneToMany(mappedBy = "assignedTo", cascade = CascadeType.ALL)
    private List<Task> assignedTasks;
}
