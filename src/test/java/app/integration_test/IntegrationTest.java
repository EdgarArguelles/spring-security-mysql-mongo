package app.integration_test;

import app.models.*;
import app.pojos.pages.FilterRequest;
import app.pojos.pages.PageDataRequest;
import app.pojos.pages.PageDataResponse;
import app.pojos.responses.error.nesteds.NestedError;
import app.pojos.responses.error.nesteds.ValidationNestedError;
import app.repositories.*;
import app.security.pojos.LoggedUser;
import app.security.services.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.atLeast;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class IntegrationTest {

    private final MockMvc mvc;

    private final ObjectMapper mapper;

    private final TokenService tokenService;

    private final String ERROR_MESSAGE_EXPECTED = "Some data aren't valid.";

    private final String NEW_TOKEN = "new";

    private final String ALL_PERMISSIONS_TOKEN = "user with all permissions";

    public IntegrationTest(MockMvc mvc, ObjectMapper mapper, TokenService tokenService) throws Exception {
        this.mvc = mvc;
        this.mapper = mapper;
        this.tokenService = tokenService;

        given(tokenService.refreshToken()).willReturn(NEW_TOKEN);
        given(tokenService.getLoggedUser(ALL_PERMISSIONS_TOKEN)).willReturn(
                new LoggedUser("ID", "FN", "R", new HashSet<>(Arrays.asList(
                        "VIEW_ROLES", "CREATE_ROLES", "REMOVE_ROLES",
                        "VIEW_USERS", "CREATE_USERS", "REMOVE_USERS")))
        );
    }

    /**
     * Should fail a POST Model Validations mocking authentication with an user with all permissions
     *
     * @param url                  url to be called
     * @param requestJson          request body
     * @param nestedErrorsExpected expected NestedErrors
     */
    public void failPostModeValidation(String url, String requestJson, List<NestedError> nestedErrorsExpected) throws Exception {
        failModelValidations(MockMvcRequestBuilders.post(url).content(requestJson), nestedErrorsExpected);
    }

    /**
     * Should fail a PUT Model Validations mocking authentication with an user with all permissions
     *
     * @param url                  url to be called
     * @param requestJson          request body
     * @param nestedErrorsExpected expected NestedErrors
     */
    public void failPutModeValidation(String url, String requestJson, List<NestedError> nestedErrorsExpected) throws Exception {
        failModelValidations(MockMvcRequestBuilders.put(url).content(requestJson), nestedErrorsExpected);
    }

    /**
     * Should fail Model Validations mocking authentication with an user with all permissions
     *
     * @param builder              request to be called (header and contentType are going to be created internally)
     * @param nestedErrorsExpected expected NestedErrors
     */
    private void failModelValidations(MockHttpServletRequestBuilder builder, List<NestedError> nestedErrorsExpected) throws Exception {
        final Map mapResult = getBadRequestResponse(builder);

        final Map errorResult = (Map) mapResult.get("error");
        final List<LinkedHashMap<String, String>> nestedErrors = (List) errorResult.get("nestedErrors");
        final List<NestedError> nestedErrorsResult = nestedErrors.stream()
                .map(n -> new ValidationNestedError(n.get("field"), n.get("message")))
                .sorted(Comparator.comparing(ValidationNestedError::getField).thenComparing(ValidationNestedError::getMessage))
                .collect(Collectors.toList());

        nestedErrorsExpected = nestedErrorsExpected.stream()
                .map(n -> (ValidationNestedError) n)
                .sorted(Comparator.comparing(ValidationNestedError::getField).thenComparing(ValidationNestedError::getMessage))
                .collect(Collectors.toList());

        assertNotSame(ERROR_MESSAGE_EXPECTED, errorResult.get("message"));
        assertEquals(ERROR_MESSAGE_EXPECTED, errorResult.get("message"));
        assertNotNull(errorResult.get("devMessage"));
        assertNotSame(nestedErrorsExpected, nestedErrorsResult);
        assertEquals(nestedErrorsExpected, nestedErrorsResult);
    }

    /**
     * Should return a BAD_REQUEST error response mocking authentication with an user with all permissions
     *
     * @param builder request to be called (header and contentType are going to be created internally)
     * @return generated response
     */
    public Map getBadRequestResponse(MockHttpServletRequestBuilder builder) throws Exception {
        return getErrorResponse(builder, HttpStatus.BAD_REQUEST);
    }

    /**
     * Should return a NOT_FOUND error response mocking authentication with an user with all permissions
     *
     * @param builder request to be called (header and contentType are going to be created internally)
     * @return generated response
     */
    public Map getNotFoundResponse(MockHttpServletRequestBuilder builder) throws Exception {
        return getErrorResponse(builder, HttpStatus.NOT_FOUND);
    }

    /**
     * Should return an INTERNAL_SERVER_ERROR error response mocking authentication with an user with all permissions
     *
     * @param builder request to be called (header and contentType are going to be created internally)
     * @return generated response
     */
    public Map getInternalServerResponse(MockHttpServletRequestBuilder builder) throws Exception {
        return getErrorResponse(builder, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Should return an error response mocking authentication with an user with all permissions
     *
     * @param builder request to be called (header and contentType are going to be created internally)
     * @param status  Http error status expected
     * @return generated response
     */
    private Map getErrorResponse(MockHttpServletRequestBuilder builder, HttpStatus status) throws Exception {
        builder = builder.header("Authorization", "Bearer " + ALL_PERMISSIONS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON);

        String bodyResult = null;
        switch (status) {
            case BAD_REQUEST:
                bodyResult = mvc.perform(builder)
                        .andExpect(status().isBadRequest())
                        .andReturn().getResponse().getContentAsString();
                break;
            case NOT_FOUND:
                bodyResult = mvc.perform(builder)
                        .andExpect(status().isNotFound())
                        .andReturn().getResponse().getContentAsString();
                break;
            case INTERNAL_SERVER_ERROR:
                bodyResult = mvc.perform(builder)
                        .andExpect(status().isInternalServerError())
                        .andReturn().getResponse().getContentAsString();
                break;
        }

        final Map mapResult = mapper.readValue(bodyResult, HashMap.class);

        assertNotNull(mapResult.get("error"));
        verify(tokenService, atLeast(1)).getLoggedUser(ALL_PERMISSIONS_TOKEN);
        verify(tokenService, never()).refreshToken();
        return mapResult;
    }

    /**
     * Should return an OK response mocking authentication with an user with all permissions
     *
     * @param builder request to be called (header and contentType are going to be created internally)
     * @return generated response
     */
    public Map getOKResponse(MockHttpServletRequestBuilder builder) throws Exception {
        builder = builder.header("Authorization", "Bearer " + ALL_PERMISSIONS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON);

        final String bodyResult = mvc.perform(builder)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        final Map mapResult = mapper.readValue(bodyResult, HashMap.class);

        assertNotSame(NEW_TOKEN, mapResult.get("newToken"));
        assertEquals(NEW_TOKEN, mapResult.get("newToken"));
        verify(tokenService, times(1)).getLoggedUser(ALL_PERMISSIONS_TOKEN);
        verify(tokenService, times(1)).refreshToken();
        return mapResult;
    }

    /**
     * Clean all database entries
     */
    public static void cleanAllData(
            AuthenticationRepository authenticationRepository,
            AuthProviderRepository authProviderRepository,
            PersonRepository personRepository,
            RoleRepository roleRepository,
            PermissionRepository permissionRepository
    ) {
        authenticationRepository.deleteAll();
        authProviderRepository.deleteAll();
        personRepository.deleteAll();
        roleRepository.deleteAll();
        permissionRepository.deleteAll();
    }

    /**
     * Create a Permission instance from a Controller body response
     *
     * @param data data response
     * @return create instance
     */
    public static Permission getPermission(Object data) {
        LinkedHashMap<String, Object> p = (LinkedHashMap<String, Object>) data;
        Permission permission = new Permission((String) p.get("name"), (String) p.get("description"));
        permission.setId((String) p.get("id"));
        return permission;
    }

    /**
     * Create a Permission List from a Controller body response
     *
     * @param data data response
     * @return create list
     */
    public static List<Permission> getPermissions(Object data) {
        return ((List<LinkedHashMap<String, Object>>) data).stream().map(p -> getPermission(p)).collect(Collectors.toList());
    }

    /**
     * Create a Permission Set from a Controller body response
     *
     * @param data data response
     * @return create set
     */
    private static Set<Permission> getPermissionsSet(Object data) {
        if (data == null) {
            return null;
        }
        return ((List<LinkedHashMap<String, Object>>) data).stream().map(p -> getPermission(p)).collect(Collectors.toSet());
    }

    /**
     * Create a Role instance from a Controller body response
     *
     * @param data data response
     * @return create instance
     */
    public static Role getRole(Object data) {
        LinkedHashMap<String, Object> r = (LinkedHashMap<String, Object>) data;
        Role role = new Role((String) r.get("name"), (String) r.get("description"), getPermissionsSet(r.get("permissions")));
        role.setId((String) r.get("id"));
        return role;
    }

    /**
     * Create a Role List from a Controller body response
     *
     * @param data data response
     * @return create list
     */
    public static List<Role> getRoles(Object data) {
        return ((List<LinkedHashMap<String, Object>>) data).stream().map(r -> getRole(r)).collect(Collectors.toList());
    }

    /**
     * Create a Role Set from a Controller body response
     *
     * @param data data response
     * @return create set
     */
    private static Set<Role> getRolesSet(Object data) {
        if (data == null) {
            return null;
        }
        return ((List<LinkedHashMap<String, Object>>) data).stream().map(r -> getRole(r)).collect(Collectors.toSet());
    }

    /**
     * Create a Person instance from a Controller body response
     *
     * @param data data response
     * @return create instance
     */
    public static Person getPerson(Object data) {
        LinkedHashMap<String, Object> p = (LinkedHashMap<String, Object>) data;
        Person person = new Person((String) p.get("name"), (String) p.get("lastName"),
                LocalDate.parse((String) p.get("birthday"), DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                (Integer) p.get("civilStatus"), (String) p.get("sex"),
                (String) p.get("email"), getRolesSet(p.get("roles")));
        person.setId((String) p.get("id"));
        return person;
    }

    /**
     * Create a Person List from a Controller body response
     *
     * @param data data response
     * @return create list
     */
    public static List<Person> getPeople(Object data) {
        return ((List<LinkedHashMap<String, Object>>) data).stream().map(p -> getPerson(p)).collect(Collectors.toList());
    }

    /**
     * Create a Authentication instance from a Controller body response
     *
     * @param data data response
     * @return create instance
     */
    public static Authentication getAuthentication(Object data) {
        LinkedHashMap<String, Object> a = (LinkedHashMap<String, Object>) data;
        Authentication authentication = new Authentication((String) a.get("username"), (String) a.get("password"),
                getAuthProvider(a.get("authProvider")), getPerson(a.get("person")));
        authentication.setId((String) a.get("id"));
        return authentication;
    }

    /**
     * Create a Authentication List from a Controller body response
     *
     * @param data data response
     * @return create list
     */
    public static List<Authentication> getAuthentications(Object data) {
        return ((List<LinkedHashMap<String, Object>>) data).stream().map(a -> getAuthentication(a)).collect(Collectors.toList());
    }

    /**
     * Create a AuthProvider instance from a Controller body response
     *
     * @param data data response
     * @return create instance
     */
    public static AuthProvider getAuthProvider(Object data) {
        LinkedHashMap<String, Object> a = (LinkedHashMap<String, Object>) data;
        AuthProvider authProvider = new AuthProvider((String) a.get("name"), (String) a.get("description"), null, null, null);
        authProvider.setId((String) a.get("id"));
        return authProvider;
    }

    /**
     * Create a AuthProvider List from a Controller body response
     *
     * @param data data response
     * @return create list
     */
    public static List<AuthProvider> getAuthProviders(Object data) {
        return ((List<LinkedHashMap<String, Object>>) data).stream().map(a -> getAuthProvider(a)).collect(Collectors.toList());
    }

    /**
     * Create a PageDataResponse instance from a Controller body response
     *
     * @param data data response
     * @return create instance
     */
    public static PageDataResponse getPageDataResponse(Object data) {
        LinkedHashMap<String, Object> pdr = (LinkedHashMap<String, Object>) data;
        PageDataResponse dataResponse = new PageDataResponse((Integer) pdr.get("totalPages"),
                ((Integer) pdr.get("totalElements")).longValue(), getPageDataRequest(pdr.get("dataRequest")));
        return dataResponse;
    }

    /**
     * Create a PageDataRequest instance from a Controller body response
     *
     * @param data data response
     * @return create instance
     */
    private static PageDataRequest getPageDataRequest(Object data) {
        if (data == null) {
            return null;
        }

        LinkedHashMap<String, Object> pdr = (LinkedHashMap<String, Object>) data;
        PageDataRequest dataRequest = new PageDataRequest((Integer) pdr.get("page"), (Integer) pdr.get("size"),
                (String) pdr.get("direction"), (List) pdr.get("sort"), getFilterRequests(pdr.get("filters")));
        return dataRequest;
    }

    /**
     * Create a FilterRequest List from a Controller body response
     *
     * @param data data response
     * @return create list
     */
    private static List<FilterRequest> getFilterRequests(Object data) {
        if (data == null) {
            return null;
        }
        return ((List<LinkedHashMap<String, Object>>) data).stream()
                .map(fr -> new FilterRequest((String) fr.get("field"), (String) fr.get("value"), (String) fr.get("operation")))
                .collect(Collectors.toList());
    }
}