package app.services.implementations;

import app.exceptions.AppDontFoundException;
import app.exceptions.AppValidationException;
import app.models.AuthProvider;
import app.models.Authentication;
import app.models.Person;
import app.pojos.pages.PageDataRequest;
import app.repositories.AuthenticationRepository;
import app.security.services.SecurityService;
import app.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired
    private AuthenticationRepository authenticationRepository;

    @Autowired
    private SecurityService securityService;

    @Override
    public List<Authentication> findAll() {
        return authenticationRepository.findAll();
    }

    @Override
    public Authentication findById(String id) {
        return authenticationRepository.findOne(id);
    }

    @Override
    public Authentication findByIdNotNull(String id) throws AppDontFoundException {
        Authentication authentication = findById(id);
        if (authentication == null) {
            throw new AppDontFoundException("Data don't found.");
        }

        return authentication;
    }

    @Override
    public Authentication findByUsername(String name) {
        return authenticationRepository.findByUsername(name);
    }

    @Override
    public Authentication findByAuthProviderAndPerson(AuthProvider authProvider, Person person) {
        return authenticationRepository.findByAuthProviderAndPerson(authProvider, person);
    }

    @Override
    @Transactional
    public Authentication save(Authentication authentication) {
        if (authentication.getUsername() != null && findByUsername(authentication.getUsername()) != null) {
            throw new AppValidationException("Username '" + authentication.getUsername() + "' is already used by another user.");
        }

        Authentication duplicated = findByAuthProviderAndPerson(authentication.getAuthProvider(), authentication.getPerson());
        if (duplicated != null) {
            throw new AppValidationException("'" + duplicated.getPerson().getFullName() + "' already has an Authorization with provider '" + duplicated.getAuthProvider().getName() + "'.");
        }

        authentication.setPassword(securityService.hashValue(authentication.getPassword()));
        return authenticationRepository.save(authentication);
    }

    @Override
    @Transactional
    public Authentication update(Authentication authentication) {
        Authentication original = findByIdNotNull(authentication.getId());
        original.setPassword(securityService.hashValue(authentication.getPassword()));
        return authenticationRepository.save(original);
    }

    @Override
    @Transactional
    public Authentication delete(String id) {
        Authentication authentication = findByIdNotNull(id);
        authenticationRepository.delete(authentication);
        return authentication;
    }

    @Override
    public Page<Authentication> page(PageDataRequest pageDataRequest) {
        return authenticationRepository.page(pageDataRequest);
    }
}