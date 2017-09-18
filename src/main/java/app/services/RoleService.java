package app.services;

import app.models.Role;

public interface RoleService extends JpaService<Role> {

    /**
     * Retrieves an entity by its name (name is an unique value).
     *
     * @param name value to search.
     * @return the entity with the given name or null if none found
     */
    Role findByName(String name);
}