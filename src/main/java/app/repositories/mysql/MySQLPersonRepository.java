package app.repositories.mysql;

import app.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

public interface MySQLPersonRepository extends JpaRepository<Person, String>, JpaSpecificationExecutor<Person>, QueryDslPredicateExecutor<Person> {

    //custom mysql query for Person
}