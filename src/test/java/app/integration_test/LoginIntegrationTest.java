package app.integration_test;

import app.models.AuthProvider;
import app.models.Authentication;
import app.models.Person;
import app.models.Role;
import app.repositories.*;
import app.security.pojos.AccountCredentials;
import app.security.pojos.LoggedUser;
import app.security.services.SecurityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.*;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class LoginIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private SecurityService securityService;

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

    private Role dbRole;

    @Before
    public void setup() throws Exception {
        IntegrationTest.cleanAllData(authenticationRepository, authProviderRepository, personRepository, roleRepository, permissionRepository);

        dbRole = roleRepository.save(new Role("R", "RD", null));
        dbPeople = Arrays.asList(
                new Person("N", "LN", LocalDate.now(), 1, "A", null, null),
                new Person("N2", "LN2", LocalDate.now(), 2, "B", null, new HashSet<>(Arrays.asList(dbRole)))
        );
        personRepository.save(dbPeople);
        AuthProvider authProvider = authProviderRepository.save(new AuthProvider("N", "D", null, null, null));
        authenticationRepository.save(Arrays.asList(
                new Authentication("user", securityService.hashValue("123"), authProvider, dbPeople.get(0)),
                new Authentication("user2", securityService.hashValue("123"), authProvider, dbPeople.get(1))
        ));
    }

    /**
     * Should return a NOT_FOUND error response
     */
    @Test
    public void loginWhenInvalidUsername() throws Exception {
        final String ERROR_EXPECTED = "Credentials incorrect.";

        final AccountCredentials c = new AccountCredentials("invalid", "123");
        final String requestJson = mapper.writeValueAsString(c);

        final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/login")
                .content(requestJson)
                .contentType(MediaType.APPLICATION_JSON);

        final String bodyResult = mvc.perform(builder)
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        final Map mapResult = mapper.readValue(bodyResult, HashMap.class);
        final Map errorResult = (Map) mapResult.get("error");

        assertNotSame(ERROR_EXPECTED, errorResult.get("message"));
        assertEquals(ERROR_EXPECTED, errorResult.get("message"));
        assertNull(errorResult.get("devMessage"));
        assertNull(errorResult.get("nestedErrors"));
    }

    /**
     * Should return a NOT_FOUND error response
     */
    @Test
    public void loginWhenInvalidPassword() throws Exception {
        final String ERROR_EXPECTED = "Credentials incorrect.";

        final AccountCredentials c = new AccountCredentials("user", "invalid");
        final String requestJson = mapper.writeValueAsString(c);

        final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/login")
                .content(requestJson)
                .contentType(MediaType.APPLICATION_JSON);

        final String bodyResult = mvc.perform(builder)
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        final Map mapResult = mapper.readValue(bodyResult, HashMap.class);
        final Map errorResult = (Map) mapResult.get("error");

        assertNotSame(ERROR_EXPECTED, errorResult.get("message"));
        assertEquals(ERROR_EXPECTED, errorResult.get("message"));
        assertNull(errorResult.get("devMessage"));
        assertNull(errorResult.get("nestedErrors"));
    }

    /**
     * Should return a BAD_REQUEST error response
     */
    @Test
    public void loginWhenNotRoles() throws Exception {
        final String ERROR_EXPECTED = "User doesn't have Roles associated.";

        final AccountCredentials c = new AccountCredentials("user", "123");
        final String requestJson = mapper.writeValueAsString(c);

        final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/login")
                .content(requestJson)
                .contentType(MediaType.APPLICATION_JSON);

        final String bodyResult = mvc.perform(builder)
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        final Map mapResult = mapper.readValue(bodyResult, HashMap.class);
        final Map errorResult = (Map) mapResult.get("error");

        assertNotSame(ERROR_EXPECTED, errorResult.get("message"));
        assertEquals(ERROR_EXPECTED, errorResult.get("message"));
        assertNull(errorResult.get("devMessage"));
        assertNull(errorResult.get("nestedErrors"));
    }

    /**
     * Should get loggedUser and token
     */
    @Test
    public void loginWhenSuccess() throws Exception {
        final Person person = dbPeople.get(1);
        final LoggedUser loggedUserExpected = new LoggedUser(person.getId(), person.getFullName(), dbRole.getId(), Collections.EMPTY_SET);

        final AccountCredentials c = new AccountCredentials("user2", "123");
        final String requestJson = mapper.writeValueAsString(c);

        final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/login")
                .content(requestJson)
                .contentType(MediaType.APPLICATION_JSON);

        final String bodyResult = mvc.perform(builder)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        final Map mapResult = mapper.readValue(bodyResult, HashMap.class);
        final Map mapUser = (Map) mapResult.get("loggedUser");
        final LoggedUser loggedUserResult = new LoggedUser((String) mapUser.get("id"), (String) mapUser.get("fullName"),
                (String) mapUser.get("role"), Collections.EMPTY_SET);

        assertNotSame(loggedUserExpected, loggedUserResult);
        assertEquals(loggedUserExpected, loggedUserResult);
        assertNotNull(mapResult.get("token"));
    }
}