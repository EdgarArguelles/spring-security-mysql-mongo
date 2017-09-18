package app.repositories.mongo;

import app.models.Person;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

public interface MongoPersonRepository extends MongoRepository<Person, String>, QueryDslPredicateExecutor<Person> {

    //custom mongo query for Person
}