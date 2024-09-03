package main.java.dao;

import main.java.models.Ticket;
import main.java.models.TicketHistory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TicketDAO {
    private final Connection connection;  // Database connection
    private final TicketHistoryDAO ticketHistoryDAO;  // DAO for ticket history

    // Constructor to initialize TicketDAO with a database connection and TicketHistoryDAO
    public TicketDAO(Connection connection, TicketHistoryDAO ticketHistoryDAO) {
        this.connection = connection;
        this.ticketHistoryDAO = ticketHistoryDAO;
    }

    // Checks if an agent exists in the database
    private boolean agentExists(int agentId) throws SQLException {
        String query = "SELECT COUNT(*) FROM agents WHERE agent_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, agentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    // Creates a new ticket and logs the creation in the ticket history
    public void createTicket(Ticket ticket) throws SQLException {
        // Check if the assigned agent ID is provided (not 0)
        Integer assignedAgentId = ticket.getAssignedAgentId() != 0 ? ticket.getAssignedAgentId() : null;
    
        // Validate assigned agent ID if it is set
        if (assignedAgentId != null && !agentExists(assignedAgentId)) {
            throw new SQLException("Assigned agent ID does not exist.");
        }
    
        String query = "INSERT INTO tickets (customer_name, issue_description, status, priority, assigned_agent_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            // Set parameters for the SQL statement
            stmt.setString(1, ticket.getCustomerName());
            stmt.setString(2, ticket.getIssueDescription());
            stmt.setString(3, ticket.getStatus());
            stmt.setString(4, ticket.getPriority());
            
            // Automatically set assignedAgentId to NULL if it is not provided
            if (assignedAgentId == null) {
                stmt.setNull(5, java.sql.Types.INTEGER);
            } else {
                stmt.setInt(5, assignedAgentId);
            }
    
            stmt.executeUpdate();
    
            // Retrieve the generated ticket ID and set it in the ticket object
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    ticket.setTicketId(generatedKeys.getInt(1));
                }
            }
    
            // Create and log a ticket history entry
            TicketHistory history = new TicketHistory();
            history.setTicketId(ticket.getTicketId());
            history.setUpdateDescription("Ticket created.");
            ticketHistoryDAO.createTicketHistory(history);
        } catch (SQLException e) {
            System.err.println("Error while creating ticket: " + e.getMessage());
            throw e;
        }
    }
    
    // Retrieves a ticket by its ID
    public Ticket getTicketById(int ticketId) throws SQLException {
        String query = "SELECT * FROM tickets WHERE ticket_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, ticketId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Ticket(
                            rs.getInt("ticket_id"),
                            rs.getString("customer_name"),
                            rs.getString("issue_description"),
                            rs.getString("status"),
                            rs.getString("priority"),
                            rs.getInt("assigned_agent_id")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error while retrieving ticket: " + e.getMessage());
            throw e;
        }
        return null;  // Return null if the ticket is not found
    }

    // Updates an existing ticket and logs the update in the ticket history
    public void updateTicket(Ticket ticket) throws SQLException {
        // Validate assigned agent ID if it is set
        if (ticket.getAssignedAgentId() != 0 && !agentExists(ticket.getAssignedAgentId())) {
            throw new SQLException("Assigned agent ID does not exist.");
        }

        String query = "UPDATE tickets SET customer_name = ?, issue_description = ?, status = ?, priority = ?, assigned_agent_id = ? WHERE ticket_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            // Set parameters for the SQL statement
            stmt.setString(1, ticket.getCustomerName());
            stmt.setString(2, ticket.getIssueDescription());
            stmt.setString(3, ticket.getStatus());
            stmt.setString(4, ticket.getPriority());
            stmt.setInt(5, ticket.getAssignedAgentId());
            stmt.setInt(6, ticket.getTicketId());
            stmt.executeUpdate();

            // Create and log a ticket history entry
            TicketHistory history = new TicketHistory();
            history.setTicketId(ticket.getTicketId());
            history.setUpdateDescription("Ticket updated.");
            ticketHistoryDAO.createTicketHistory(history);
        } catch (SQLException e) {
            System.err.println("Error while updating ticket: " + e.getMessage());
            throw e;
        }
    }

    // Deletes a ticket by its ID
    public void deleteTicket(int ticketId) throws SQLException {
        String query = "DELETE FROM tickets WHERE ticket_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, ticketId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error while deleting ticket: " + e.getMessage());
            throw e;
        }
    }
}
