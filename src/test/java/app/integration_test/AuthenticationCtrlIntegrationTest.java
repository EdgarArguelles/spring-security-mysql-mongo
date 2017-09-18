package app.integration_test;

import app.models.*;
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
public class AuthenticationCtrlIntegrationTest {

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

    private List<AuthProvider> dbAuthProviders;

    private List<Person> dbPeople;

    private final String BASE_URL = "/authentications";

    private final String COMPLETE = "?complete=true";

    private final String ALL_RELATIONS = "?all_relations=true";

    private final String BOTH = "?complete=true&all_relations=true";

    private final String NOT_FOUND_ERROR = "Data don't found.";

    private IntegrationTest integrationTest;

    @Before
    public void setup() throws Exception {
        integrationTest = new IntegrationTest(mvc, mapper, tokenService);
        IntegrationTest.cleanAllData(authenticationRepository, authProviderRepository, personRepository, roleRepository, permissionRepository);

        final List<Permission> dbPermissions = Arrays.asList(
                new Permission("P1", "D1"),
                new Permission("P2", "D2")
        );
        permissionRepository.save(dbPermissions);

        final List<Role> dbRoles = Arrays.asList(
                new Role("R1", "D1", new HashSet<>(dbPermissions)),
                new Role("R2", "D2", new HashSet<>(Arrays.asList(dbPermissions.get(0))))
        );
        roleRepository.save(dbRoles);

        dbPeople = Arrays.asList(
                new Person("N1", "LN1", LocalDate.now(), 1, "A", null, new HashSet<>(dbRoles)),
                new Person("N2", "LN2", LocalDate.now(), 2, "B", "a@a.com", new HashSet<>(Arrays.asList(dbRoles.get(0))))
        );
        personRepository.save(dbPeople);

        dbAuthProviders = Arrays.asList(
                new AuthProvider("N1", "D1", "U1", "AK1", "AS1"),
                new AuthProvider("N2", "D2", "U2", "AK2", "AS2"),
                new AuthProvider("N3", "D3", "U3", "AK3", "AS3")
        );
        authProviderRepository.save(dbAuthProviders);

        dbAuthentications = Arrays.asList(
                new Authentication("user1", "123", dbAuthProviders.get(0), dbPeople.get(0)),
                new Authentication(null, "123", dbAuthProviders.get(0), dbPeople.get(1)),
                new Authentication("user2", "123", dbAuthProviders.get(1), dbPeople.get(1))
        );
        authenticationRepository.save(dbAuthentications);
    }

    /**
     * Should get authentications list when complete is false and allRelations is false
     */
    @Test
    public void listWhenNotCompleteAndNotAllRelations() throws Exception {
        final List<Authentication> authenticationsExpected = dbAuthentications;
        cleanAuthentications(authenticationsExpected, true, false);

        final Map mapResult = integrationTest.getOKResponse(MockMvcRequestBuilders.get(BASE_URL));
        final List<Authentication> authenticationsResult = IntegrationTest.getAuthentications(mapResult.get("data"));

        assertNotSame(authenticationsExpected, authenticationsResult);
        assertEquals(authenticationsExpected, authenticationsResult);
        assertNull(mapResult.get("metaData"));
    }

    /**
     * Should get authentications list when complete is true and allRelations is false
     */
    @Test
    public void listWhenCompleteAndNotAllRelations() throws Exception {
        final List<Authentication> authenticationsExpected = dbAuthentications;
        cleanAuthentications(authenticationsExpected, false, true);

        final Map mapResult = integrationTest.getOKResponse(MockMvcRequestBuilders.get(BASE_URL + COMPLETE));
        final List<Authentication> authenticationsResult = IntegrationTest.getAuthentications(mapResult.get("data"));

        assertNotSame(authenticationsExpected, authenticationsResult);
        assertEquals(authenticationsExpected, authenticationsResult);
        assertNull(mapResult.get("metaData"));
    }

    /**
     * Should get authentications list when complete is false and allRelations is true
     */
    @Test
    public void listWhenNotCompleteAndAllRelations() throws Exception {
        final List<Authentication> authenticationsExpected = dbAuthentications;
        cleanAuthentications(authenticationsExpected, false, false);

        final Map mapResult = integrationTest.getOKResponse(MockMvcRequestBuilders.get(BASE_URL + ALL_RELATIONS));
        final List<Authentication> authenticationsResult = IntegrationTest.getAuthentications(mapResult.get("data"));

        assertNotSame(authenticationsExpected, authenticationsResult);
        assertEquals(authenticationsExpected, authenticationsResult);
        assertNull(mapResult.get("metaData"));
    }

