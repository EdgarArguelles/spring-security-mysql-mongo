package app.repositories.implementations;

import app.factories.PageFactory;
import app.models.Authentication;
import app.models.QAuthentication;
import app.pojos.pages.PageDataRequest;
import app.repositories.AuthenticationRepository;
import app.repositories.executor.QueryExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

public class AuthenticationRepositoryImpl implements QueryExecutor<Authentication> {

    @Autowired
    private AuthenticationRepository authenticationRepository;

    @Autowired
    private PageFactory pageFactory;

    @Override
    public Page<Authentication> page(PageDataRequest pageDataRequest) {
        if (DEFAULT_EXECUTOR == QueryDslPredicateExecutor.class || !(authenticationRepository instanceof JpaSpecificationExecutor)) {
            return authenticationRepository.findAll(pageFactory.getPredicate(pageDataRequest.getFilters(), QAuthentication.authentication), pageFactory.pageRequest(pageDataRequest));
        }

        JpaSpecificationExecutor specification = (JpaSpecificationExecutor) authenticationRepository;
        return specification.findAll(pageFactory.getSpecifications(pageDataRequest.getFilters()), pageFactory.pageRequest(pageDataRequest));
    }
}