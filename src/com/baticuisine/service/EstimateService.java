package com.baticuisine.service;

import com.baticuisine.model.Estimate;
import com.baticuisine.repository.EstimateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class EstimateService {
    private static final Logger logger = LoggerFactory.getLogger(EstimateService.class);
    private final EstimateRepository estimateRepository;

    public EstimateService(EstimateRepository estimateRepository) {
        this.estimateRepository = estimateRepository;
    }

    public void addEstimate(Estimate estimate) {
        logger.info("Adding new estimate for project ID: {}", estimate.getProjectId());
        estimateRepository.add(estimate);
    }

    public Optional<Estimate> getEstimateById(Long id) {
        logger.info("Fetching estimate with ID: {}", id);
        return estimateRepository.getById(id);
    }

    public List<Estimate> getAllEstimates() {
        logger.info("Fetching all estimates");
        return estimateRepository.getAll();
    }

    public Optional<Estimate> getEstimateByProject(Long projectId) {
        logger.info("Fetching estimate for project ID: {}", projectId);
        return estimateRepository.getByProject(projectId);
    }

    public void updateEstimate(Estimate estimate) {
        logger.info("Updating estimate ID: {}", estimate.getId());
        estimateRepository.update(estimate);
    }

    public void deleteEstimate(Long id) {
        logger.info("Deleting estimate with ID: {}", id);
        estimateRepository.delete(id);
    }
}
