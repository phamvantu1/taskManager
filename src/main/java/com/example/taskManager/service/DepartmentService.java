package com.example.taskManager.service;

import com.example.taskManager.common.exception.CustomException;
import com.example.taskManager.common.exception.ResponseCode;
import com.example.taskManager.model.DTO.request.DepartmentRequest;
import com.example.taskManager.model.entity.Department;
import com.example.taskManager.model.entity.User;
import com.example.taskManager.repository.DepartmentRepository;
import com.example.taskManager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;

    public Map<String, String> createDepartment(DepartmentRequest departmentRequest,
                                                Authentication authentication) {
        try{

            String email = authentication.getName();
            User creator = userRepository.findByEmail(email)
                    .orElseThrow(() -> new CustomException(ResponseCode.USER_NOT_FOUND));

            User leader = userRepository.findById(departmentRequest.getLeader_id())
                    .orElseThrow(()-> new CustomException(ResponseCode.USER_NOT_FOUND));

            Department department = new Department();
            department.setName(departmentRequest.getName());
            department.setDescription(departmentRequest.getDescription());
            department.setCreatedBy(creator);
            department.setLeader(leader);
            department.setCreatedAt(LocalDateTime.now());
            department.setUpdatedAt(LocalDateTime.now());

            departmentRepository.save(department);

            return Map.of("message", "Department created successfully", "departmentId", String.valueOf(department.getId()));

        }catch (CustomException e) {
            throw e;
        }catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred while creating the department: " + e.getMessage());
        }
    }
}
