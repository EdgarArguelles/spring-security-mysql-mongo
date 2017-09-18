package app;

import app.models.*;
import app.repositories.*;
import app.security.services.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Populates data tables at application start
 */
@Component
public class DataLoader {

    @Autowired
    private AuthProviderRepository authProviderRepository;

    @Autowired
    private AuthenticationRepository authenticationRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private SecurityService securityService;

    @Value("${data-loader}")
    private Boolean loadData;

    @PostConstruct
    private void setupDatabase() {
        try {
            if (loadData && authProviderRepository.count() == 0) {
                insertData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Transactional
    private void insertData() throws Exception {
        insertUser();
    }

    private void insertUser() throws Exception {
        List<AuthProvider> authProviders = Arrays.asList(new AuthProvider("LOCAL", "Provide with local username and password", null, null, null));
        authProviderRepository.save(authProviders);

        Set<Permission> allPermissions = new HashSet<>(Arrays.asList(
                new Permission("CREATE_USERS", "Allows to create and edit users and people"),
                new Permission("REMOVE_USERS", "Allows to delete users and people"),
                new Permission("VIEW_USERS", "Allows to view users and people"),
                new Permission("CREATE_ROLES", "Allows to create and edit roles and permissions"),
                new Permission("REMOVE_ROLES", "Allows to delete roles and permissions"),
                new Permission("VIEW_ROLES", "Allows to view roles and permissions")
        ));
        permissionRepository.save(allPermissions);

        Set<Permission> userPermissions = new HashSet<>(
                allPermissions.stream().filter(p -> Arrays.asList("VIEW_USERS", "VIEW_ROLES").contains(p.getName())).collect(Collectors.toList())
        );

        Set<Role> allRoles = new HashSet<>(Arrays.asList(
                new Role("ADMIN", "User with all permissions", allPermissions),
                new Role("USER", "User that only cans view", userPermissions)
        ));
        roleRepository.save(allRoles);

        Set<Role> userRoles = new HashSet<>(
                allRoles.stream().filter(p -> Arrays.asList("USER").contains(p.getName())).collect(Collectors.toList())
        );

        Set<Role> adminRoles = new HashSet<>(
                allRoles.stream().filter(p -> Arrays.asList("ADMIN").contains(p.getName())).collect(Collectors.toList())
        );

        List<Person> people = Arrays.asList(
                new Person("name 1", "last name 1", LocalDate.now(), Person.CIVIL_STATUS.SINGLE, Person.SEX.M, null, allRoles),
                new Person("name 2", "last name 2", LocalDate.now(), Person.CIVIL_STATUS.MARRIED, Person.SEX.F, "a2@a.com", userRoles),
                new Person("name 3", "last name 3", LocalDate.now(), Person.CIVIL_STATUS.SINGLE, Person.SEX.M, null, adminRoles)
        );
        personRepository.save(people);

        String password = securityService.hashValue("123");
        List<Authentication> authentications = Arrays.asList(
                new Authentication("user1", password, authProviders.get(0), people.get(0)),
                new Authentication("user2", password, authProviders.get(0), people.get(1)),
                new Authentication("user3", password, authProviders.get(0), people.get(2))
        );
        authenticationRepository.save(authentications);
    }
}