    /**
     * Should get authentications list when complete is true and allRelations is true
     */
    @Test
    public void listWhenCompleteAndAllRelations() throws Exception {
        final List<Authentication> authenticationsExpected = dbAuthentications;
        cleanAuthentications(authenticationsExpected, false, false);

        final Map mapResult = integrationTest.getOKResponse(MockMvcRequestBuilders.get(BASE_URL + BOTH + ""));
        final List<Authentication> authenticationsResult = IntegrationTest.getAuthentications(mapResult.get("data"));

        assertNotSame(authenticationsExpected, authenticationsResult);
        assertEquals(authenticationsExpected, authenticationsResult);
        assertNull(mapResult.get("metaData"));
    }

    /**
     * Should return a NOT_FOUND error response
     */
    @Test
    public void getWhenNotFound() throws Exception {
        final Map mapResult = integrationTest.getNotFoundResponse(MockMvcRequestBuilders.get(
                BASE_URL + "/atd"));
        final Map errorResult = (Map) mapResult.get("error");

        assertNotSame(NOT_FOUND_ERROR, errorResult.get("message"));
        assertEquals(NOT_FOUND_ERROR, errorResult.get("message"));
        assertNull(errorResult.get("devMessage"));
        assertNull(errorResult.get("nestedErrors"));
    }

    /**
     * Should get an authentication when complete is false and allRelations is false
     */
    @Test
    public void getWhenNotCompleteAndNotAllRelations() throws Exception {
        final Authentication authenticationExpected = dbAuthentications.get(0);
        cleanAuthentication(authenticationExpected, true, false);
        final String ID = authenticationExpected.getId();

        final Map mapResult = integrationTest.getOKResponse(MockMvcRequestBuilders.get(
                BASE_URL + "/" + ID));
        final Authentication authenticationResult = IntegrationTest.getAuthentication(mapResult.get("data"));

        assertNotSame(authenticationExpected, authenticationResult);
        assertEquals(authenticationExpected, authenticationResult);
        assertNull(mapResult.get("metaData"));
    }

    /**
     * Should get an authentication when complete is true and allRelations is false
     */
    @Test
    public void getWhenCompleteAndNotAllRelations() throws Exception {
        final Authentication authenticationExpected = dbAuthentications.get(0);
        cleanAuthentication(authenticationExpected, false, true);
        final String ID = authenticationExpected.getId();

        final Map mapResult = integrationTest.getOKResponse(MockMvcRequestBuilders.get(
                BASE_URL + "/" + ID + COMPLETE));
        final Authentication authenticationResult = IntegrationTest.getAuthentication(mapResult.get("data"));

        assertNotSame(authenticationExpected, authenticationResult);
        assertEquals(authenticationExpected, authenticationResult);
        assertNull(mapResult.get("metaData"));
    }

    /**
     * Should get an authentication when complete is false and allRelations is true
     */
    @Test
    public void getWhenNotCompleteAndAllRelations() throws Exception {
        final Authentication authenticationExpected = dbAuthentications.get(0);
        cleanAuthentication(authenticationExpected, false, false);
        final String ID = authenticationExpected.getId();

        final Map mapResult = integrationTest.getOKResponse(MockMvcRequestBuilders.get(
                BASE_URL + "/" + ID + ALL_RELATIONS));
        final Authentication authenticationResult = IntegrationTest.getAuthentication(mapResult.get("data"));

        assertNotSame(authenticationExpected, authenticationResult);
        assertEquals(authenticationExpected, authenticationResult);
        assertNull(mapResult.get("metaData"));
    }

    /**
     * Should get an authentication when complete is true and allRelations is true
     */
    @Test
    public void getWhenCompleteAndAllRelations() throws Exception {
        final Authentication authenticationExpected = dbAuthentications.get(0);
        cleanAuthentication(authenticationExpected, false, false);
        final String ID = authenticationExpected.getId();

        final Map mapResult = integrationTest.getOKResponse(MockMvcRequestBuilders.get(
                BASE_URL + "/" + ID + BOTH + ""));
        final Authentication authenticationResult = IntegrationTest.getAuthentication(mapResult.get("data"));

        assertNotSame(authenticationExpected, authenticationResult);
        assertEquals(authenticationExpected, authenticationResult);
        assertNull(mapResult.get("metaData"));
    }

