package app.repositories.implementations;

import app.factories.PageFactory;
import app.models.Person;
import app.models.QPerson;
import app.pojos.pages.PageDataRequest;
import app.repositories.PersonRepository;
import app.repositories.executor.QueryExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

public class PersonRepositoryImpl implements QueryExecutor<Person> {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PageFactory pageFactory;

    @Override
    public Page<Person> page(PageDataRequest pageDataRequest) {
        if (DEFAULT_EXECUTOR == QueryDslPredicateExecutor.class || !(personRepository instanceof JpaSpecificationExecutor)) {
            return personRepository.findAll(pageFactory.getPredicate(pageDataRequest.getFilters(), QPerson.person), pageFactory.pageRequest(pageDataRequest));
        }

        JpaSpecificationExecutor specification = (JpaSpecificationExecutor) personRepository;
        return specification.findAll(pageFactory.getSpecifications(pageDataRequest.getFilters()), pageFactory.pageRequest(pageDataRequest));
    }
}