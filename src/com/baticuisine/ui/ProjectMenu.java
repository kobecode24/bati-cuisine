package com.baticuisine.ui;

import com.baticuisine.model.Project;
import com.baticuisine.model.Client;
import com.baticuisine.model.Component;
import com.baticuisine.model.Estimate;
import com.baticuisine.enums.ProjectStatus;
import com.baticuisine.service.*;
import java.util.Scanner;
import java.util.List;
import java.util.Optional;


public class ProjectMenu {
    private final Scanner scanner;
    private final ProjectService projectService;
    private final ClientService clientService;
    private final ComponentMenu componentMenu;
    private final EstimateMenu estimateMenu;
    private final ComponentService componentService;
    private final EstimateService estimateService;


    public ProjectMenu(Scanner scanner, ProjectService projectService, ClientService clientService,
                       ComponentService componentService, EstimateService estimateService) {
        this.scanner = scanner;
        this.projectService = projectService;
        this.clientService = clientService;
        this.componentService = componentService;
        this.estimateService = estimateService;
        this.componentMenu = new ComponentMenu(scanner, componentService, projectService);
        this.estimateMenu = new EstimateMenu(scanner, estimateService, projectService , componentService);
    }

    public void displayProjectMenu() {
        while (true) {
            System.out.println("\n=== Gestion des Projets ===");
            System.out.println("1. Créer un nouveau projet");
            System.out.println("2. Afficher les projets existants");
            System.out.println("3. Rechercher un projet");
            System.out.println("4. Modifier un projet");
            System.out.println("5. Mettre à jour le statut d'un projet");
            System.out.println("6. Calculer le coût d'un projet");
            System.out.println("7. Voir les composants d'un projet");
            System.out.println("8. Voir l'estimation d'un projet");
            System.out.println("9. Supprimer un projet");
            System.out.println("10. Retour au menu principal");
            System.out.print("Choisissez une option : ");

            int choice = getValidIntInput(1, 10);

            switch (choice) {
                case 1:
                    createNewProject();
                    break;
                case 2:
                    displayExistingProjects();
                    break;
                case 3:
                    searchProject();
                    break;
                case 4:
                    modifyProject();
                    break;
                case 5:
                    updateProjectStatus();
                    break;
                case 6:
                    calculateProjectCost();
                    break;
                case 7:
                    viewProjectComponents();
                    break;
                case 8:
                    viewProjectEstimate();
                    break;
                case 9:
                    deleteProject();
                    break;
                case 10:
                    return;
            }
        }
    }

    private void createNewProject() {
        System.out.println("\n=== Création d'un Nouveau Projet ===");

        Client client = selectClient();
        if (client == null) {
            System.out.println("Création de projet annulée.");
            return;
        }

        System.out.print("Entrez le nom du projet : ");
        String projectName = scanner.nextLine();

        Project project = new Project(projectName, client);
        try {
            projectService.addProject(project);
            System.out.println("Projet créé avec succès. ID du projet : " + project.getId());

            componentMenu.addComponentsToProject(project);

            double totalCost = projectService.calculateProjectTotalCost(project.getId());
            project.setTotalCost(totalCost);
            System.out.println("\nCoût total du projet avant marge : " + totalCost + " €");

            System.out.print("Souhaitez-vous appliquer une marge bénéficiaire au projet ? (O/N) : ");
            String applyMargin = scanner.nextLine();

            double profitMargin = 0.0;
            if ("O".equalsIgnoreCase(applyMargin)) {
                System.out.print("Entrez la marge bénéficiaire (en %, ex: 15 pour 15%) : ");
                profitMargin = getValidDoubleInput() / 100.0;
                project.setProfitMargin(profitMargin);
            }

            projectService.updateProject(project);

            estimateMenu.createEstimateForProject(project, profitMargin);

            System.out.println("\nProjet créé et configuré avec succès !");
        } catch (Exception e) {
            System.out.println("Erreur lors de la création du projet : " + e.getMessage());
        }
    }