    /**
     * Should return an BAD_REQUEST error response
     */
    @Test
    public void createWhenUsernameDuplicated() throws Exception {
        final String ERROR_EXPECTED = "Username 'user2' is already used by another user.";

        final Authentication a = new Authentication("user2", "new pass",
                new AuthProvider(), new Person());
        final String requestJson = mapper.writeValueAsString(a);

        final Map mapResult = integrationTest.getBadRequestResponse(
                MockMvcRequestBuilders.post(BASE_URL).content(requestJson));
        final Map errorResult = (Map) mapResult.get("error");

        assertNotSame(ERROR_EXPECTED, errorResult.get("message"));
        assertEquals(ERROR_EXPECTED, errorResult.get("message"));
        assertNull(errorResult.get("devMessage"));
        assertNull(errorResult.get("nestedErrors"));

        // not inserted in data base
        assertEquals(authenticationRepository.count(), 3);
        assertEquals(authProviderRepository.count(), 3);
        assertEquals(personRepository.count(), 2);
    }

    /**
     * Should return an BAD_REQUEST error response
     */
    @Test
    public void createWhenPersonAndProviderDuplicated() throws Exception {
        final String ERROR_EXPECTED = "'N1 LN1' already has an Authorization with provider 'N1'.";

        final Authentication a = new Authentication(null, "new pass",
                new AuthProvider(dbAuthProviders.get(0).getId()), new Person(dbPeople.get(0).getId()));
        final String requestJson = mapper.writeValueAsString(a);

        final Map mapResult = integrationTest.getBadRequestResponse(
                MockMvcRequestBuilders.post(BASE_URL).content(requestJson));
        final Map errorResult = (Map) mapResult.get("error");

        assertNotSame(ERROR_EXPECTED, errorResult.get("message"));
        assertEquals(ERROR_EXPECTED, errorResult.get("message"));
        assertNull(errorResult.get("devMessage"));
        assertNull(errorResult.get("nestedErrors"));

        // not inserted in data base
        assertEquals(authenticationRepository.count(), 3);
        assertEquals(authProviderRepository.count(), 3);
        assertEquals(personRepository.count(), 2);
    }

    /**
     * Should return an INTERNAL_SERVER_ERROR error response
     */
    @Test
    public void createWhenInvalidAuthProviderID() throws Exception {
        final String ERROR_EXPECTED = "An error has occurred.";

        final Authentication a = new Authentication("new user", "123",
                new AuthProvider("invalid"), new Person(dbPeople.get(0).getId()));
        final String requestJson = mapper.writeValueAsString(a);

        final Map mapResult = integrationTest.getInternalServerResponse(
                MockMvcRequestBuilders.post(BASE_URL).content(requestJson));
        final Map errorResult = (Map) mapResult.get("error");

        assertNotSame(ERROR_EXPECTED, errorResult.get("message"));
        assertEquals(ERROR_EXPECTED, errorResult.get("message"));
        assertNotNull(errorResult.get("devMessage"));
        assertNull(errorResult.get("nestedErrors"));

        // not inserted in data base
        assertEquals(authenticationRepository.count(), 3);
        assertEquals(authProviderRepository.count(), 3);
        assertEquals(personRepository.count(), 2);
    }

    /**
     * Should return an INTERNAL_SERVER_ERROR error response
     */
    @Test
    public void createWhenNotAuthProviderID() throws Exception {
        final String ERROR_EXPECTED = "An error has occurred.";

        final Authentication a = new Authentication(null, "new pass",
                new AuthProvider("NN", "DD", "UU", "AKK", "ASS"),
                new Person(dbPeople.get(0).getId()));
        final String requestJson = mapper.writeValueAsString(a);

        final Map mapResult = integrationTest.getInternalServerResponse(
                MockMvcRequestBuilders.post(BASE_URL).content(requestJson));
        final Map errorResult = (Map) mapResult.get("error");

        assertNotSame(ERROR_EXPECTED, errorResult.get("message"));
        assertEquals(ERROR_EXPECTED, errorResult.get("message"));
        assertNotNull(errorResult.get("devMessage"));
        assertNull(errorResult.get("nestedErrors"));

        // not inserted in data base
        assertEquals(authenticationRepository.count(), 3);
        assertEquals(authProviderRepository.count(), 3);
        assertEquals(personRepository.count(), 2);
    }

