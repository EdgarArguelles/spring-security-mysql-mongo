package app.security.controllers;

import app.controllers.CtrlTest;
import app.factories.ResponseFactory;
import app.security.pojos.AccountCredentials;
import app.security.pojos.LoggedUser;
import app.security.services.SecurityService;
import app.security.services.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.*;
import java.util.stream.IntStream;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SecurityCtrlTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Captor
    private ArgumentCaptor<Exception> captor;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private ResponseFactory responseFactory;

    @MockBean
    private SecurityService securityService;

    private final String INVALID_TOKEN = "invalid";

    private final String VALID_TOKEN = "valid";

    private CtrlTest ctrlTest;

    @Before
    public void setup() throws Exception {
        ctrlTest = new CtrlTest(mvc, captor, tokenService, responseFactory, INVALID_TOKEN, VALID_TOKEN);
    }

    /**
     * Should return a BAD_REQUEST error response when null values
     */
    @Test
    public void loginWhenNull() throws Exception {
        final AccountCredentials credentials = new AccountCredentials();
        final String requestJson = mapper.writeValueAsString(credentials);
        final String bodyExpected = "error";
        given(responseFactory.error(any(Exception.class))).willReturn(new ResponseEntity(bodyExpected, HttpStatus.BAD_REQUEST));

        final MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.post("/login")
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON);

        final String bodyResult = mvc.perform(builder)
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        assertNotSame(bodyExpected, bodyResult);
        assertEquals(bodyExpected, bodyResult);
        verify(securityService, never()).authenticate(any());
        verify(tokenService, never()).createToken(any());
        verify(tokenService, never()).getLoggedUser(any());
        verify(responseFactory, times(1)).error(captor.capture());
        assertTrue(captor.getValue() instanceof MethodArgumentNotValidException);
    }

    /**
     * Should return a BAD_REQUEST error response when empty values
     */
    @Test
    public void loginWhenEmpty() throws Exception {
        final AccountCredentials credentials = new AccountCredentials("", "");
        final String requestJson = mapper.writeValueAsString(credentials);
        final String bodyExpected = "error";
        given(responseFactory.error(any(Exception.class))).willReturn(new ResponseEntity(bodyExpected, HttpStatus.BAD_REQUEST));

        final MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.post("/login")
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON);

        final String bodyResult = mvc.perform(builder)
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        assertNotSame(bodyExpected, bodyResult);
        assertEquals(bodyExpected, bodyResult);
        verify(securityService, never()).authenticate(any());
        verify(tokenService, never()).createToken(any());
        verify(tokenService, never()).getLoggedUser(any());
        verify(responseFactory, times(1)).error(captor.capture());
        assertTrue(captor.getValue() instanceof MethodArgumentNotValidException);
    }

    /**
     * Should return a BAD_REQUEST error response when bigger than max values
     */
    @Test
    public void loginWhenMax() throws Exception {
        final StringBuffer longText = new StringBuffer();
        IntStream.range(0, 256).forEach(i -> longText.append("a"));
        final AccountCredentials credentials = new AccountCredentials(longText.toString(), longText.toString());
        final String requestJson = mapper.writeValueAsString(credentials);
        final String bodyExpected = "error";
        given(responseFactory.error(any(Exception.class))).willReturn(new ResponseEntity(bodyExpected, HttpStatus.BAD_REQUEST));

        final MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.post("/login")
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON);

        final String bodyResult = mvc.perform(builder)
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        assertNotSame(bodyExpected, bodyResult);
        assertEquals(bodyExpected, bodyResult);
        verify(securityService, never()).authenticate(any());
        verify(tokenService, never()).createToken(any());
        verify(tokenService, never()).getLoggedUser(any());
        verify(responseFactory, times(1)).error(captor.capture());
        assertTrue(captor.getValue() instanceof MethodArgumentNotValidException);
    }

    /**
     * Should return a OK response
     */
    @Test
    public void loginWhenOK() throws Exception {
        final StringBuffer longText = new StringBuffer();
        IntStream.range(0, 255).forEach(i -> longText.append("a"));
        final AccountCredentials credentials = new AccountCredentials(longText.toString(), longText.toString());
        final String requestJson = mapper.writeValueAsString(credentials);

        final MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.post("/login")
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON);

        final LoggedUser loggedUserMocked = new LoggedUser("ID", "full name", "R1", new HashSet<>(Arrays.asList("P1", "P2", "P3")));
        final String tokenExpected = "token";
        final LoggedUser loggedUserExpected = new LoggedUser("ID", "full name", "R1", new HashSet<>(Arrays.asList("P1", "P2", "P3")));
        given(securityService.authenticate(credentials)).willReturn(loggedUserMocked);
        given(tokenService.createToken(loggedUserMocked)).willReturn(tokenExpected);
        given(tokenService.getLoggedUser(tokenExpected)).willReturn(loggedUserExpected);

        final String bodyResult = mvc.perform(builder)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        final Map mapResult = mapper.readValue(bodyResult, HashMap.class);
        final Map loggedUserResult = (Map) mapResult.get("loggedUser");
        final String tokenResult = (String) mapResult.get("token");

        assertEquals(loggedUserExpected.getId(), loggedUserResult.get("id"));
        assertEquals(loggedUserExpected.getFullName(), loggedUserResult.get("fullName"));
        assertEquals(loggedUserExpected.getRole(), loggedUserResult.get("role"));
        assertEquals(loggedUserExpected.getPermissions(), new HashSet<>((List) loggedUserResult.get("permissions")));
        assertEquals(tokenExpected, tokenResult);
        verify(securityService, times(1)).authenticate(credentials);
        verify(tokenService, times(1)).createToken(loggedUserMocked);
        verify(tokenService, times(1)).getLoggedUser(tokenExpected);
    }

    /**
     * Should return an UNAUTHORIZED error response when not token
     */
    @Test
    public void changeRoleNotToken() throws Exception {
        ctrlTest.postNotToken("/change_role/5");
        verify(securityService, never()).changeRole(any());
        verify(tokenService, never()).createToken(any());
        verify(tokenService, never()).getLoggedUser(any());
    }

    /**
     * Should return an UNAUTHORIZED error response when token invalid
     */
    @Test
    public void changeRoleInvalid() throws Exception {
        ctrlTest.postInvalid("/change_role/5");
        verify(securityService, never()).changeRole(any());
        verify(tokenService, never()).createToken(any());
        verify(tokenService, times(1)).getLoggedUser(INVALID_TOKEN);
    }

    /**
     * Should return a OK when token valid
     */
    @Test
    public void changeRoleNotPermission() throws Exception {
        final String ID = "R1";
        final MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.post("/change_role/" + ID)
                        .header("Authorization", "Bearer " + VALID_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON);

        final LoggedUser loggedUserMocked = new LoggedUser("ID", "full name", "R1", new HashSet<>(Arrays.asList("P1", "P2", "P3")));
        final String tokenExpected = "token";
        final LoggedUser loggedUserExpected = new LoggedUser("ID", "full name", "R1", new HashSet<>(Arrays.asList("P1", "P2", "P3")));
        given(securityService.changeRole(ID)).willReturn(loggedUserMocked);
        given(tokenService.createToken(loggedUserMocked)).willReturn(tokenExpected);
        given(tokenService.getLoggedUser(tokenExpected)).willReturn(loggedUserExpected);

        final String bodyResult = mvc.perform(builder)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        final Map mapResult = mapper.readValue(bodyResult, HashMap.class);
        final Map loggedUserResult = (Map) mapResult.get("loggedUser");
        final String tokenResult = (String) mapResult.get("token");

        assertEquals(loggedUserExpected.getId(), loggedUserResult.get("id"));
        assertEquals(loggedUserExpected.getFullName(), loggedUserResult.get("fullName"));
        assertEquals(loggedUserExpected.getRole(), loggedUserResult.get("role"));
        assertEquals(loggedUserExpected.getPermissions(), new HashSet<>((List) loggedUserResult.get("permissions")));
        assertEquals(tokenExpected, tokenResult);
        verify(tokenService, times(1)).getLoggedUser(VALID_TOKEN);
        verify(securityService, times(1)).changeRole(ID);
        verify(tokenService, times(1)).createToken(loggedUserMocked);
        verify(tokenService, times(1)).getLoggedUser(tokenExpected);
    }

    /**
     * Should return an UNAUTHORIZED error response when not token
     */
    @Test
    public void pingNotToken() throws Exception {
        ctrlTest.getNotToken("/ping");
        verify(securityService, never()).getLoggedUser();
        verify(responseFactory, never()).success(any());
    }

    /**
     * Should return an UNAUTHORIZED error response when token invalid
     */
    @Test
    public void pingInvalid() throws Exception {
        ctrlTest.getInvalid("/ping");
        verify(securityService, never()).getLoggedUser();
        verify(responseFactory, never()).success(any());
    }

    /**
     * Should return logged user when token valid
     */
    @Test
    public void pingNotPermission() throws Exception {
        final MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.get("/ping")
                        .header("Authorization", "Bearer " + VALID_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON);

        final LoggedUser loggedUser = new LoggedUser("ID", "ROLE");
        final String bodyExpected = "OK";
        given(securityService.getLoggedUser()).willReturn(loggedUser);
        given(responseFactory.success(loggedUser)).willReturn(new ResponseEntity(bodyExpected, HttpStatus.OK));

        final String bodyResult = mvc.perform(builder)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertNotSame(bodyExpected, bodyResult);
        assertEquals(bodyExpected, bodyResult);
        verify(tokenService, times(1)).getLoggedUser(VALID_TOKEN);
        verify(securityService, times(1)).getLoggedUser();
        verify(responseFactory, times(1)).success(loggedUser);
    }
}