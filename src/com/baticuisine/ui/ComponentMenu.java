package com.baticuisine.ui;

import com.baticuisine.model.Component;
import com.baticuisine.model.Material;
import com.baticuisine.model.Labor;
import com.baticuisine.model.Project;
import com.baticuisine.service.ComponentService;
import com.baticuisine.service.ProjectService;
import java.util.Scanner;
import java.util.List;
import java.util.Optional;

public class ComponentMenu {
    private final Scanner scanner;
    private final ComponentService componentService;
    private final ProjectService projectService;

    public ComponentMenu(Scanner scanner, ComponentService componentService, ProjectService projectService) {
        this.scanner = scanner;
        this.componentService = componentService;
        this.projectService = projectService;
    }

    public void manageComponents() {
        while (true) {
            System.out.println("\n=== Gestion des Composants ===");
            System.out.println("1. Ajouter un composant à un projet");
            System.out.println("2. Afficher tous les composants");
            System.out.println("3. Rechercher un composant");
            System.out.println("4. Modifier un composant");
            System.out.println("5. Supprimer un composant");
            System.out.println("6. Retour au menu principal");
            System.out.print("Choisissez une option : ");

            int choice = getValidIntInput(1, 6);

            switch (choice) {
                case 1:
                    addComponentToProject();
                    break;
                case 2:
                    displayAllComponents();
                    break;
                case 3:
                    searchComponent();
                    break;
                case 4:
                    modifyComponent();
                    break;
                case 5:
                    deleteComponent();
                    break;
                case 6:
                    return;
            }
        }
    }

    public void addComponentsToProject(Project project) {
        while (true) {
            System.out.println("\n--- Ajout des composants au projet: " + project.getProjectName() + " ---");
            System.out.println("1. Ajouter un matériau");
            System.out.println("2. Ajouter de la main-d'œuvre");
            System.out.println("3. Terminer l'ajout de composants");
            System.out.print("Choisissez une option : ");

            int choice = getValidIntInput(1, 3);

            switch (choice) {
                case 1:
                    addMaterial(project);
                    break;
                case 2:
                    addLabor(project);
                    break;
                case 3:
                    return;
            }
        }
    }

    private void addComponentToProject() {
        System.out.print("Entrez l'ID du projet : ");
        Long projectId = getValidLongInput();
        Optional<Project> projectOpt = projectService.getProjectById(projectId);

        if (projectOpt.isPresent()) {
            Project project = projectOpt.get();
            System.out.println("1. Ajouter un matériau");
            System.out.println("2. Ajouter de la main-d'œuvre");
            System.out.print("Choisissez le type de composant : ");
            int choice = getValidIntInput(1, 2);

            if (choice == 1) {
                addMaterial(project);
            } else {
                addLabor(project);
            }
        } else {
            System.out.println("Projet non trouvé.");
        }
    }

    private void addMaterial(Project project) {
        System.out.println("--- Ajout d'un matériau ---");
        System.out.print("Nom du matériau : ");
        String name = scanner.nextLine();
        System.out.print("Quantité : ");
        double quantity = getValidPositiveDoubleInput();
        System.out.print("Coût unitaire (€) : ");
        double unitCost = getValidPositiveDoubleInput();
        System.out.print("Coût de transport (€) : ");
        double transportCost = getValidPositiveDoubleInput();
        System.out.print("Coefficient de qualité (1.0 = standard, > 1.0 = haute qualité) : ");
        double qualityCoefficient = getValidPositiveDoubleInput();
        System.out.print("Taux de TVA (%) : ");
        double taxRate = getValidPositiveDoubleInput() / 100.0; // Convert percentage to decimal

        Material material = new Material(name, unitCost, quantity, taxRate, project.getId(), transportCost, qualityCoefficient);
        try {
            componentService.addComponent(material);
            System.out.println("Matériau ajouté avec succès !");
        } catch (Exception e) {
            System.out.println("Erreur lors de l'ajout du matériau : " + e.getMessage());
        }
    }

