package app.repositories.mongo;

import app.models.Role;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

public interface MongoRoleRepository extends MongoRepository<Role, String>, QueryDslPredicateExecutor<Role> {

    //custom mongo query for Role
}