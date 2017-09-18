package app.repositories.mysql;

import app.models.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

public interface MySQLPermissionRepository extends JpaRepository<Permission, String>, JpaSpecificationExecutor<Permission>, QueryDslPredicateExecutor<Permission> {

    //custom mysql query for Permission
}