    private void addLabor(Project project) {
        System.out.println("--- Ajout de main-d'œuvre ---");
        System.out.print("Type de main-d'œuvre : ");
        String name = scanner.nextLine();
        System.out.print("Taux horaire (€/h) : ");
        double hourlyRate = getValidPositiveDoubleInput();
        System.out.print("Nombre d'heures travaillées : ");
        double hoursWorked = getValidPositiveDoubleInput();
        System.out.print("Facteur de productivité (1.0 = standard, > 1.0 = haute productivité) : ");
        double workerProductivity = getValidPositiveDoubleInput();
        System.out.print("Taux de TVA (%) : ");
        double taxRate = getValidPositiveDoubleInput() / 100.0; // Convert percentage to decimal

        Labor labor = new Labor(name, taxRate, project.getId(), hourlyRate, hoursWorked, workerProductivity);
        try {
            componentService.addComponent(labor);
            System.out.println("Main-d'œuvre ajoutée avec succès !");
        } catch (Exception e) {
            System.out.println("Erreur lors de l'ajout de la main-d'œuvre : " + e.getMessage());
        }
    }

    private void displayAllComponents() {
        try {
            List<Component> components = componentService.getAllComponents();
            if (components.isEmpty()) {
                System.out.println("Aucun composant trouvé.");
                return;
            }

            System.out.println("\n--- Liste de tous les composants ---");
            for (Component component : components) {
                displayDetailedComponent(component);
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de la récupération des composants : " + e.getMessage());
        }
    }

    private void displayDetailedComponent(Component component) {
        System.out.println("ID: " + component.getId() + ", Nom: " + component.getName() + ", Type: " + component.getComponentType());
        System.out.println("Taux de TVA: " + (component.getTaxRate() * 100) + "%");
        if (component instanceof Material) {
            Material material = (Material) component;
            System.out.println("Quantité: " + material.getQuantity() + ", Coût unitaire: " + material.getUnitCost() + "€");
            System.out.println("Coût de transport: " + material.getTransportCost() + "€, Coefficient de qualité: " + material.getQualityCoefficient());
        } else if (component instanceof Labor) {
            Labor labor = (Labor) component;
            System.out.println("Taux horaire: " + labor.getHourlyRate() + "€/h, Heures travaillées: " + labor.getHoursWorked());
            System.out.println("Productivité: " + labor.getWorkerProductivity());
        }
        System.out.println("--------------------");
    }

    private void searchComponent() {
        System.out.print("Entrez l'ID ou le nom du composant : ");
        String search = scanner.nextLine();

        try {
            List<Component> components = componentService.searchComponents(search);
            if (components.isEmpty()) {
                System.out.println("Aucun composant trouvé.");
            } else {
                System.out.println("\n--- Résultats de la recherche ---");
                for (Component component : components) {
                    displayDetailedComponent(component);
                }
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de la recherche de composants : " + e.getMessage());
        }
    }

    private void modifyComponent() {
        System.out.print("Entrez l'ID du composant à modifier : ");
        Long componentId = getValidLongInput();

        try {
            Optional<Component> componentOpt = componentService.getComponentById(componentId);
            if (componentOpt.isPresent()) {
                Component component = componentOpt.get();
                System.out.println("Composant actuel : ");
                displayDetailedComponent(component);

                System.out.print("Nouveau nom (laissez vide pour ne pas changer) : ");
                String newName = scanner.nextLine();
                if (!newName.isEmpty()) {
                    component.setName(newName);
                }

                if (component instanceof Material) {
                    Material material = (Material) component;
                    System.out.print("Nouvelle quantité (entrez -1 pour ne pas changer) : ");
                    double newQuantity = getValidDoubleInput();
                    if (newQuantity != -1) {
                        material.setQuantity(newQuantity);
                    }

                    System.out.print("Nouveau coût unitaire (entrez -1 pour ne pas changer) : ");
                    double newUnitCost = getValidDoubleInput();
                    if (newUnitCost != -1) {
                        material.setUnitCost(newUnitCost);
                    }

                    System.out.print("Nouveau coût de transport (entrez -1 pour ne pas changer) : ");
                    double newTransportCost = getValidDoubleInput();
                    if (newTransportCost != -1) {
                        material.setTransportCost(newTransportCost);
                    }

                    System.out.print("Nouveau coefficient de qualité (entrez -1 pour ne pas changer) : ");
                    double newQualityCoefficient = getValidDoubleInput();
                    if (newQualityCoefficient != -1) {
                        material.setQualityCoefficient(newQualityCoefficient);
                    }
                } else if (component instanceof Labor) {
                    Labor labor = (Labor) component;
                    System.out.print("Nouveau taux horaire (entrez -1 pour ne pas changer) : ");
                    double newHourlyRate = getValidDoubleInput();
                    if (newHourlyRate != -1) {
                        labor.setHourlyRate(newHourlyRate);
                    }

                    System.out.print("Nouvelles heures travaillées (entrez -1 pour ne pas changer) : ");
                    double newHoursWorked = getValidDoubleInput();
                    if (newHoursWorked != -1) {
                        labor.setHoursWorked(newHoursWorked);
                    }

                    System.out.print("Nouveau facteur de productivité (entrez -1 pour ne pas changer) : ");
                    double newProductivity = getValidDoubleInput();
                    if (newProductivity != -1) {
                        labor.setWorkerProductivity(newProductivity);
                    }
                }

                System.out.print("Nouveau taux de TVA (%) (entrez -1 pour ne pas changer) : ");
                double newTaxRate = getValidDoubleInput();
                if (newTaxRate != -1) {
                    component.setTaxRate(newTaxRate / 100.0); // Convert percentage to decimal
                }

                System.out.print("Êtes-vous sûr de vouloir mettre à jour ce composant ? (O/N) : ");
                String confirmation = scanner.nextLine();
                if (confirmation.equalsIgnoreCase("O")) {
                    componentService.updateComponent(component);
                    System.out.println("Composant mis à jour avec succès.");
                } else {
                    System.out.println("Mise à jour annulée.");
                }
            } else {
                System.out.println("Composant non trouvé.");
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de la modification du composant : " + e.getMessage());
        }
    }

    private void deleteComponent() {
        System.out.print("Entrez l'ID du composant à supprimer : ");
        Long componentId = getValidLongInput();

        try {
            Optional<Component> componentOpt = componentService.getComponentById(componentId);
            if (componentOpt.isPresent()) {
                Component component = componentOpt.get();
                System.out.println("Êtes-vous sûr de vouloir supprimer ce composant ? (O/N)");
                displayDetailedComponent(component);

                String confirmation = scanner.nextLine();
                if (confirmation.equalsIgnoreCase("O")) {
                    componentService.deleteComponent(componentId);
                    System.out.println("Composant supprimé avec succès.");
                } else {
                    System.out.println("Suppression annulée.");
                }
            } else {
                System.out.println("Composant non trouvé.");
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de la suppression du composant : " + e.getMessage());
        }
    }

    private int getValidIntInput(int min, int max) {
        while (true) {
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                if (choice >= min && choice <= max) {
                    return choice;
                } else {
                    System.out.print("Option invalide. Veuillez entrer un nombre entre " + min + " et " + max + " : ");
                }
            } catch (NumberFormatException e) {
                System.out.print("Entrée invalide. Veuillez entrer un nombre : ");
            }
        }
    }

    private double getValidDoubleInput() {
        while (true) {
            try {
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Entrée invalide. Veuillez entrer un nombre : ");
            }
        }
    }

    private double getValidPositiveDoubleInput() {
        while (true) {
            double value = getValidDoubleInput();
            if (value >= 0) {
                return value;
            } else {
                System.out.print("Veuillez entrer un nombre positif : ");
            }
        }
    }

    private long getValidLongInput() {
        while (true) {
            try {
                return Long.parseLong(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Entrée invalide. Veuillez entrer un nombre entier : ");
            }
        }
    }
}
