package app.repositories.mysql;

import app.models.AuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

public interface MySQLAuthProviderRepository extends JpaRepository<AuthProvider, String>, JpaSpecificationExecutor<AuthProvider>, QueryDslPredicateExecutor<AuthProvider> {

    //custom mysql query for AuthProvider
}