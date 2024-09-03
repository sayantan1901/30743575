package main.java.models;

public class Agent {
    private int agentId;
    private String agentName;
    private String skillset;
    private boolean availability;

    public Agent(int agentId, String agentName, String skillset, boolean availability) {
        this.agentId = agentId;
        this.agentName = agentName;
        this.skillset = skillset;
        this.availability = availability;
    }

    public int getAgentId() {
        return agentId;
    }

    public void setAgentId(int agentId) {
        this.agentId = agentId;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getSkillset() {
        return skillset;
    }

    public void setSkillset(String skillset) {
        this.skillset = skillset;
    }

    public boolean isAvailability() {
        return availability;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }

    @Override
    public String toString() {
        return "Agent{" +
                "agentId=" + agentId +
                ", agentName='" + agentName + '\'' +
                ", skillset='" + skillset + '\'' +
                ", availability=" + availability +
                '}';
    }
}