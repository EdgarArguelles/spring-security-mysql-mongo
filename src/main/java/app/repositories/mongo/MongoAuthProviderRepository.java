package app.repositories.mongo;

import app.models.AuthProvider;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

public interface MongoAuthProviderRepository extends MongoRepository<AuthProvider, String>, QueryDslPredicateExecutor<AuthProvider> {

    //custom mongo query for AuthProvider
}