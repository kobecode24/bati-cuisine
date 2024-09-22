package com.baticuisine.repository;

import com.baticuisine.dao.ClientDAO;
import com.baticuisine.model.Client;
import com.baticuisine.util.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClientRepository implements ClientDAO {

    private static final Logger logger = LoggerFactory.getLogger(ClientRepository.class);

    private static final String INSERT_CLIENT = "INSERT INTO clients (name, address, phone, is_professional) VALUES (?, ?, ?, ?)";
    private static final String SELECT_CLIENT_BY_ID = "SELECT * FROM clients WHERE id = ?";
    private static final String SELECT_ALL_CLIENTS = "SELECT * FROM clients";
    private static final String UPDATE_CLIENT = "UPDATE clients SET name = ?, address = ?, phone = ?, is_professional = ? WHERE id = ?";
    private static final String DELETE_CLIENT = "DELETE FROM clients WHERE id = ?";

    @Override
    public void add(Client client) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_CLIENT, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, client.getName());
            pstmt.setString(2, client.getAddress());
            pstmt.setString(3, client.getPhone());
            pstmt.setBoolean(4, client.isProfessional());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating client failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    client.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating client failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            logger.error("Error adding client: ", e);
            throw new RuntimeException("Error adding client", e);
        }
    }

    @Override
    public Optional<Client> getById(Long id) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_CLIENT_BY_ID)) {

            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return Optional.of(createClientFromResultSet(rs));
            }
        } catch (SQLException e) {
            logger.error("Error getting client by ID: ", e);
            throw new RuntimeException("Error getting client by ID", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Client> getAll() {
        List<Client> clients = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_CLIENTS)) {

            while (rs.next()) {
                clients.add(createClientFromResultSet(rs));
            }
        } catch (SQLException e) {
            logger.error("Error getting all clients: ", e);
            throw new RuntimeException("Error getting all clients", e);
        }
        return clients;
    }

    @Override
    public void update(Client client) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(UPDATE_CLIENT)) {

            pstmt.setString(1, client.getName());
            pstmt.setString(2, client.getAddress());
            pstmt.setString(3, client.getPhone());
            pstmt.setBoolean(4, client.isProfessional());
            pstmt.setLong(5, client.getId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating client failed, no rows affected.");
            }
        } catch (SQLException e) {
            logger.error("Error updating client: ", e);
            throw new RuntimeException("Error updating client", e);
        }
    }

    @Override
    public void delete(Long id) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(DELETE_CLIENT)) {

            pstmt.setLong(1, id);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Deleting client failed, no rows affected.");
            }
        } catch (SQLException e) {
            logger.error("Error deleting client: ", e);
            throw new RuntimeException("Error deleting client", e);
        }
    }

    private Client createClientFromResultSet(ResultSet rs) throws SQLException {
        return new Client(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("address"),
                rs.getString("phone"),
                rs.getBoolean("is_professional")
        );
    }
}
