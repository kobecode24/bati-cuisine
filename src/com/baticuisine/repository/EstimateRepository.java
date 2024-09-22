package com.baticuisine.repository;

import com.baticuisine.dao.EstimateDAO;
import com.baticuisine.model.Estimate;
import com.baticuisine.util.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EstimateRepository implements EstimateDAO {

    private static final Logger logger = LoggerFactory.getLogger(EstimateRepository.class);

    private static final String INSERT_ESTIMATE = "INSERT INTO estimates (estimated_amount, issue_date, validity_date, accepted, project_id) VALUES (?, ?, ?, ?, ?)";
    private static final String SELECT_ESTIMATE_BY_ID = "SELECT * FROM estimates WHERE id = ?";
    private static final String SELECT_ALL_ESTIMATES = "SELECT * FROM estimates";
    private static final String SELECT_ESTIMATE_BY_PROJECT = "SELECT * FROM estimates WHERE project_id = ?";
    private static final String UPDATE_ESTIMATE = "UPDATE estimates SET estimated_amount = ?, issue_date = ?, validity_date = ?, accepted = ?, project_id = ? WHERE id = ?";
    private static final String DELETE_ESTIMATE = "DELETE FROM estimates WHERE id = ?";

    @Override
    public void add(Estimate estimate) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_ESTIMATE, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setDouble(1, estimate.getEstimatedAmount());
            pstmt.setDate(2, Date.valueOf(estimate.getIssueDate()));
            pstmt.setDate(3, Date.valueOf(estimate.getValidityDate()));
            pstmt.setBoolean(4, estimate.isAccepted());
            pstmt.setLong(5, estimate.getProjectId());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating estimate failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    estimate.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating estimate failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            logger.error("Error adding estimate: ", e);
            throw new RuntimeException("Error adding estimate", e);
        }
    }

    @Override
    public Optional<Estimate> getById(Long id) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_ESTIMATE_BY_ID)) {

            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return Optional.of(createEstimateFromResultSet(rs));
            }
        } catch (SQLException e) {
            logger.error("Error getting estimate by ID: ", e);
            throw new RuntimeException("Error getting estimate by ID", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Estimate> getAll() {
        List<Estimate> estimates = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_ESTIMATES)) {

            while (rs.next()) {
                estimates.add(createEstimateFromResultSet(rs));
            }
        } catch (SQLException e) {
            logger.error("Error getting all estimates: ", e);
            throw new RuntimeException("Error getting all estimates", e);
        }
        return estimates;
    }

    @Override
    public Optional<Estimate> getByProject(Long projectId) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_ESTIMATE_BY_PROJECT)) {

            pstmt.setLong(1, projectId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return Optional.of(createEstimateFromResultSet(rs));
            }
        } catch (SQLException e) {
            logger.error("Error getting estimate by project: ", e);
            throw new RuntimeException("Error getting estimate by project", e);
        }
        return Optional.empty();
    }

    @Override
    public void update(Estimate estimate) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(UPDATE_ESTIMATE)) {

            pstmt.setDouble(1, estimate.getEstimatedAmount());
            pstmt.setDate(2, Date.valueOf(estimate.getIssueDate()));
            pstmt.setDate(3, Date.valueOf(estimate.getValidityDate()));
            pstmt.setBoolean(4, estimate.isAccepted());
            pstmt.setLong(5, estimate.getProjectId());
            pstmt.setLong(6, estimate.getId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating estimate failed, no rows affected.");
            }
        } catch (SQLException e) {
            logger.error("Error updating estimate: ", e);
            throw new RuntimeException("Error updating estimate", e);
        }
    }

    @Override
    public void delete(Long id) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(DELETE_ESTIMATE)) {

            pstmt.setLong(1, id);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Deleting estimate failed, no rows affected.");
            }
        } catch (SQLException e) {
            logger.error("Error deleting estimate: ", e);
            throw new RuntimeException("Error deleting estimate", e);
        }
    }

    private Estimate createEstimateFromResultSet(ResultSet rs) throws SQLException {
        Estimate estimate = new Estimate(
                rs.getDouble("estimated_amount"),
                rs.getDate("issue_date").toLocalDate(),
                rs.getDate("validity_date").toLocalDate()
        );
        estimate.setId(rs.getLong("id"));
        estimate.setAccepted(rs.getBoolean("accepted"));
        estimate.setProjectId(rs.getLong("project_id"));
        return estimate;
    }
}
