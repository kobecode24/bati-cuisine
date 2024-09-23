package com.baticuisine.ui;

import com.baticuisine.model.Estimate;
import com.baticuisine.model.Project;
import com.baticuisine.model.Component;
import com.baticuisine.service.EstimateService;
import com.baticuisine.service.ProjectService;
import com.baticuisine.service.ComponentService;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

public class EstimateMenu {
    private final Scanner scanner;
    private final EstimateService estimateService;
    private final ComponentService componentService;
    private final ProjectService projectService;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public EstimateMenu(Scanner scanner, EstimateService estimateService, ProjectService projectService, ComponentService componentService) {
        this.scanner = scanner;
        this.estimateService = estimateService;
        this.projectService = projectService;
        this.componentService = componentService;
    }

    public void manageEstimates() {
        while (true) {
            System.out.println("\n=== Gestion des Devis ===");
            System.out.println("1. Créer un nouveau devis");
            System.out.println("2. Afficher tous les devis");
            System.out.println("3. Rechercher un devis");
            System.out.println("4. Modifier un devis");
            System.out.println("5. Accepter/Refuser un devis");
            System.out.println("6. Supprimer un devis");
            System.out.println("7. Retour au menu principal");
            System.out.print("Choisissez une option : ");

            int choice = getValidIntInput(1, 7);

            switch (choice) {
                case 1:
                    createNewEstimate();
                    break;
                case 2:
                    displayAllEstimates();
                    break;
                case 3:
                    searchEstimate();
                    break;
                case 4:
                    modifyEstimate();
                    break;
                case 5:
                    acceptOrRejectEstimate();
                    break;
                case 6:
                    deleteEstimate();
                    break;
                case 7:
                    return;
            }
        }
    }

    public void createEstimateForProject(Project project, double profitMargin) {
        System.out.println("\n--- Création du Devis pour le projet: " + project.getProjectName() + " ---");

        List<Component> components = componentService.getComponentsByProject(project.getId());

        double totalCostBeforeTVA = 0;
        double totalCostAfterTVA = 0;

        System.out.println("\nDétail des coûts:");
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

        double estimatedAmount = totalCostAfterTVA * (1 + profitMargin);

        LocalDate issueDate = null;
        LocalDate validityDate = null;

        while (issueDate == null) {
            issueDate = getDateInput("Entrez la date d'émission du devis (format : dd/MM/yyyy) : ");
            if (issueDate == null) {
                System.out.println("La date d'émission est obligatoire. Veuillez réessayer.");
            }
        }

        while (validityDate == null) {
            validityDate = getDateInput("Entrez la date de validité du devis (format : dd/MM/yyyy) : ");
            if (validityDate == null) {
                System.out.println("La date de validité est obligatoire. Veuillez réessayer.");
            }
        }

        Estimate estimate = new Estimate(estimatedAmount, issueDate, validityDate);
        estimate.setProjectId(project.getId());

        System.out.println("\n--- Résultat du Calcul ---");
        System.out.println("Nom du projet : " + project.getProjectName());
        System.out.println("Client : " + project.getClient().getName());
        System.out.println("Adresse du chantier : " + project.getClient().getAddress());
        System.out.printf("Coût total avant TVA : %.2f €\n", totalCostBeforeTVA);
        System.out.printf("Coût total après TVA : %.2f €\n", totalCostAfterTVA);
        System.out.printf("TVA totale : %.2f €\n", totalCostAfterTVA - totalCostBeforeTVA);
        System.out.printf("Marge bénéficiaire (%.2f%%) : %.2f €\n", profitMargin * 100, estimatedAmount - totalCostAfterTVA);
        System.out.printf("Coût total final du projet : %.2f €\n", estimatedAmount);

        System.out.print("Souhaitez-vous enregistrer le devis ? (O/N) : ");
        String saveEstimate = scanner.nextLine();
        if ("O".equalsIgnoreCase(saveEstimate) || "Y".equalsIgnoreCase(saveEstimate)) {
            try {
                estimateService.addEstimate(estimate);
                System.out.println("Devis enregistré avec succès !");
            } catch (Exception e) {
                System.out.println("Erreur lors de l'enregistrement du devis : " + e.getMessage());
            }
        } else {
            System.out.println("Le devis n'a pas été enregistré.");
        }
    }

