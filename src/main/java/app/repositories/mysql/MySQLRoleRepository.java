package app.repositories.mysql;

import app.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

public interface MySQLRoleRepository extends JpaRepository<Role, String>, JpaSpecificationExecutor<Role>, QueryDslPredicateExecutor<Role> {

    //custom mysql query for Role
}