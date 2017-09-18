package app.security.services.implementations;

import app.exceptions.AppDontFoundException;
import app.exceptions.AppValidationException;
import app.models.Person;
import app.models.Role;
import app.repositories.AuthenticationRepository;
import app.repositories.PersonRepository;
import app.security.pojos.AccountCredentials;
import app.security.pojos.LoggedUser;
import app.security.services.SecurityService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SecurityServiceImpl implements SecurityService {

    @Autowired
    private AuthenticationRepository authenticationRepository;

    @Autowired
    private PersonRepository personRepository;

    @Override
    public LoggedUser authenticate(AccountCredentials credentials) {
        app.models.Authentication authentication = authenticationRepository.findByUsername(credentials.getUsername());
        if (authentication == null) {
            throw new AppDontFoundException("Credentials incorrect.");
        }

        String password = hashValue(credentials.getPassword());
        if (!authentication.getPassword().equals(password)) {
            throw new AppDontFoundException("Credentials incorrect.");
        }

        return createLoggedUser(authentication.getPerson(), null);
    }

    @Override
    public LoggedUser changeRole(String roleId) {
        LoggedUser loggedUser = getLoggedUser();
        if (loggedUser == null) {
            throw new AppValidationException("There isn't any logged user.");
        }

        Person person = personRepository.findOne(loggedUser.getId());
        return createLoggedUser(person, roleId);
    }

    @Override
    public LoggedUser getLoggedUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            return (LoggedUser) authentication.getPrincipal();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String hashValue(String value) {
        return DigestUtils.sha512Hex(value);
    }

    /**
     * Creates a LoggedUser instance with Person info and Role requested (if roleId is null first Role associated will be used)
     *
     * @param person from which LoggedUser will be created
     * @param roleId role id that LoggedUser will use (if null first Role associated will be used)
     * @return LoggedUser instance created
     */
    private LoggedUser createLoggedUser(Person person, String roleId) {
        if (person == null) {
            throw new AppValidationException("User doesn't have personal information associated.");
        }
        if (person.getRoles() == null || person.getRoles().isEmpty()) {
            throw new AppValidationException("User doesn't have Roles associated.");
        }

        Role role;
        if (roleId != null) {
            List<Role> selectedRoles = person.getRoles().stream().filter(r -> r.getId().equals(roleId)).collect(Collectors.toList());
            if (selectedRoles.isEmpty()) {
                throw new AppValidationException("User doesn't have the requested Role.");
            }
            role = selectedRoles.get(0);
        } else {
            role = person.getRoles().iterator().next();
        }

        LoggedUser loggedUser = new LoggedUser(person.getId(), person.getFullName(), role.getId(), new HashSet<>());
        if (role.getPermissions() != null) {
            loggedUser.setPermissions(role.getPermissions().stream().map(p -> p.getName()).collect(Collectors.toSet()));
        }

        return loggedUser;
    }
}