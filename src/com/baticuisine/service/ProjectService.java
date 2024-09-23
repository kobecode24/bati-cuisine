package com.baticuisine.service;

import com.baticuisine.model.Project;
import com.baticuisine.model.Component;
import com.baticuisine.enums.ProjectStatus;
import com.baticuisine.repository.ProjectRepository;
import com.baticuisine.repository.ComponentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProjectService {
    private static final Logger logger = LoggerFactory.getLogger(ProjectService.class);
    private final ProjectRepository projectRepository;
    private final ComponentRepository componentRepository;

    public ProjectService(ProjectRepository projectRepository, ComponentRepository componentRepository) {
        this.projectRepository = projectRepository;
        this.componentRepository = componentRepository;
    }

    public void addProject(Project project) {
        logger.info("Adding new project: {}", project.getProjectName());
        projectRepository.add(project);
    }

    public Optional<Project> getProjectById(Long id) {
        logger.info("Fetching project with ID: {}", id);
        return projectRepository.getById(id);
    }

    public List<Project> getAllProjects() {
        logger.info("Fetching all projects");
        return projectRepository.getAll();
    }

    public List<Project> searchProjects(String searchTerm) {
        logger.info("Searching for projects with term: {}", searchTerm);
        return projectRepository.getAll().stream()
                .filter(project -> project.getProjectName().toLowerCase().contains(searchTerm.toLowerCase())
                        || (project.getId() != null && project.getId().toString().equals(searchTerm)))
                .collect(Collectors.toList());
    }

    public void updateProject(Project project) {
        logger.info("Updating project: {}", project.getProjectName());
        projectRepository.update(project);
    }

    public void deleteProject(Long id) {
        logger.info("Deleting project with ID: {}", id);
        projectRepository.delete(id);
    }

    public void updateProjectStatus(Long projectId, ProjectStatus newStatus) {
        logger.info("Updating status of project ID {} to {}", projectId, newStatus);
        Optional<Project> projectOpt = projectRepository.getById(projectId);
        if (projectOpt.isPresent()) {
            Project project = projectOpt.get();
            project.setProjectStatus(newStatus);
            projectRepository.update(project);
        } else {
            logger.warn("Project with ID {} not found", projectId);
        }
    }

    public double calculateProjectTotalCost(Long projectId) {
        logger.info("Calculating total cost for project ID: {}", projectId);
        Optional<Project> projectOpt = projectRepository.getById(projectId);
        if (projectOpt.isPresent()) {
            Project project = projectOpt.get();
            List<Component> components = componentRepository.getByProject(projectId);
            double totalCost = components.stream()
                    .mapToDouble(Component::calculateCost)
                    .sum();
            project.setTotalCost(totalCost);
            projectRepository.update(project);
            logger.info("Total cost calculated: {}", totalCost);
            return totalCost;
        } else {
            logger.warn("Project with ID {} not found", projectId);
            return 0;
        }
    }
}
