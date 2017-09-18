package app.integration_test;

import app.models.*;
import app.pojos.pages.FilterRequest;
import app.pojos.pages.PageDataRequest;
import app.pojos.pages.PageDataResponse;
import app.pojos.responses.error.nesteds.NestedError;
import app.pojos.responses.error.nesteds.ValidationNestedError;
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
public class PersonCtrlIntegrationTest {

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

    private List<Authentication> dbAuthentications;

    private List<Person> dbPeople;

    private List<Role> dbRoles;

    private List<Permission> dbPermissions;

    private final String BASE_URL = "/people";

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
                new Permission("P1", "D1")
        );
        permissionRepository.save(dbPermissions);

        dbRoles = Arrays.asList(
                new Role("R1", "D1", Collections.EMPTY_SET),
                new Role("R2", "D2", new HashSet<>(dbPermissions))
        );
        roleRepository.save(dbRoles);

        dbPeople = Arrays.asList(
                new Person("N1", "LN1", LocalDate.now(), 1, "A", "aa@aa.com", Collections.EMPTY_SET),
                new Person("N2", "LN2", LocalDate.now(), 2, "B", "a@a.com", new HashSet<>(Arrays.asList(dbRoles.get(1)))),
                new Person("N3", "LN3", LocalDate.now(), 3, "C", null, new HashSet<>(dbRoles))
        );
        personRepository.save(dbPeople);

        AuthProvider authProvider = authProviderRepository.save(new AuthProvider("N", "D", "U", "AK", "AS"));
        dbAuthentications = Arrays.asList(
                new Authentication("user", "123", authProvider, dbPeople.get(1))
        );
        authenticationRepository.save(dbAuthentications);
    }

    /**
     * Should get people list when complete is false and allRelations is false
     */
    @Test
    public void listWhenNotCompleteAndNotAllRelations() throws Exception {
        final List<Person> peopleExpected = dbPeople;
        peopleExpected.forEach(p -> p.setRoles(null));

        final Map mapResult = integrationTest.getOKResponse(MockMvcRequestBuilders.get(BASE_URL));
        final List<Person> peopleResult = IntegrationTest.getPeople(mapResult.get("data"));

        assertNotSame(peopleExpected, peopleResult);
        assertEquals(peopleExpected, peopleResult);
        assertNull(mapResult.get("metaData"));
    }

    /**
     * Should get people list when complete is true and allRelations is false
     */
    @Test
    public void listWhenCompleteAndNotAllRelations() throws Exception {
        final List<Person> peopleExpected = dbPeople;
        peopleExpected.forEach(p -> {
            p.getRoles().forEach(r -> r.setPermissions(null));
            // sorted is needed when model is edited to pass isEquals conditions
            p.setRoles(p.getRoles().stream().sorted(Comparator.comparing(Role::getId)).collect(Collectors.toSet()));
        });

        final Map mapResult = integrationTest.getOKResponse(MockMvcRequestBuilders.get(BASE_URL + COMPLETE));
        final List<Person> peopleResult = IntegrationTest.getPeople(mapResult.get("data"));

        assertNotSame(peopleExpected, peopleResult);
        assertEquals(peopleExpected, peopleResult);
        assertNull(mapResult.get("metaData"));
    }

    /**
     * Should get people list when complete is false and allRelations is true
     */
    @Test
    public void listWhenNotCompleteAndAllRelations() throws Exception {
        final List<Person> peopleExpected = dbPeople;

        final Map mapResult = integrationTest.getOKResponse(MockMvcRequestBuilders.get(BASE_URL + ALL_RELATIONS));
        final List<Person> peopleResult = IntegrationTest.getPeople(mapResult.get("data"));

        assertNotSame(peopleExpected, peopleResult);
        assertEquals(peopleExpected, peopleResult);
        assertNull(mapResult.get("metaData"));
    }

    /**
     * Should get people list when complete is true and allRelations is true
     */
    @Test
    public void listWhenCompleteAndAllRelations() throws Exception {
        final List<Person> peopleExpected = dbPeople;

        final Map mapResult = integrationTest.getOKResponse(MockMvcRequestBuilders.get(BASE_URL + BOTH + ""));
        final List<Person> peopleResult = IntegrationTest.getPeople(mapResult.get("data"));

        assertNotSame(peopleExpected, peopleResult);
        assertEquals(peopleExpected, peopleResult);
        assertNull(mapResult.get("metaData"));
    }

    /**
     * Should return a NOT_FOUND error response
     */
    @Test
    public void getWhenNotFound() throws Exception {
        final Map mapResult = integrationTest.getNotFoundResponse(MockMvcRequestBuilders.get(
                BASE_URL + "/ad"));
        final Map errorResult = (Map) mapResult.get("error");

        assertNotSame(NOT_FOUND_ERROR, errorResult.get("message"));
        assertEquals(NOT_FOUND_ERROR, errorResult.get("message"));
        assertNull(errorResult.get("devMessage"));
        assertNull(errorResult.get("nestedErrors"));
    }

    /**
     * Should get a person when complete is false and allRelations is false
     */
    @Test
    public void getWhenNotCompleteAndNotAllRelations() throws Exception {
        final Person personExpected = dbPeople.get(2);
        personExpected.setRoles(null);
        final String ID = personExpected.getId();

        final Map mapResult = integrationTest.getOKResponse(MockMvcRequestBuilders.get(
                BASE_URL + "/" + ID));
        final Person personResult = IntegrationTest.getPerson(mapResult.get("data"));

        assertNotSame(personExpected, personResult);
        assertEquals(personExpected, personResult);
        assertNull(mapResult.get("metaData"));
    }

    /**
     * Should get a person when complete is true and allRelations is false
     */
    @Test
    public void getWhenCompleteAndNotAllRelations() throws Exception {
        final Person personExpected = dbPeople.get(2);
        personExpected.getRoles().forEach(r -> r.setPermissions(null));
        // sorted is needed when model is edited to pass isEquals conditions
        personExpected.setRoles(personExpected.getRoles().stream().sorted(Comparator.comparing(Role::getId)).collect(Collectors.toSet()));
        final String ID = personExpected.getId();

        final Map mapResult = integrationTest.getOKResponse(MockMvcRequestBuilders.get(
                BASE_URL + "/" + ID + COMPLETE));
        final Person personResult = IntegrationTest.getPerson(mapResult.get("data"));

        assertNotSame(personExpected, personResult);
        assertEquals(personExpected, personResult);
        assertNull(mapResult.get("metaData"));
    }

    /**
     * Should get a person when complete is false and allRelations is true
     */
    @Test
    public void getWhenNotCompleteAndAllRelations() throws Exception {
        final Person personExpected = dbPeople.get(2);
        final String ID = personExpected.getId();

        final Map mapResult = integrationTest.getOKResponse(MockMvcRequestBuilders.get(
                BASE_URL + "/" + ID + ALL_RELATIONS));
        final Person personResult = IntegrationTest.getPerson(mapResult.get("data"));

        assertNotSame(personExpected, personResult);
        assertEquals(personExpected, personResult);
        assertNull(mapResult.get("metaData"));
    }

    /**
     * Should get a person when complete is true and allRelations is true
     */
    @Test
    public void getWhenCompleteAndAllRelations() throws Exception {
        final Person personExpected = dbPeople.get(2);
        final String ID = personExpected.getId();

        final Map mapResult = integrationTest.getOKResponse(MockMvcRequestBuilders.get(
                BASE_URL + "/" + ID + BOTH + ""));
        final Person personResult = IntegrationTest.getPerson(mapResult.get("data"));

        assertNotSame(personExpected, personResult);
        assertEquals(personExpected, personResult);
        assertNull(mapResult.get("metaData"));
    }

    /**
     * Should return an BAD_REQUEST error response
     */
    @Test
    public void createWhenInvalidCivilStatusAndSex() throws Exception {
        final String ERROR_EXPECTED = "Some data aren't valid.";
        final List<NestedError> nestedErrorsExpected = Arrays.asList(
                new ValidationNestedError("civilStatus", "'5' is not a valid Civil Status value, it only allows [1, 2]"),
                new ValidationNestedError("sex", "'A' is not a valid Sex value, it only allows [M, F]")
        ).stream().sorted(Comparator.comparing(ValidationNestedError::getField).thenComparing(ValidationNestedError::getMessage)).collect(Collectors.toList());

        final Person p = new Person("new name", "new last name", LocalDate.now(), 5, "A", null, null);
        final String requestJson = mapper.writeValueAsString(p);

        final Map mapResult = integrationTest.getBadRequestResponse(
                MockMvcRequestBuilders.post(BASE_URL).content(requestJson));
        final Map errorResult = (Map) mapResult.get("error");
        final List<LinkedHashMap<String, String>> nestedErrors = (List) errorResult.get("nestedErrors");
        final List<NestedError> nestedErrorsResult = nestedErrors.stream()
                .map(n -> new ValidationNestedError(n.get("field"), n.get("message")))
                .sorted(Comparator.comparing(ValidationNestedError::getField).thenComparing(ValidationNestedError::getMessage))
                .collect(Collectors.toList());

        assertNotSame(ERROR_EXPECTED, errorResult.get("message"));
        assertEquals(ERROR_EXPECTED, errorResult.get("message"));
        assertNull(errorResult.get("devMessage"));
        assertNotSame(nestedErrorsExpected, nestedErrorsResult);
        assertEquals(nestedErrorsExpected, nestedErrorsResult);

        // not inserted in data base
        assertEquals(personRepository.count(), 3);
        assertEquals(roleRepository.count(), 2);
    }

    /**
     * Should return an INTERNAL_SERVER_ERROR error response
     */
    @Test
    public void createWhenInvalidRoleID() throws Exception {
        final String ERROR_EXPECTED = "An error has occurred.";

        final Set<Role> roles = new HashSet<>(Arrays.asList(
                new Role(dbRoles.get(0).getId()),
                new Role("invalid")
        ));
        final Person p = new Person("new name", "new last name", LocalDate.now(), 1, "M", null, roles);
        final String requestJson = mapper.writeValueAsString(p);

        final Map mapResult = integrationTest.getInternalServerResponse(
                MockMvcRequestBuilders.post(BASE_URL).content(requestJson));
        final Map errorResult = (Map) mapResult.get("error");

        assertNotSame(ERROR_EXPECTED, errorResult.get("message"));
        assertEquals(ERROR_EXPECTED, errorResult.get("message"));
        assertNotNull(errorResult.get("devMessage"));
        assertNull(errorResult.get("nestedErrors"));

        // not inserted in data base
        assertEquals(personRepository.count(), 3);
        assertEquals(roleRepository.count(), 2);
    }

    /**
     * Should return an INTERNAL_SERVER_ERROR error response
     */
    @Test
    public void createWhenNotRoleID() throws Exception {
        final String ERROR_EXPECTED = "An error has occurred.";

        final Set<Role> roles = new HashSet<>(Arrays.asList(
                new Role(dbRoles.get(0).getId()),
                new Role("R33", "D33", null)
        ));
        final Person p = new Person("new name", "new last name", LocalDate.now(), 1, "M", null, roles);
        final String requestJson = mapper.writeValueAsString(p);

        final Map mapResult = integrationTest.getInternalServerResponse(
                MockMvcRequestBuilders.post(BASE_URL).content(requestJson));
        final Map errorResult = (Map) mapResult.get("error");

        assertNotSame(ERROR_EXPECTED, errorResult.get("message"));
        assertEquals(ERROR_EXPECTED, errorResult.get("message"));
        assertNotNull(errorResult.get("devMessage"));
        assertNull(errorResult.get("nestedErrors"));

        // not inserted in data base
        assertEquals(personRepository.count(), 3);
        assertEquals(roleRepository.count(), 2);
    }

    /**
     * Should create a new Person
     */
    @Test
    public void createWhenSuccessNullRoles() throws Exception {
        final LocalDate birthday = LocalDate.now();
        final Person personExpected = new Person("new name", "new last name", birthday, 1, "M", null, null);
        final Person p = new Person("new name", "new last name", birthday, 1, "M", null, null);
        final String requestJson = mapper.writeValueAsString(p);

        final Map mapResult = integrationTest.getOKResponse(
                MockMvcRequestBuilders.post(BASE_URL).content(requestJson));
        final Person personResult = IntegrationTest.getPerson(mapResult.get("data"));

        personExpected.setId(personResult.getId());

        assertNotSame(personExpected, personResult);
        assertEquals(personExpected, personResult);
        assertNull(mapResult.get("metaData"));

        // inserted in data base
        assertEquals(personRepository.count(), 4);
        assertEquals(roleRepository.count(), 2);
    }

    /**
     * Should create a new Person
     */
    @Test
    public void createWhenSuccessEmptyRoles() throws Exception {
        final LocalDate birthday = LocalDate.now();
        final Person personExpected = new Person("new name", "new last name", birthday, 1, "M", "a@a.com", Collections.EMPTY_SET);
        final Person p = new Person("new name", "new last name", birthday, 1, "M", "a@a.com", Collections.EMPTY_SET);
        final String requestJson = mapper.writeValueAsString(p);

        final Map mapResult = integrationTest.getOKResponse(
                MockMvcRequestBuilders.post(BASE_URL).content(requestJson));
        final Person personResult = IntegrationTest.getPerson(mapResult.get("data"));

        personExpected.setId(personResult.getId());

        assertNotSame(personExpected, personResult);
        assertEquals(personExpected, personResult);
        assertNull(mapResult.get("metaData"));

        // inserted in data base
        assertEquals(personRepository.count(), 4);
        assertEquals(roleRepository.count(), 2);
    }

    /**
     * Should create a new Person
     */
    @Test
    public void createWhenSuccess() throws Exception {
        final LocalDate birthday = LocalDate.now();
        final String duplicateID = dbRoles.get(0).getId();
        final String fakeID = dbRoles.get(1).getId();
        final Role roleFake = new Role("fake", "fake2", null); // this fake info will be returned but not saved
        roleFake.setId(fakeID);

        final Set<Role> rolesExpected = new HashSet<>(Arrays.asList(
                new Role(duplicateID),
                roleFake
        ));
        final Person personExpected = new Person("new name", "new last name", birthday, 1, "M", "a@a.com", rolesExpected);

        final Set<Role> roles = new HashSet<>(Arrays.asList(
                new Role(duplicateID),
                roleFake,
                new Role(duplicateID) // this duplicate ID will be ignored
        ));
        final Person p = new Person("new name", "new last name", birthday, 1, "M", "a@a.com", roles);
        final String requestJson = mapper.writeValueAsString(p);

        final Map mapResult = integrationTest.getOKResponse(
                MockMvcRequestBuilders.post(BASE_URL).content(requestJson));
        final Person personResult = IntegrationTest.getPerson(mapResult.get("data"));

        personExpected.setId(personResult.getId());

        assertNotSame(personExpected, personResult);
        assertEquals(personExpected, personResult);
        assertNull(mapResult.get("metaData"));

        // inserted in data base
        assertEquals(personRepository.count(), 4);
        assertEquals(roleRepository.count(), 2);

        // roles not edited
        validateRolesNotEdited();
    }

    /**
     * Should return an BAD_REQUEST error response
     */
    @Test
    public void editWhenInvalidCivilStatusAndSex() throws Exception {
        final LocalDate birthday = LocalDate.now();
        final String ERROR_EXPECTED = "Some data aren't valid.";
        final List<NestedError> nestedErrorsExpected = Arrays.asList(
                new ValidationNestedError("civilStatus", "'5' is not a valid Civil Status value, it only allows [1, 2]"),
                new ValidationNestedError("sex", "'A' is not a valid Sex value, it only allows [M, F]")
        ).stream().sorted(Comparator.comparing(ValidationNestedError::getField).thenComparing(ValidationNestedError::getMessage)).collect(Collectors.toList());

        final Person p = new Person("new name", "new last name", birthday, 5, "A", null, null);
        p.setId("old");
        final String requestJson = mapper.writeValueAsString(p);

        final Map mapResult = integrationTest.getBadRequestResponse(
                MockMvcRequestBuilders.put(BASE_URL + "/abc").content(requestJson));
        final Map errorResult = (Map) mapResult.get("error");
        final List<LinkedHashMap<String, String>> nestedErrors = (List) errorResult.get("nestedErrors");
        final List<NestedError> nestedErrorsResult = nestedErrors.stream()
                .map(n -> new ValidationNestedError(n.get("field"), n.get("message")))
                .sorted(Comparator.comparing(ValidationNestedError::getField).thenComparing(ValidationNestedError::getMessage))
                .collect(Collectors.toList());

        assertNotSame(ERROR_EXPECTED, errorResult.get("message"));
        assertEquals(ERROR_EXPECTED, errorResult.get("message"));
        assertNull(errorResult.get("devMessage"));
        assertNotSame(nestedErrorsExpected, nestedErrorsResult);
        assertEquals(nestedErrorsExpected, nestedErrorsResult);

        // not updated in data base
        validatePeopleNotEdited();
        validateRolesNotEdited();
    }

    /**
     * Should return a NOT_FOUND error response
     */
    @Test
    public void editWhenNotFound() throws Exception {
        final LocalDate birthday = LocalDate.now();
        final Person p = new Person("new name", "new last name", birthday, 1, "M", null, null);
        p.setId("old");
        final String requestJson = mapper.writeValueAsString(p);

        final Map mapResult = integrationTest.getNotFoundResponse(
                MockMvcRequestBuilders.put(BASE_URL + "/abc").content(requestJson));
        final Map errorResult = (Map) mapResult.get("error");

        assertNotSame(NOT_FOUND_ERROR, errorResult.get("message"));
        assertEquals(NOT_FOUND_ERROR, errorResult.get("message"));
        assertNull(errorResult.get("devMessage"));
        assertNull(errorResult.get("nestedErrors"));

        // not updated in data base
        validatePeopleNotEdited();
        validateRolesNotEdited();
    }

    /**
     * Should return an INTERNAL_SERVER_ERROR error response
     */
    @Test
    public void editWhenInvalidRoleID() throws Exception {
        final LocalDate birthday = LocalDate.now();
        final String ID = dbPeople.get(0).getId();
        final String ERROR_EXPECTED = "An error has occurred.";

        final Set<Role> roles = new HashSet<>(Arrays.asList(
                new Role(dbRoles.get(0).getId()),
                new Role("invalid")
        ));
        final Person p = new Person("new name", "new last name", birthday, 1, "M", null, roles);
        p.setId("old");
        final String requestJson = mapper.writeValueAsString(p);

        final Map mapResult = integrationTest.getInternalServerResponse(
                MockMvcRequestBuilders.put(BASE_URL + "/" + ID).content(requestJson));
        final Map errorResult = (Map) mapResult.get("error");

        assertNotSame(ERROR_EXPECTED, errorResult.get("message"));
        assertEquals(ERROR_EXPECTED, errorResult.get("message"));
        assertNotNull(errorResult.get("devMessage"));
        assertNull(errorResult.get("nestedErrors"));

        // not updated in data base
        validatePeopleNotEdited();
        validateRolesNotEdited();
    }

    /**
     * Should return an INTERNAL_SERVER_ERROR error response
     */
    @Test
    public void editWhenNotRoleID() throws Exception {
        final LocalDate birthday = LocalDate.now();
        final String ID = dbPeople.get(0).getId();
        final String ERROR_EXPECTED = "An error has occurred.";

        final Set<Role> roles = new HashSet<>(Arrays.asList(
                new Role(dbRoles.get(0).getId()),
                new Role("RRR", "DDDD", null)
        ));
        final Person p = new Person("new name", "new last name", birthday, 1, "M", null, roles);
        p.setId("old");
        final String requestJson = mapper.writeValueAsString(p);

        final Map mapResult = integrationTest.getInternalServerResponse(
                MockMvcRequestBuilders.put(BASE_URL + "/" + ID).content(requestJson));
        final Map errorResult = (Map) mapResult.get("error");

        assertNotSame(ERROR_EXPECTED, errorResult.get("message"));
        assertEquals(ERROR_EXPECTED, errorResult.get("message"));
        assertNotNull(errorResult.get("devMessage"));
        assertNull(errorResult.get("nestedErrors"));

        // not updated in data base
        validatePeopleNotEdited();
        validateRolesNotEdited();
    }

    /**
     * Should update a Person
     */
    @Test
    public void editWhenSuccessNullRoles() throws Exception {
        final LocalDate birthday = LocalDate.now();
        final String ID = dbPeople.get(2).getId();
        final Person personExpected = new Person("new name", "new last name", birthday, 2, "M", "new@new.com", null);
        personExpected.setId(ID);
        final Person p = new Person("new name", "new last name", birthday, 2, "M", "new@new.com", null);
        p.setId("old");
        final String requestJson = mapper.writeValueAsString(p);

        final Map mapResult = integrationTest.getOKResponse(
                MockMvcRequestBuilders.put(BASE_URL + "/" + ID).content(requestJson));
        final Person personResult = IntegrationTest.getPerson(mapResult.get("data"));

        assertNotSame(personExpected, personResult);
        assertEquals(personExpected, personResult);
        assertNull(mapResult.get("metaData"));

        // updated in data base
        validatePeopleEdited();
        validateRolesNotEdited();
    }

    /**
     * Should update a Person
     */
    @Test
    public void editWhenSuccessEmptyRoles() throws Exception {
        final LocalDate birthday = LocalDate.now();
        final String ID = dbPeople.get(0).getId();
        final Person personExpected = new Person("new name", "new last name", birthday, 2, "M", null, Collections.EMPTY_SET);
        personExpected.setId(ID);
        final Person p = new Person("new name", "new last name", birthday, 2, "M", null, Collections.EMPTY_SET);
        p.setId("old");
        final String requestJson = mapper.writeValueAsString(p);

        final Map mapResult = integrationTest.getOKResponse(
                MockMvcRequestBuilders.put(BASE_URL + "/" + ID).content(requestJson));
        final Person personResult = IntegrationTest.getPerson(mapResult.get("data"));

        assertNotSame(personExpected, personResult);
        assertEquals(personExpected, personResult);
        assertNull(mapResult.get("metaData"));

        // updated in data base
        validatePeopleEdited();
        validateRolesNotEdited();
    }

    /**
     * Should update a Person
     */
    @Test
    public void editWhenSuccess() throws Exception {
        final LocalDate birthday = LocalDate.now();
        final String ID = dbPeople.get(1).getId();
        final String duplicateID = dbRoles.get(0).getId();
        final String fakeID = dbRoles.get(1).getId();
        final Role roleFake = new Role("fake", "fake2", null); // this fake info will be returned but not saved
        roleFake.setId(fakeID);

        final Set<Role> rolesExpected = new HashSet<>(Arrays.asList(dbRoles.get(0), dbRoles.get(1)));
        final Person personExpected = new Person("new name", "new last name", birthday, 1, "M", "b@b.com", rolesExpected);
        personExpected.setId(ID);

        final Set<Role> roles = new HashSet<>(Arrays.asList(
                new Role(duplicateID),
                roleFake,
                new Role(duplicateID) // this duplicate ID will be ignored
        ));
        final Person p = new Person("new name", "new last name", birthday, 1, "M", "b@b.com", roles);
        p.setId("old");
        final String requestJson = mapper.writeValueAsString(p);

        final Map mapResult = integrationTest.getOKResponse(
                MockMvcRequestBuilders.put(BASE_URL + "/" + ID).content(requestJson));
        final Person personResult = IntegrationTest.getPerson(mapResult.get("data"));

        assertNotSame(personExpected, personResult);
        assertEquals(personExpected, personResult);
        assertNull(mapResult.get("metaData"));

        // updated in data base
        validatePeopleEdited();
        validateRolesNotEdited();
    }

    /**
     * Should return a NOT_FOUND error response
     */
    @Test
    public void deleteWhenNotFound() throws Exception {
        final Map mapResult = integrationTest.getNotFoundResponse(
                MockMvcRequestBuilders.delete(BASE_URL + "/abcd"));
        final Map errorResult = (Map) mapResult.get("error");

        assertNotSame(NOT_FOUND_ERROR, errorResult.get("message"));
        assertEquals(NOT_FOUND_ERROR, errorResult.get("message"));
        assertNull(errorResult.get("devMessage"));
        assertNull(errorResult.get("nestedErrors"));

        // not deleted in data base
        assertEquals(personRepository.count(), 3);
        assertEquals(roleRepository.count(), 2);
    }

    /**
     * Should return a BAD_REQUEST error response
     */
    @Test
    public void deleteWhenUsed() throws Exception {
        final String ID = dbPeople.get(1).getId();
        final String ERROR_EXPECTED = "Person 'N2 LN2' has one or more authentications associated.";

        final Map mapResult = integrationTest.getBadRequestResponse(
                MockMvcRequestBuilders.delete(BASE_URL + "/" + ID));
        final Map errorResult = (Map) mapResult.get("error");

        assertNotSame(ERROR_EXPECTED, errorResult.get("message"));
        assertEquals(ERROR_EXPECTED, errorResult.get("message"));
        assertNull(errorResult.get("devMessage"));
        assertNull(errorResult.get("nestedErrors"));

        // not deleted in data base
        assertEquals(personRepository.count(), 3);
        assertEquals(roleRepository.count(), 2);
    }

    /**
     * Should delete a Person
     */
    @Test
    public void deleteWhenSuccess() throws Exception {
        final Person personExpected = dbPeople.get(2);
        personExpected.setRoles(null);
        final String ID = personExpected.getId();

        final Map mapResult = integrationTest.getOKResponse(
                MockMvcRequestBuilders.delete(BASE_URL + "/" + ID));
        final Person personResult = IntegrationTest.getPerson(mapResult.get("data"));

        assertNotSame(personExpected, personResult);
        assertEquals(personExpected, personResult);
        assertNull(mapResult.get("metaData"));

        // deleted in data base
        assertEquals(personRepository.count(), 2);
        assertEquals(roleRepository.count(), 2);
    }

    /**
     * Should return a NOT_FOUND error response
     */
    @Test
    public void getAuthenticationsWhenNotFound() throws Exception {
        final Map mapResult = integrationTest.getNotFoundResponse(
                MockMvcRequestBuilders.get(BASE_URL + "/abc/authentications"));
        final Map errorResult = (Map) mapResult.get("error");

        assertNotSame(NOT_FOUND_ERROR, errorResult.get("message"));
        assertEquals(NOT_FOUND_ERROR, errorResult.get("message"));
        assertNull(errorResult.get("devMessage"));
        assertNull(errorResult.get("nestedErrors"));
    }

    /**
     * Should get empty list when no authentications
     */
    @Test
    public void getAuthenticationsWhenNotAuthentications() throws Exception {
        final String ID = dbPeople.get(0).getId();
        final List<Authentication> authenticationsExpected = Collections.EMPTY_LIST;

        final Map mapResult = integrationTest.getOKResponse(
                MockMvcRequestBuilders.get(BASE_URL + "/" + ID + "/authentications"));
        final List<Authentication> authenticationsResult = IntegrationTest.getAuthentications(mapResult.get("data"));

        assertNotSame(authenticationsExpected, authenticationsResult);
        assertEquals(authenticationsExpected, authenticationsResult);
        assertNull(mapResult.get("metaData"));
    }

    /**
     * Should get authentications list when complete is false and allRelations is false
     */
    @Test
    public void getAuthenticationsWhenNotCompleteAndNotAllRelations() throws Exception {
        final String ID = dbPeople.get(1).getId();
        final List<Authentication> authenticationsExpected = Arrays.asList(dbAuthentications.get(0));
        authenticationsExpected.forEach(a -> {
            a.setPassword(null);

            a.getAuthProvider().setUrl(null);
            a.getAuthProvider().setAuthKey(null);
            a.getAuthProvider().setAuthSecret(null);

            a.getPerson().setRoles(null);
        });

        final Map mapResult = integrationTest.getOKResponse(
                MockMvcRequestBuilders.get(BASE_URL + "/" + ID + "/authentications"));
        final List<Authentication> authenticationsResult = IntegrationTest.getAuthentications(mapResult.get("data"));

        assertNotSame(authenticationsExpected, authenticationsResult);
        assertEquals(authenticationsExpected, authenticationsResult);
        assertNull(mapResult.get("metaData"));
    }

    /**
     * Should get authentications list when complete is true and allRelations is false
     */
    @Test
    public void getAuthenticationsWhenCompleteAndNotAllRelations() throws Exception {
        final String ID = dbPeople.get(1).getId();
        final List<Authentication> authenticationsExpected = Arrays.asList(dbAuthentications.get(0));
        authenticationsExpected.forEach(a -> {
            a.setPassword(null);

            a.getAuthProvider().setUrl(null);
            a.getAuthProvider().setAuthKey(null);
            a.getAuthProvider().setAuthSecret(null);

            a.getPerson().getRoles().forEach(r -> r.setPermissions(null));
            // sorted is needed when model is edited to pass isEquals conditions
            a.getPerson().setRoles(a.getPerson().getRoles().stream().sorted(Comparator.comparing(Role::getId)).collect(Collectors.toSet()));
        });

        final Map mapResult = integrationTest.getOKResponse(
                MockMvcRequestBuilders.get(BASE_URL + "/" + ID + "/authentications" + COMPLETE));
        final List<Authentication> authenticationsResult = IntegrationTest.getAuthentications(mapResult.get("data"));

        assertNotSame(authenticationsExpected, authenticationsResult);
        assertEquals(authenticationsExpected, authenticationsResult);
        assertNull(mapResult.get("metaData"));
    }

    /**
     * Should get authentications list when complete is false and allRelations is true
     */
    @Test
    public void getAuthenticationsWhenNotCompleteAndAllRelations() throws Exception {
        final String ID = dbPeople.get(1).getId();
        final List<Authentication> authenticationsExpected = Arrays.asList(dbAuthentications.get(0));
        authenticationsExpected.forEach(a -> {
            a.setPassword(null);

            a.getAuthProvider().setUrl(null);
            a.getAuthProvider().setAuthKey(null);
            a.getAuthProvider().setAuthSecret(null);
        });

        final Map mapResult = integrationTest.getOKResponse(
                MockMvcRequestBuilders.get(BASE_URL + "/" + ID + "/authentications" + ALL_RELATIONS));
        final List<Authentication> authenticationsResult = IntegrationTest.getAuthentications(mapResult.get("data"));

        assertNotSame(authenticationsExpected, authenticationsResult);
        assertEquals(authenticationsExpected, authenticationsResult);
        assertNull(mapResult.get("metaData"));
    }

    /**
     * Should get authentications list when complete is true and allRelations is true
     */
    @Test
    public void getAuthenticationsWhenCompleteAndAllRelations() throws Exception {
        final String ID = dbPeople.get(1).getId();
        final List<Authentication> authenticationsExpected = Arrays.asList(dbAuthentications.get(0));
        authenticationsExpected.forEach(a -> {
            a.setPassword(null);

            final String value = null;
            a.getAuthProvider().setUrl(value);
            a.getAuthProvider().setAuthKey(value);
            a.getAuthProvider().setAuthSecret(value);
        });

        final Map mapResult = integrationTest.getOKResponse(
                MockMvcRequestBuilders.get(BASE_URL + "/" + ID + "/authentications" + BOTH + ""));
        final List<Authentication> authenticationsResult = IntegrationTest.getAuthentications(mapResult.get("data"));

        assertNotSame(authenticationsExpected, authenticationsResult);
        assertEquals(authenticationsExpected, authenticationsResult);
        assertNull(mapResult.get("metaData"));
    }

    /**
     * Should return an INTERNAL_SERVER_ERROR error response
     */
    @Test
    public void pageWhenInvalidSort() throws Exception {
        final String ERROR_EXPECTED = "An error has occurred.";

        final PageDataRequest dataRequest = new PageDataRequest(-1, -1, "Asc", Arrays.asList("test2"), null);
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

        final PageDataRequest dataRequest = new PageDataRequest(-1, -1, "Asc", null, Arrays.asList(new FilterRequest("invalid", "c", "eq")));
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
     * Should get people page when complete is false and allRelations is false
     */
    @Test
    public void pageWhenNotCompleteAndNotAllRelations() throws Exception {
        final List<Person> peopleExpected = Arrays.asList(dbPeople.get(1));
        peopleExpected.forEach(p -> p.setRoles(null));

        final PageDataResponse dataResponseExpected = new PageDataResponse(3, 3L, new PageDataRequest(1, 1, "ASC", Arrays.asList("name"), null));
        final PageDataRequest dataRequest = new PageDataRequest(1, -1, "Asc", Arrays.asList("name"), null);
        final String requestJson = mapper.writeValueAsString(dataRequest);

        final Map mapResult = integrationTest.getOKResponse(
                MockMvcRequestBuilders.post(BASE_URL + "/Page").content(requestJson));
        final List<Person> peopleResult = IntegrationTest.getPeople(mapResult.get("data"));
        final PageDataResponse dataResponseResult = IntegrationTest.getPageDataResponse(mapResult.get("metaData"));

        assertNotSame(peopleExpected, peopleResult);
        assertEquals(peopleExpected, peopleResult);
        assertNotSame(dataResponseExpected, dataResponseResult);
        assertEquals(dataResponseExpected, dataResponseResult);
    }

    /**
     * Should get people page when complete is true and allRelations is false
     */
    @Test
    public void pageWhenCompleteAndNotAllRelations() throws Exception {
        final List<Person> peopleExpected = Arrays.asList(dbPeople.get(1));
        peopleExpected.forEach(p -> {
            p.getRoles().forEach(r -> r.setPermissions(null));
            // sorted is needed when model is edited to pass isEquals conditions
            p.setRoles(p.getRoles().stream().sorted(Comparator.comparing(Role::getId)).collect(Collectors.toSet()));
        });

        final PageDataResponse dataResponseExpected = new PageDataResponse(3, 3L, new PageDataRequest(1, 1, "ASC", Arrays.asList("name"), null));
        final PageDataRequest dataRequest = new PageDataRequest(1, -1, "Asc", Arrays.asList("name"), null);
        final String requestJson = mapper.writeValueAsString(dataRequest);

        final Map mapResult = integrationTest.getOKResponse(
                MockMvcRequestBuilders.post(BASE_URL + "/Page" + COMPLETE).content(requestJson));
        final List<Person> peopleResult = IntegrationTest.getPeople(mapResult.get("data"));
        final PageDataResponse dataResponseResult = IntegrationTest.getPageDataResponse(mapResult.get("metaData"));

        assertNotSame(peopleExpected, peopleResult);
        assertEquals(peopleExpected, peopleResult);
        assertNotSame(dataResponseExpected, dataResponseResult);
        assertEquals(dataResponseExpected, dataResponseResult);
    }

    /**
     * Should get people page when complete is false and allRelations is true
     */
    @Test
    public void pageWhenNotCompleteAndAllRelations() throws Exception {
        final List<Person> peopleExpected = Arrays.asList(dbPeople.get(1));

        final PageDataResponse dataResponseExpected = new PageDataResponse(3, 3L, new PageDataRequest(1, 1, "ASC", Arrays.asList("name"), null));
        final PageDataRequest dataRequest = new PageDataRequest(1, -1, "Asc", Arrays.asList("name"), null);
        final String requestJson = mapper.writeValueAsString(dataRequest);

        final Map mapResult = integrationTest.getOKResponse(
                MockMvcRequestBuilders.post(BASE_URL + "/Page" + ALL_RELATIONS).content(requestJson));
        final List<Person> peopleResult = IntegrationTest.getPeople(mapResult.get("data"));
        final PageDataResponse dataResponseResult = IntegrationTest.getPageDataResponse(mapResult.get("metaData"));

        assertNotSame(peopleExpected, peopleResult);
        assertEquals(peopleExpected, peopleResult);
        assertNotSame(dataResponseExpected, dataResponseResult);
        assertEquals(dataResponseExpected, dataResponseResult);
    }

    /**
     * Should get people page when complete is true and allRelations is true
     */
    @Test
    public void pageWhenCompleteAndAllRelations() throws Exception {
        final List<Person> peopleExpected = Arrays.asList(dbPeople.get(1));

        final PageDataResponse dataResponseExpected = new PageDataResponse(3, 3L, new PageDataRequest(1, 1, "ASC", Arrays.asList("name"), null));
        final PageDataRequest dataRequest = new PageDataRequest(1, -1, "Asc", Arrays.asList("name"), null);
        final String requestJson = mapper.writeValueAsString(dataRequest);

        final Map mapResult = integrationTest.getOKResponse(
                MockMvcRequestBuilders.post(BASE_URL + "/Page" + BOTH + "").content(requestJson));
        final List<Person> peopleResult = IntegrationTest.getPeople(mapResult.get("data"));
        final PageDataResponse dataResponseResult = IntegrationTest.getPageDataResponse(mapResult.get("metaData"));

        assertNotSame(peopleExpected, peopleResult);
        assertEquals(peopleExpected, peopleResult);
        assertNotSame(dataResponseExpected, dataResponseResult);
        assertEquals(dataResponseExpected, dataResponseResult);
    }

    private void validatePeopleEdited() {
        final List<Person> newDBPeople = personRepository.findAll();
        newDBPeople.forEach(p -> {
            p.setAuthentications(null);
            p.setRoles(null);
        });
        dbPeople.forEach(p -> p.setRoles(null));
        assertNotSame(dbPeople, newDBPeople);
        assertNotEquals(dbPeople, newDBPeople);
        assertEquals(personRepository.count(), 3);
    }

    private void validatePeopleNotEdited() {
        final List<Person> newDBPeople = personRepository.findAll();
        newDBPeople.forEach(p -> {
            p.setAuthentications(null);
            p.setRoles(null);
        });
        dbPeople.forEach(p -> p.setRoles(null));
        assertNotSame(dbPeople, newDBPeople);
        assertEquals(dbPeople, newDBPeople);
        assertEquals(personRepository.count(), 3);
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
        assertEquals(roleRepository.count(), 2);
    }
}