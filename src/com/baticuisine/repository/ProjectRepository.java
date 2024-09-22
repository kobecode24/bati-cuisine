package com.baticuisine.repository;

import com.baticuisine.dao.ProjectDAO;
import com.baticuisine.model.Project;
import com.baticuisine.model.Client;
import com.baticuisine.enums.ProjectStatus;
import com.baticuisine.util.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProjectRepository implements ProjectDAO {

    private static final Logger logger = LoggerFactory.getLogger(ProjectRepository.class);

    private static final String INSERT_PROJECT = "INSERT INTO projects (project_name, client_id, project_status, profit_margin, total_cost, creation_date) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SELECT_PROJECT_WITH_CLIENT =
            "SELECT p.*, c.name as client_name, c.address as client_address, " +
                    "c.phone as client_phone, c.is_professional as client_is_professional " +
                    "FROM projects p JOIN clients c ON p.client_id = c.id WHERE p.id = ?";
    private static final String SELECT_ALL_PROJECTS_WITH_CLIENTS =
            "SELECT p.*, c.name as client_name, c.address as client_address, " +
                    "c.phone as client_phone, c.is_professional as client_is_professional " +
                    "FROM projects p JOIN clients c ON p.client_id = c.id";
    private static final String UPDATE_PROJECT = "UPDATE projects SET project_name = ?, client_id = ?, project_status = ?, profit_margin = ?, total_cost = ?, creation_date = ? WHERE id = ?";
    private static final String DELETE_PROJECT = "DELETE FROM projects WHERE id = ?";

    @Override
    public void add(Project project) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_PROJECT, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, project.getProjectName());
            pstmt.setLong(2, project.getClient().getId());
            pstmt.setString(3, project.getProjectStatus().name());
            pstmt.setDouble(4, project.getProfitMargin());
            pstmt.setDouble(5, project.getTotalCost());
            pstmt.setDate(6, Date.valueOf(project.getCreationDate()));

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating project failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    project.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating project failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            logger.error("Error adding project: ", e);
            throw new RuntimeException("Error adding project", e);
        }
    }

    @Override
    public Optional<Project> getById(Long id) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_PROJECT_WITH_CLIENT)) {
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToProject(rs));
            }
        } catch (SQLException e) {
            logger.error("Error getting project by ID: ", e);
            throw new RuntimeException("Error getting project by ID", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Project> getAll() {
        List<Project> projects = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_PROJECTS_WITH_CLIENTS)) {
            while (rs.next()) {
                projects.add(mapResultSetToProject(rs));
            }
        } catch (SQLException e) {
            logger.error("Error getting all projects: ", e);
            throw new RuntimeException("Error getting all projects", e);
        }
        return projects;
    }

    @Override
    public void update(Project project) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(UPDATE_PROJECT)) {
            pstmt.setString(1, project.getProjectName());
            pstmt.setLong(2, project.getClient().getId());
            pstmt.setString(3, project.getProjectStatus().name());
            pstmt.setDouble(4, project.getProfitMargin());
            pstmt.setDouble(5, project.getTotalCost());
            pstmt.setDate(6, Date.valueOf(project.getCreationDate()));
            pstmt.setLong(7, project.getId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating project failed, no rows affected.");
            }
        } catch (SQLException e) {
            logger.error("Error updating project: ", e);
            throw new RuntimeException("Error updating project", e);
        }
    }

    @Override
    public void delete(Long id) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(DELETE_PROJECT)) {
            pstmt.setLong(1, id);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Deleting project failed, no rows affected.");
            }
        } catch (SQLException e) {
            logger.error("Error deleting project: ", e);
            throw new RuntimeException("Error deleting project", e);
        }
    }

    private Project mapResultSetToProject(ResultSet rs) throws SQLException {
        return new Project(
                rs.getLong("id"),
                rs.getString("project_name"),
                new Client(
                        rs.getLong("client_id"),
                        rs.getString("client_name"),
                        rs.getString("client_address"),
                        rs.getString("client_phone"),
                        rs.getBoolean("client_is_professional")
                ),
                ProjectStatus.valueOf(rs.getString("project_status")),
                rs.getDouble("profit_margin"),
                rs.getDouble("total_cost"),
                rs.getDate("creation_date").toLocalDate()
        );
    }
}
