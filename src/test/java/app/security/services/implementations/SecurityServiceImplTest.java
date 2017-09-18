package app.security.services.implementations;

import app.exceptions.AppDontFoundException;
import app.exceptions.AppValidationException;
import app.models.Authentication;
import app.models.Permission;
import app.models.Person;
import app.models.Role;
import app.repositories.AuthenticationRepository;
import app.repositories.PersonRepository;
import app.security.pojos.AccountCredentials;
import app.security.pojos.LoggedUser;
import app.security.services.SecurityService;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SecurityServiceImplTest {

    @Autowired
    private SecurityService securityService;

    @MockBean
    private AuthenticationRepository authenticationRepository;

    @MockBean
    private PersonRepository personRepository;

    /**
     * Should throw AppDontFoundException when authentication null
     */
    @Test(expected = AppDontFoundException.class)
    public void authenticateAuthenticationNull() {
        final String USERNAME = "user";
        final AccountCredentials credentials = new AccountCredentials(USERNAME, null);
        given(authenticationRepository.findByUsername(USERNAME)).willReturn(null);

        securityService.authenticate(credentials);
    }

    /**
     * Should throw AppDontFoundException when password incorrect
     */
    @Test(expected = AppDontFoundException.class)
    public void authenticatePasswordIncorrect() {
        final String USERNAME = "user";
        final String PASSWORD = "pass";
        final AccountCredentials credentials = new AccountCredentials(USERNAME, PASSWORD);
        final Authentication authentication = new Authentication(USERNAME, PASSWORD, null, null);
        given(authenticationRepository.findByUsername(USERNAME)).willReturn(authentication);

        securityService.authenticate(credentials);
    }

    /**
     * Should throw AppValidationException when person null
     */
    @Test(expected = AppValidationException.class)
    public void authenticatePersonNull() {
        final String USERNAME = "user";
        final String PASSWORD = "pass";
        final AccountCredentials credentials = new AccountCredentials(USERNAME, PASSWORD);
        final Authentication authentication = new Authentication(USERNAME, DigestUtils.sha512Hex(PASSWORD), null, null);
        given(authenticationRepository.findByUsername(USERNAME)).willReturn(authentication);

        securityService.authenticate(credentials);
    }

    /**
     * Should throw AppValidationException when person's Roles is null
     */
    @Test(expected = AppValidationException.class)
    public void authenticateRolesNull() {
        final String USERNAME = "user";
        final String PASSWORD = "pass";
        final AccountCredentials credentials = new AccountCredentials(USERNAME, PASSWORD);
        final Person person = new Person("P1");
        final Authentication authentication = new Authentication(USERNAME, DigestUtils.sha512Hex(PASSWORD), null, person);
        given(authenticationRepository.findByUsername(USERNAME)).willReturn(authentication);

        securityService.authenticate(credentials);
    }

    /**
     * Should throw AppValidationException when person's Roles is empty
     */
    @Test(expected = AppValidationException.class)
    public void authenticateRolesEmpty() {
        final String USERNAME = "user";
        final String PASSWORD = "pass";
        final AccountCredentials credentials = new AccountCredentials(USERNAME, PASSWORD);
        final Person person = new Person("P1");
        person.setRoles(Collections.EMPTY_SET);
        final Authentication authentication = new Authentication(USERNAME, DigestUtils.sha512Hex(PASSWORD), null, person);
        given(authenticationRepository.findByUsername(USERNAME)).willReturn(authentication);

        securityService.authenticate(credentials);
    }

    /**
     * Should return a LoggedUser when credentials correct with Permissions null
     */
    @Test
    public void authenticateCorrectWithPermissionsNull() {
        final String USERNAME = "user";
        final String PASSWORD = "pass";
        final AccountCredentials credentials = new AccountCredentials(USERNAME, PASSWORD);
        final Person person = new Person("P1");
        person.setName("Name");
        person.setLastName("Last Name");
        person.setRoles(new HashSet<>(Arrays.asList(new Role("R1"))));
        final Authentication authentication = new Authentication(USERNAME, DigestUtils.sha512Hex(PASSWORD), null, person);
        given(authenticationRepository.findByUsername(USERNAME)).willReturn(authentication);

        final LoggedUser loggedUserExpected = new LoggedUser("P1", "Name Last Name", "R1", new HashSet<>());

        final LoggedUser loggedUserResult = securityService.authenticate(credentials);

        assertNotSame(loggedUserExpected, loggedUserResult);
        assertEquals(loggedUserExpected, loggedUserResult);
        verify(authenticationRepository, times(1)).findByUsername(USERNAME);
    }

    /**
     * Should return a LoggedUser when credentials correct with Permissions empty
     */
    @Test
    public void authenticateCorrectWithPermissionsEmpty() {
        final String USERNAME = "user";
        final String PASSWORD = "pass";
        final AccountCredentials credentials = new AccountCredentials(USERNAME, PASSWORD);
        final Role role = new Role("R1");
        role.setPermissions(Collections.EMPTY_SET);
        final Person person = new Person("P1");
        person.setName("Name");
        person.setLastName("Last Name");
        person.setRoles(new HashSet<>(Arrays.asList(role)));
        final Authentication authentication = new Authentication(USERNAME, DigestUtils.sha512Hex(PASSWORD), null, person);
        given(authenticationRepository.findByUsername(USERNAME)).willReturn(authentication);

        final LoggedUser loggedUserExpected = new LoggedUser("P1", "Name Last Name", "R1", new HashSet<>());

        final LoggedUser loggedUserResult = securityService.authenticate(credentials);

        assertNotSame(loggedUserExpected, loggedUserResult);
        assertEquals(loggedUserExpected, loggedUserResult);
        verify(authenticationRepository, times(1)).findByUsername(USERNAME);
    }

    /**
     * Should return a LoggedUser when credentials correct with Permissions
     */
    @Test
    public void authenticateCorrectWithPermissions() {
        final String USERNAME = "user";
        final String PASSWORD = "pass";
        final AccountCredentials credentials = new AccountCredentials(USERNAME, PASSWORD);
        final Role role = new Role("R1");
        role.setPermissions(new HashSet<>(Arrays.asList(new Permission("PP1", "D"), new Permission("PP2", "D2"), new Permission("PP1", "D3"))));
        final Person person = new Person("P1");
        person.setName("Name");
        person.setLastName("Last Name");
        person.setRoles(new HashSet<>(Arrays.asList(role)));
        final Authentication authentication = new Authentication(USERNAME, DigestUtils.sha512Hex(PASSWORD), null, person);
        given(authenticationRepository.findByUsername(USERNAME)).willReturn(authentication);

        final LoggedUser loggedUserExpected = new LoggedUser("P1", "Name Last Name", "R1", new HashSet<>(Arrays.asList("PP1", "PP2")));

        final LoggedUser loggedUserResult = securityService.authenticate(credentials);

        assertNotSame(loggedUserExpected, loggedUserResult);
        assertEquals(loggedUserExpected, loggedUserResult);
        verify(authenticationRepository, times(1)).findByUsername(USERNAME);
    }

    /**
     * Should throw AppValidationException when not context
     */
    @Test(expected = AppValidationException.class)
    public void changeRoleNotContext() {
        SecurityContextHolder.getContext().setAuthentication(null);
        final String ROLE_ID = "R1";

        securityService.changeRole(ROLE_ID);
    }

    /**
     * Should throw AppValidationException when person null
     */
    @Test(expected = AppValidationException.class)
    public void changeRolePersonNull() {
        final String PERSON_ID = "P1";
        final LoggedUser userMocked = new LoggedUser(PERSON_ID, "ROLE");
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userMocked, null));

        final String ROLE_ID = "R1";
        given(personRepository.findOne(PERSON_ID)).willReturn(null);

        securityService.changeRole(ROLE_ID);
    }

    /**
     * Should throw AppValidationException when person's Roles is null
     */
    @Test(expected = AppValidationException.class)
    public void changeRoleRolesNull() {
        final String PERSON_ID = "P1";
        final LoggedUser userMocked = new LoggedUser(PERSON_ID, "ROLE");
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userMocked, null));

        final String ROLE_ID = "R1";
        final Person person = new Person("P1");
        given(personRepository.findOne(PERSON_ID)).willReturn(person);

        securityService.changeRole(ROLE_ID);
    }

    /**
     * Should throw AppValidationException when person's Roles is empty
     */
    @Test(expected = AppValidationException.class)
    public void changeRoleRolesEmpty() {
        final String PERSON_ID = "P1";
        final LoggedUser userMocked = new LoggedUser(PERSON_ID, "ROLE");
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userMocked, null));

        final String ROLE_ID = "R1";
        final Person person = new Person("P1");
        person.setRoles(Collections.EMPTY_SET);
        given(personRepository.findOne(PERSON_ID)).willReturn(person);

        securityService.changeRole(ROLE_ID);
    }

    /**
     * Should throw AppValidationException when requested role id doesn't belong to person
     */
    @Test(expected = AppValidationException.class)
    public void changeRoleIncorrectRoleId() {
        final String PERSON_ID = "P1";
        final LoggedUser userMocked = new LoggedUser(PERSON_ID, "ROLE");
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userMocked, null));

        final String ROLE_ID = "R0";
        final Person person = new Person("P1");
        person.setRoles(new HashSet<>(Arrays.asList(new Role("R1"), new Role("R2"), new Role("R3"))));
        given(personRepository.findOne(PERSON_ID)).willReturn(person);

        securityService.changeRole(ROLE_ID);
    }

    /**
     * Should return a LoggedUser when correct with Permissions null
     */
    @Test
    public void changeRoleCorrectWithPermissionsNull() {
        final String PERSON_ID = "P1";
        final LoggedUser userMocked = new LoggedUser(PERSON_ID, "ROLE");
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userMocked, null));

        final String ROLE_ID = "R2";
        final Person person = new Person("P1");
        person.setName("Name");
        person.setLastName("Last Name");
        person.setRoles(new HashSet<>(Arrays.asList(new Role("R1"), new Role("R2"), new Role("R3"))));
        given(personRepository.findOne(PERSON_ID)).willReturn(person);

        final LoggedUser loggedUserExpected = new LoggedUser("P1", "Name Last Name", "R2", new HashSet<>());

        final LoggedUser loggedUserResult = securityService.changeRole(ROLE_ID);

        assertNotSame(loggedUserExpected, loggedUserResult);
        assertEquals(loggedUserExpected, loggedUserResult);
        verify(personRepository, times(1)).findOne(PERSON_ID);
    }

    /**
     * Should return a LoggedUser when correct with Permissions empty
     */
    @Test
    public void changeRoleCorrectWithPermissionsEmpty() {
        final String PERSON_ID = "P1";
        final LoggedUser userMocked = new LoggedUser(PERSON_ID, "ROLE");
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userMocked, null));

        final String ROLE_ID = "R2";
        final Role role = new Role("R2");
        role.setPermissions(Collections.EMPTY_SET);
        final Person person = new Person("P1");
        person.setName("Name");
        person.setLastName("Last Name");
        person.setRoles(new HashSet<>(Arrays.asList(new Role("R1"), role, new Role("R3"))));
        given(personRepository.findOne(PERSON_ID)).willReturn(person);

        final LoggedUser loggedUserExpected = new LoggedUser("P1", "Name Last Name", "R2", new HashSet<>());

        final LoggedUser loggedUserResult = securityService.changeRole(ROLE_ID);

        assertNotSame(loggedUserExpected, loggedUserResult);
        assertEquals(loggedUserExpected, loggedUserResult);
        verify(personRepository, times(1)).findOne(PERSON_ID);
    }

    /**
     * Should return a LoggedUser when correct with Permissions
     */
    @Test
    public void changeRoleCorrectWithPermissions() {
        final String PERSON_ID = "P1";
        final LoggedUser userMocked = new LoggedUser(PERSON_ID, "ROLE");
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userMocked, null));

        final String ROLE_ID = "R2";
        final Role role = new Role("R2");
        role.setPermissions(new HashSet<>(Arrays.asList(new Permission("PP1", "D"), new Permission("PP2", "D2"), new Permission("PP1", "D3"))));
        final Person person = new Person("P1");
        person.setName("Name");
        person.setLastName("Last Name");
        person.setRoles(new HashSet<>(Arrays.asList(new Role("R1"), role, new Role("R3"))));
        given(personRepository.findOne(PERSON_ID)).willReturn(person);

        final LoggedUser loggedUserExpected = new LoggedUser("P1", "Name Last Name", "R2", new HashSet<>(Arrays.asList("PP1", "PP2")));

        final LoggedUser loggedUserResult = securityService.changeRole(ROLE_ID);

        assertNotSame(loggedUserExpected, loggedUserResult);
        assertEquals(loggedUserExpected, loggedUserResult);
        verify(personRepository, times(1)).findOne(PERSON_ID);
    }

    /**
     * Should return null when not context
     */
    @Test
    public void getLoggedUserNotContext() {
        SecurityContextHolder.getContext().setAuthentication(null);

        final LoggedUser userResult = securityService.getLoggedUser();

        assertNull(userResult);
    }

    /**
     * Should return null when context invalid
     */
    @Test
    public void getLoggedUserInvalid() {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(new Person("ID"), null));

        final LoggedUser userResult = securityService.getLoggedUser();

        assertNull(userResult);
    }

    /**
     * Should return a LoggedUser when context valid
     */
    @Test
    public void getLoggedUserCorrect() {
        final LoggedUser userMocked = new LoggedUser("ID", "ROLE");

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userMocked, null));

        final LoggedUser userExpected = new LoggedUser("ID", "ROLE");

        final LoggedUser userResult = securityService.getLoggedUser();

        assertSame(userMocked, userResult);
        assertNotSame(userExpected, userResult);
        assertEquals(userExpected, userResult);
    }

    /**
     * Should hash value
     */
    @Test
    public void hashValue() {
        final String VALUE = "test";
        final String HASHED = DigestUtils.sha512Hex(VALUE);

        final String RESULT = securityService.hashValue(VALUE);

        assertNotSame(HASHED, RESULT);
        assertEquals(HASHED, RESULT);
    }
}