package main.java.models;

import java.sql.Timestamp;

public class TicketHistory {
    private int historyId;
    private int ticket_id;
    private Timestamp updateDate;
    private String updateDescription;

    public TicketHistory() {
        
    }

    public TicketHistory(int historyId, int ticket_id, Timestamp updateDate, String updateDescription) {
        this.historyId = historyId;
        this.ticket_id = ticket_id;
        this.updateDate = updateDate;
        this.updateDescription = updateDescription;
    }

    
    public int getHistoryId() {
        return historyId;
    }

    public void setHistoryId(int historyId) {
        this.historyId = historyId;
    }

    public int getTicketId() {
        return ticket_id;
    }

    public void setTicketId(int ticket_id) {
        this.ticket_id = ticket_id;
    }

    public Timestamp getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Timestamp updateDate) {
        this.updateDate = updateDate;
    }

    public String getUpdateDescription() {
        return updateDescription;
    }

    public void setUpdateDescription(String updateDescription) {
        this.updateDescription = updateDescription;
    }

    @Override
    public String toString() {
        return "TicketHistory{" +
                "historyId=" + historyId +
                ", ticketId=" + ticket_id +
                ", updateDate=" + updateDate +
                ", updateDescription='" + updateDescription + '\'' +
                '}';
    }

}
