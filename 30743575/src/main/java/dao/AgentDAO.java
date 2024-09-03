package main.java.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import main.java.models.Agent;

public class AgentDAO {
    private final Connection connection;

    public AgentDAO(Connection connection) {
        this.connection = connection;
    }

    // Method to create a new agent
    public void createAgent(Agent agent) throws SQLException {
        String query = "INSERT INTO agents (agent_name, skillset, availability) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, agent.getAgentName());
            stmt.setString(2, agent.getSkillset());
            stmt.setBoolean(3, agent.isAvailability());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error while creating agent: " + e.getMessage());
            throw e;  // Re-throw exception after logging
        }
    }

    // Method to update agent details, such as availability and skillset
    public void updateAgent(Agent agent) throws SQLException {
        String sql = "UPDATE agents SET availability = ?, skillset = ? WHERE agent_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBoolean(1, agent.isAvailability());
            stmt.setString(2, agent.getSkillset());
            stmt.setInt(3, agent.getAgentId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error while updating agent: " + e.getMessage());
            throw e;  // Re-throw exception after logging
        }
    }

    // Method to retrieve an agent by their ID
    public Agent getAgentById(int agentId) throws SQLException {
        String query = "SELECT * FROM agents WHERE agent_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, agentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Agent(
                            rs.getInt("agent_id"),
                            rs.getString("agent_name"),
                            rs.getString("skillset"),
                            rs.getBoolean("availability")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error while retrieving agent: " + e.getMessage());
            throw e;  // Re-throw exception after logging
        }
        return null; // Agent not found
    }
}