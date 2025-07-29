package com.example.taskManager.service;

import com.example.taskManager.common.constant.DepartmentStatus;
import com.example.taskManager.common.exception.CustomException;
import com.example.taskManager.common.exception.ResponseCode;
import com.example.taskManager.common.response.ResponseWrapperAdvice;
import com.example.taskManager.mapper.DepartmentMapper;
import com.example.taskManager.model.DTO.request.DepartmentRequest;
import com.example.taskManager.model.DTO.response.*;
import com.example.taskManager.model.entity.Department;
import com.example.taskManager.model.entity.DepartmentUser;
import com.example.taskManager.model.entity.Project;
import com.example.taskManager.model.entity.User;
import com.example.taskManager.repository.DepartmentRepository;
import com.example.taskManager.repository.DepartmentUserRepository;
import com.example.taskManager.repository.ProjectRepository;
import com.example.taskManager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final DepartmentMapper departmentMapper;
    private final DepartmentUserRepository departmentUserRepository;
    private final ProjectRepository projectRepository;

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

            return Map.of("message", "Thêm mới phòng ban thành công", "departmentId", String.valueOf(department.getId()));

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

            List<DepartmentUser> departmentUser = departmentUserRepository.findByDepartmentId(departmentId);

            DepartmentCommonResponse response = new DepartmentCommonResponse();
            response.setId(department.getId());
            response.setName(department.getName());
            response.setDescription(department.getDescription());
            response.setLeaderName(departmentMapper.mapFullName(department.getLeader()));
            response.setCreatedByName(departmentMapper.mapFullName(department.getCreatedBy()));
            response.setCreatedAt(department.getCreatedAt());
            response.setUpdatedAt(department.getUpdatedAt());
            response.setNumberOfProjects((long) department.getProject().size());
            response.setNumberOfUsers((long) departmentUser.size());

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

            List<DepartmentUser> departmentUsers = departmentUserRepository.findByDepartmentId(departmentId);

            List<NewParticipient> newUsers = departmentUsers.stream()
                    .filter(newUser -> newUser.getJoinedAt() != null
                            && newUser.getJoinedAt().isAfter(LocalDateTime.now().minusDays(30)))
                    .sorted(Comparator.comparing(DepartmentUser::getJoinedAt).reversed())
                    .map(newUser -> {
                        NewParticipient np = new NewParticipient();
                        np.setId(newUser.getUser().getId());
                        np.setName(departmentMapper.mapFullName(newUser.getUser()));
                        np.setEmail(newUser.getUser().getEmail());
                        return np;
                    })
                    .toList();

            dashboard.setListNewUsers(new HashSet<>(newUsers));

            List<Project> listProject = projectRepository.findByDepartmentId(departmentId);

            List<ProjectInProgress> projectInProgresses = listProject.stream()
                    .filter(project -> project.getStatus().equals("PROCESSING"))
                    .map(project -> {
                        ProjectInProgress pi = new ProjectInProgress();
                        pi.setId(project.getId());
                        pi.setName(project.getName());
                        return pi;
                    })
                    .toList();

            dashboard.setListProjectsInProgress(new HashSet<>(projectInProgresses));

            List<ProjectCompleted> projectCompleted = listProject.stream()
                    .filter(project -> project.getStatus().equals("COMPLETED"))
                    .map(project -> {
                        ProjectCompleted pc = new ProjectCompleted();
                        pc.setId(project.getId());
                        pc.setName(project.getName());
                        pc.setCompletedDate(project.getFinishTime());
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

    @Transactional
    public Map<String, String> deleteDepartment(Long departmentId) {
        try {

            Department department = departmentRepository.findById(departmentId)
                    .orElseThrow(() -> new CustomException(ResponseCode.DEPARTMENT_NOT_FOUND));

            if(department.getStatus().equals(DepartmentStatus.INACTIVE.name())) {
                throw new CustomException(ResponseCode.DEPARTMENT_ALREADY_DELETED);
            }

            department.setStatus(DepartmentStatus.INACTIVE.name());

            departmentRepository.save(department);

            return Map.of("message", "Xoá phòng ban thành công");

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred while deleting the department: " + e.getMessage());
        }
    }


    @Transactional
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

            return Map.of("message", "Cập nhập phòng ban thành công");

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred while updating the department: " + e.getMessage());
        }
    }

    @Transactional
    public Map<String, String> addUserToDepartment(Long departmentId, Long userId, Authentication authentication) {
        try {

            String email = authentication.getName();
            User updater = userRepository.findByEmail(email)
                    .orElseThrow(() -> new CustomException(ResponseCode.USER_NOT_FOUND));

            Department department = departmentRepository.findById(departmentId)
                    .orElseThrow(() -> new CustomException(ResponseCode.DEPARTMENT_NOT_FOUND));

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new CustomException(ResponseCode.USER_NOT_FOUND));


            boolean checkUserExistInDepartment = departmentUserRepository.existsByDepartmentIdAndUserId(departmentId, userId);
            if (checkUserExistInDepartment) {
                throw new CustomException(ResponseCode.USER_ALREADY_IN_DEPARTMENT);
            }

            DepartmentUser departmentUser = new DepartmentUser();
            departmentUser.setDepartment(department);
            departmentUser.setUser(user);
            departmentUser.setRole("MEMBER"); // Mặc định là MEMBER, có thể thay đổi sau
            departmentUser.setJoinedAt(LocalDateTime.now());

            // Lưu thông tin vào bảng trung gian
            departmentUserRepository.save(departmentUser);

            // Cập nhật thời gian update của department
            department.setUpdatedAt(LocalDateTime.now());
            departmentRepository.save(department);

            return Map.of("message", "Thêm mới thành viên thành công");

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred while adding the user to the department: " + e.getMessage());
        }
    }
}
