package com.baticuisine;

import com.baticuisine.ui.MainMenu;
import com.baticuisine.service.*;
import com.baticuisine.repository.*;
import com.baticuisine.util.DatabaseConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BatiCuisineApp {
    private static final Logger logger = LoggerFactory.getLogger(BatiCuisineApp.class);

    public static void main(String[] args) {
        logger.info("Starting Bati-Cuisine application");

        try {
            DatabaseConnection.getInstance();

            ClientRepository clientRepository = new ClientRepository();
            ProjectRepository projectRepository = new ProjectRepository();
            ComponentRepository componentRepository = new ComponentRepository();
            EstimateRepository estimateRepository = new EstimateRepository();

            ClientService clientService = new ClientService(clientRepository);
            ProjectService projectService = new ProjectService(projectRepository, componentRepository); // Modified this line
            ComponentService componentService = new ComponentService(componentRepository);
            EstimateService estimateService = new EstimateService(estimateRepository);

            MainMenu mainMenu = new MainMenu(projectService, clientService, componentService, estimateService);
            mainMenu.displayMenu();

        } catch (Exception e) {
            logger.error("An error occurred while starting the application", e);
            System.out.println("Une erreur est survenue lors du démarrage de l'application. Veuillez consulter les logs pour plus de détails.");
        } finally {
            DatabaseConnection.getInstance().closeConnection();
            logger.info("Bati-Cuisine application shutdown complete");
        }
    }
}