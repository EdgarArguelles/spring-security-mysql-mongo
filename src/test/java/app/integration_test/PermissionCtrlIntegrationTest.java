package app.integration_test;

import app.models.Permission;
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

import java.util.*;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PermissionCtrlIntegrationTest {

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

    private List<Role> dbRoles;

    private List<Permission> dbPermissions;

    private final String BASE_URL = "/permissions";

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
                new Permission("N", "D"),
                new Permission("N2", "D2")
        );
        permissionRepository.save(dbPermissions);

        dbRoles = Arrays.asList(
                new Role("NR", "DR", new HashSet<>(Arrays.asList(dbPermissions.get(1))))
        );
        roleRepository.save(dbRoles);
    }

    /**
     * Should get permissions list when complete is false and allRelations is false
     */
    @Test
    public void listWhenNotCompleteAndNotAllRelations() throws Exception {
        final List<Permission> permissionsExpected = dbPermissions;

        final Map mapResult = integrationTest.getOKResponse(MockMvcRequestBuilders.get(BASE_URL));
        final List<Permission> permissionsResult = IntegrationTest.getPermissions(mapResult.get("data"));

        assertNotSame(permissionsExpected, permissionsResult);
        assertEquals(permissionsExpected, permissionsResult);
        assertNull(mapResult.get("metaData"));
    }

    /**
     * Should get permissions list when complete is true and allRelations is false
     */
    @Test
    public void listWhenCompleteAndNotAllRelations() throws Exception {
        final List<Permission> permissionsExpected = dbPermissions;

        final Map mapResult = integrationTest.getOKResponse(MockMvcRequestBuilders.get(BASE_URL + COMPLETE + ""));
        final List<Permission> permissionsResult = IntegrationTest.getPermissions(mapResult.get("data"));

        assertNotSame(permissionsExpected, permissionsResult);
        assertEquals(permissionsExpected, permissionsResult);
        assertNull(mapResult.get("metaData"));
    }

    /**
     * Should get permissions list when complete is false and allRelations is true
     */
    @Test
    public void listWhenNotCompleteAndAllRelations() throws Exception {
        final List<Permission> permissionsExpected = dbPermissions;

        final Map mapResult = integrationTest.getOKResponse(MockMvcRequestBuilders.get(BASE_URL + ALL_RELATIONS + " "));
        final List<Permission> permissionsResult = IntegrationTest.getPermissions(mapResult.get("data"));

        assertNotSame(permissionsExpected, permissionsResult);
        assertEquals(permissionsExpected, permissionsResult);
        assertNull(mapResult.get("metaData"));
    }

    /**
     * Should get permissions list when complete is true and allRelations is true
     */
    @Test
    public void listWhenCompleteAndAllRelations() throws Exception {
        final List<Permission> permissionsExpected = dbPermissions;

        final Map mapResult = integrationTest.getOKResponse(MockMvcRequestBuilders.get(BASE_URL + BOTH));
        final List<Permission> permissionsResult = IntegrationTest.getPermissions(mapResult.get("data"));

        assertNotSame(permissionsExpected, permissionsResult);
        assertEquals(permissionsExpected, permissionsResult);
        assertNull(mapResult.get("metaData"));
    }

    /**
     * Should return a NOT_FOUND error response
     */
    @Test
    public void getWhenNotFound() throws Exception {
        final Map mapResult = integrationTest.getNotFoundResponse(MockMvcRequestBuilders.get(
                BASE_URL + "/abc"));
        final Map errorResult = (Map) mapResult.get("error");

        assertNotSame(NOT_FOUND_ERROR, errorResult.get("message"));
        assertEquals(NOT_FOUND_ERROR, errorResult.get("message"));
        assertNull(errorResult.get("devMessage"));
        assertNull(errorResult.get("nestedErrors"));
    }

    /**
     * Should get a permission when complete is false and allRelations is false
     */
    @Test
    public void getWhenNotCompleteAndNotAllRelations() throws Exception {
        final Permission permissionExpected = dbPermissions.get(0);
        final String ID = permissionExpected.getId();

        final Map mapResult = integrationTest.getOKResponse(MockMvcRequestBuilders.get(
                BASE_URL + "/" + ID));
        final Permission permissionResult = IntegrationTest.getPermission(mapResult.get("data"));

        assertNotSame(permissionExpected, permissionResult);
        assertEquals(permissionExpected, permissionResult);
        assertNull(mapResult.get("metaData"));
    }

    /**
     * Should get a permission when complete is true and allRelations is false
     */
    @Test
    public void getWhenCompleteAndNotAllRelations() throws Exception {
        final Permission permissionExpected = dbPermissions.get(0);
        final String ID = permissionExpected.getId();

        final Map mapResult = integrationTest.getOKResponse(MockMvcRequestBuilders.get(
                BASE_URL + "/" + ID + COMPLETE + ""));
        final Permission permissionResult = IntegrationTest.getPermission(mapResult.get("data"));

        assertNotSame(permissionExpected, permissionResult);
        assertEquals(permissionExpected, permissionResult);
        assertNull(mapResult.get("metaData"));
    }

    /**
     * Should get a permission when complete is false and allRelations is true
     */
    @Test
    public void getWhenNotCompleteAndAllRelations() throws Exception {
        final Permission permissionExpected = dbPermissions.get(0);
        final String ID = permissionExpected.getId();

        final Map mapResult = integrationTest.getOKResponse(MockMvcRequestBuilders.get(
                BASE_URL + "/" + ID + ALL_RELATIONS + " "));
        final Permission permissionResult = IntegrationTest.getPermission(mapResult.get("data"));

        assertNotSame(permissionExpected, permissionResult);
        assertEquals(permissionExpected, permissionResult);
        assertNull(mapResult.get("metaData"));
    }

    /**
     * Should get a permission when complete is true and allRelations is true
     */
    @Test
    public void getWhenCompleteAndAllRelations() throws Exception {
        final Permission permissionExpected = dbPermissions.get(0);
        final String ID = permissionExpected.getId();

        final Map mapResult = integrationTest.getOKResponse(MockMvcRequestBuilders.get(
                BASE_URL + "/" + ID + BOTH + "  "));
        final Permission permissionResult = IntegrationTest.getPermission(mapResult.get("data"));

        assertNotSame(permissionExpected, permissionResult);
        assertEquals(permissionExpected, permissionResult);
        assertNull(mapResult.get("metaData"));
    }

    /**
     * Should return a BAD_REQUEST error response
     */
    @Test
    public void createWhenDuplicate() throws Exception {
        final String ERROR_EXPECTED = "Permission name 'N2' is already used.";

        final Permission p = new Permission("N2", "new description");
        final String requestJson = mapper.writeValueAsString(p);

        final Map mapResult = integrationTest.getBadRequestResponse(
                MockMvcRequestBuilders.post(BASE_URL).content(requestJson));
        final Map errorResult = (Map) mapResult.get("error");

        assertNotSame(ERROR_EXPECTED, errorResult.get("message"));
        assertEquals(ERROR_EXPECTED, errorResult.get("message"));
        assertNull(errorResult.get("devMessage"));
        assertNull(errorResult.get("nestedErrors"));

        // not inserted in data base
        assertEquals(permissionRepository.count(), 2);
    }

    /**
     * Should create a new Permission
     */
    @Test
    public void createWhenSuccess() throws Exception {
        final Permission permissionExpected = new Permission("NN", "new entry");
        final Permission p = new Permission("NN", "new entry");
        final String requestJson = mapper.writeValueAsString(p);

        final Map mapResult = integrationTest.getOKResponse(
                MockMvcRequestBuilders.post(BASE_URL).content(requestJson));
        final Permission permissionResult = IntegrationTest.getPermission(mapResult.get("data"));

        permissionExpected.setId(permissionResult.getId());

        assertNotSame(permissionExpected, permissionResult);
        assertEquals(permissionExpected, permissionResult);
        assertNull(mapResult.get("metaData"));

        // inserted in data base
        assertEquals(permissionRepository.count(), 3);
    }

    /**
     * Should return a NOT_FOUND error response
     */
    @Test
    public void editWhenNotFound() throws Exception {
        final Permission p = new Permission("new name", "new description");
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
        final List<Permission> newDBPermissions = permissionRepository.findAll();
        newDBPermissions.forEach(pe -> pe.setRoles(null));
        assertNotSame(dbPermissions, newDBPermissions);
        assertEquals(dbPermissions, newDBPermissions);
        assertEquals(permissionRepository.count(), 2);
    }

    /**
     * Should update a Permission
     */
    @Test
    public void editWhenSuccess() throws Exception {
        final String ID = dbPermissions.get(1).getId();
        final Permission permissionExpected = new Permission("N2", "new description");
        permissionExpected.setId(ID);
        final Permission p = new Permission("new name", "new description");
        p.setId("old");
        final String requestJson = mapper.writeValueAsString(p);

        final Map mapResult = integrationTest.getOKResponse(
                MockMvcRequestBuilders.put(BASE_URL + "/" + ID).content(requestJson));
        final Permission permissionResult = IntegrationTest.getPermission(mapResult.get("data"));

        assertNotSame(permissionExpected, permissionResult);
        assertEquals(permissionExpected, permissionResult);
        assertNull(mapResult.get("metaData"));

        // updated in data base
        final List<Permission> newDBPermissions = permissionRepository.findAll();
        newDBPermissions.forEach(pe -> pe.setRoles(null));
        assertNotSame(dbPermissions, newDBPermissions);
        assertNotEquals(dbPermissions, newDBPermissions);
        assertEquals(permissionRepository.count(), 2);
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
        assertEquals(permissionRepository.count(), 2);
    }

    /**
     * Should return a BAD_REQUEST error response
     */
    @Test
    public void deleteWhenUsed() throws Exception {
        final String ID = dbPermissions.get(1).getId();
        final String ERROR_EXPECTED = "There are some roles using the Permission 'N2'.";

        final Map mapResult = integrationTest.getBadRequestResponse(
                MockMvcRequestBuilders.delete(BASE_URL + "/" + ID));
        final Map errorResult = (Map) mapResult.get("error");

        assertNotSame(ERROR_EXPECTED, errorResult.get("message"));
        assertEquals(ERROR_EXPECTED, errorResult.get("message"));
        assertNull(errorResult.get("devMessage"));
        assertNull(errorResult.get("nestedErrors"));

        // not deleted in data base
        assertEquals(permissionRepository.count(), 2);
    }

    /**
     * Should delete a Permission
     */
    @Test
    public void deleteWhenSuccess() throws Exception {
        final Permission permissionExpected = dbPermissions.get(0);
        final String ID = permissionExpected.getId();

        final Map mapResult = integrationTest.getOKResponse(
                MockMvcRequestBuilders.delete(BASE_URL + "/" + ID));
        final Permission permissionResult = IntegrationTest.getPermission(mapResult.get("data"));

        assertNotSame(permissionExpected, permissionResult);
        assertEquals(permissionExpected, permissionResult);
        assertNull(mapResult.get("metaData"));

        // deleted in data base
        assertEquals(permissionRepository.count(), 1);
    }

    /**
     * Should return a NOT_FOUND error response
     */
    @Test
    public void getRolesWhenNotFound() throws Exception {
        final Map mapResult = integrationTest.getNotFoundResponse(
                MockMvcRequestBuilders.get(BASE_URL + "/abc/roles"));
        final Map errorResult = (Map) mapResult.get("error");

        assertNotSame(NOT_FOUND_ERROR, errorResult.get("message"));
        assertEquals(NOT_FOUND_ERROR, errorResult.get("message"));
        assertNull(errorResult.get("devMessage"));
        assertNull(errorResult.get("nestedErrors"));
    }

    /**
     * Should get empty list when no roles
     */
    @Test
    public void getRolesWhenNotRoles() throws Exception {
        final String ID = dbPermissions.get(0).getId();
        final List<Role> rolesExpected = Collections.EMPTY_LIST;

        final Map mapResult = integrationTest.getOKResponse(
                MockMvcRequestBuilders.get(BASE_URL + "/" + ID + "/roles"));
        final List<Role> rolesResult = IntegrationTest.getRoles(mapResult.get("data"));

        assertNotSame(rolesExpected, rolesResult);
        assertEquals(rolesExpected, rolesResult);
        assertNull(mapResult.get("metaData"));
    }

    /**
     * Should get roles list when complete is false and allRelations is false
     */
    @Test
    public void getRolesWhenNotCompleteAndNotAllRelations() throws Exception {
        final String ID = dbPermissions.get(1).getId();
        final List<Role> rolesExpected = Arrays.asList(dbRoles.get(0));
        rolesExpected.forEach(r -> r.setPermissions(null));

        final Map mapResult = integrationTest.getOKResponse(
                MockMvcRequestBuilders.get(BASE_URL + "/" + ID + "/roles"));
        final List<Role> rolesResult = IntegrationTest.getRoles(mapResult.get("data"));

        assertNotSame(rolesExpected, rolesResult);
        assertEquals(rolesExpected, rolesResult);
        assertNull(mapResult.get("metaData"));
    }

    /**
     * Should get roles list when complete is true and allRelations is false
     */
    @Test
    public void getRolesWhenCompleteAndNotAllRelations() throws Exception {
        final String ID = dbPermissions.get(1).getId();
        final List<Role> rolesExpected = Arrays.asList(dbRoles.get(0));

        final Map mapResult = integrationTest.getOKResponse(
                MockMvcRequestBuilders.get(BASE_URL + "/" + ID + "/roles" + COMPLETE + ""));
        final List<Role> rolesResult = IntegrationTest.getRoles(mapResult.get("data"));

        assertNotSame(rolesExpected, rolesResult);
        assertEquals(rolesExpected, rolesResult);
        assertNull(mapResult.get("metaData"));
    }

    /**
     * Should get roles list when complete is false and allRelations is true
     */
    @Test
    public void getRolesWhenNotCompleteAndAllRelations() throws Exception {
        final String ID = dbPermissions.get(1).getId();
        final List<Role> rolesExpected = Arrays.asList(dbRoles.get(0));

        final Map mapResult = integrationTest.getOKResponse(
                MockMvcRequestBuilders.get(BASE_URL + "/" + ID + "/roles" + ALL_RELATIONS + " "));
        final List<Role> rolesResult = IntegrationTest.getRoles(mapResult.get("data"));

        assertNotSame(rolesExpected, rolesResult);
        assertEquals(rolesExpected, rolesResult);
        assertNull(mapResult.get("metaData"));
    }

    /**
     * Should get roles list when complete is true and allRelations is true
     */
    @Test
    public void getRolesWhenCompleteAndAllRelations() throws Exception {
        final String ID = dbPermissions.get(1).getId();
        final List<Role> rolesExpected = Arrays.asList(dbRoles.get(0));

        final Map mapResult = integrationTest.getOKResponse(
                MockMvcRequestBuilders.get(BASE_URL + "/" + ID + "/roles" + BOTH + "  "));
        final List<Role> rolesResult = IntegrationTest.getRoles(mapResult.get("data"));

        assertNotSame(rolesExpected, rolesResult);
        assertEquals(rolesExpected, rolesResult);
        assertNull(mapResult.get("metaData"));
    }

    /**
     * Should return an INTERNAL_SERVER_ERROR error response
     */
    @Test
    public void pageWhenInvalidSort() throws Exception {
        final String ERROR_EXPECTED = "An error has occurred.";

        final PageDataRequest dataRequest = new PageDataRequest(-1, -1, "Asc", Arrays.asList("invalid"), null);
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

        final PageDataRequest dataRequest = new PageDataRequest(-1, -1, "Asc", null, Arrays.asList(new FilterRequest("invalid", "a", "eq")));
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
     * Should get permissions page when complete is false and allRelations is false
     */
    @Test
    public void pageWhenNotCompleteAndNotAllRelations() throws Exception {
        final List<Permission> permissionsExpected = Arrays.asList(dbPermissions.get(1));

        final PageDataResponse dataResponseExpected = new PageDataResponse(2, 2L, new PageDataRequest(1, 1, "ASC", Arrays.asList("name"), null));
        final PageDataRequest dataRequest = new PageDataRequest(1, -1, "Asc", Arrays.asList("name"), null);
        final String requestJson = mapper.writeValueAsString(dataRequest);

        final Map mapResult = integrationTest.getOKResponse(
                MockMvcRequestBuilders.post(BASE_URL + "/Page").content(requestJson));
        final List<Permission> permissionsResult = IntegrationTest.getPermissions(mapResult.get("data"));
        final PageDataResponse dataResponseResult = IntegrationTest.getPageDataResponse(mapResult.get("metaData"));

        assertNotSame(permissionsExpected, permissionsResult);
        assertEquals(permissionsExpected, permissionsResult);
        assertNotSame(dataResponseExpected, dataResponseResult);
        assertEquals(dataResponseExpected, dataResponseResult);
    }

    /**
     * Should get permissions page when complete is true and allRelations is false
     */
    @Test
    public void pageWhenCompleteAndNotAllRelations() throws Exception {
        final List<Permission> permissionsExpected = Arrays.asList(dbPermissions.get(1));

        final PageDataResponse dataResponseExpected = new PageDataResponse(2, 2L, new PageDataRequest(1, 1, "ASC", Arrays.asList("name"), null));
        final PageDataRequest dataRequest = new PageDataRequest(1, -1, "Asc", Arrays.asList("name"), null);
        final String requestJson = mapper.writeValueAsString(dataRequest);

        final Map mapResult = integrationTest.getOKResponse(
                MockMvcRequestBuilders.post(BASE_URL + "/Page" + COMPLETE + "").content(requestJson));
        final List<Permission> permissionsResult = IntegrationTest.getPermissions(mapResult.get("data"));
        final PageDataResponse dataResponseResult = IntegrationTest.getPageDataResponse(mapResult.get("metaData"));

        assertNotSame(permissionsExpected, permissionsResult);
        assertEquals(permissionsExpected, permissionsResult);
        assertNotSame(dataResponseExpected, dataResponseResult);
        assertEquals(dataResponseExpected, dataResponseResult);
    }

    /**
     * Should get permissions page when complete is false and allRelations is true
     */
    @Test
    public void pageWhenNotCompleteAndAllRelations() throws Exception {
        final List<Permission> permissionsExpected = Arrays.asList(dbPermissions.get(1));

        final PageDataResponse dataResponseExpected = new PageDataResponse(2, 2L, new PageDataRequest(1, 1, "ASC", Arrays.asList("name"), null));
        final PageDataRequest dataRequest = new PageDataRequest(1, -1, "Asc", Arrays.asList("name"), null);
        final String requestJson = mapper.writeValueAsString(dataRequest);

        final Map mapResult = integrationTest.getOKResponse(
                MockMvcRequestBuilders.post(BASE_URL + "/Page" + ALL_RELATIONS + " ").content(requestJson));
        final List<Permission> permissionsResult = IntegrationTest.getPermissions(mapResult.get("data"));
        final PageDataResponse dataResponseResult = IntegrationTest.getPageDataResponse(mapResult.get("metaData"));

        assertNotSame(permissionsExpected, permissionsResult);
        assertEquals(permissionsExpected, permissionsResult);
        assertNotSame(dataResponseExpected, dataResponseResult);
        assertEquals(dataResponseExpected, dataResponseResult);
    }

    /**
     * Should get permissions page when complete is true and allRelations is true
     */
    @Test
    public void pageWhenCompleteAndAllRelations() throws Exception {
        final List<Permission> permissionsExpected = Arrays.asList(dbPermissions.get(1));

        final PageDataResponse dataResponseExpected = new PageDataResponse(2, 2L, new PageDataRequest(1, 1, "ASC", Arrays.asList("name"), null));
        final PageDataRequest dataRequest = new PageDataRequest(1, -1, "Asc", Arrays.asList("name"), null);
        final String requestJson = mapper.writeValueAsString(dataRequest);

        final Map mapResult = integrationTest.getOKResponse(
                MockMvcRequestBuilders.post(BASE_URL + "/Page" + BOTH + "  ").content(requestJson));
        final List<Permission> permissionsResult = IntegrationTest.getPermissions(mapResult.get("data"));
        final PageDataResponse dataResponseResult = IntegrationTest.getPageDataResponse(mapResult.get("metaData"));

        assertNotSame(permissionsExpected, permissionsResult);
        assertEquals(permissionsExpected, permissionsResult);
        assertNotSame(dataResponseExpected, dataResponseResult);
        assertEquals(dataResponseExpected, dataResponseResult);
    }
}