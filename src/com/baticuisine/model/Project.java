package com.baticuisine.model;

import com.baticuisine.enums.ProjectStatus;

import java.time.LocalDate;

public class Project {
    private Long id;
    private String projectName;
    private Client client;
    private ProjectStatus projectStatus;
    private double profitMargin;
    private double totalCost;
    private LocalDate creationDate;

    public Project(String projectName, Client client) {
        this.projectName = projectName;
        this.client = client;
        this.projectStatus = ProjectStatus.IN_PROGRESS;
        this.profitMargin = 0.0;
        this.totalCost = 0.0;
        this.creationDate = LocalDate.now();
    }

    public Project(Long id, String projectName, Client client, ProjectStatus projectStatus,
                   double profitMargin, double totalCost, LocalDate creationDate) {
        this.id = id;
        this.projectName = projectName;
        this.client = client;
        this.projectStatus = projectStatus;
        this.profitMargin = profitMargin;
        this.totalCost = totalCost;
        this.creationDate = creationDate;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }

    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }

    public ProjectStatus getProjectStatus() { return projectStatus; }
    public void setProjectStatus(ProjectStatus projectStatus) { this.projectStatus = projectStatus; }

    public double getProfitMargin() { return profitMargin; }
    public void setProfitMargin(double profitMargin) { this.profitMargin = profitMargin; }

    public double getTotalCost() { return totalCost; }
    public void setTotalCost(double totalCost) { this.totalCost = totalCost; }

    public LocalDate getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDate creationDate) { this.creationDate = creationDate; }


    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", projectName='" + projectName + '\'' +
                ", client=" + client +
                ", projectStatus=" + projectStatus +
                ", profitMargin=" + profitMargin +
                ", totalCost=" + totalCost +
                ", creationDate=" + creationDate +
                '}';
    }
}
