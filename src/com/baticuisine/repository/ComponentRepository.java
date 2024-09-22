package com.baticuisine.repository;

import com.baticuisine.dao.ComponentDAO;
import com.baticuisine.model.Component;
import com.baticuisine.model.Material;
import com.baticuisine.model.Labor;
import com.baticuisine.enums.ComponentType;
import com.baticuisine.util.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ComponentRepository implements ComponentDAO {

    private static final Logger logger = LoggerFactory.getLogger(ComponentRepository.class);

    private static final String INSERT_COMPONENT = "INSERT INTO components (project_id, name, component_type, tax_rate) VALUES (?, ?, ?, ?)";
    private static final String INSERT_MATERIAL = "INSERT INTO materials (component_id, unit_cost, quantity, transport_cost, quality_coefficient) VALUES (?, ?, ?, ?, ?)";
    private static final String INSERT_LABOR = "INSERT INTO labor (component_id, hourly_rate, hours_worked, worker_productivity) VALUES (?, ?, ?, ?)";
    private static final String SELECT_COMPONENT_BY_ID = "SELECT c.*, m.unit_cost, m.quantity, m.transport_cost, m.quality_coefficient, l.hourly_rate, l.hours_worked, l.worker_productivity FROM components c LEFT JOIN materials m ON c.id = m.component_id LEFT JOIN labor l ON c.id = l.component_id WHERE c.id = ?";
    private static final String SELECT_ALL_COMPONENTS = "SELECT c.*, m.unit_cost, m.quantity, m.transport_cost, m.quality_coefficient, l.hourly_rate, l.hours_worked, l.worker_productivity FROM components c LEFT JOIN materials m ON c.id = m.component_id LEFT JOIN labor l ON c.id = l.component_id";
    private static final String SELECT_COMPONENTS_BY_PROJECT = "SELECT c.*, m.unit_cost, m.quantity, m.transport_cost, m.quality_coefficient, l.hourly_rate, l.hours_worked, l.worker_productivity FROM components c LEFT JOIN materials m ON c.id = m.component_id LEFT JOIN labor l ON c.id = l.component_id WHERE c.project_id = ?";
    private static final String UPDATE_COMPONENT = "UPDATE components SET name = ?, component_type = ?, tax_rate = ? WHERE id = ?";
    private static final String UPDATE_MATERIAL = "UPDATE materials SET unit_cost = ?, quantity = ?, transport_cost = ?, quality_coefficient = ? WHERE component_id = ?";
    private static final String UPDATE_LABOR = "UPDATE labor SET hourly_rate = ?, hours_worked = ?, worker_productivity = ? WHERE component_id = ?";
    private static final String DELETE_COMPONENT = "DELETE FROM components WHERE id = ?";

    @Override
    public void add(Component component) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement pstmt = conn.prepareStatement(INSERT_COMPONENT, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setLong(1, component.getProjectId());
                pstmt.setString(2, component.getName());
                pstmt.setString(3, component.getComponentType().name());
                pstmt.setDouble(4, component.getTaxRate());

                int affectedRows = pstmt.executeUpdate();

                if (affectedRows == 0) {
                    throw new SQLException("Creating component failed, no rows affected.");
                }

                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        component.setId(generatedKeys.getLong(1));
                    } else {
                        throw new SQLException("Creating component failed, no ID obtained.");
                    }
                }
            }

            if (component instanceof Material) {
                addMaterial(conn, (Material) component);
            } else if (component instanceof Labor) {
                addLabor(conn, (Labor) component);
            }

            conn.commit();
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                logger.error("Error rolling back transaction: ", ex);
            }
            logger.error("Error adding component: ", e);
            throw new RuntimeException("Error adding component", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    logger.error("Error closing connection: ", e);
                }
            }
        }
    }

    // CHANGED: Updated addMaterial method to include unit_cost and quantity
    private void addMaterial(Connection conn, Material material) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(INSERT_MATERIAL)) {
            pstmt.setLong(1, material.getId());
            pstmt.setDouble(2, material.getUnitCost());
            pstmt.setDouble(3, material.getQuantity());
            pstmt.setDouble(4, material.getTransportCost());
            pstmt.setDouble(5, material.getQualityCoefficient());
            pstmt.executeUpdate();
        }
    }

    private void addLabor(Connection conn, Labor labor) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(INSERT_LABOR)) {
            pstmt.setLong(1, labor.getId());
            pstmt.setDouble(2, labor.getHourlyRate());
            pstmt.setDouble(3, labor.getHoursWorked());
            pstmt.setDouble(4, labor.getWorkerProductivity());
            pstmt.executeUpdate();
        }
    }

    @Override
    public Optional<Component> getById(Long id) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_COMPONENT_BY_ID)) {

            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return Optional.of(createComponentFromResultSet(rs));
            }
        } catch (SQLException e) {
            logger.error("Error getting component by ID: ", e);
            throw new RuntimeException("Error getting component by ID", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Component> getAll() {
        List<Component> components = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_COMPONENTS)) {

            while (rs.next()) {
                components.add(createComponentFromResultSet(rs));
            }
        } catch (SQLException e) {
            logger.error("Error getting all components: ", e);
            throw new RuntimeException("Error getting all components", e);
        }
        return components;
    }

    @Override
    public List<Component> getByProject(Long projectId) {
        List<Component> components = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_COMPONENTS_BY_PROJECT)) {

            pstmt.setLong(1, projectId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                components.add(createComponentFromResultSet(rs));
            }
        } catch (SQLException e) {
            logger.error("Error getting components by project: ", e);
            throw new RuntimeException("Error getting components by project", e);
        }
        return components;
    }

    @Override
    public void update(Component component) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement pstmt = conn.prepareStatement(UPDATE_COMPONENT)) {
                pstmt.setString(1, component.getName());
                pstmt.setString(2, component.getComponentType().name());
                pstmt.setDouble(3, component.getTaxRate());
                pstmt.setLong(4, component.getId());

                int affectedRows = pstmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Updating component failed, no rows affected.");
                }
            }

            if (component instanceof Material) {
                updateMaterial(conn, (Material) component);
            } else if (component instanceof Labor) {
                updateLabor(conn, (Labor) component);
            }

            conn.commit();
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                logger.error("Error rolling back transaction: ", ex);
            }
            logger.error("Error updating component: ", e);
            throw new RuntimeException("Error updating component", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    logger.error("Error closing connection: ", e);
                }
            }
        }
    }

    private void updateMaterial(Connection conn, Material material) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(UPDATE_MATERIAL)) {
            pstmt.setDouble(1, material.getUnitCost());
            pstmt.setDouble(2, material.getQuantity());
            pstmt.setDouble(3, material.getTransportCost());
            pstmt.setDouble(4, material.getQualityCoefficient());
            pstmt.setLong(5, material.getId());
            pstmt.executeUpdate();
        }
    }

    private void updateLabor(Connection conn, Labor labor) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(UPDATE_LABOR)) {
            pstmt.setDouble(1, labor.getHourlyRate());
            pstmt.setDouble(2, labor.getHoursWorked());
            pstmt.setDouble(3, labor.getWorkerProductivity());
            pstmt.setLong(4, labor.getId());
            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(Long id) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(DELETE_COMPONENT)) {

            pstmt.setLong(1, id);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Deleting component failed, no rows affected.");
            }
        } catch (SQLException e) {
            logger.error("Error deleting component: ", e);
            throw new RuntimeException("Error deleting component", e);
        }
    }

    private Component createComponentFromResultSet(ResultSet rs) throws SQLException {
        ComponentType type = ComponentType.valueOf(rs.getString("component_type"));
        Component component;

        if (type == ComponentType.MATERIAL) {
            component = new Material(
                    rs.getString("name"),
                    rs.getDouble("unit_cost"),
                    rs.getDouble("quantity"),
                    rs.getDouble("tax_rate"),
                    rs.getLong("project_id"),
                    rs.getDouble("transport_cost"),
                    rs.getDouble("quality_coefficient")
            );
        } else {
            component = new Labor(
                    rs.getString("name"),
                    rs.getDouble("tax_rate"),
                    rs.getLong("project_id"),
                    rs.getDouble("hourly_rate"),
                    rs.getDouble("hours_worked"),
                    rs.getDouble("worker_productivity")
            );
        }

        component.setId(rs.getLong("id"));
        component.setProjectId(rs.getLong("project_id"));
        return component;
    }
}
