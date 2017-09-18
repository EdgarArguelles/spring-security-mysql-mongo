package app.repositories;

import app.repositories.mysql.MySQLAuthProviderRepository;

public interface AuthProviderRepository extends MySQLAuthProviderRepository {

    //generic query not depends of mongo or sql
}