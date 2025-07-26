package com.example.taskManager.service;

import com.example.taskManager.common.constant.DepartmentStatus;
import com.example.taskManager.common.exception.CustomException;
import com.example.taskManager.common.exception.ResponseCode;
import com.example.taskManager.common.response.ResponseWrapperAdvice;
import com.example.taskManager.mapper.DepartmentMapper;
import com.example.taskManager.model.DTO.request.DepartmentRequest;
import com.example.taskManager.model.DTO.response.*;
import com.example.taskManager.model.entity.Department;
import com.example.taskManager.model.entity.User;
import com.example.taskManager.repository.DepartmentRepository;
import com.example.taskManager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final DepartmentMapper departmentMapper;
    private final ResponseWrapperAdvice responseWrapperAdvice;

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
            department.setStatus(DepartmentStatus.ACTIVE.name());

            departmentRepository.save(department);

            return Map.of("message", "Department created successfully", "departmentId", String.valueOf(department.getId()));

        }catch (CustomException e) {
            throw e;
        }catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred while creating the department: " + e.getMessage());
        }
    }

    public Page<DepartmentResponse> getAllDepartments(int page, int size, String textSearch) {
        try {

            textSearch = (textSearch != null && !textSearch.isBlank()) ? textSearch.trim() : null;

            Page<Department> departments = departmentRepository.findAllDepartment(textSearch, PageRequest.of(page, size));

            return departments.map(departmentMapper::toDepartmentResponse);

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred while fetching departments: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public DepartmentCommonResponse getCommonDepartment(Long departmentId) {
        try {

            Department department = departmentRepository.findById(departmentId)
                    .orElseThrow(() -> new CustomException(ResponseCode.DEPARTMENT_NOT_FOUND));

            DepartmentCommonResponse response = new DepartmentCommonResponse();
            response.setId(department.getId());
            response.setName(department.getName());
            response.setDescription(department.getDescription());
            response.setLeaderName(departmentMapper.mapFullName(department.getLeader()));
            response.setCreatedByName(departmentMapper.mapFullName(department.getCreatedBy()));
            response.setCreatedAt(department.getCreatedAt());
            response.setUpdatedAt(department.getUpdatedAt());
            response.setNumberOfProjects((long) department.getProject().size());
            response.setNumberOfUsers((long) department.getUsers().size());

            return response;

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred while fetching the department: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public DashboardDepartment getDashboardDepartment(Long departmentId) {
        try {

            Department department = departmentRepository.findById(departmentId)
                    .orElseThrow(() -> new CustomException(ResponseCode.DEPARTMENT_NOT_FOUND));

            DashboardDepartment dashboard = new DashboardDepartment();

            List<NewParticipient> newUsers = department.getUsers().stream()
                    .filter(newUser -> newUser.getCreatedAt().isBefore(LocalDateTime.now().minusDays(30)))
                    .map(newUser -> {
                        NewParticipient np = new NewParticipient();
                        np.setId(newUser.getId());
                        np.setName(departmentMapper.mapFullName(newUser));
                        np.setEmail(newUser.getEmail());
                        return np;
                    })
                    .toList();

            dashboard.setListNewUsers(new HashSet<>(newUsers));

            List<ProjectInProgress> projectInProgresses = department.getProject().stream()
                    .filter(project -> project.getStatus().equals("PROCESSING"))
                    .map(project -> {
                        ProjectInProgress pi = new ProjectInProgress();
                        pi.setId(project.getId());
                        pi.setName(project.getName());
                        return pi;
                    })
                    .toList();

            dashboard.setListProjectsInProgress(new HashSet<>(projectInProgresses));

            List<ProjectCompleted> projectCompleted = department.getProject().stream()
                    .filter(project -> project.getStatus().equals("COMPLETED"))
                    .map(project -> {
                        ProjectCompleted pc = new ProjectCompleted();
                        pc.setId(project.getId());
                        pc.setName(project.getName());
                        return pc;
                    })
                    .toList();

            dashboard.setListProjectsCompleted(new HashSet<>(projectCompleted));

            return dashboard;

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred while fetching the dashboard: " + e.getMessage());
        }
    }

    public Map<String, String> deleteDepartment(Long departmentId) {
        try {

            Department department = departmentRepository.findById(departmentId)
                    .orElseThrow(() -> new CustomException(ResponseCode.DEPARTMENT_NOT_FOUND));

            department.setStatus(DepartmentStatus.INACTIVE.name());

            departmentRepository.save(department);

            return Map.of("message", "Department deleted successfully");

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred while deleting the department: " + e.getMessage());
        }
    }

    public Map<String, String> updateDepartment(Long departmentId, DepartmentRequest departmentRequest,
                                                Authentication authentication) {
        try {

            String email = authentication.getName();
            User updater = userRepository.findByEmail(email)
                    .orElseThrow(() -> new CustomException(ResponseCode.USER_NOT_FOUND));

            Department department = departmentRepository.findById(departmentId)
                    .orElseThrow(() -> new CustomException(ResponseCode.DEPARTMENT_NOT_FOUND));

            if (departmentRequest.getName() != null) {
                department.setName(departmentRequest.getName());
            }
            if (departmentRequest.getDescription() != null) {
                department.setDescription(departmentRequest.getDescription());
            }
            if (departmentRequest.getLeader_id() != null) {
                User leader = userRepository.findById(departmentRequest.getLeader_id())
                        .orElseThrow(() -> new CustomException(ResponseCode.USER_NOT_FOUND));
                department.setLeader(leader);
            }

            department.setUpdatedAt(LocalDateTime.now());

            departmentRepository.save(department);

            return Map.of("message", "Department updated successfully");

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred while updating the department: " + e.getMessage());
        }
    }

    public Map<String, String> addUserToDepartment(Long departmentId, Long userId, Authentication authentication) {
        try {

            String email = authentication.getName();
            User updater = userRepository.findByEmail(email)
                    .orElseThrow(() -> new CustomException(ResponseCode.USER_NOT_FOUND));

            Department department = departmentRepository.findById(departmentId)
                    .orElseThrow(() -> new CustomException(ResponseCode.DEPARTMENT_NOT_FOUND));

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new CustomException(ResponseCode.USER_NOT_FOUND));

            if (department.getUsers().contains(user)) {
                throw new CustomException(ResponseCode.USER_ALREADY_IN_DEPARTMENT);
            }

            department.getUsers().add(user);
            department.setUpdatedAt(LocalDateTime.now());

            departmentRepository.save(department);

            return Map.of("message", "User added to department successfully");

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred while adding the user to the department: " + e.getMessage());
        }
    }
}
