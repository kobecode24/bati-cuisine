package com.baticuisine.dao;

import com.baticuisine.model.Component;
import java.util.List;
import java.util.Optional;

public interface ComponentDAO {
    void add(Component component);
    Optional<Component> getById(Long id);
    List<Component> getAll();
    List<Component> getByProject(Long projectId);
    void update(Component component);
    void delete(Long id);
}
