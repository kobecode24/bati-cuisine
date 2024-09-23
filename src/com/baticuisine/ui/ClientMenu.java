package com.baticuisine.ui;

import com.baticuisine.model.Client;
import com.baticuisine.service.ClientService;
import java.util.Scanner;
import java.util.List;
import java.util.Optional;

public class ClientMenu {
    private final Scanner scanner;
    private final ClientService clientService;

    public ClientMenu(Scanner scanner, ClientService clientService) {
        this.scanner = scanner;
        this.clientService = clientService;
    }

    public void manageClients() {
        while (true) {
            System.out.println("\n=== Gestion des Clients ===");
            System.out.println("1. Ajouter un nouveau client");
            System.out.println("2. Afficher tous les clients");
            System.out.println("3. Rechercher un client");
            System.out.println("4. Modifier un client");
            System.out.println("5. Supprimer un client");
            System.out.println("6. Retour au menu principal");
            System.out.print("Choisissez une option : ");

            int choice = getValidIntInput();

            switch (choice) {
                case 1:
                    addNewClient();
                    break;
                case 2:
                    displayAllClients();
                    break;
                case 3:
                    searchClient();
                    break;
                case 4:
                    modifyClient();
                    break;
                case 5:
                    deleteClient();
                    break;
                case 6:
                    return;
            }
        }
    }

    public void addNewClient() {
        System.out.println("--- Ajout d'un Nouveau Client ---");
        System.out.print("Nom : ");
        String name = scanner.nextLine();
        System.out.print("Adresse : ");
        String address = scanner.nextLine();
        System.out.print("Téléphone : ");
        String phone = scanner.nextLine();
        System.out.print("Est professionnel (true/false) : ");
        boolean isProfessional = Boolean.parseBoolean(scanner.nextLine());

        Client newClient = new Client(name, address, phone, isProfessional);
        clientService.addClient(newClient);
        System.out.println("Client ajouté avec succès !");
    }

    private void displayAllClients() {
        List<Client> clients = clientService.getAllClients();
        if (clients.isEmpty()) {
            System.out.println("Aucun client enregistré.");
            return;
        }

        System.out.println("\n--- Liste de tous les clients ---");
        for (Client client : clients) {
            displayClientDetails(client);
        }
    }

    private void searchClient() {
        System.out.print("Entrez le nom du client à rechercher : ");
        String searchName = scanner.nextLine();
        List<Client> foundClients = clientService.searchClientsByName(searchName);

        if (foundClients.isEmpty()) {
            System.out.println("Aucun client trouvé avec ce nom.");
        } else {
            System.out.println("\n--- Résultats de la recherche ---");
            for (Client client : foundClients) {
                displayClientDetails(client);
            }
        }
    }

    private void modifyClient() {
        System.out.print("Entrez l'ID du client à modifier : ");
        Long clientId = getValidLongInput();

        Optional<Client> clientOpt = clientService.getClientById(clientId);
        if (clientOpt.isPresent()) {
            Client client = clientOpt.get();
            System.out.println("Client actuel : ");
            displayClientDetails(client);

            System.out.print("Nouveau nom (laissez vide pour ne pas changer) : ");
            String newName = scanner.nextLine();
            if (!newName.isEmpty()) {
                client.setName(newName);
            }

            System.out.print("Nouvelle adresse (laissez vide pour ne pas changer) : ");
            String newAddress = scanner.nextLine();
            if (!newAddress.isEmpty()) {
                client.setAddress(newAddress);
            }

            System.out.print("Nouveau numéro de téléphone (laissez vide pour ne pas changer) : ");
            String newPhone = scanner.nextLine();
            if (!newPhone.isEmpty()) {
                client.setPhone(newPhone);
            }

            System.out.print("Est professionnel (true/false, laissez vide pour ne pas changer) : ");
            String newIsProfessional = scanner.nextLine();
            if (!newIsProfessional.isEmpty()) {
                client.setProfessional(Boolean.parseBoolean(newIsProfessional));
            }

            clientService.updateClient(client);
            System.out.println("Client mis à jour avec succès.");
        } else {
            System.out.println("Client non trouvé.");
        }
    }

    private void deleteClient() {
        System.out.print("Entrez l'ID du client à supprimer : ");
        Long clientId = getValidLongInput();

        Optional<Client> clientOpt = clientService.getClientById(clientId);
        if (clientOpt.isPresent()) {
            System.out.println("Êtes-vous sûr de vouloir supprimer ce client ? (O/N)");
            displayClientDetails(clientOpt.get());

            String confirmation = scanner.nextLine();
            if ("O".equalsIgnoreCase(confirmation)) {
                clientService.deleteClient(clientId);
                System.out.println("Client supprimé avec succès.");
            } else {
                System.out.println("Suppression annulée.");
            }
        } else {
            System.out.println("Client non trouvé.");
        }
    }

    private void displayClientDetails(Client client) {
        System.out.println("ID : " + client.getId());
        System.out.println("Nom : " + client.getName());
        System.out.println("Adresse : " + client.getAddress());
        System.out.println("Téléphone : " + client.getPhone());
        System.out.println("Professionnel : " + (client.isProfessional() ? "Oui" : "Non"));
        System.out.println("--------------------");
    }

    private int getValidIntInput() {
        while (true) {
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                if (choice >= 1 && choice <= 6) {
                    return choice;
                } else {
                    System.out.print("Option invalide. Veuillez entrer un nombre entre " + 1 + " et " + 6 + " : ");
                }
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
