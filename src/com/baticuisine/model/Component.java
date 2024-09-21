package com.baticuisine.model;

import com.baticuisine.enums.ComponentType;

public abstract class Component {
    protected Long id;
    protected String name;
    protected ComponentType componentType;
    protected double taxRate;
    protected Long projectId;

    public Component(String name, ComponentType componentType, double taxRate, Long projectId) {
        this.name = name;
        this.componentType = componentType;
        this.taxRate = taxRate;
        this.projectId = projectId;
    }

    public abstract double calculateCost();
    public abstract double calculateCostWithoutTax();


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public ComponentType getComponentType() { return componentType; }
    public void setComponentType(ComponentType componentType) { this.componentType = componentType; }

    public double getTaxRate() { return taxRate; }
    public void setTaxRate(double taxRate) { this.taxRate = taxRate; }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    @Override
    public String toString() {
        return "Component{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", componentType=" + componentType +
                ", taxRate=" + taxRate +
                ", projectId=" + projectId +
                '}';
    }
}