    /**
     * Should return an INTERNAL_SERVER_ERROR error response
     */
    @Test
    public void createWhenInvalidPersonID() throws Exception {
        final String ERROR_EXPECTED = "An error has occurred.";

        final Authentication a = new Authentication("new user", "123",
                new AuthProvider(dbAuthProviders.get(0).getId()), new Person("invalid"));
        final String requestJson = mapper.writeValueAsString(a);

        final Map mapResult = integrationTest.getInternalServerResponse(
                MockMvcRequestBuilders.post(BASE_URL).content(requestJson));
        final Map errorResult = (Map) mapResult.get("error");

        assertNotSame(ERROR_EXPECTED, errorResult.get("message"));
        assertEquals(ERROR_EXPECTED, errorResult.get("message"));
        assertNotNull(errorResult.get("devMessage"));
        assertNull(errorResult.get("nestedErrors"));

        // not inserted in data base
        assertEquals(authenticationRepository.count(), 3);
        assertEquals(authProviderRepository.count(), 3);
        assertEquals(personRepository.count(), 2);
    }

    /**
     * Should return an INTERNAL_SERVER_ERROR error response
     */
    @Test
    public void createWhenNotPersonID() throws Exception {
        final String ERROR_EXPECTED = "An error has occurred.";

        final Authentication a = new Authentication(null, "123",
                new AuthProvider(dbAuthProviders.get(0).getId()),
                new Person("NN", "LNLN", LocalDate.now(), 1, "A", null, null));
        final String requestJson = mapper.writeValueAsString(a);

        final Map mapResult = integrationTest.getInternalServerResponse(
                MockMvcRequestBuilders.post(BASE_URL).content(requestJson));
        final Map errorResult = (Map) mapResult.get("error");

        assertNotSame(ERROR_EXPECTED, errorResult.get("message"));
        assertEquals(ERROR_EXPECTED, errorResult.get("message"));
        assertNotNull(errorResult.get("devMessage"));
        assertNull(errorResult.get("nestedErrors"));

        // not inserted in data base
        assertEquals(authenticationRepository.count(), 3);
        assertEquals(authProviderRepository.count(), 3);
        assertEquals(personRepository.count(), 2);
    }

    /**
     * Should create a new Authentication
     */
    @Test
    public void createWhenSuccess() throws Exception {
        final String fakeAuthProviderID = dbAuthProviders.get(2).getId();
        final AuthProvider authProviderFake = new AuthProvider("fake", "fake2", null, null, null); // this fake info will be returned but not saved
        authProviderFake.setId(fakeAuthProviderID);

        final String fakePersonID = dbPeople.get(0).getId();
        final Person personFake = new Person("fake", "fake2", LocalDate.now(), 1, "A", null, null); // this fake info will be returned but not saved
        personFake.setId(fakePersonID);

        final Authentication authenticationExpected = new Authentication("new user", null, authProviderFake, personFake);
        final Authentication a = new Authentication("new user", "new pass", authProviderFake, personFake);
        final String requestJson = mapper.writeValueAsString(a);

        final Map mapResult = integrationTest.getOKResponse(
                MockMvcRequestBuilders.post(BASE_URL).content(requestJson));
        final Authentication authenticationResult = IntegrationTest.getAuthentication(mapResult.get("data"));

        authenticationExpected.setId(authenticationResult.getId());

        assertNotSame(authenticationExpected, authenticationResult);
        assertEquals(authenticationExpected, authenticationResult);
        assertNull(mapResult.get("metaData"));

        // inserted in data base
        assertEquals(authenticationRepository.count(), 4);
        assertEquals(authProviderRepository.count(), 3);
        assertEquals(personRepository.count(), 2);

        // authProviders not edited
        validateAuthProvidersNotEdited();
        // people not edited
        validatePeopleNotEdited();
    }

    /**
     * Should return a NOT_FOUND error response
     */
    @Test
    public void editWhenNotFound() throws Exception {
        final Authentication a = new Authentication("user1", "123", new AuthProvider(), new Person());
        a.setId("old");
        final String requestJson = mapper.writeValueAsString(a);

        final Map mapResult = integrationTest.getNotFoundResponse(
                MockMvcRequestBuilders.put(BASE_URL + "/abc").content(requestJson));
        final Map errorResult = (Map) mapResult.get("error");

        assertNotSame(NOT_FOUND_ERROR, errorResult.get("message"));
        assertEquals(NOT_FOUND_ERROR, errorResult.get("message"));
        assertNull(errorResult.get("devMessage"));
        assertNull(errorResult.get("nestedErrors"));

        // not updated in data base
        validateAuthenticationsNotEdited();
        validateAuthProvidersNotEdited();
        validatePeopleNotEdited();
    }

