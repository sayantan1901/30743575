package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Scanner;

import main.java.dao.AgentDAO;
import main.java.dao.TicketDAO;
import main.java.dao.TicketHistoryDAO;
import main.java.models.Agent;
import main.java.models.Ticket;
import main.java.models.TicketHistory;
import main.java.services.TicketService;

public class Main {
    private static TicketService ticketService;

    public static void main(String[] args) {
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
                            createTicket(scanner);  // Create a new ticket
                            break;
                        case 2:
                            viewTicket(scanner);  // View details of a specific ticket
                            break;
                        case 3:
                            updateTicketStatus(scanner);  // Update the status of a ticket
                            break;
                        case 4:
                            deleteTicket(scanner);  // Delete a ticket
                            break;
                        case 5:
                            assignTicket(scanner);  // Assign a ticket to an agent
                            break;
                        case 6:
                            viewTicketHistory(scanner);  // View the history of a ticket
                            break;
                        case 7:
                            createTicketHistory(scanner);  // Create a new entry in the ticket history
                            break;
                        case 8:
                            viewAgentDetails(scanner);  // View details of an agent
                            break;
                        case 9:
                            updateAgentInformation(scanner);  // Update information of an agent
                            break;
                        case 10:
                            System.out.println("Exiting...");  // Exit the program
                            return;
                        default:
                            System.out.println("Invalid choice. Please try again.");
                    }
                }

            } catch (SQLException e) {
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
        System.out.println("7. update Ticket History description");
        System.out.println("8. View Agent Details");
        System.out.println("9. Update Agent Information");
        System.out.println("10. Exit");
        System.out.print("Enter your choice: ");
    }

    // Method to handle the creation of a new ticket
    private static void createTicket(Scanner scanner) {
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
    try {
        int ticketId = ticketService.createNewTicket(ticket);  // Create ticket in the database
        if (ticketId > 0) {
            System.out.println("Ticket created successfully.");
            System.out.println("Ticket ID: " + ticketId);
        } else {
            System.out.println("Ticket creation failed.");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}


    // Method to update agent information (availability, skillset)
    private static void updateAgentInformation(Scanner scanner) {
        System.out.print("Enter agent ID: ");
        int agentId = scanner.nextInt();
        scanner.nextLine();  // Consume newline
        System.out.print("Enter new availability (e.g., available, unavailable): ");
        String availability = scanner.nextLine();
        System.out.print("Enter new skillset: ");
        String skillset = scanner.nextLine();

        try {
            ticketService.updateAgentInformation(agentId, availability, skillset);  // Update agent details
            System.out.println("Agent information updated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to view details of an agent by ID
    private static void viewAgentDetails(Scanner scanner) {
        System.out.print("Enter agent ID: ");
        int agentId = scanner.nextInt();
        try {
            Agent agent = ticketService.viewAgentDetails(agentId);  // Fetch agent details
            if (agent != null) {
                System.out.println(agent);
            } else {
                System.out.println("Agent not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to view details of a specific ticket by ID
    private static void viewTicket(Scanner scanner) {
        System.out.print("Enter ticket ID: ");
        int ticketId = scanner.nextInt();
        try {
            Ticket ticket = ticketService.viewTicketDetails(ticketId);  // Fetch ticket details
            if (ticket != null) {
                System.out.println(ticket);
            } else {
                System.out.println("Ticket not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to update the status of a specific ticket
    private static void updateTicketStatus(Scanner scanner) {
        System.out.print("Enter ticket ID: ");
        int ticketId = scanner.nextInt();
        scanner.nextLine();  // Consume newline
        System.out.print("Enter new status: ");
        String status = scanner.nextLine();

        try {
            ticketService.updateTicketStatus(ticketId, status);  // Update ticket status in the database
            System.out.println("Ticket status updated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to delete a specific ticket by ID
    private static void deleteTicket(Scanner scanner) {
        System.out.print("Enter ticket ID: ");
        int ticketId = scanner.nextInt();
        try {
            ticketService.deleteTicket(ticketId);  // Delete ticket from the database
            System.out.println("Ticket deleted successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to assign a specific ticket to an agent
    private static void assignTicket(Scanner scanner) {
        System.out.print("Enter ticket ID: ");
        int ticketId = scanner.nextInt();
        System.out.print("Enter agent ID: ");
        int agentId = scanner.nextInt();

        try {
            ticketService.assignTicketToAgent(ticketId, agentId);  // Assign ticket to the agent
            System.out.println("Ticket assigned to agent successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to view the history of a specific ticket
    private static void viewTicketHistory(Scanner scanner) {
        System.out.print("Enter ticket ID: ");
        int ticketId = scanner.nextInt();
        try {
            ticketService.viewTicketHistory(ticketId).forEach(System.out::println);  // Fetch and display ticket history
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to create a new entry in the ticket history
    private static void createTicketHistory(Scanner scanner) {
        System.out.print("Enter ticket ID: ");
        int ticketId = scanner.nextInt();
        scanner.nextLine();  // Consume newline
        System.out.print("Enter update description: ");
        String updateDescription = scanner.nextLine();
        Timestamp updateDate = new Timestamp(System.currentTimeMillis());  // Get the current timestamp

        TicketHistory ticketHistory = new TicketHistory(0, ticketId, updateDate, updateDescription);

        try {
            ticketService.createTicketHistory(ticketHistory);  // Insert new ticket history record into the database
            System.out.println("Ticket history created successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
