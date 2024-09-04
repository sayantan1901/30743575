package main.java.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import main.java.Exceptions.DatabaseOperationException;
import main.java.models.TicketHistory;

public class TicketHistoryDAO {
    private final Connection connection;

    public TicketHistoryDAO(Connection connection) {
        this.connection = connection;
    }

    // Method to create a new ticket history entry
    public void createTicketHistory(TicketHistory ticketHistory) throws SQLException, DatabaseOperationException {
        String query = "INSERT INTO tickethistory (ticket_id, update_date, update_description) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, ticketHistory.getTicketId());
            stmt.setTimestamp(2, ticketHistory.getUpdateDate());
            stmt.setString(3, ticketHistory.getUpdateDescription());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseOperationException("Failed to create ticket history entry.", e);
        }
    }

    // Method to retrieve all ticket history entries by ticket ID
    public List<TicketHistory> getTicketHistoryByTicketId(int ticketId) throws SQLException, DatabaseOperationException {
        String query = "SELECT * FROM tickethistory WHERE ticket_id = ?";
        List<TicketHistory> ticketHistories = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, ticketId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    TicketHistory ticketHistory = new TicketHistory(
                        rs.getInt("history_id"),
                        rs.getInt("ticket_id"),
                        rs.getTimestamp("update_date"),
                        rs.getString("update_description")
                    );
                    ticketHistories.add(ticketHistory);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException("Failed to retrieve ticket history entries.", e);
        }
        return ticketHistories; // Return list of ticket histories
    }
}
