package com.baticuisine.service;

import com.baticuisine.model.Component;
import com.baticuisine.repository.ComponentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ComponentService {
    private static final Logger logger = LoggerFactory.getLogger(ComponentService.class);
    private final ComponentRepository componentRepository;

    public ComponentService(ComponentRepository componentRepository) {
        this.componentRepository = componentRepository;
    }

    public void addComponent(Component component) {
        logger.info("Adding new component: {}", component.getName());
        componentRepository.add(component);
    }

    public Optional<Component> getComponentById(Long id) {
        logger.info("Fetching component with ID: {}", id);
        return componentRepository.getById(id);
    }

    public List<Component> getAllComponents() {
        logger.info("Fetching all components");
        return componentRepository.getAll();
    }

    public List<Component> searchComponents(String searchTerm) {
        logger.info("Searching for components with term: {}", searchTerm);
        return componentRepository.getAll().stream()
                .filter(component -> component.getName().toLowerCase().contains(searchTerm.toLowerCase())
                        || component.getId().toString().equals(searchTerm))
                .collect(Collectors.toList());
    }

    public List<Component> getComponentsByProject(Long projectId) {
        logger.info("Fetching components for project ID: {}", projectId);
        return componentRepository.getByProject(projectId);
    }

    public void updateComponent(Component component) {
        logger.info("Updating component: {}", component.getName());
        componentRepository.update(component);
    }

    public void deleteComponent(Long id) {
        logger.info("Deleting component with ID: {}", id);
        componentRepository.delete(id);
    }
}
