package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Scanner;

import main.java.Exceptions.DatabaseOperationException;
import main.java.dao.AgentDAO;
import main.java.dao.TicketDAO;
import main.java.dao.TicketHistoryDAO;
import main.java.models.Agent;
import main.java.models.Ticket;
import main.java.models.TicketHistory;
import main.java.services.TicketService;

public class Main {
    private static TicketService ticketService;

    public static void main(String[] args) throws DatabaseOperationException {
        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish the database connection
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/supportticketingsystem", "root", "Sayantan@1901")) {
                
                // Initialize the DAOs and the TicketService
                TicketHistoryDAO ticketHistoryDAO = new TicketHistoryDAO(connection);
                TicketDAO ticketDAO = new TicketDAO(connection, ticketHistoryDAO);
                ticketService = new TicketService(ticketDAO, new AgentDAO(connection), ticketHistoryDAO);

                Scanner scanner = new Scanner(System.in);
                
                // Main loop for interacting with the system
                while (true) {
                    showMenu();  // Display the menu options
                    int choice = scanner.nextInt();
                    scanner.nextLine();  // Consume newline

                    // Handle user selection
                    switch (choice) {
                        case 1:
                            createTicket(connection, scanner);  // Create a new ticket
                            break;
                        case 2:
                            viewTicket(connection, scanner);  // View details of a specific ticket
                            break;
                        case 3:
                            updateTicketStatus(connection, scanner);  // Update the status of a ticket
                            break;
                        case 4:
                            deleteTicket(connection, scanner);  // Delete a ticket
                            break;
                        case 5:
                            assignTicket(connection, scanner);  // Assign a ticket to an agent
                            break;
                        case 6:
                            viewTicketHistory(connection, scanner);  // View the history of a ticket
                            break;
                        case 7:
                            createTicketHistory(connection, scanner);  // Create a new entry in the ticket history
                            break;
                        case 8:
                            viewAgentDetails(connection, scanner);  // View details of an agent
                            break;
                        case 9:
                            updateAgentInformation(connection, scanner);  // Update information of an agent
                            break;
                        case 10:
                            System.out.println("Exiting...");  // Exit the program
                            return;
                        default:
                            System.out.println("Invalid choice. Please try again.");
                    }
                }

            } catch (SQLException e) {
                System.err.println("Database error occurred.");
                e.printStackTrace();  // Handle SQL exceptions
            }
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC driver not found.");
            e.printStackTrace();  // Handle Class not found exception for JDBC driver
        }
    }

    // Display menu options to the user
    private static void showMenu() {
        System.out.println("\nCustomer Support Ticketing System");
        System.out.println("1. Create Ticket");
        System.out.println("2. View Ticket");
        System.out.println("3. Update Ticket Status");
        System.out.println("4. Delete Ticket");
        System.out.println("5. Assign Ticket to Agent");
        System.out.println("6. View Ticket History");
        System.out.println("7. Create Ticket History");
        System.out.println("8. View Agent Details");
        System.out.println("9. Update Agent Information");
        System.out.println("10. Exit");
        System.out.print("Enter your choice: ");
    }

    // Method to handle the creation of a new ticket
    private static void createTicket(Connection connection, Scanner scanner) throws DatabaseOperationException {
        try {
            System.out.print("Enter customer name: ");
            String customerName = scanner.nextLine();
            System.out.print("Enter issue description: ");
            String issueDescription = scanner.nextLine();
            System.out.print("Enter status: ");
            String status = scanner.nextLine();
            System.out.print("Enter priority: ");
            String priority = scanner.nextLine();

            System.out.print("Enter assigned agent ID (or leave blank if unassigned): ");
            String assignedAgentIdInput = scanner.nextLine();
            int assignedAgentId = 0;  // Default to 0 (which we'll treat as "no agent assigned")

            // Check if the user provided an agent ID
            if (!assignedAgentIdInput.isEmpty()) {
                try {
                    assignedAgentId = Integer.parseInt(assignedAgentIdInput);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid agent ID entered. Agent will not be assigned.");
                }
            }

            Ticket ticket = new Ticket(0, customerName, issueDescription, status, priority, assignedAgentId);
            int ticketId = ticketService.createNewTicket(ticket);  // Create ticket in the database
            if (ticketId > 0) {
                System.out.println("Ticket created successfully.");
                System.out.println("Ticket ID: " + ticketId);
            } else {
                System.out.println("Ticket creation failed.");
            }
        } catch (SQLException e) {
            System.err.println("Error creating ticket: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to update agent information (availability, skillset)
    private static void updateAgentInformation(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter agent ID: ");
            int agentId = scanner.nextInt();
            scanner.nextLine();  // Consume newline
            System.out.print("Enter new availability (e.g., available, unavailable): ");
            String availability = scanner.nextLine();
            System.out.print("Enter new skillset: ");
            String skillset = scanner.nextLine();

            ticketService.updateAgentInformation(agentId, availability, skillset);  // Update agent details
            System.out.println("Agent information updated successfully.");
        } catch (SQLException | DatabaseOperationException e) {
            System.err.println("Error updating agent information: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to view details of an agent by ID
    private static void viewAgentDetails(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter agent ID: ");
            int agentId = scanner.nextInt();
            Agent agent = ticketService.viewAgentDetails(agentId);  // Fetch agent details
            if (agent != null) {
                System.out.println(agent);
            } else {
                System.out.println("Agent not found.");
            }
        } catch (SQLException | DatabaseOperationException e) {
            System.err.println("Error viewing agent details: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to view details of a specific ticket by ID
    private static void viewTicket(Connection connection, Scanner scanner) throws SQLException {
        try {
            System.out.print("Enter ticket ID: ");
            int ticketId = scanner.nextInt();
            Ticket ticket = ticketService.viewTicketDetails(ticketId);  // Fetch ticket details
            
            System.out.println(ticket); // This will only print if the ticket is not null
            
        } catch (DatabaseOperationException e) {
            // Handle the case where the ticket is not found
            if (e.getMessage().contains("not found")) {
                System.out.println("Ticket not found.");
            } else {
                System.err.println("Error viewing ticket details: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    

    // Method to update the status of a specific ticket
    private static void updateTicketStatus(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter ticket ID: ");
            int ticketId = scanner.nextInt();
            scanner.nextLine();  // Consume newline
            
            System.out.print("Enter new status: ");
            String status = scanner.nextLine();
            
            ticketService.updateTicketStatus(ticketId, status);  // Update ticket status in the database
            System.out.println("Ticket status updated successfully.");
        } catch (DatabaseOperationException e) {
            // Handle custom exceptions (e.g., ticket not found)
            System.err.println("Error: " + e.getMessage());
        } catch (SQLException e) {
            // Handle SQL exceptions
            System.err.println("Database error occurred while updating ticket status.");
            e.printStackTrace();
        }
    }
    
    // Method to delete a specific ticket by ID
    private static void deleteTicket(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter ticket ID: ");
            int ticketId = scanner.nextInt();
            ticketService.deleteTicket(ticketId);  // Delete ticket from the database
            System.out.println("Ticket deleted successfully.");
        } catch (SQLException | DatabaseOperationException e) {
            System.err.println("Error deleting ticket: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to assign a specific ticket to an agent
    private static void assignTicket(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter ticket ID: ");
            int ticketId = scanner.nextInt();
            System.out.print("Enter agent ID: ");
            int agentId = scanner.nextInt();

            ticketService.assignTicketToAgent(ticketId, agentId);  // Assign ticket to the agent
            System.out.println("Ticket assigned to agent successfully.");
        } catch (SQLException | DatabaseOperationException e) {
            System.err.println("Error assigning ticket to agent: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to view the history of a specific ticket
    private static void viewTicketHistory(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter ticket ID: ");
            int ticketId = scanner.nextInt();
            ticketService.viewTicketHistory(ticketId).forEach(System.out::println);  // Fetch and display ticket history
        } catch (SQLException | DatabaseOperationException e) {
            System.err.println("Error viewing ticket history: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to create a new entry in the ticket history
    private static void createTicketHistory(Connection connection, Scanner scanner) throws DatabaseOperationException {
        try {
            System.out.print("Enter ticket ID: ");
            int ticketId = scanner.nextInt();
            scanner.nextLine();  // Consume newline
            System.out.print("Enter update description: ");
            String updateDescription = scanner.nextLine();
            Timestamp updateDate = new Timestamp(System.currentTimeMillis());  // Get the current timestamp

            TicketHistory ticketHistory = new TicketHistory(0, ticketId, updateDate, updateDescription);

            ticketService.createTicketHistory(ticketHistory);  // Insert new ticket history record into the database
            System.out.println("Ticket history created successfully.");
        } catch (SQLException e) {
            System.err.println("Error creating ticket history: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

