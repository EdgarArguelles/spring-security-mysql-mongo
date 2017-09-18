package app.services.implementations;

import app.exceptions.AppDontFoundException;
import app.exceptions.AppValidationException;
import app.models.AuthProvider;
import app.models.Authentication;
import app.models.Person;
import app.pojos.pages.PageDataRequest;
import app.repositories.AuthenticationRepository;
import app.security.services.SecurityService;
import app.services.AuthenticationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AuthenticationServiceImplTest {

    @Autowired
    private AuthenticationService authenticationService;

    @MockBean
    private AuthenticationRepository authenticationRepository;

    @MockBean
    private SecurityService securityService;

    /**
     * Should call findAll function
     */
    @Test
    public void findAll() {
        final List<Authentication> authenticationsMocked = Arrays.asList(
                new Authentication("ID1"), new Authentication("ID2"), null, new Authentication("ID4"));
        given(authenticationRepository.findAll()).willReturn(authenticationsMocked);

        final List<Authentication> authenticationsExpected = Arrays.asList(
                new Authentication("ID1"), new Authentication("ID2"), null, new Authentication("ID4"));

        final List<Authentication> authenticationsResult = authenticationService.findAll();

        assertSame(authenticationsMocked, authenticationsResult);
        assertNotSame(authenticationsExpected, authenticationsResult);
        assertEquals(authenticationsExpected, authenticationsResult);
        verify(authenticationRepository, times(1)).findAll();
    }

    /**
     * Should call findOne function
     */
    @Test
    public void findById() {
        final String ID = "ID";
        final Authentication authenticationMocked = new Authentication(ID);
        given(authenticationRepository.findOne(ID)).willReturn(authenticationMocked);

        final Authentication authenticationExpected = new Authentication(ID);

        final Authentication authenticationResult = authenticationService.findById(ID);

        assertSame(authenticationMocked, authenticationResult);
        assertNotSame(authenticationExpected, authenticationResult);
        assertEquals(authenticationExpected, authenticationResult);
        verify(authenticationRepository, times(1)).findOne(ID);
    }

    /**
     * Should throw AppDontFoundException when null
     */
    @Test(expected = AppDontFoundException.class)
    public void findByIdNotNullWhenNull() {
        final String ID = "ID";
        given(authenticationRepository.findOne(ID)).willReturn(null);

        authenticationService.findByIdNotNull(ID);
    }

    /**
     * Should return an authentication when not null
     */
    @Test
    public void findByIdNotNullWhenNotNull() {
        final String ID = "ID";
        final Authentication authenticationMocked = new Authentication(ID);
        given(authenticationRepository.findOne(ID)).willReturn(authenticationMocked);

        final Authentication authenticationExpected = new Authentication(ID);

        final Authentication authenticationResult = authenticationService.findByIdNotNull(ID);

        assertSame(authenticationMocked, authenticationResult);
        assertNotSame(authenticationExpected, authenticationResult);
        assertEquals(authenticationExpected, authenticationResult);
        verify(authenticationRepository, times(1)).findOne(ID);
    }

    /**
     * Should call findByUsername function
     */
    @Test
    public void findByUsername() {
        final String USERNAME = "test";
        final Authentication authenticationMocked = new Authentication(USERNAME, null, null, null);
        given(authenticationRepository.findByUsername(USERNAME)).willReturn(authenticationMocked);

        final Authentication authenticationExpected = new Authentication(USERNAME, null, null, null);

        final Authentication authenticationResult = authenticationService.findByUsername(USERNAME);

        assertSame(authenticationMocked, authenticationResult);
        assertNotSame(authenticationExpected, authenticationResult);
        assertEquals(authenticationExpected, authenticationResult);
        verify(authenticationRepository, times(1)).findByUsername(USERNAME);
    }

    /**
     * Should call findByAuthProviderAndPerson function
     */
    @Test
    public void findByAuthProviderAndPerson() {
        final AuthProvider AUTHPROVIDER = new AuthProvider("AP1");
        final Person PERSON = new Person("P1");
        final Authentication authenticationMocked = new Authentication(null, null, AUTHPROVIDER, PERSON);
        given(authenticationRepository.findByAuthProviderAndPerson(AUTHPROVIDER, PERSON)).willReturn(authenticationMocked);

        final Authentication authenticationExpected = new Authentication(null, null, AUTHPROVIDER, PERSON);

        final Authentication authenticationResult = authenticationService.findByAuthProviderAndPerson(AUTHPROVIDER, PERSON);

        assertSame(authenticationMocked, authenticationResult);
        assertNotSame(authenticationExpected, authenticationResult);
        assertEquals(authenticationExpected, authenticationResult);
        verify(authenticationRepository, times(1)).findByAuthProviderAndPerson(AUTHPROVIDER, PERSON);
    }

    /**
     * Should throw AppValidationException when username duplicated
     */
    @Test(expected = AppValidationException.class)
    public void saveUsernameDuplicate() {
        final String USERNAME = "test";
        final Authentication authentication = new Authentication(USERNAME, null, null, null);
        given(authenticationRepository.findByUsername(USERNAME)).willReturn(authentication);

        authenticationService.save(authentication);
    }

    /**
     * Should throw AppalidationException when AuthProvider and person duplicated
     */
    @Test(expected = AppValidationException.class)
    public void saveAuthProviderPersonDuplicate() {
        final AuthProvider AUTHPROVIDER = new AuthProvider("AP1");
        final Person PERSON = new Person("P1");
        final Authentication authentication = new Authentication(null, null, AUTHPROVIDER, PERSON);
        given(authenticationRepository.findByAuthProviderAndPerson(AUTHPROVIDER, PERSON)).willReturn(authentication);

        authenticationService.save(authentication);
    }

    /**
     * Should return an authentication when save successfully
     */
    @Test
    public void saveSuccessfully() {
        final String USERNAME = "test";
        final String PASSWORD = "pass";
        final String PASSWORD_HASH = "pass hash";
        final AuthProvider AUTHPROVIDER = new AuthProvider("AP1");
        final Person PERSON = new Person("P1");
        final Authentication authentication = new Authentication(USERNAME, PASSWORD, AUTHPROVIDER, PERSON);
        given(authenticationRepository.findByUsername(USERNAME)).willReturn(null);
        given(authenticationRepository.findByAuthProviderAndPerson(AUTHPROVIDER, PERSON)).willReturn(null);
        given(securityService.hashValue(PASSWORD)).willReturn(PASSWORD_HASH);
        given(authenticationRepository.save(authentication)).willReturn(authentication);

        final Authentication authenticationExpected = new Authentication(USERNAME, PASSWORD_HASH, AUTHPROVIDER, PERSON);

        final Authentication authenticationResult = authenticationService.save(authentication);

        assertSame(authentication, authenticationResult);
        assertNotSame(authenticationExpected, authenticationResult);
        assertEquals(authenticationExpected, authenticationResult);
        verify(authenticationRepository, times(1)).findByUsername(USERNAME);
        verify(authenticationRepository, times(1)).findByAuthProviderAndPerson(AUTHPROVIDER, PERSON);
        verify(securityService, times(1)).hashValue(PASSWORD);
        verify(authenticationRepository, times(1)).save(authentication);
    }

    /**
     * Should throw AppDontFoundException when authentication doesn't exist
     */
    @Test(expected = AppDontFoundException.class)
    public void updateDontFound() {
        final String ID = "ID";
        final Authentication authentication = new Authentication(ID);
        given(authenticationRepository.findOne(ID)).willReturn(null);

        authenticationService.update(authentication);
    }

    /**
     * Should return an authentication when update successfully
     */
    @Test
    public void updateSuccessfully() {
        final String ID = "ID";
        final String USERNAME_AUTHENTICATION = "username after";
        final String USERNAME_ORIGINAL = "username before";
        final String PASSWORD_AUTHENTICATION = "pass after";
        final String PASSWORD_ORIGINAL = "pass before";
        final String PASSWORD_HASH = "pass hash";
        final AuthProvider AUTHPROVIDER_AUTHENTICATION = new AuthProvider("AP1");
        final AuthProvider AUTHPROVIDER_ORIGINAL = new AuthProvider("AP2");
        final Person PERSON_AUTHENTICATION = new Person("P1");
        final Person PERSON_ORIGINAL = new Person("P2");
        final Authentication authentication = new Authentication(USERNAME_AUTHENTICATION, PASSWORD_AUTHENTICATION, AUTHPROVIDER_AUTHENTICATION, PERSON_AUTHENTICATION);
        authentication.setId(ID);
        final Authentication authenticationOriginal = new Authentication(USERNAME_ORIGINAL, PASSWORD_ORIGINAL, AUTHPROVIDER_ORIGINAL, PERSON_ORIGINAL);
        authenticationOriginal.setId(ID);
        //only change password
        final Authentication authenticationMocked = new Authentication(USERNAME_ORIGINAL, PASSWORD_HASH, AUTHPROVIDER_ORIGINAL, PERSON_ORIGINAL);
        authenticationMocked.setId(ID);
        given(authenticationRepository.findOne(ID)).willReturn(authenticationOriginal);
        given(securityService.hashValue(PASSWORD_AUTHENTICATION)).willReturn(PASSWORD_HASH);
        given(authenticationRepository.save(authenticationOriginal)).willReturn(authenticationMocked);

        final Authentication authenticationExpected = new Authentication(USERNAME_ORIGINAL, PASSWORD_HASH, AUTHPROVIDER_ORIGINAL, PERSON_ORIGINAL);
        authenticationExpected.setId(ID);

        final Authentication authenticationResult = authenticationService.update(authentication);

        assertSame(authenticationMocked, authenticationResult);
        assertNotSame(authentication, authenticationResult);
        assertNotSame(authenticationOriginal, authenticationResult);
        assertNotSame(authenticationExpected, authenticationResult);
        assertEquals(authenticationExpected, authenticationResult);
        verify(authenticationRepository, times(1)).findOne(ID);
        verify(securityService, times(1)).hashValue(PASSWORD_AUTHENTICATION);
        verify(authenticationRepository, times(1)).save(authenticationOriginal);
    }

    /**
     * Should throw AppDontFoundException when authentication doesn't exist
     */
    @Test(expected = AppDontFoundException.class)
    public void deleteDontFound() {
        final String ID = "ID";
        given(authenticationRepository.findOne(ID)).willReturn(null);

        authenticationService.delete(ID);
    }

    /**
     * Should return an authentication when delete successfully
     */
    @Test
    public void deleteSuccessfully() {
        final String ID = "ID";
        final String USERNAME = "test";
        final String PASSWORD = "pass";
        final AuthProvider AUTHPROVIDER = new AuthProvider("AP1");
        final Person PERSON = new Person("P1");
        final Authentication authentication = new Authentication(USERNAME, PASSWORD, AUTHPROVIDER, PERSON);
        authentication.setId(ID);
        given(authenticationRepository.findOne(ID)).willReturn(authentication);
        doNothing().when(authenticationRepository).delete(authentication);

        final Authentication authenticationExpected = new Authentication(USERNAME, PASSWORD, AUTHPROVIDER, PERSON);
        authenticationExpected.setId(ID);

        final Authentication authenticationResult = authenticationService.delete(ID);

        assertSame(authentication, authenticationResult);
        assertNotSame(authenticationExpected, authenticationResult);
        assertEquals(authenticationExpected, authenticationResult);
        verify(authenticationRepository, times(1)).findOne(ID);
        verify(authenticationRepository, times(1)).delete(authentication);
    }

    /**
     * Should call page function
     */
    @Test
    public void page() {
        final PageDataRequest pageDataRequest = new PageDataRequest();
        final Page<Authentication> authenticationsMocked = new PageImpl(Arrays.asList(new Authentication("ID1"), new Authentication("ID2")));
        given(authenticationRepository.page(pageDataRequest)).willReturn(authenticationsMocked);

        final Page<Authentication> authenticationsExpected = new PageImpl(Arrays.asList(new Authentication("ID1"), new Authentication("ID2")));

        final Page<Authentication> authenticationsResult = authenticationService.page(pageDataRequest);

        assertSame(authenticationsMocked, authenticationsResult);
        assertNotSame(authenticationsExpected, authenticationsResult);
        assertEquals(authenticationsExpected, authenticationsResult);
        verify(authenticationRepository, times(1)).page(pageDataRequest);
    }
}