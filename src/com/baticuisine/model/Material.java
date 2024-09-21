package com.baticuisine.model;

import com.baticuisine.enums.ComponentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Material extends Component {
    private double transportCost;
    private double qualityCoefficient;
    private double unitCost;
    private double quantity;
    private static final Logger logger = LoggerFactory.getLogger(Material.class);


    public Material(String name, double unitCost, double quantity, double taxRate , long projectId , double transportCost, double qualityCoefficient) {
        super(name , ComponentType.MATERIAL, taxRate , projectId);
        this.transportCost = transportCost;
        this.qualityCoefficient = qualityCoefficient;
        this.unitCost = unitCost;
        this.quantity = quantity;

    }

    @Override
    public double calculateCost() {
        double cost = (unitCost * quantity * getQualityCoefficient() + getTransportCost()) * (1 + getTaxRate());
        logger.info("Calculating material cost with taxes for {}: {}", getName(), cost);
        return cost;
    }

    @Override
    public double calculateCostWithoutTax() {
        double cost = getUnitCost() * getQuantity() * getQualityCoefficient() + getTransportCost();
        logger.info("Calculating material cost without taxes for {}: {}", getName(), cost);
        return cost;
    }

    public double getTransportCost() {
        return transportCost;
    }

    public void setTransportCost(double transportCost) {
        this.transportCost = transportCost;
    }

    public double getQualityCoefficient() {
        return qualityCoefficient;
    }

    public void setQualityCoefficient(double qualityCoefficient) {
        this.qualityCoefficient = qualityCoefficient;
    }

    public double getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(double unitCost) {
        this.unitCost = unitCost;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Material{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", unitCost=" + unitCost +
                ", quantity=" + quantity +
                ", taxRate=" + taxRate +
                ", transportCost=" + transportCost +
                ", qualityCoefficient=" + qualityCoefficient +
                ", projectId=" + projectId +
                '}';
    }
}
