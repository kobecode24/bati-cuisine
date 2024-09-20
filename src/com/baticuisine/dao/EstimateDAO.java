package com.baticuisine.dao;

import com.baticuisine.model.Estimate;
import java.util.List;
import java.util.Optional;

public interface EstimateDAO {
    void add(Estimate estimate);
    Optional<Estimate> getById(Long id);
    List<Estimate> getAll();
    Optional<Estimate> getByProject(Long projectId);
    void update(Estimate estimate);
    void delete(Long id);
}
