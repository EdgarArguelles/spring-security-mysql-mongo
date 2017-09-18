package app.integration_test;

import app.models.Person;
import app.models.Role;
import app.repositories.*;
import app.security.pojos.LoggedUser;
import app.security.services.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SecurityCtrlIntegrationTest {

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

    private final String NEW_TOKEN = "new";

    private final String TOKEN1 = "token1";

    private final String TOKEN2 = "token2";

    private final String TOKEN3 = "token3";

    @Before
    public void setup() throws Exception {
        IntegrationTest.cleanAllData(authenticationRepository, authProviderRepository, personRepository, roleRepository, permissionRepository);

        dbRoles = Arrays.asList(
                new Role("R1", "D1", null),
                new Role("R2", "D2", null)
        );
        roleRepository.save(dbRoles);

        dbPeople = Arrays.asList(
                new Person("N1", "LN1", LocalDate.now(), 1, "A", null, Collections.EMPTY_SET),
                new Person("N2", "LN2", LocalDate.now(), 2, "B", null, new HashSet<>(dbRoles))
        );
        personRepository.save(dbPeople);

        given(tokenService.refreshToken()).willReturn(NEW_TOKEN);
        given(tokenService.createToken(any(LoggedUser.class))).willReturn(NEW_TOKEN);
        given(tokenService.getLoggedUser(NEW_TOKEN)).willReturn(new LoggedUser());
        given(tokenService.getLoggedUser(TOKEN1)).willReturn(new LoggedUser("ID", "FN", "R", Collections.EMPTY_SET));
        given(tokenService.getLoggedUser(TOKEN2)).willReturn(new LoggedUser(dbPeople.get(0).getId(), "FN", "R", Collections.EMPTY_SET));
        given(tokenService.getLoggedUser(TOKEN3)).willReturn(new LoggedUser(dbPeople.get(1).getId(), "FN", "R", Collections.EMPTY_SET));
    }

    /**
     * Should return a BAD_REQUEST error response
     */
    @Test
    public void changeRoleWhenNotPerson() throws Exception {
        final String ERROR_EXPECTED = "User doesn't have personal information associated.";

        final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/change_role/abc")
                .header("Authorization", "Bearer " + TOKEN1)
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
        verify(tokenService, times(1)).getLoggedUser(TOKEN1);
        verify(tokenService, never()).refreshToken();
    }

    /**
     * Should return a BAD_REQUEST error response
     */
    @Test
    public void changeRoleWhenNotRoles() throws Exception {
        final String ERROR_EXPECTED = "User doesn't have Roles associated.";

        final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/change_role/abc")
                .header("Authorization", "Bearer " + TOKEN2)
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
        verify(tokenService, times(1)).getLoggedUser(TOKEN2);
        verify(tokenService, never()).refreshToken();
    }

    /**
     * Should return a BAD_REQUEST error response
     */
    @Test
    public void changeRoleWhenNotValidRole() throws Exception {
        final String ERROR_EXPECTED = "User doesn't have the requested Role.";

        final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/change_role/abc")
                .header("Authorization", "Bearer " + TOKEN3)
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
        verify(tokenService, times(1)).getLoggedUser(TOKEN3);
        verify(tokenService, never()).refreshToken();
    }

    /**
     * Should get loggedUser and token
     */
    @Test
    public void changeRoleWhenSuccess() throws Exception {
        final String ID = dbRoles.get(0).getId();
        final Person person = dbPeople.get(1);
        final LoggedUser loggedUser = new LoggedUser(person.getId(), person.getFullName(), ID, Collections.EMPTY_SET);
        final LoggedUser loggedUserExpected = new LoggedUser();

        final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/change_role/" + ID)
                .header("Authorization", "Bearer " + TOKEN3)
                .contentType(MediaType.APPLICATION_JSON);

        final String bodyResult = mvc.perform(builder)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        final Map mapResult = mapper.readValue(bodyResult, HashMap.class);
        final Map mapUser = (Map) mapResult.get("loggedUser");
        final LoggedUser loggedUserResult = new LoggedUser((String) mapUser.get("id"), (String) mapUser.get("fullName"),
                (String) mapUser.get("role"), (Set) mapUser.get("permissions"));

        assertNotSame(loggedUserExpected, loggedUserResult);
        assertEquals(loggedUserExpected, loggedUserResult);
        assertNotSame(NEW_TOKEN, mapResult.get("token"));
        assertEquals(NEW_TOKEN, mapResult.get("token"));
        verify(tokenService, times(1)).createToken(loggedUser);
        verify(tokenService, times(1)).getLoggedUser(NEW_TOKEN);
        verify(tokenService, times(1)).getLoggedUser(TOKEN3);
        verify(tokenService, never()).refreshToken();
    }

    /**
     * Should get loggedUser
     */
    @Test
    public void ping() throws Exception {
        final Person person = dbPeople.get(1);
        final LoggedUser loggedUserExpected = new LoggedUser(person.getId(), "FN", "R", Collections.EMPTY_SET);

        final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/ping")
                .header("Authorization", "Bearer " + TOKEN3)
                .contentType(MediaType.APPLICATION_JSON);

        final String bodyResult = mvc.perform(builder)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        final Map mapResult = mapper.readValue(bodyResult, HashMap.class);
        final Map mapUser = (Map) mapResult.get("data");
        final LoggedUser loggedUserResult = new LoggedUser((String) mapUser.get("id"), (String) mapUser.get("fullName"),
                (String) mapUser.get("role"), Collections.EMPTY_SET);

        assertNotSame(loggedUserExpected, loggedUserResult);
        assertEquals(loggedUserExpected, loggedUserResult);
        assertNotSame(NEW_TOKEN, mapResult.get("newToken"));
        assertEquals(NEW_TOKEN, mapResult.get("newToken"));
        verify(tokenService, times(1)).getLoggedUser(TOKEN3);
        verify(tokenService, times(1)).refreshToken();
    }
}