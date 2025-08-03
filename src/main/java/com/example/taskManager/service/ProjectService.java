package com.example.taskManager.service;

import com.example.taskManager.common.constant.ProjectStatusEnum;
import com.example.taskManager.common.exception.CustomException;
import com.example.taskManager.common.exception.ResponseCode;
import com.example.taskManager.model.DTO.request.ProjectRequest;
import com.example.taskManager.model.DTO.response.InfoProjectResponse;
import com.example.taskManager.model.DTO.response.ProjectMemberView;
import com.example.taskManager.model.entity.Department;
import com.example.taskManager.model.entity.Project;
import com.example.taskManager.model.entity.Task;
import com.example.taskManager.model.entity.User;
import com.example.taskManager.repository.DepartmentRepository;
import com.example.taskManager.repository.ProjectRepository;
import com.example.taskManager.repository.TaskRepository;
import com.example.taskManager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final TaskRepository taskRepository;

    @Transactional
    public Map<String, String> createProject(ProjectRequest projectRequest) {
        try{

            Project project = new Project();

            if(projectRequest.getDepartmentId() != null){
                Department department = departmentRepository.findById(projectRequest.getDepartmentId())
                        .orElseThrow(() -> new CustomException(ResponseCode.DEPARTMENT_NOT_FOUND));
                project.setDepartment(department);
            }

            project.setName(projectRequest.getName());
            project.setDescription(projectRequest.getDescription());
            project.setEndTime(projectRequest.getEndTime());
            project.setStartTime(projectRequest.getStartTime());
            project.setStatus(ProjectStatusEnum.PENDING.name());
            project.setType(projectRequest.getType());
            project.setIsDeleted(false);

            User user = userRepository.findById(projectRequest.getOwnerId())
                    .orElseThrow(() -> new CustomException(ResponseCode.USER_NOT_FOUND));

            project.setOwner(user);

            projectRepository.save(project);

            return Map.of("message", "Project created successfully", "projectId", project.getId().toString());
        }catch(CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Project creation failed: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public Page<Project> getAllProjects(int page, int size, Long departmentId,String textSearch, String status, String startTime ,String endTime ) {
        try {

            if (!StringUtils.hasText(startTime)) {
                startTime = null;
            }
            if (!StringUtils.hasText(endTime)) {
                endTime = null;
            }

            textSearch = (textSearch != null && textSearch.trim().isEmpty()) ? null : textSearch;
            status = (status != null && status.trim().isEmpty()) ? null : status;

            if(status != null){
                status = ProjectStatusEnum.fromLevel(Integer.parseInt(status)).name();
            }

            Pageable pageable =  PageRequest.of(page, size);
            return  projectRepository.findAllProject(pageable, departmentId, textSearch, status,startTime , endTime);

        } catch(CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Project creation failed: " + e.getMessage());
        }
    }

    @Transactional
    public InfoProjectResponse getInfoProject(Long projectId) {
        try {

            InfoProjectResponse response = new InfoProjectResponse();

            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new CustomException(ResponseCode.PROJECT_NOT_FOUND));

            List<Task> taskList = taskRepository.findByProjectId(projectId);

            int numberOfTaskFinish = taskList.stream()
                    .filter(task -> task.getStatus().equals("COMPLETED"))
                    .toList().size();

            if(project.getDepartment()!= null){
                Department department = departmentRepository.findById(project.getDepartment().getId())
                        .orElseThrow(() -> new CustomException(ResponseCode.DEPARTMENT_NOT_FOUND));
                response.setDepartmentName(department.getName());
            }

            List<User> listMember = userRepository.listUserInProject(projectId);

            String ownerName = "Unknown";
            if (project.getOwner() != null) {
                String firstName = project.getOwner().getFirstName() != null ? project.getOwner().getFirstName() : "";
                String lastName = project.getOwner().getLastName() != null ? project.getOwner().getLastName() : "";

                if (!firstName.isEmpty() || !lastName.isEmpty()) {
                    ownerName = (firstName + " " + lastName).trim();
                }
            }

            response.setId(project.getId());
            response.setName(project.getName());
            response.setDescription(project.getDescription());
            response.setOwnerName(ownerName);
            response.setNumberOfMembers(listMember.size() + 1);
            response.setNumberOfTasks(project.getTasks().size());
            response.setStatus(project.getStatus());
            response.setStartDate(project.getStartTime());
            response.setEndDate(project.getEndTime());
            response.setProgress(!taskList.isEmpty() ? (numberOfTaskFinish * 100 / taskList.size()) : 0);
            response.setDepartmentId(project.getDepartment().getId());
            response.setOwnerId(project.getOwner().getId());
            response.setType_project(project.getType());

            return response;

        } catch(CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve project information: " + e.getMessage());
        }
    }

    public Page<ProjectMemberView> getUserByProject(Long projectId, String textSearch, int page, int size) {
        try {


            Pageable pageable = PageRequest.of(page, size);
            return projectRepository.findProjectMembersByProjectId(projectId, textSearch, pageable);

        } catch(CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve project members: " + e.getMessage());
        }
    }

    public Map<String, String> updateProject(Long projectId ,ProjectRequest projectRequest){
        try {

            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new CustomException(ResponseCode.PROJECT_NOT_FOUND));

            User owner = userRepository.findById(projectRequest.getOwnerId())
                    .orElseThrow(() -> new CustomException(ResponseCode.USER_NOT_FOUND));

            Department department = departmentRepository.findById(projectRequest.getDepartmentId())
                    .orElseThrow(() -> new CustomException(ResponseCode.DEPARTMENT_NOT_FOUND));

            project.setName(projectRequest.getName());
            project.setDescription(projectRequest.getDescription());
            project.setStartTime(projectRequest.getStartTime());
            project.setEndTime(projectRequest.getEndTime());
            project.setUpdateTime(LocalDateTime.now());
            project.setOwner(owner);
            project.setType(projectRequest.getType());
            project.setDepartment(department);

            projectRepository.save(project);

            return Map.of("message", "Cập nhập dự án thất bại");
        } catch(CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to update project : " + e.getMessage());
        }
    }

    public Map<String, String> deleteProject(Long projectId){
        try {

            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new CustomException(ResponseCode.PROJECT_NOT_FOUND));

            project.setIsDeleted(true);

            projectRepository.save(project);

            return Map.of("message", "Xóa dự án thành công");
        } catch(CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to update project members: " + e.getMessage());
        }
    }

}
