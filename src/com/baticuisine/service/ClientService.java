package com.baticuisine.service;

import com.baticuisine.model.Client;
import com.baticuisine.repository.ClientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ClientService {
    private static final Logger logger = LoggerFactory.getLogger(ClientService.class);
    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public void addClient(Client client) {
        logger.info("Adding new client: {}", client.getName());
        clientRepository.add(client);
    }

    public Optional<Client> getClientById(Long id) {
        logger.info("Fetching client with ID: {}", id);
        return clientRepository.getById(id);
    }

    public List<Client> searchClientsByName(String name) {
        logger.info("Searching for clients with name containing: {}", name);
        return clientRepository.getAll().stream()
                .filter(client -> client.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Client> getAllClients() {
        logger.info("Fetching all clients");
        return clientRepository.getAll();
    }

    public void updateClient(Client client) {
        logger.info("Updating client: {}", client.getName());
        clientRepository.update(client);
    }

    public void deleteClient(Long id) {
        logger.info("Deleting client with ID: {}", id);
        clientRepository.delete(id);
    }
}
