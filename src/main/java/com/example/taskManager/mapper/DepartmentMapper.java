package com.example.taskManager.mapper;

import com.example.taskManager.model.DTO.response.DepartmentResponse;
import com.example.taskManager.model.entity.Department;
import com.example.taskManager.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {

    @Mapping(source = "createdBy", target = "createdByName", qualifiedByName = "mapFullName")
    DepartmentResponse toDepartmentResponse(Department department);

    @Named("mapFullName")
    default String mapFullName(User user) {
        if (user == null) return null;

        String firstName = user.getFirstName() != null ? user.getFirstName() : "";
        String lastName = user.getLastName() != null ? user.getLastName() : "";
        String email = user.getEmail() != null ? user.getEmail() : "";

        String fullName = (firstName + " " + lastName).trim();
        if (!email.isEmpty()) {
            fullName += " (" + email + ")";
        }

        return fullName;
    }
}

