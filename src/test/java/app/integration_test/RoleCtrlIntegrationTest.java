package app.integration_test;

import app.models.Permission;
import app.models.Person;
import app.models.Role;
import app.pojos.pages.FilterRequest;
import app.pojos.pages.PageDataRequest;
import app.pojos.pages.PageDataResponse;
import app.repositories.*;
import app.security.services.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class RoleCtrlIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private TokenService tokenService;

    @Autowired
    private AuthenticationRepository authenticationRepository;

    @Autowired
    private AuthProviderRepository authProviderRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    private List<Person> dbPeople;

    private List<Role> dbRoles;

    private List<Permission> dbPermissions;

    private final String BASE_URL = "/roles";

    private final String COMPLETE = "?complete=true";

    private final String ALL_RELATIONS = "?all_relations=true";

    private final String BOTH = "?complete=true&all_relations=true";

    private final String NOT_FOUND_ERROR = "Data don't found.";

    private IntegrationTest integrationTest;

    @Before
    public void setup() throws Exception {
        integrationTest = new IntegrationTest(mvc, mapper, tokenService);
        IntegrationTest.cleanAllData(authenticationRepository, authProviderRepository, personRepository, roleRepository, permissionRepository);

        dbPermissions = Arrays.asList(
                new Permission("P1", "D1"),
                new Permission("P2", "D2")
        );
        permissionRepository.save(dbPermissions);

        dbRoles = Arrays.asList(
                new Role("R1", "D1", Collections.EMPTY_SET),
                new Role("R2", "D2", new HashSet<>(Arrays.asList(dbPermissions.get(1)))),
                new Role("R3", "D3", new HashSet<>(dbPermissions))
        );
        roleRepository.save(dbRoles);

        dbPeople = Arrays.asList(
                new Person("N", "LN", LocalDate.now(), 1, "A", null, new HashSet<>(Arrays.asList(dbRoles.get(2))))
        );
        personRepository.save(dbPeople);
    }

    /**
     * Should get roles list when complete is false and allRelations is false
     */
    @Test
    public void listWhenNotCompleteAndNotAllRelations() throws Exception {
        final List<Role> rolesExpected = dbRoles;
        rolesExpected.forEach(r -> r.setPermissions(null));

        final Map mapResult = integrationTest.getOKResponse(MockMvcRequestBuilders.get(BASE_URL));
        final List<Role> rolesResult = IntegrationTest.getRoles(mapResult.get("data"));

        assertNotSame(rolesExpected, rolesResult);
        assertEquals(rolesExpected, rolesResult);
        assertNull(mapResult.get("metaData"));
    }

    /**
     * Should get roles list when complete is true and allRelations is false
     */
    @Test
    public void listWhenCompleteAndNotAllRelations() throws Exception {
        final List<Role> rolesExpected = dbRoles;

        final Map mapResult = integrationTest.getOKResponse(MockMvcRequestBuilders.get(BASE_URL + COMPLETE));
        final List<Role> rolesResult = IntegrationTest.getRoles(mapResult.get("data"));

        assertNotSame(rolesExpected, rolesResult);
        assertEquals(rolesExpected, rolesResult);
        assertNull(mapResult.get("metaData"));
    }

    /**
     * Should get roles list when complete is false and allRelations is true
     */
    @Test
    public void listWhenNotCompleteAndAllRelations() throws Exception {
        final List<Role> rolesExpected = dbRoles;

        final Map mapResult = integrationTest.getOKResponse(MockMvcRequestBuilders.get(BASE_URL + ALL_RELATIONS + ""));
        final List<Role> rolesResult = IntegrationTest.getRoles(mapResult.get("data"));

        assertNotSame(rolesExpected, rolesResult);
        assertEquals(rolesExpected, rolesResult);
        assertNull(mapResult.get("metaData"));
    }

    /**
     * Should get roles list when complete is true and allRelations is true
     */
    @Test
    public void listWhenCompleteAndAllRelations() throws Exception {
        final List<Role> rolesExpected = dbRoles;

        final Map mapResult = integrationTest.getOKResponse(MockMvcRequestBuilders.get(BASE_URL + BOTH + " "));
        final List<Role> rolesResult = IntegrationTest.getRoles(mapResult.get("data"));

        assertNotSame(rolesExpected, rolesResult);
        assertEquals(rolesExpected, rolesResult);
        assertNull(mapResult.get("metaData"));
    }

    /**
     * Should return a NOT_FOUND error response
     */
    @Test
    public void getWhenNotFound() throws Exception {
        final Map mapResult = integrationTest.getNotFoundResponse(MockMvcRequestBuilders.get(
                BASE_URL + "/ab"));
        final Map errorResult = (Map) mapResult.get("error");

        assertNotSame(NOT_FOUND_ERROR, errorResult.get("message"));
        assertEquals(NOT_FOUND_ERROR, errorResult.get("message"));
        assertNull(errorResult.get("devMessage"));
        assertNull(errorResult.get("nestedErrors"));
    }

    /**
     * Should get a role when complete is false and allRelations is false
     */
    @Test
    public void getWhenNotCompleteAndNotAllRelations() throws Exception {
        final Role roleExpected = dbRoles.get(2);
        roleExpected.setPermissions(null);
        final String ID = roleExpected.getId();

        final Map mapResult = integrationTest.getOKResponse(MockMvcRequestBuilders.get(
                BASE_URL + "/" + ID));
        final Role roleResult = IntegrationTest.getRole(mapResult.get("data"));

        assertNotSame(roleExpected, roleResult);
        assertEquals(roleExpected, roleResult);
        assertNull(mapResult.get("metaData"));
    }

    /**
     * Should get a role when complete is true and allRelations is false
     */
    @Test
    public void getWhenCompleteAndNotAllRelations() throws Exception {
        final Role roleExpected = dbRoles.get(2);
        final String ID = roleExpected.getId();

        final Map mapResult = integrationTest.getOKResponse(MockMvcRequestBuilders.get(
                BASE_URL + "/" + ID + COMPLETE));
        final Role roleResult = IntegrationTest.getRole(mapResult.get("data"));

        assertNotSame(roleExpected, roleResult);
        assertEquals(roleExpected, roleResult);
        assertNull(mapResult.get("metaData"));
    }

    /**
     * Should get a role when complete is false and allRelations is true
     */
    @Test
    public void getWhenNotCompleteAndAllRelations() throws Exception {
        final Role roleExpected = dbRoles.get(2);
        final String ID = roleExpected.getId();

        final Map mapResult = integrationTest.getOKResponse(MockMvcRequestBuilders.get(
                BASE_URL + "/" + ID + ALL_RELATIONS + ""));
        final Role roleResult = IntegrationTest.getRole(mapResult.get("data"));

        assertNotSame(roleExpected, roleResult);
        assertEquals(roleExpected, roleResult);
        assertNull(mapResult.get("metaData"));
    }

    /**
     * Should get a role when complete is true and allRelations is true
     */
    @Test
    public void getWhenCompleteAndAllRelations() throws Exception {
        final Role roleExpected = dbRoles.get(2);
        final String ID = roleExpected.getId();

        final Map mapResult = integrationTest.getOKResponse(MockMvcRequestBuilders.get(
                BASE_URL + "/" + ID + BOTH + " "));
        final Role roleResult = IntegrationTest.getRole(mapResult.get("data"));

        assertNotSame(roleExpected, roleResult);
        assertEquals(roleExpected, roleResult);
        assertNull(mapResult.get("metaData"));
    }

    /**
     * Should return a BAD_REQUEST error response
     */
    @Test
    public void createWhenDuplicate() throws Exception {
        final String ERROR_EXPECTED = "Role name 'R2' is already used.";

        final Role r = new Role("R2", "new description", null);
        final String requestJson = mapper.writeValueAsString(r);

        final Map mapResult = integrationTest.getBadRequestResponse(
                MockMvcRequestBuilders.post(BASE_URL).content(requestJson));
        final Map errorResult = (Map) mapResult.get("error");

        assertNotSame(ERROR_EXPECTED, errorResult.get("message"));
        assertEquals(ERROR_EXPECTED, errorResult.get("message"));
        assertNull(errorResult.get("devMessage"));
        assertNull(errorResult.get("nestedErrors"));

        // not inserted in data base
        assertEquals(roleRepository.count(), 3);
        assertEquals(permissionRepository.count(), 2);
    }

    /**
     * Should return an INTERNAL_SERVER_ERROR error response
     */
    @Test
    public void createWhenInvalidPermissionID() throws Exception {
        final String ERROR_EXPECTED = "An error has occurred.";

        final Set<Permission> permissions = new HashSet<>(Arrays.asList(
                new Permission(dbPermissions.get(0).getId()),
                new Permission("invalid")
        ));
        final Role r = new Role("R4", "new description", permissions);
        final String requestJson = mapper.writeValueAsString(r);

        final Map mapResult = integrationTest.getInternalServerResponse(
                MockMvcRequestBuilders.post(BASE_URL).content(requestJson));
        final Map errorResult = (Map) mapResult.get("error");

        assertNotSame(ERROR_EXPECTED, errorResult.get("message"));
        assertEquals(ERROR_EXPECTED, errorResult.get("message"));
        assertNotNull(errorResult.get("devMessage"));
        assertNull(errorResult.get("nestedErrors"));

        // not inserted in data base
        assertEquals(roleRepository.count(), 3);
        assertEquals(permissionRepository.count(), 2);
    }

    /**
     * Should return an INTERNAL_SERVER_ERROR error response
     */
    @Test
    public void createWhenNotPermissionID() throws Exception {
        final String ERROR_EXPECTED = "An error has occurred.";

        final Set<Permission> permissions = new HashSet<>(Arrays.asList(
                new Permission(dbPermissions.get(0).getId()),
                new Permission("N", "D")
        ));
        final Role r = new Role("R4", "new description", permissions);
        final String requestJson = mapper.writeValueAsString(r);

        final Map mapResult = integrationTest.getInternalServerResponse(
                MockMvcRequestBuilders.post(BASE_URL).content(requestJson));
        final Map errorResult = (Map) mapResult.get("error");

        assertNotSame(ERROR_EXPECTED, errorResult.get("message"));
        assertEquals(ERROR_EXPECTED, errorResult.get("message"));
        assertNotNull(errorResult.get("devMessage"));
        assertNull(errorResult.get("nestedErrors"));

        // not inserted in data base
        assertEquals(roleRepository.count(), 3);
        assertEquals(permissionRepository.count(), 2);
    }

    /**
     * Should create a new Role
     */
    @Test
    public void createWhenSuccessNullPermissions() throws Exception {
        final Role roleExpected = new Role("NR", "new entry", null);
        final Role r = new Role("NR", "new entry", null);
        final String requestJson = mapper.writeValueAsString(r);

        final Map mapResult = integrationTest.getOKResponse(
                MockMvcRequestBuilders.post(BASE_URL).content(requestJson));
        final Role roleResult = IntegrationTest.getRole(mapResult.get("data"));

        roleExpected.setId(roleResult.getId());

        assertNotSame(roleExpected, roleResult);
        assertEquals(roleExpected, roleResult);
        assertNull(mapResult.get("metaData"));

        // inserted in data base
        assertEquals(roleRepository.count(), 4);
        assertEquals(permissionRepository.count(), 2);
    }

    /**
     * Should create a new Role
     */
    @Test
    public void createWhenSuccessEmptyPermissions() throws Exception {
        final Role roleExpected = new Role("NR", "new entry", Collections.EMPTY_SET);
        final Role r = new Role("NR", "new entry", Collections.EMPTY_SET);
        final String requestJson = mapper.writeValueAsString(r);

        final Map mapResult = integrationTest.getOKResponse(
                MockMvcRequestBuilders.post(BASE_URL).content(requestJson));
        final Role roleResult = IntegrationTest.getRole(mapResult.get("data"));

        roleExpected.setId(roleResult.getId());

        assertNotSame(roleExpected, roleResult);
        assertEquals(roleExpected, roleResult);
        assertNull(mapResult.get("metaData"));

        // inserted in data base
        assertEquals(roleRepository.count(), 4);
        assertEquals(permissionRepository.count(), 2);
    }

    /**
     * Should create a new Role
     */
    @Test
    public void createWhenSuccess() throws Exception {
        final String duplicateID = dbPermissions.get(0).getId();
        final String fakeID = dbPermissions.get(1).getId();
        final Permission permissionFake = new Permission("fake", "fake2"); // this fake info will be returned but not saved
        permissionFake.setId(fakeID);

        final Set<Permission> permissionsExpected = new HashSet<>(Arrays.asList(
                new Permission(duplicateID),
                permissionFake
        ));
        final Role roleExpected = new Role("NR", "new entry", permissionsExpected);

        final Set<Permission> permissions = new HashSet<>(Arrays.asList(
                new Permission(duplicateID),
                permissionFake,
                new Permission(duplicateID) // this duplicate ID will be ignored
        ));
        final Role r = new Role("NR", "new entry", permissions);
        final String requestJson = mapper.writeValueAsString(r);

        final Map mapResult = integrationTest.getOKResponse(
                MockMvcRequestBuilders.post(BASE_URL).content(requestJson));
        final Role roleResult = IntegrationTest.getRole(mapResult.get("data"));

        roleExpected.setId(roleResult.getId());

        assertNotSame(roleExpected, roleResult);
        assertEquals(roleExpected, roleResult);
        assertNull(mapResult.get("metaData"));

        // inserted in data base
        assertEquals(roleRepository.count(), 4);
        assertEquals(permissionRepository.count(), 2);

        // permissions not edited
        validatePermissionsNotEdited();
    }

    /**
     * Should return a NOT_FOUND error response
     */
    @Test
    public void editWhenNotFound() throws Exception {
        final Role r = new Role("new name", "new description", null);
        r.setId("old");
        final String requestJson = mapper.writeValueAsString(r);

        final Map mapResult = integrationTest.getNotFoundResponse(
                MockMvcRequestBuilders.put(BASE_URL + "/abc").content(requestJson));
        final Map errorResult = (Map) mapResult.get("error");

        assertNotSame(NOT_FOUND_ERROR, errorResult.get("message"));
        assertEquals(NOT_FOUND_ERROR, errorResult.get("message"));
        assertNull(errorResult.get("devMessage"));
        assertNull(errorResult.get("nestedErrors"));

        // not updated in data base
        validateRolesNotEdited();
        validatePermissionsNotEdited();
    }

    /**
     * Should return an INTERNAL_SERVER_ERROR error response
     */
    @Test
    public void editWhenInvalidPermissionID() throws Exception {
        final String ID = dbRoles.get(0).getId();
        final String ERROR_EXPECTED = "An error has occurred.";

        final Set<Permission> permissions = new HashSet<>(Arrays.asList(
                new Permission(dbPermissions.get(0).getId()),
                new Permission("invalid")
        ));
        final Role r = new Role("new name", "new description", permissions);
        r.setId("old");
        final String requestJson = mapper.writeValueAsString(r);

        final Map mapResult = integrationTest.getInternalServerResponse(
                MockMvcRequestBuilders.put(BASE_URL + "/" + ID).content(requestJson));
        final Map errorResult = (Map) mapResult.get("error");

        assertNotSame(ERROR_EXPECTED, errorResult.get("message"));
        assertEquals(ERROR_EXPECTED, errorResult.get("message"));
        assertNotNull(errorResult.get("devMessage"));
        assertNull(errorResult.get("nestedErrors"));

        // not updated in data base
        validateRolesNotEdited();
        validatePermissionsNotEdited();
    }

    /**
     * Should return an INTERNAL_SERVER_ERROR error response
     */
    @Test
    public void editWhenNotPermissionID() throws Exception {
        final String ID = dbRoles.get(0).getId();
        final String ERROR_EXPECTED = "An error has occurred.";

        final Set<Permission> permissions = new HashSet<>(Arrays.asList(
                new Permission(dbPermissions.get(0).getId()),
                new Permission("N", "D")
        ));
        final Role r = new Role("new name", "new description", permissions);
        r.setId("old");
        final String requestJson = mapper.writeValueAsString(r);

        final Map mapResult = integrationTest.getInternalServerResponse(
                MockMvcRequestBuilders.put(BASE_URL + "/" + ID).content(requestJson));
        final Map errorResult = (Map) mapResult.get("error");

        assertNotSame(ERROR_EXPECTED, errorResult.get("message"));
        assertEquals(ERROR_EXPECTED, errorResult.get("message"));
        assertNotNull(errorResult.get("devMessage"));
        assertNull(errorResult.get("nestedErrors"));

        // not updated in data base
        validateRolesNotEdited();
        validatePermissionsNotEdited();
    }

    /**
     * Should update a Role
     */
    @Test
    public void editWhenSuccessNullPermissions() throws Exception {
        final String ID = dbRoles.get(2).getId();
        final Role roleExpected = new Role("R3", "new description", null);
        roleExpected.setId(ID);
        final Role r = new Role("new name", "new description", null);
        r.setId("old");
        final String requestJson = mapper.writeValueAsString(r);

        final Map mapResult = integrationTest.getOKResponse(
                MockMvcRequestBuilders.put(BASE_URL + "/" + ID).content(requestJson));
        final Role roleResult = IntegrationTest.getRole(mapResult.get("data"));

        assertNotSame(roleExpected, roleResult);
        assertEquals(roleExpected, roleResult);
        assertNull(mapResult.get("metaData"));

        // updated in data base
        validateRolesEdited();
        validatePermissionsNotEdited();
    }

    /**
     * Should update a Role
     */
    @Test
    public void editWhenSuccessEmptyPermissions() throws Exception {
        final String ID = dbRoles.get(0).getId();
        final Role roleExpected = new Role("R1", "new description", Collections.EMPTY_SET);
        roleExpected.setId(ID);
        final Role r = new Role("new name", "new description", Collections.EMPTY_SET);
        r.setId("old");
        final String requestJson = mapper.writeValueAsString(r);

        final Map mapResult = integrationTest.getOKResponse(
                MockMvcRequestBuilders.put(BASE_URL + "/" + ID).content(requestJson));
        final Role roleResult = IntegrationTest.getRole(mapResult.get("data"));

        assertNotSame(roleExpected, roleResult);
        assertEquals(roleExpected, roleResult);
        assertNull(mapResult.get("metaData"));

        // updated in data base
        validateRolesEdited();
        validatePermissionsNotEdited();
    }

    /**
     * Should update a Role
     */
    @Test
    public void editWhenSuccess() throws Exception {
        final String ID = dbRoles.get(1).getId();
        final String duplicateID = dbPermissions.get(0).getId();
        final String fakeID = dbPermissions.get(1).getId();
        final Permission permissionFake = new Permission("fake", "fake2"); // this fake info will be returned but not saved
        permissionFake.setId(fakeID);

        final Set<Permission> permissionsExpected = new HashSet<>(Arrays.asList(dbPermissions.get(0), dbPermissions.get(1)));
        final Role roleExpected = new Role("R2", "new description", permissionsExpected);
        roleExpected.setId(ID);

        final Set<Permission> permissions = new HashSet<>(Arrays.asList(
                new Permission(duplicateID),
                permissionFake,
                new Permission(duplicateID) // this duplicate ID will be ignored
        ));
        final Role r = new Role("new name", "new description", permissions);
        r.setId("old");
        final String requestJson = mapper.writeValueAsString(r);

        final Map mapResult = integrationTest.getOKResponse(
                MockMvcRequestBuilders.put(BASE_URL + "/" + ID).content(requestJson));
        final Role roleResult = IntegrationTest.getRole(mapResult.get("data"));

        assertNotSame(roleExpected, roleResult);
        assertEquals(roleExpected, roleResult);
        assertNull(mapResult.get("metaData"));

        // updated in data base
        validateRolesEdited();
        validatePermissionsNotEdited();
    }

    /**
     * Should return a NOT_FOUND error response
     */
    @Test
    public void deleteWhenNotFound() throws Exception {
        final Map mapResult = integrationTest.getNotFoundResponse(
                MockMvcRequestBuilders.delete(BASE_URL + "/abc"));
        final Map errorResult = (Map) mapResult.get("error");

        assertNotSame(NOT_FOUND_ERROR, errorResult.get("message"));
        assertEquals(NOT_FOUND_ERROR, errorResult.get("message"));
        assertNull(errorResult.get("devMessage"));
        assertNull(errorResult.get("nestedErrors"));

        // not deleted in data base
        assertEquals(roleRepository.count(), 3);
        assertEquals(permissionRepository.count(), 2);
    }

    /**
     * Should return a BAD_REQUEST error response
     */
    @Test
    public void deleteWhenUsed() throws Exception {
        final String ID = dbRoles.get(2).getId();
        final String ERROR_EXPECTED = "There are some people using the Role 'R3'.";

        final Map mapResult = integrationTest.getBadRequestResponse(
                MockMvcRequestBuilders.delete(BASE_URL + "/" + ID));
        final Map errorResult = (Map) mapResult.get("error");

        assertNotSame(ERROR_EXPECTED, errorResult.get("message"));
        assertEquals(ERROR_EXPECTED, errorResult.get("message"));
        assertNull(errorResult.get("devMessage"));
        assertNull(errorResult.get("nestedErrors"));

        // not deleted in data base
        assertEquals(roleRepository.count(), 3);
        assertEquals(permissionRepository.count(), 2);
    }

    /**
     * Should delete a Role
     */
    @Test
    public void deleteWhenSuccess() throws Exception {
        final Role roleExpected = dbRoles.get(1);
        roleExpected.setPermissions(null);
        final String ID = roleExpected.getId();

        final Map mapResult = integrationTest.getOKResponse(
                MockMvcRequestBuilders.delete(BASE_URL + "/" + ID));
        final Role roleResult = IntegrationTest.getRole(mapResult.get("data"));

        assertNotSame(roleExpected, roleResult);
        assertEquals(roleExpected, roleResult);
        assertNull(mapResult.get("metaData"));

        // deleted in data base
        assertEquals(roleRepository.count(), 2);
        assertEquals(permissionRepository.count(), 2);
    }

    /**
     * Should return a NOT_FOUND error response
     */
    @Test
    public void getPeopleWhenNotFound() throws Exception {
        final Map mapResult = integrationTest.getNotFoundResponse(
                MockMvcRequestBuilders.get(BASE_URL + "/abc/people"));
        final Map errorResult = (Map) mapResult.get("error");

        assertNotSame(NOT_FOUND_ERROR, errorResult.get("message"));
        assertEquals(NOT_FOUND_ERROR, errorResult.get("message"));
        assertNull(errorResult.get("devMessage"));
        assertNull(errorResult.get("nestedErrors"));
    }

    /**
     * Should get empty list when no people
     */
    @Test
    public void getPeopleWhenNotPeople() throws Exception {
        final String ID = dbRoles.get(0).getId();
        final List<Person> peopleExpected = Collections.EMPTY_LIST;

        final Map mapResult = integrationTest.getOKResponse(
                MockMvcRequestBuilders.get(BASE_URL + "/" + ID + "/people"));
        final List<Person> peopleResult = IntegrationTest.getPeople(mapResult.get("data"));

        assertNotSame(peopleExpected, peopleResult);
        assertEquals(peopleExpected, peopleResult);
        assertNull(mapResult.get("metaData"));
    }

    /**
     * Should get people list when complete is false and allRelations is false
     */
    @Test
    public void getPeopleWhenNotCompleteAndNotAllRelations() throws Exception {
        final String ID = dbRoles.get(2).getId();
        final List<Person> peopleExpected = Arrays.asList(dbPeople.get(0));
        peopleExpected.forEach(p -> p.setRoles(null));

        final Map mapResult = integrationTest.getOKResponse(
                MockMvcRequestBuilders.get(BASE_URL + "/" + ID + "/people"));
        final List<Person> peopleResult = IntegrationTest.getPeople(mapResult.get("data"));

        assertNotSame(peopleExpected, peopleResult);
        assertEquals(peopleExpected, peopleResult);
        assertNull(mapResult.get("metaData"));
    }

    /**
     * Should get people list when complete is true and allRelations is false
     */
    @Test
    public void getPeopleWhenCompleteAndNotAllRelations() throws Exception {
        final String ID = dbRoles.get(2).getId();
        final List<Person> peopleExpected = Arrays.asList(dbPeople.get(0));
        peopleExpected.forEach(p -> {
            p.getRoles().forEach(r -> r.setPermissions(null));
            // sorted is needed when model is edited to pass isEquals conditions
            p.setRoles(p.getRoles().stream().sorted(Comparator.comparing(Role::getId)).collect(Collectors.toSet()));
        });

        final Map mapResult = integrationTest.getOKResponse(
                MockMvcRequestBuilders.get(BASE_URL + "/" + ID + "/people" + COMPLETE));
        final List<Person> peopleResult = IntegrationTest.getPeople(mapResult.get("data"));

        assertNotSame(peopleExpected, peopleResult);
        assertEquals(peopleExpected, peopleResult);
        assertNull(mapResult.get("metaData"));
    }

    /**
     * Should get people list when complete is false and allRelations is true
     */
    @Test
    public void getPeopleWhenNotCompleteAndAllRelations() throws Exception {
        final String ID = dbRoles.get(2).getId();
        final List<Person> peopleExpected = Arrays.asList(dbPeople.get(0));

        final Map mapResult = integrationTest.getOKResponse(
                MockMvcRequestBuilders.get(BASE_URL + "/" + ID + "/people" + ALL_RELATIONS));
        final List<Person> peopleResult = IntegrationTest.getPeople(mapResult.get("data"));

        assertNotSame(peopleExpected, peopleResult);
        assertEquals(peopleExpected, peopleResult);
        assertNull(mapResult.get("metaData"));
    }

    /**
     * Should get people list when complete is true and allRelations is true
     */
    @Test
    public void getPeopleWhenCompleteAndAllRelations() throws Exception {
        final String ID = dbRoles.get(2).getId();
        final List<Person> peopleExpected = Arrays.asList(dbPeople.get(0));

        final Map mapResult = integrationTest.getOKResponse(
                MockMvcRequestBuilders.get(BASE_URL + "/" + ID + "/people" + BOTH + ""));
        final List<Person> peopleResult = IntegrationTest.getPeople(mapResult.get("data"));

        assertNotSame(peopleExpected, peopleResult);
        assertEquals(peopleExpected, peopleResult);
        assertNull(mapResult.get("metaData"));
    }

    /**
     * Should return an INTERNAL_SERVER_ERROR error response
     */
    @Test
    public void pageWhenInvalidSort() throws Exception {
        final String ERROR_EXPECTED = "An error has occurred.";

        final PageDataRequest dataRequest = new PageDataRequest(-1, -1, "Asc", Arrays.asList("test"), null);
        final String requestJson = mapper.writeValueAsString(dataRequest);

        final Map mapResult = integrationTest.getInternalServerResponse(
                MockMvcRequestBuilders.post(BASE_URL + "/Page").content(requestJson));
        final Map errorResult = (Map) mapResult.get("error");

        assertNotSame(ERROR_EXPECTED, errorResult.get("message"));
        assertEquals(ERROR_EXPECTED, errorResult.get("message"));
        assertNotNull(errorResult.get("devMessage"));
        assertNull(errorResult.get("nestedErrors"));
    }

    /**
     * Should return an INTERNAL_SERVER_ERROR error response
     */
    @Test
    public void pageWhenInvalidFilter() throws Exception {
        final String ERROR_EXPECTED = "An error has occurred.";

        final PageDataRequest dataRequest = new PageDataRequest(-1, -1, "Asc", null, Arrays.asList(new FilterRequest("invalid", "b", "eq")));
        final String requestJson = mapper.writeValueAsString(dataRequest);

        final Map mapResult = integrationTest.getInternalServerResponse(
                MockMvcRequestBuilders.post(BASE_URL + "/Page").content(requestJson));
        final Map errorResult = (Map) mapResult.get("error");

        assertNotSame(ERROR_EXPECTED, errorResult.get("message"));
        assertEquals(ERROR_EXPECTED, errorResult.get("message"));
        assertNotNull(errorResult.get("devMessage"));
        assertNull(errorResult.get("nestedErrors"));
    }

    /**
     * Should get roles page when complete is false and allRelations is false
     */
    @Test
    public void pageWhenNotCompleteAndNotAllRelations() throws Exception {
        final List<Role> rolesExpected = Arrays.asList(dbRoles.get(2));
        rolesExpected.forEach(r -> r.setPermissions(null));

        final PageDataResponse dataResponseExpected = new PageDataResponse(3, 3L, new PageDataRequest(2, 1, "ASC", Arrays.asList("name"), null));
        final PageDataRequest dataRequest = new PageDataRequest(2, -1, "Asc", Arrays.asList("name"), null);
        final String requestJson = mapper.writeValueAsString(dataRequest);

        final Map mapResult = integrationTest.getOKResponse(
                MockMvcRequestBuilders.post(BASE_URL + "/Page").content(requestJson));
        final List<Role> rolesResult = IntegrationTest.getRoles(mapResult.get("data"));
        final PageDataResponse dataResponseResult = IntegrationTest.getPageDataResponse(mapResult.get("metaData"));

        assertNotSame(rolesExpected, rolesResult);
        assertEquals(rolesExpected, rolesResult);
        assertNotSame(dataResponseExpected, dataResponseResult);
        assertEquals(dataResponseExpected, dataResponseResult);
    }

    /**
     * Should get roles page when complete is true and allRelations is false
     */
    @Test
    public void pageWhenCompleteAndNotAllRelations() throws Exception {
        final List<Role> rolesExpected = Arrays.asList(dbRoles.get(2));

        final PageDataResponse dataResponseExpected = new PageDataResponse(3, 3L, new PageDataRequest(2, 1, "ASC", Arrays.asList("name"), null));
        final PageDataRequest dataRequest = new PageDataRequest(2, -1, "Asc", Arrays.asList("name"), null);
        final String requestJson = mapper.writeValueAsString(dataRequest);

        final Map mapResult = integrationTest.getOKResponse(
                MockMvcRequestBuilders.post(BASE_URL + "/Page" + COMPLETE).content(requestJson));
        final List<Role> rolesResult = IntegrationTest.getRoles(mapResult.get("data"));
        final PageDataResponse dataResponseResult = IntegrationTest.getPageDataResponse(mapResult.get("metaData"));

        assertNotSame(rolesExpected, rolesResult);
        assertEquals(rolesExpected, rolesResult);
        assertNotSame(dataResponseExpected, dataResponseResult);
        assertEquals(dataResponseExpected, dataResponseResult);
    }

    /**
     * Should get roles page when complete is false and allRelations is true
     */
    @Test
    public void pageWhenNotCompleteAndAllRelations() throws Exception {
        final List<Role> rolesExpected = Arrays.asList(dbRoles.get(2));

        final PageDataResponse dataResponseExpected = new PageDataResponse(3, 3L, new PageDataRequest(2, 1, "ASC", Arrays.asList("name"), null));
        final PageDataRequest dataRequest = new PageDataRequest(2, -1, "Asc", Arrays.asList("name"), null);
        final String requestJson = mapper.writeValueAsString(dataRequest);

        final Map mapResult = integrationTest.getOKResponse(
                MockMvcRequestBuilders.post(BASE_URL + "/Page" + ALL_RELATIONS + "").content(requestJson));
        final List<Role> rolesResult = IntegrationTest.getRoles(mapResult.get("data"));
        final PageDataResponse dataResponseResult = IntegrationTest.getPageDataResponse(mapResult.get("metaData"));

        assertNotSame(rolesExpected, rolesResult);
        assertEquals(rolesExpected, rolesResult);
        assertNotSame(dataResponseExpected, dataResponseResult);
        assertEquals(dataResponseExpected, dataResponseResult);
    }

    /**
     * Should get roles page when complete is true and allRelations is true
     */
    @Test
    public void pageWhenCompleteAndAllRelations() throws Exception {
        final List<Role> rolesExpected = Arrays.asList(dbRoles.get(2));

        final PageDataResponse dataResponseExpected = new PageDataResponse(3, 3L, new PageDataRequest(2, 1, "ASC", Arrays.asList("name"), null));
        final PageDataRequest dataRequest = new PageDataRequest(2, -1, "Asc", Arrays.asList("name"), null);
        final String requestJson = mapper.writeValueAsString(dataRequest);

        final Map mapResult = integrationTest.getOKResponse(
                MockMvcRequestBuilders.post(BASE_URL + "/Page" + BOTH + " ").content(requestJson));
        final List<Role> rolesResult = IntegrationTest.getRoles(mapResult.get("data"));
        final PageDataResponse dataResponseResult = IntegrationTest.getPageDataResponse(mapResult.get("metaData"));

        assertNotSame(rolesExpected, rolesResult);
        assertEquals(rolesExpected, rolesResult);
        assertNotSame(dataResponseExpected, dataResponseResult);
        assertEquals(dataResponseExpected, dataResponseResult);
    }

    private void validateRolesEdited() {
        final List<Role> newDBRoles = roleRepository.findAll();
        newDBRoles.forEach(r -> {
            r.setPeople(null);
            r.setPermissions(null);
        });
        dbRoles.forEach(r -> r.setPermissions(null));
        assertNotSame(dbRoles, newDBRoles);
        assertNotEquals(dbRoles, newDBRoles);
        assertEquals(roleRepository.count(), 3);
    }

    private void validateRolesNotEdited() {
        final List<Role> newDBRoles = roleRepository.findAll();
        newDBRoles.forEach(r -> {
            r.setPeople(null);
            r.setPermissions(null);
        });
        dbRoles.forEach(r -> r.setPermissions(null));
        assertNotSame(dbRoles, newDBRoles);
        assertEquals(dbRoles, newDBRoles);
        assertEquals(roleRepository.count(), 3);
    }

    private void validatePermissionsNotEdited() {
        final List<Permission> newDBPermissions = permissionRepository.findAll();
        newDBPermissions.forEach(p -> p.setRoles(null));
        assertNotSame(dbPermissions, newDBPermissions);
        assertEquals(dbPermissions, newDBPermissions);
        assertEquals(permissionRepository.count(), 2);
    }
}