    /**
     * Should update an Authentication
     */
    @Test
    public void editWhenSuccessDuplicate() throws Exception {
        final String ID = dbAuthentications.get(1).getId();

        final Authentication authenticationExpected = dbAuthentications.get(1);
        cleanAuthentication(authenticationExpected, false, false);
        final Authentication a = new Authentication("user1", "123", dbAuthProviders.get(0), dbPeople.get(0));
        a.setId("old");
        final String requestJson = mapper.writeValueAsString(a);

        final Map mapResult = integrationTest.getOKResponse(
                MockMvcRequestBuilders.put(BASE_URL + "/" + ID).content(requestJson));
        final Authentication authenticationResult = IntegrationTest.getAuthentication(mapResult.get("data"));

        assertNotSame(authenticationExpected, authenticationResult);
        assertEquals(authenticationExpected, authenticationResult);
        assertNull(mapResult.get("metaData"));

        // updated in data base
        validateAuthenticationsNotEdited(); // because is only possible to update password, and password is returned as null, apparently nothing change
        validateAuthProvidersNotEdited();
        validatePeopleNotEdited();
    }

    /**
     * Should update an Authentication
     */
    @Test
    public void editWhenSuccessInvalidRelations() throws Exception {
        final String ID = dbAuthentications.get(2).getId();
        final AuthProvider authProviderFake = new AuthProvider("A", "B", "C", "D", "E");
        authProviderFake.setId("invalid1");
        final Person personFake = new Person("A", "B", LocalDate.now(), 1, "C", null, null);
        personFake.setId("invalid2");

        final Authentication authenticationExpected = dbAuthentications.get(2);
        cleanAuthentication(authenticationExpected, false, false);
        final Authentication a = new Authentication("user1", "123", authProviderFake, personFake);
        a.setId("old");
        final String requestJson = mapper.writeValueAsString(a);

        final Map mapResult = integrationTest.getOKResponse(
                MockMvcRequestBuilders.put(BASE_URL + "/" + ID).content(requestJson));
        final Authentication authenticationResult = IntegrationTest.getAuthentication(mapResult.get("data"));

        assertNotSame(authenticationExpected, authenticationResult);
        assertEquals(authenticationExpected, authenticationResult);
        assertNull(mapResult.get("metaData"));

        // updated in data base
        validateAuthenticationsNotEdited(); // because is only possible to update password, and password is returned as null, apparently nothing change
        validateAuthProvidersNotEdited();
        validatePeopleNotEdited();
    }

    /**
     * Should return a NOT_FOUND error response
     */
    @Test
    public void deleteWhenNotFound() throws Exception {
        final Map mapResult = integrationTest.getNotFoundResponse(
                MockMvcRequestBuilders.delete(BASE_URL + "/aaa"));
        final Map errorResult = (Map) mapResult.get("error");

        assertNotSame(NOT_FOUND_ERROR, errorResult.get("message"));
        assertEquals(NOT_FOUND_ERROR, errorResult.get("message"));
        assertNull(errorResult.get("devMessage"));
        assertNull(errorResult.get("nestedErrors"));

        // not deleted in data base
        assertEquals(authenticationRepository.count(), 3);
        assertEquals(authProviderRepository.count(), 3);
        assertEquals(personRepository.count(), 2);
    }

    /**
     * Should delete an Authentication
     */
    @Test
    public void deleteWhenSuccess() throws Exception {
        final Authentication authenticationExpected = dbAuthentications.get(1);
        cleanAuthentication(authenticationExpected, false, false);
        final String ID = authenticationExpected.getId();

        final Map mapResult = integrationTest.getOKResponse(
                MockMvcRequestBuilders.delete(BASE_URL + "/" + ID));
        final Authentication authenticationResult = IntegrationTest.getAuthentication(mapResult.get("data"));

        assertNotSame(authenticationExpected, authenticationResult);
        assertEquals(authenticationExpected, authenticationResult);
        assertNull(mapResult.get("metaData"));

        // deleted in data base
        assertEquals(authenticationRepository.count(), 2);
        assertEquals(authProviderRepository.count(), 3);
        assertEquals(personRepository.count(), 2);
    }

