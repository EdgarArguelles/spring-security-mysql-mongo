package app.repositories.mongo;

import app.models.Permission;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

public interface MongoPermissionRepository extends MongoRepository<Permission, String>, QueryDslPredicateExecutor<Permission> {

    //custom mongo query for Permission
}