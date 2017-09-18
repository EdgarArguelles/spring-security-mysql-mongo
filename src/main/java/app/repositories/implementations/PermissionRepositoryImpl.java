package app.repositories.implementations;

import app.factories.PageFactory;
import app.models.Permission;
import app.models.QPermission;
import app.pojos.pages.PageDataRequest;
import app.repositories.PermissionRepository;
import app.repositories.executor.QueryExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

public class PermissionRepositoryImpl implements QueryExecutor<Permission> {

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private PageFactory pageFactory;

    @Override
    public Page<Permission> page(PageDataRequest pageDataRequest) {
        if (DEFAULT_EXECUTOR == QueryDslPredicateExecutor.class || !(permissionRepository instanceof JpaSpecificationExecutor)) {
            return permissionRepository.findAll(pageFactory.getPredicate(pageDataRequest.getFilters(), QPermission.permission), pageFactory.pageRequest(pageDataRequest));
        }

        JpaSpecificationExecutor specification = (JpaSpecificationExecutor) permissionRepository;
        return specification.findAll(pageFactory.getSpecifications(pageDataRequest.getFilters()), pageFactory.pageRequest(pageDataRequest));
    }
}