    private void displayExistingProjects() {
        try {
            List<Project> projects = projectService.getAllProjects();
            if (projects.isEmpty()) {
                System.out.println("Aucun projet existant.");
                return;
            }

            System.out.println("\n--- Liste des Projets ---");
            for (Project project : projects) {
                displayProjectSummary(project);
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de la récupération des projets : " + e.getMessage());
        }
    }

    private void searchProject() {
        System.out.print("Entrez l'ID ou le nom du projet : ");
        String search = scanner.nextLine();

        try {
            List<Project> projects = projectService.searchProjects(search);
            if (projects.isEmpty()) {
                System.out.println("Aucun projet trouvé.");
            } else {
                System.out.println("\n--- Résultats de la recherche ---");
                for (Project project : projects) {
                    displayProjectSummary(project);
                }
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de la recherche de projets : " + e.getMessage());
        }
    }

    private void modifyProject() {
        System.out.print("Entrez l'ID du projet à modifier : ");
        Long projectId = getValidLongInput();

        try {
            Optional<Project> projectOpt = projectService.getProjectById(projectId);
            if (projectOpt.isPresent()) {
                Project project = projectOpt.get();
                displayProjectDetails(project);

                System.out.print("Nouveau nom du projet (laissez vide pour ne pas changer) : ");
                String newName = scanner.nextLine();
                if (!newName.isEmpty()) {
                    project.setProjectName(newName);
                }

                System.out.print("Nouvelle marge bénéficiaire (en %, entrez -1 pour ne pas changer) : ");
                double newMargin = getValidDoubleInput();
                if (newMargin != -1) {
                    project.setProfitMargin(newMargin / 100.0);
                }

                System.out.print("Voulez-vous modifier le client ? (O/N) : ");
                if (scanner.nextLine().equalsIgnoreCase("O")) {
                    Client newClient = selectClient();
                    if (newClient != null) {
                        project.setClient(newClient);
                    }
                }

                System.out.print("Confirmer les modifications ? (O/N) : ");
                if (scanner.nextLine().equalsIgnoreCase("O")) {
                    projectService.updateProject(project);
                    System.out.println("Projet mis à jour avec succès.");
                } else {
                    System.out.println("Modifications annulées.");
                }
            } else {
                System.out.println("Projet non trouvé.");
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de la modification du projet : " + e.getMessage());
        }
    }

    private void updateProjectStatus() {
        System.out.print("Entrez l'ID du projet : ");
        Long projectId = getValidLongInput();

        try {
            Optional<Project> projectOpt = projectService.getProjectById(projectId);
            if (projectOpt.isPresent()) {
                Project project = projectOpt.get();
                System.out.println("Statut actuel : " + project.getProjectStatus());
                System.out.println("Nouveaux statuts disponibles :");
                for (ProjectStatus status : ProjectStatus.values()) {
                    System.out.println(status.ordinal() + ". " + status);
                }
                System.out.print("Choisissez le nouveau statut : ");
                int statusChoice = getValidIntInput(0, ProjectStatus.values().length - 1);
                ProjectStatus newStatus = ProjectStatus.values()[statusChoice];

                System.out.print("Confirmer le changement de statut ? (O/N) : ");
                if (scanner.nextLine().equalsIgnoreCase("O")) {
                    projectService.updateProjectStatus(projectId, newStatus);
                    System.out.println("Statut du projet mis à jour avec succès.");
                } else {
                    System.out.println("Changement de statut annulé.");
                }
            } else {
                System.out.println("Projet non trouvé.");
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de la mise à jour du statut : " + e.getMessage());
        }
    }

    private void calculateProjectCost() {
        System.out.print("Entrez l'ID ou le nom du projet : ");
        String input = scanner.nextLine();

        try {
            Optional<Project> projectOpt = findProjectByIdOrName(input);
            if (projectOpt.isPresent()) {
                Project project = projectOpt.get();
                List<Component> components = componentService.getComponentsByProject(project.getId());

                double totalCostBeforeTVA = 0;
                double totalCostAfterTVA = 0;

                System.out.println("\n--- Détail des coûts pour le projet '" + project.getProjectName() + "' ---");
                for (Component component : components) {
                    double costBeforeTVA = component.calculateCostWithoutTax();
                    double costAfterTVA = component.calculateCost();

                    System.out.printf("%s:\n", component.getName());
                    System.out.printf("  Coût avant TVA: %.2f €\n", costBeforeTVA);
                    System.out.printf("  Coût après TVA: %.2f €\n", costAfterTVA);
                    System.out.printf("  TVA: %.2f €\n", costAfterTVA - costBeforeTVA);

                    totalCostBeforeTVA += costBeforeTVA;
                    totalCostAfterTVA += costAfterTVA;
                }

                System.out.println("\nRésumé des coûts:");
                System.out.printf("Coût total avant TVA: %.2f €\n", totalCostBeforeTVA);
                System.out.printf("Coût total après TVA: %.2f €\n", totalCostAfterTVA);
                System.out.printf("TVA totale: %.2f €\n", totalCostAfterTVA - totalCostBeforeTVA);

                double finalCost = totalCostAfterTVA * (1 + project.getProfitMargin());
                System.out.printf("Coût final (avec marge de %.2f%%): %.2f €\n",
                        project.getProfitMargin() * 100, finalCost);

            } else {
                System.out.println("Projet non trouvé.");
            }
        } catch (Exception e) {
            System.out.println("Erreur lors du calcul du coût du projet : " + e.getMessage());
        }
    }

    private void viewProjectComponents() {
        System.out.print("Entrez l'ID ou le nom du projet : ");
        String input = scanner.nextLine();

        try {
            Optional<Project> projectOpt = findProjectByIdOrName(input);
            if (projectOpt.isPresent()) {
                Project project = projectOpt.get();
                List<Component> components = componentService.getComponentsByProject(project.getId());
                if (components.isEmpty()) {
                    System.out.println("Aucun composant trouvé pour le projet '" + project.getProjectName() + "'.");
                    System.out.println("Voulez-vous ajouter des composants à ce projet ? (O/N)");
                    if ("O".equalsIgnoreCase(scanner.nextLine())) {
                        componentMenu.addComponentsToProject(project);
                    }
                } else {
                    System.out.println("\n--- Composants du projet '" + project.getProjectName() + "' ---");
                    for (Component component : components) {
                        System.out.println(component);
                    }
                }
            } else {
                System.out.println("Projet non trouvé.");
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de la récupération des composants : " + e.getMessage());
        }
    }

    private void viewProjectEstimate() {
        System.out.print("Entrez l'ID ou le nom du projet : ");
        String input = scanner.nextLine();

        try {
            Optional<Project> projectOpt = findProjectByIdOrName(input);
            if (projectOpt.isPresent()) {
                Project project = projectOpt.get();
                Optional<Estimate> estimateOpt = estimateService.getEstimateByProject(project.getId());
                if (estimateOpt.isPresent()) {
                    System.out.println("\n--- Estimation du projet '" + project.getProjectName() + "' ---");
                    System.out.println(estimateOpt.get());
                } else {
                    System.out.println("Aucune estimation trouvée pour le projet '" + project.getProjectName() + "'.");
                    System.out.println("Voulez-vous créer une estimation pour ce projet ? (O/N)");
                    if ("O".equalsIgnoreCase(scanner.nextLine())) {
                        estimateMenu.createEstimateForProject(project, project.getProfitMargin());
                    }
                }
            } else {
                System.out.println("Projet non trouvé.");
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de la récupération de l'estimation : " + e.getMessage());
        }
    }

    private void deleteProject() {
        System.out.print("Entrez l'ID du projet à supprimer : ");
        Long projectId = getValidLongInput();

        try {
            Optional<Project> projectOpt = projectService.getProjectById(projectId);
            if (projectOpt.isPresent()) {
                Project project = projectOpt.get();
                displayProjectDetails(project);
                System.out.println("Êtes-vous sûr de vouloir supprimer ce projet ? (O/N)");
                String confirmation = scanner.nextLine();
                if (confirmation.equalsIgnoreCase("O")) {
                    projectService.deleteProject(projectId);
                    System.out.println("Projet supprimé avec succès.");
                } else {
                    System.out.println("Suppression annulée.");
                }
            } else {
                System.out.println("Projet non trouvé.");
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de la suppression du projet : " + e.getMessage());
        }
    }

    private Client selectClient() {
        System.out.println("--- Recherche de client ---");
        System.out.println("Souhaitez-vous chercher un client existant ou en ajouter un nouveau ?");
        System.out.println("1. Chercher un client existant");
        System.out.println("2. Ajouter un nouveau client");
        System.out.print("Choisissez une option : ");

        int choice = getValidIntInput(1, 2);

        if (choice == 1) {
            return searchExistingClient();
        } else {
            return addNewClient();
        }
    }

    private Client searchExistingClient() {
        System.out.print("Entrez le nom du client : ");
        String clientName = scanner.nextLine();
        List<Client> clients = clientService.searchClientsByName(clientName);

        if (clients.isEmpty()) {
            System.out.println("Aucun client trouvé avec ce nom.");
            return null;
        }

        System.out.println("Clients trouvés :");
        for (int i = 0; i < clients.size(); i++) {
            System.out.println((i + 1) + ". " + clients.get(i).getName());
        }

        System.out.print("Sélectionnez un client (numéro) : ");
        int selection = getValidIntInput(1, clients.size());

        return clients.get(selection - 1);
    }

    private Client addNewClient() {
        System.out.println("--- Ajout d'un nouveau client ---");
        System.out.print("Nom : ");
        String name = scanner.nextLine();
        System.out.print("Adresse : ");
        String address = scanner.nextLine();
        System.out.print("Téléphone : ");
        String phone = scanner.nextLine();
        System.out.print("Est professionnel (true/false) : ");
        boolean isProfessional = false;
        while (true) {
            System.out.print("Est professionnel (O/N) : ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("o") || input.equals("oui")) {
                isProfessional = true;
                break;
            } else if (input.equals("n") || input.equals("non")) {
                break;
            } else {
                System.out.println("Entrée invalide. Veuillez répondre par O (Oui) ou N (Non).");
            }
        }

        Client newClient = new Client(name, address, phone, isProfessional);
        try {
            clientService.addClient(newClient);
            System.out.println("Nouveau client ajouté avec succès.");
            return newClient;
        } catch (Exception e) {
            System.out.println("Erreur lors de l'ajout du client : " + e.getMessage());
            return null;
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

    private long getValidLongInput() {
        while (true) {
            try {
                return Long.parseLong(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Entrée invalide. Veuillez entrer un nombre entier : ");
            }
        }
    }
    private void displayProjectSummary(Project project) {
        System.out.println("ID: " + project.getId() +"\n"+ " Nom: " + project.getProjectName() +"\n"+
                " Client: " + project.getClient().getName() +"\n"+
                " Statut: " + project.getProjectStatus());
    }

    private void displayProjectDetails(Project project) {
        System.out.println("\n--- Détails du projet ---");
        System.out.println("ID: " + project.getId());
        System.out.println("Nom: " + project.getProjectName());
        System.out.println("Client: " + project.getClient().getName());
        System.out.println("Statut: " + project.getProjectStatus());
        System.out.println("Marge bénéficiaire: " + (project.getProfitMargin() * 100) + "%");
        System.out.println("Date de création: " + project.getCreationDate());
        System.out.println("Coût total: " + project.getTotalCost() + " €");
    }

    private Optional<Project> findProjectByIdOrName(String input) {
        try {
            Long id = Long.parseLong(input);
            return projectService.getProjectById(id);
        } catch (NumberFormatException e) {
            List<Project> projects = projectService.searchProjects(input);
            if (projects.size() == 1) {
                return Optional.of(projects.getFirst());
            } else if (projects.size() > 1) {
                System.out.println("Plusieurs projets trouvés. Veuillez choisir :");
                for (int i = 0; i < projects.size(); i++) {
                    System.out.println((i + 1) + ". " + projects.get(i).getProjectName());
                }
                int choice = getValidIntInput(1, projects.size()) - 1;
                return Optional.of(projects.get(choice));
            }
        }
        return Optional.empty();
    }
}
