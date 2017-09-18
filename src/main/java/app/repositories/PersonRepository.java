package app.repositories;

import app.models.Person;
import app.models.Role;
import app.repositories.executor.QueryExecutor;
import app.repositories.mysql.MySQLPersonRepository;

import java.util.List;

public interface PersonRepository extends MySQLPersonRepository, QueryExecutor<Person> {

    //generic query not depends of mongo or sql

    /**
     * Find all People associated with the Role (not needed with JPA because SQL allows bi-directional relationship).
     *
     * @param role value to search.
     * @return associated people list
     */
    List<Person> findByRoles(Role role);
}