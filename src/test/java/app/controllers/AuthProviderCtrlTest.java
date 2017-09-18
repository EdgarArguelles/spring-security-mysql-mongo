package app.controllers;

import app.factories.ResponseFactory;
import app.models.AuthProvider;
import app.security.pojos.LoggedUser;
import app.security.services.TokenService;
import app.services.AuthProviderService;
import app.services.PresentationService;
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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AuthProviderCtrlTest {

    @Autowired
    private MockMvc mvc;

    @Captor
    private ArgumentCaptor<Exception> captor;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private ResponseFactory responseFactory;

    @MockBean
    private PresentationService presentationService;

    @MockBean
    private AuthProviderService authProviderService;

    private final String INVALID_TOKEN = "invalid";

    private final String VALID_TOKEN = "valid";

    private final String CREATE_USERS_TOKEN = "create";

    private final String BASE_URL = "/auth_providers";

    private CtrlTest ctrlTest;

    @Before
    public void setup() throws Exception {
        ctrlTest = new CtrlTest(mvc, captor, tokenService, responseFactory, INVALID_TOKEN, VALID_TOKEN);

        given(tokenService.getLoggedUser(CREATE_USERS_TOKEN)).willReturn(
                new LoggedUser(null, null, null, new HashSet<>(Arrays.asList("CREATE_USERS"))));
    }

    /**
     * Should return an UNAUTHORIZED error response when not token
     */
    @Test
    public void listNotToken() throws Exception {
        ctrlTest.getNotToken(BASE_URL);
        verify(authProviderService, never()).findAll();
        verify(responseFactory, never()).success(any());
    }

    /**
     * Should return an UNAUTHORIZED error response when token invalid
     */
    @Test
    public void listInvalid() throws Exception {
        ctrlTest.getInvalid(BASE_URL);
        verify(authProviderService, never()).findAll();
        verify(responseFactory, never()).success(any());
    }

    /**
     * Should return an UNAUTHORIZED error response when not permissions
     */
    @Test
    public void listNotPermission() throws Exception {
        ctrlTest.getNotPermission(BASE_URL);
        verify(authProviderService, never()).findAll();
        verify(responseFactory, never()).success(any());
    }

    /**
     * Should return auth providers list when permission
     */
    @Test
    public void listSuccess() throws Exception {
        final MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.get(BASE_URL)
                        .header("Authorization", "Bearer " + CREATE_USERS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON);

        final String bodyExpected = "test";
        final List<AuthProvider> providers = Arrays.asList(new AuthProvider("AP1"), new AuthProvider("AP2"));
        given(authProviderService.findAll()).willReturn(providers);
        doNothing().when(presentationService).prepare(providers, false, false);
        given(responseFactory.success(providers)).willReturn(new ResponseEntity(bodyExpected, HttpStatus.OK));

        final String bodyResult = mvc.perform(builder)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertNotSame(bodyExpected, bodyResult);
        assertEquals(bodyExpected, bodyResult);
        verify(tokenService, times(1)).getLoggedUser(CREATE_USERS_TOKEN);
        verify(authProviderService, times(1)).findAll();
        verify(presentationService, times(1)).prepare(providers, false, false);
        verify(responseFactory, times(1)).success(providers);
    }
}