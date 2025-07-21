package com.example.taskManager.service;

import com.example.taskManager.common.constant.ProjectStatusEnum;
import com.example.taskManager.common.exception.CustomException;
import com.example.taskManager.common.exception.ResponseCode;
import com.example.taskManager.model.DTO.request.ProjectRequest;
import com.example.taskManager.model.DTO.response.InfoProjectResponse;
import com.example.taskManager.model.entity.Project;
import com.example.taskManager.model.entity.User;
import com.example.taskManager.repository.ProjectRepository;
import com.example.taskManager.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Transactional
    public Map<String, String> createProject(ProjectRequest projectRequest) {
        try{

            Project project = new Project();
            project.setName(projectRequest.getName());
            project.setDescription(projectRequest.getDescription());
            project.setEndTime(projectRequest.getEndTime());
            project.setStartTime(projectRequest.getStartTime());
            project.setStatus(ProjectStatusEnum.PENDING.name());
            project.setType(projectRequest.getType());

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

    public Page<Project> getAllProjects(int page, int size) {
        try {

            Pageable pageable = (Pageable) PageRequest.of(page, size);
            return  projectRepository.findAll(pageable);

        } catch(CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Project creation failed: " + e.getMessage());
        }
    }

    @Transactional
    public InfoProjectResponse getInfoProject(Long projectId) {
        try {
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new CustomException(ResponseCode.PROJECT_NOT_FOUND));

            List<User> listMember = userRepository.listUserInProject(projectId);

            String ownerName = "Unknown";
            if (project.getOwner() != null) {
                String firstName = project.getOwner().getFirstName() != null ? project.getOwner().getFirstName() : "";
                String lastName = project.getOwner().getLastName() != null ? project.getOwner().getLastName() : "";

                if (!firstName.isEmpty() || !lastName.isEmpty()) {
                    ownerName = (firstName + " " + lastName).trim();
                }
            }

            InfoProjectResponse response = new InfoProjectResponse();
            response.setId(project.getId());
            response.setName(project.getName());
            response.setDescription(project.getDescription());
            response.setOwnerName(ownerName);
            response.setNumberOfMembers(listMember.size() + 1);
            response.setNumberOfTasks(project.getTasks().size());
            response.setStatus(project.getStatus());
            response.setStartDate(project.getStartTime());
            response.setEndDate(project.getEndTime());

            return response;

        } catch(CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve project information: " + e.getMessage());
        }
    }
}
