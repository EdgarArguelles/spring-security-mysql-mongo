package app.services;

import app.models.AuthProvider;

import java.util.List;

public interface AuthProviderService {

    /**
     * Retrieves all entities.
     *
     * @return list of entities.
     */
    List<AuthProvider> findAll();
}