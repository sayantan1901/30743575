package main.java.models;

public class Ticket {
    private int ticketId;
    private String customerName;
    private String issueDescription;
    private String status;
    private String priority;
    private int assignedAgentId;

    public Ticket(int ticketId, String customerName, String issueDescription, String status, String priority, int assignedAgentId) {
        this.ticketId = ticketId;
        this.customerName = customerName;
        this.issueDescription = issueDescription;
        this.status = status;
        this.priority = priority;
        this.assignedAgentId = assignedAgentId;
    }

    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getIssueDescription() {
        return issueDescription;
    }

    public void setIssueDescription(String issueDescription) {
        this.issueDescription = issueDescription;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public int getAssignedAgentId() {
        return assignedAgentId;
    }

    public void setAssignedAgentId(int assignedAgentId) {
        this.assignedAgentId = assignedAgentId;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "ticketId=" + ticketId +
                ", customerName='" + customerName + '\'' +
                ", issueDescription='" + issueDescription + '\'' +
                ", status='" + status + '\'' +
                ", priority='" + priority + '\'' +
                ", assignedAgentId=" + assignedAgentId +
                '}';
    }
}