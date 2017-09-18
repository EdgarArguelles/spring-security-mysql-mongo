package app.repositories.mongo;

import app.models.Authentication;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

public interface MongoAuthenticationRepository extends MongoRepository<Authentication, String>, QueryDslPredicateExecutor<Authentication> {

    //custom mongo query for Authentication
}