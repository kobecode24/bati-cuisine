package com.baticuisine.ui;

import com.baticuisine.service.*;
import java.util.Scanner;

public class MainMenu {
    private final Scanner scanner;
    private final ProjectMenu projectMenu;
    private final ClientMenu clientMenu;
    private final ComponentMenu componentMenu;
    private final EstimateMenu estimateMenu;

    public MainMenu(ProjectService projectService, ClientService clientService,
                    ComponentService componentService, EstimateService estimateService) {
        this.scanner = new Scanner(System.in);
        this.projectMenu = new ProjectMenu(scanner, projectService, clientService, componentService, estimateService);
        this.clientMenu = new ClientMenu(scanner, clientService);
        this.componentMenu = new ComponentMenu(scanner, componentService , projectService);
        this.estimateMenu = new EstimateMenu(scanner, estimateService, projectService , componentService);
    }

    public void displayMenu() {
        while (true) {
            System.out.println("\n=== Bati-Cuisine: Gestion des Projets de Rénovation de Cuisines ===");
            System.out.println("1. Gestion des Projets");
            System.out.println("2. Gestion des Clients");
            System.out.println("3. Gestion des Composants");
            System.out.println("4. Gestion des Devis");
            System.out.println("5. Quitter");
            System.out.print("Choisissez une option : ");

            int choice = getValidIntInput();

            switch (choice) {
                case 1:
                    projectMenu.displayProjectMenu();
                    break;
                case 2:
                    clientMenu.manageClients();
                    break;
                case 3:
                    componentMenu.manageComponents();
                    break;
                case 4:
                    estimateMenu.manageEstimates();
                    break;
                case 5:
                    System.out.println("Merci d'avoir utilisé l'application Bati-Cuisine. Au revoir!");
                    return;
            }
        }
    }

    private int getValidIntInput() {
        int choice;
        while (true) {
            try {
                choice = Integer.parseInt(scanner.nextLine());
                if (choice >= 1 && choice <= 5) {
                    return choice;
                } else {
                    System.out.print("Option invalide. Veuillez entrer un nombre entre " + 1 + " et " + 5 + " : ");
                }
            } catch (NumberFormatException e) {
                System.out.print("Entrée invalide. Veuillez entrer un nombre : ");
            }
        }
    }
}