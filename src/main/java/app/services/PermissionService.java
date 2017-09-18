package app.services;

import app.models.Permission;

public interface PermissionService extends JpaService<Permission> {

    /**
     * Retrieves an entity by its name (name is an unique value).
     *
     * @param name value to search.
     * @return the entity with the given name or null if none found
     */
    Permission findByName(String name);
}