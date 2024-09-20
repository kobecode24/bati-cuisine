package com.baticuisine.dao;

import com.baticuisine.model.Project;
import java.util.List;
import java.util.Optional;

public interface ProjectDAO {
    void add(Project project);
    Optional<Project> getById(Long id);
    List<Project> getAll();
    void update(Project project);
    void delete(Long id);
}
