package com.example.taskManager.service;

import com.example.taskManager.common.constant.ProjectStatusEnum;
import com.example.taskManager.common.exception.CustomException;
import com.example.taskManager.common.exception.ResponseCode;
import com.example.taskManager.model.DTO.request.ProjectRequest;
import com.example.taskManager.model.entity.Project;
import com.example.taskManager.model.entity.User;
import com.example.taskManager.repository.ProjectRepository;
import com.example.taskManager.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;
import java.util.Map;

@Service
@AllArgsConstructor
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
}
