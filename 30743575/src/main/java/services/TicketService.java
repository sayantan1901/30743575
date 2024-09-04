package main.java.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import main.java.Exceptions.DatabaseOperationException;
import main.java.dao.AgentDAO;
import main.java.dao.TicketDAO;
import main.java.dao.TicketHistoryDAO;
import main.java.models.Agent;
import main.java.models.Ticket;
import main.java.models.TicketHistory;

public class TicketService {
    private final TicketDAO ticketDAO;
    private final AgentDAO agentDAO;
    private final TicketHistoryDAO ticketHistoryDAO;

    public TicketService(TicketDAO ticketDAO, AgentDAO agentDAO, TicketHistoryDAO ticketHistoryDAO) {
        this.ticketDAO = ticketDAO;
        this.agentDAO = agentDAO;
        this.ticketHistoryDAO = ticketHistoryDAO;
    }
    // View agent details by agent ID
    public Agent viewAgentDetails(int agentId) throws SQLException, DatabaseOperationException {
        return agentDAO.getAgentById(agentId);
    }
    
    // Update agent information, such as availability and skillset
    public void updateAgentInformation(int agentId, String availability, String skillset) throws SQLException, DatabaseOperationException {
        Agent agent = agentDAO.getAgentById(agentId);
        if (agent != null) {
            agent.setAvailability(availability.equalsIgnoreCase("available"));
            agent.setSkillset(skillset);
            agentDAO.updateAgent(agent);
        }
    }

    
   /*create ticket history */
    public int createTicketHistory(TicketHistory ticketHistory) throws SQLException {
        String query = "INSERT INTO tickethistory (ticket_id, update_date, update_description) VALUES (?, ?, ?)";
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/supportticketingsystem", "root", "Sayantan@1901");
             PreparedStatement stmt = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, ticketHistory.getTicketId());
            stmt.setTimestamp(2, ticketHistory.getUpdateDate());
            stmt.setString(3, ticketHistory.getUpdateDescription());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1); // Return the generated history ID
                    } else {
                        throw new SQLException("Creating ticket history failed, no ID obtained.");
                    }
                }
            } else {
                throw new SQLException("Creating ticket history failed, no rows affected.");
            }
        }
    }

    // Create a new ticket and return the generated ticket ID
    public int createNewTicket(Ticket ticket) throws SQLException {
        String query = "INSERT INTO tickets (customer_name, issue_description, status, priority, assigned_agent_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/supportticketingsystem", "root", "Sayantan@1901");
             PreparedStatement stmt = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
    
            stmt.setString(1, ticket.getCustomerName());
            stmt.setString(2, ticket.getIssueDescription());
            stmt.setString(3, ticket.getStatus());
            stmt.setString(4, ticket.getPriority());
    
            // Check if the assigned agent ID is 0 (indicating no assignment), and set to NULL if so
            if (ticket.getAssignedAgentId() == 0) {
                stmt.setNull(5, java.sql.Types.INTEGER); // Set assigned_agent_id to NULL
            } else {
                stmt.setInt(5, ticket.getAssignedAgentId());
            }
    
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1); // Return the generated ticket ID
                    } else {
                        throw new SQLException("Creating ticket failed, no ID obtained.");
                    }
                }
            } else {
                throw new SQLException("Creating ticket failed, no rows affected.");
            }
        }
    }
    

    // View ticket details by ticket ID
    public Ticket viewTicketDetails(int ticketId) throws SQLException, DatabaseOperationException {
        Ticket ticket = ticketDAO.getTicketById(ticketId);
        if (ticket == null) {
            throw new DatabaseOperationException("Ticket with ID " + ticketId + " not found.", null);
        }
        return ticket;
    }
    

    // Update ticket status
    public void updateTicketStatus(int ticket_id, String status) throws SQLException, DatabaseOperationException {
        Ticket ticket = ticketDAO.getTicketById(ticket_id);
        if (ticket == null) {
            throw new DatabaseOperationException("Ticket with ID " + ticket_id + " not found.", null);
        }
        
        ticket.setStatus(status);
        ticketDAO.updateTicket(ticket);
        
        TicketHistory history = new TicketHistory(0, ticket_id, new java.sql.Timestamp(System.currentTimeMillis()), "Status updated to " + status);
        ticketHistoryDAO.createTicketHistory(history);
    }
    

    // Delete a ticket by ticket ID
    public void deleteTicket(int ticket_id) throws SQLException, DatabaseOperationException {
        ticketDAO.deleteTicket(ticket_id);
    }

    // Assign a ticket to an agent
    public void assignTicketToAgent(int ticket_id, int agentId) throws SQLException, DatabaseOperationException {
        Ticket ticket = ticketDAO.getTicketById(ticket_id);
        Agent agent = agentDAO.getAgentById(agentId);
        if (ticket != null && agent != null && agent.isAvailability()) {
            ticket.setAssignedAgentId(agentId);
            ticketDAO.updateTicket(ticket);
            TicketHistory history = new TicketHistory(0, ticket_id, new java.sql.Timestamp(System.currentTimeMillis()), "Ticket assigned to Agent ID " + agentId);
            ticketHistoryDAO.createTicketHistory(history);
            agent.setAvailability(false);
            agentDAO.updateAgent(agent);
        }
    }

    // View ticket history by ticket ID
    public List<TicketHistory> viewTicketHistory(int ticket_id) throws SQLException, DatabaseOperationException {
        return ticketHistoryDAO.getTicketHistoryByTicketId(ticket_id); 
    }
    
}
