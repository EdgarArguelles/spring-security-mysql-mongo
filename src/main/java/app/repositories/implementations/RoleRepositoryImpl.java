package app.repositories.implementations;

import app.factories.PageFactory;
import app.models.QRole;
import app.models.Role;
import app.pojos.pages.PageDataRequest;
import app.repositories.RoleRepository;
import app.repositories.executor.QueryExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

public class RoleRepositoryImpl implements QueryExecutor<Role> {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PageFactory pageFactory;

    @Override
    public Page<Role> page(PageDataRequest pageDataRequest) {
        if (DEFAULT_EXECUTOR == QueryDslPredicateExecutor.class || !(roleRepository instanceof JpaSpecificationExecutor)) {
            return roleRepository.findAll(pageFactory.getPredicate(pageDataRequest.getFilters(), QRole.role), pageFactory.pageRequest(pageDataRequest));
        }

        JpaSpecificationExecutor specification = (JpaSpecificationExecutor) roleRepository;
        return specification.findAll(pageFactory.getSpecifications(pageDataRequest.getFilters()), pageFactory.pageRequest(pageDataRequest));
    }
}