    /**
     * Should return an INTERNAL_SERVER_ERROR error response
     */
    @Test
    public void pageWhenInvalidSort() throws Exception {
        final String ERROR_EXPECTED = "An error has occurred.";

        final PageDataRequest dataRequest = new PageDataRequest(-1, -1, "Asc", Arrays.asList("test3"), null);
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

        final PageDataRequest dataRequest = new PageDataRequest(-1, -1, "Asc", null, Arrays.asList(new FilterRequest("invalid", "cc", "eq")));
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
     * Should get authentications page when complete is false and allRelations is false
     */
    @Test
    public void pageWhenNotCompleteAndNotAllRelations() throws Exception {
        final List<Authentication> authenticationsExpected = Arrays.asList(dbAuthentications.get(1));
        cleanAuthentications(authenticationsExpected, true, false);

        final PageDataResponse dataResponseExpected = new PageDataResponse(3, 3L, new PageDataRequest(0, 1, "ASC", Arrays.asList("username"), null));
        final PageDataRequest dataRequest = new PageDataRequest(-1, -1, "Asc", Arrays.asList("username"), null);
        final String requestJson = mapper.writeValueAsString(dataRequest);

        final Map mapResult = integrationTest.getOKResponse(
                MockMvcRequestBuilders.post(BASE_URL + "/Page").content(requestJson));
        final List<Authentication> authenticationsResult = IntegrationTest.getAuthentications(mapResult.get("data"));
        final PageDataResponse dataResponseResult = IntegrationTest.getPageDataResponse(mapResult.get("metaData"));

        assertNotSame(authenticationsExpected, authenticationsResult);
        assertEquals(authenticationsExpected, authenticationsResult);
        assertNotSame(dataResponseExpected, dataResponseResult);
        assertEquals(dataResponseExpected, dataResponseResult);
    }

    /**
     * Should get authentications page when complete is true and allRelations is false
     */
    @Test
    public void pageWhenCompleteAndNotAllRelations() throws Exception {
        final List<Authentication> authenticationsExpected = Arrays.asList(dbAuthentications.get(1));
        cleanAuthentications(authenticationsExpected, false, true);

        final PageDataResponse dataResponseExpected = new PageDataResponse(3, 3L, new PageDataRequest(0, 1, "ASC", Arrays.asList("username"), null));
        final PageDataRequest dataRequest = new PageDataRequest(-1, -1, "Asc", Arrays.asList("username"), null);
        final String requestJson = mapper.writeValueAsString(dataRequest);

        final Map mapResult = integrationTest.getOKResponse(
                MockMvcRequestBuilders.post(BASE_URL + "/Page" + COMPLETE).content(requestJson));
        final List<Authentication> authenticationsResult = IntegrationTest.getAuthentications(mapResult.get("data"));
        final PageDataResponse dataResponseResult = IntegrationTest.getPageDataResponse(mapResult.get("metaData"));

        assertNotSame(authenticationsExpected, authenticationsResult);
        assertEquals(authenticationsExpected, authenticationsResult);
        assertNotSame(dataResponseExpected, dataResponseResult);
        assertEquals(dataResponseExpected, dataResponseResult);
    }

    /**
     * Should get authentications page when complete is false and allRelations is true
     */
    @Test
    public void pageWhenNotCompleteAndAllRelations() throws Exception {
        final List<Authentication> authenticationsExpected = Arrays.asList(dbAuthentications.get(1));
        cleanAuthentications(authenticationsExpected, false, false);

        final PageDataResponse dataResponseExpected = new PageDataResponse(3, 3L, new PageDataRequest(0, 1, "ASC", Arrays.asList("username"), null));
        final PageDataRequest dataRequest = new PageDataRequest(-1, -1, "Asc", Arrays.asList("username"), null);
        final String requestJson = mapper.writeValueAsString(dataRequest);

        final Map mapResult = integrationTest.getOKResponse(
                MockMvcRequestBuilders.post(BASE_URL + "/Page" + ALL_RELATIONS).content(requestJson));
        final List<Authentication> authenticationsResult = IntegrationTest.getAuthentications(mapResult.get("data"));
        final PageDataResponse dataResponseResult = IntegrationTest.getPageDataResponse(mapResult.get("metaData"));

        assertNotSame(authenticationsExpected, authenticationsResult);
        assertEquals(authenticationsExpected, authenticationsResult);
        assertNotSame(dataResponseExpected, dataResponseResult);
        assertEquals(dataResponseExpected, dataResponseResult);
    }

    /**
     * Should get authentications page when complete is true and allRelations is true
     */
    @Test
    public void pageWhenCompleteAndAllRelations() throws Exception {
        final List<Authentication> authenticationsExpected = Arrays.asList(dbAuthentications.get(1));
        cleanAuthentications(authenticationsExpected, false, false);

        final PageDataResponse dataResponseExpected = new PageDataResponse(3, 3L, new PageDataRequest(0, 1, "ASC", Arrays.asList("username"), null));
        final PageDataRequest dataRequest = new PageDataRequest(-1, -1, "Asc", Arrays.asList("username"), null);
        final String requestJson = mapper.writeValueAsString(dataRequest);

        final Map mapResult = integrationTest.getOKResponse(
                MockMvcRequestBuilders.post(BASE_URL + "/Page" + BOTH + "").content(requestJson));
        final List<Authentication> authenticationsResult = IntegrationTest.getAuthentications(mapResult.get("data"));
        final PageDataResponse dataResponseResult = IntegrationTest.getPageDataResponse(mapResult.get("metaData"));

        assertNotSame(authenticationsExpected, authenticationsResult);
        assertEquals(authenticationsExpected, authenticationsResult);
        assertNotSame(dataResponseExpected, dataResponseResult);
        assertEquals(dataResponseExpected, dataResponseResult);
    }

    private void cleanAuthentications(List<Authentication> authentications, boolean cleanPerson, boolean cleanRoles) {
        authentications.forEach(a -> cleanAuthentication(a, cleanPerson, cleanRoles));
    }

    private void cleanAuthentication(Authentication authentication, boolean cleanPerson, boolean cleanRoles) {
        authentication.setPassword(null);

        authentication.getAuthProvider().setUrl(null);
        authentication.getAuthProvider().setAuthKey(null);
        authentication.getAuthProvider().setAuthSecret(null);

        if (cleanRoles) {
            authentication.getPerson().getRoles().forEach(r -> r.setPermissions(null));
            // sorted is needed when model is edited to pass isEquals conditions
            authentication.getPerson().setRoles(authentication.getPerson().getRoles().stream().sorted(Comparator.comparing(Role::getId)).collect(Collectors.toSet()));
        } else if (cleanPerson) {
            authentication.getPerson().setRoles(null);
        }
    }

    private void validateAuthenticationsNotEdited() {
        List<Authentication> newDBAuthentications = authenticationRepository.findAll();
        newDBAuthentications.forEach(a -> {
            cleanAuthentication(a, true, false);
            a.getAuthProvider().setAuthentications(null);
            a.getPerson().setAuthentications(null);
        });
        dbAuthentications.forEach(a -> {
            cleanAuthentication(a, true, false);
            a.getAuthProvider().setAuthentications(null);
            a.getPerson().setAuthentications(null);
        });
        // sorted is needed when model is edited to pass isEquals conditions
        newDBAuthentications = newDBAuthentications.stream().sorted(Comparator.comparing(Authentication::getId)).collect(Collectors.toList());
        dbAuthentications = dbAuthentications.stream().sorted(Comparator.comparing(Authentication::getId)).collect(Collectors.toList());
        assertNotSame(dbAuthentications, newDBAuthentications);
        assertEquals(dbAuthentications, newDBAuthentications);
        assertEquals(authenticationRepository.count(), 3);
    }

    private void validateAuthProvidersNotEdited() {
        final List<AuthProvider> newDBAuthProviders = authProviderRepository.findAll();
        newDBAuthProviders.forEach(a -> {
            a.setUrl(null);
            a.setAuthKey(null);
            a.setAuthSecret(null);
            a.setAuthentications(null);
        });
        dbAuthProviders.forEach(a -> {
            String value = null;
            a.setUrl(value);
            a.setAuthKey(value);
            a.setAuthSecret(value);
            a.setAuthentications(null);
        });
        assertNotSame(dbAuthProviders, newDBAuthProviders);
        assertEquals(dbAuthProviders, newDBAuthProviders);
        assertEquals(authProviderRepository.count(), 3);
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
        assertEquals(personRepository.count(), 2);
    }
}