    private void createNewEstimate() {
        System.out.print("Entrez l'ID du projet pour lequel créer un devis : ");
        Long projectId = getValidLongInput();
        try {
            Optional<Project> projectOpt = projectService.getProjectById(projectId);
            if (projectOpt.isPresent()) {
                Project project = projectOpt.get();
                createEstimateForProject(project, project.getProfitMargin());
            } else {
                System.out.println("Projet non trouvé.");
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de la création du devis : " + e.getMessage());
        }
    }

    private void displayAllEstimates() {
        try {
            List<Estimate> estimates = estimateService.getAllEstimates();
            if (estimates.isEmpty()) {
                System.out.println("Aucun devis trouvé.");
                return;
            }

            System.out.println("\n--- Liste de tous les devis ---");
            for (Estimate estimate : estimates) {
                displayEstimateDetails(estimate);
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de la récupération des devis : " + e.getMessage());
        }
    }

    private void searchEstimate() {
        System.out.print("Entrez l'ID du devis ou l'ID du projet : ");
        Long id = getValidLongInput();

        try {
            Optional<Estimate> estimateOpt = estimateService.getEstimateById(id);
            if (estimateOpt.isPresent()) {
                displayEstimateDetails(estimateOpt.get());
            } else {
                Optional<Estimate> estimateByProjectOpt = estimateService.getEstimateByProject(id);
                if (estimateByProjectOpt.isPresent()) {
                    displayEstimateDetails(estimateByProjectOpt.get());
                } else {
                    System.out.println("Aucun devis trouvé.");
                }
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de la recherche du devis : " + e.getMessage());
        }
    }

    private void modifyEstimate() {
        System.out.print("Entrez l'ID du devis à modifier : ");
        Long estimateId = getValidLongInput();

        try {
            Optional<Estimate> estimateOpt = estimateService.getEstimateById(estimateId);
            if (estimateOpt.isPresent()) {
                Estimate estimate = estimateOpt.get();
                System.out.println("Devis actuel : ");
                displayEstimateDetails(estimate);

                System.out.print("Nouveau montant estimé (entrez -1 pour ne pas changer) : ");
                double newAmount = getValidDoubleInput();
                if (newAmount != -1) {
                    estimate.setEstimatedAmount(newAmount);
                }

                LocalDate newIssueDate = getDateInput("Nouvelle date d'émission (format : dd/MM/yyyy, laissez vide pour ne pas changer) : ");
                if (newIssueDate != null) {
                    estimate.setIssueDate(newIssueDate);
                }

                LocalDate newValidityDate = getDateInput("Nouvelle date de validité (format : dd/MM/yyyy, laissez vide pour ne pas changer) : ");
                if (newValidityDate != null) {
                    estimate.setValidityDate(newValidityDate);
                }

                System.out.print("Confirmer les modifications ? (O/N) : ");
                if ("O".equalsIgnoreCase(scanner.nextLine())) {
                    estimateService.updateEstimate(estimate);
                    System.out.println("Devis mis à jour avec succès.");
                } else {
                    System.out.println("Modification annulée.");
                }
            } else {
                System.out.println("Devis non trouvé.");
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de la modification du devis : " + e.getMessage());
        }
    }

    private void acceptOrRejectEstimate() {
        System.out.print("Entrez l'ID du devis à accepter/refuser : ");
        Long estimateId = getValidLongInput();

        try {
            Optional<Estimate> estimateOpt = estimateService.getEstimateById(estimateId);
            if (estimateOpt.isPresent()) {
                Estimate estimate = estimateOpt.get();
                System.out.println("Statut actuel : " + (estimate.isAccepted() ? "Accepté" : "Non accepté"));
                System.out.print("Voulez-vous accepter ce devis ? (O/N) : ");
                String choice = scanner.nextLine();
                estimate.setAccepted("O".equalsIgnoreCase(choice));
                estimateService.updateEstimate(estimate);
                System.out.println("Statut du devis mis à jour avec succès.");
            } else {
                System.out.println("Devis non trouvé.");
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de la mise à jour du statut du devis : " + e.getMessage());
        }
    }

    private void deleteEstimate() {
        System.out.print("Entrez l'ID du devis à supprimer : ");
        Long estimateId = getValidLongInput();

        try {
            Optional<Estimate> estimateOpt = estimateService.getEstimateById(estimateId);
            if (estimateOpt.isPresent()) {
                System.out.println("Êtes-vous sûr de vouloir supprimer ce devis ? (O/N)");
                displayEstimateDetails(estimateOpt.get());

                String confirmation = scanner.nextLine();
                if ("O".equalsIgnoreCase(confirmation)) {
                    estimateService.deleteEstimate(estimateId);
                    System.out.println("Devis supprimé avec succès.");
                } else {
                    System.out.println("Suppression annulée.");
                }
            } else {
                System.out.println("Devis non trouvé.");
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de la suppression du devis : " + e.getMessage());
        }
    }

    private void displayEstimateDetails(Estimate estimate) {
        System.out.println("ID du devis : " + estimate.getId());
        System.out.println("ID du projet : " + estimate.getProjectId());
        System.out.println("Montant estimé : " + estimate.getEstimatedAmount() + " €");
        System.out.println("Date d'émission : " + estimate.getIssueDate().format(dateFormatter));
        System.out.println("Date de validité : " + estimate.getValidityDate().format(dateFormatter));
        System.out.println("Statut : " + (estimate.isAccepted() ? "Accepté" : "Non accepté"));
        System.out.println("--------------------");
    }

    private LocalDate getDateInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String dateString = scanner.nextLine().trim();
            if (dateString.isEmpty()) {
                return null;
            }
            try {
                return LocalDate.parse(dateString, dateFormatter);
            } catch (DateTimeParseException e) {
                System.out.println("Format de date invalide. Veuillez réessayer.");
            }
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
}
