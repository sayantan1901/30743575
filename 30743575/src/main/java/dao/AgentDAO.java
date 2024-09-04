package main.java.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import main.java.Exceptions.DatabaseOperationException;
import main.java.models.Agent;

public class AgentDAO {
    private final Connection connection;

    public AgentDAO(Connection connection) {
        this.connection = connection;
    }

    

    // Method to update agent details, such as availability and skillset
    public void updateAgent(Agent agent) throws SQLException, DatabaseOperationException {
        String sql = "UPDATE agents SET availability = ?, skillset = ? WHERE agent_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBoolean(1, agent.isAvailability());
            stmt.setString(2, agent.getSkillset());
            stmt.setInt(3, agent.getAgentId());
            stmt.executeUpdate();
        }  catch (SQLException e) {
            throw new DatabaseOperationException("Failed to update agent details.", e);
        }
    }

    // Method to retrieve an agent by their ID
    public Agent getAgentById(int agentId) throws SQLException, DatabaseOperationException {
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
        }  catch (SQLException e) {
            throw new DatabaseOperationException("Failed to retrieve agent by ID.", e);
        }
        return null; // Agent not found
    }
}

}
