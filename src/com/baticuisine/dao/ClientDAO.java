package com.baticuisine.dao;

import com.baticuisine.model.Client;
import java.util.List;
import java.util.Optional;

public interface ClientDAO {
    void add(Client client);
    Optional<Client> getById(Long id);
    List<Client> getAll();
    void update(Client client);
    void delete(Long id);
}
