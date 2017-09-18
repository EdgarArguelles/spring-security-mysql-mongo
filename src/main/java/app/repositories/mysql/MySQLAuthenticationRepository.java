package app.repositories.mysql;

import app.models.Authentication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

public interface MySQLAuthenticationRepository extends JpaRepository<Authentication, String>, JpaSpecificationExecutor<Authentication>, QueryDslPredicateExecutor<Authentication> {

    //custom mysql query for Authentication
}