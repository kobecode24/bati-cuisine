package com.baticuisine.model;

import com.baticuisine.enums.ComponentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Labor extends Component {
    private double hourlyRate;
    private double hoursWorked;
    private double workerProductivity;
    private static final Logger logger = LoggerFactory.getLogger(Labor.class);


    public Labor(String name, double taxRate, Long projectId,
                 double hourlyRate, double hoursWorked, double workerProductivity) {
        super(name , ComponentType.LABOR, taxRate, projectId);
        this.hourlyRate = hourlyRate;
        this.hoursWorked = hoursWorked;
        this.workerProductivity = workerProductivity;
    }

    @Override
    public double calculateCost() {
        double cost = getHourlyRate() * getHoursWorked() * getWorkerProductivity() * (1 + getTaxRate());
        logger.info("Calculating labor cost with taxes for {}: {}", getName(), cost);
        return cost;
    }

    @Override
    public double calculateCostWithoutTax() {
        double cost = getHourlyRate() * getHoursWorked() * getWorkerProductivity();
        logger.info("Calculating labor cost without taxes for {}: {}", getName(), cost);
        return cost;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public double getHoursWorked() {
        return hoursWorked;
    }

    public void setHoursWorked(double hoursWorked) {
        this.hoursWorked = hoursWorked;
    }

    public double getWorkerProductivity() {
        return workerProductivity;
    }

    public void setWorkerProductivity(double workerProductivity) {
        this.workerProductivity = workerProductivity;
    }

    @Override
    public String toString() {
        return "Labor{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", hourlyRate=" + hourlyRate +
                ", hoursWorked=" + hoursWorked +
                ", taxRate=" + taxRate +
                ", workerProductivity=" + workerProductivity +
                ", projectId=" + projectId +
                '}';
    }
}
