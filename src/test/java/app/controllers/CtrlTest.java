package app.controllers;

import app.exceptions.AppAuthenticationException;
import app.factories.ResponseFactory;
import app.security.pojos.LoggedUser;
import app.security.services.TokenService;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CtrlTest {

    private final MockMvc mvc;

    private final ArgumentCaptor<Exception> captor;

    private final TokenService tokenService;

    private final ResponseFactory responseFactory;

    private final String INVALID_TOKEN;

    private final String VALID_TOKEN;

    private final String ACCESS_DENIED_EXPECTED = "Access is denied.";

    public CtrlTest(MockMvc mvc, ArgumentCaptor<Exception> captor, TokenService tokenService, ResponseFactory responseFactory, String invalidToken, String validToken) throws Exception {
        this.mvc = mvc;
        this.captor = captor;
        this.tokenService = tokenService;
        this.responseFactory = responseFactory;
        this.INVALID_TOKEN = invalidToken;
        this.VALID_TOKEN = validToken;

        given(tokenService.getLoggedUser(INVALID_TOKEN)).willThrow(new IOException());
        given(tokenService.getLoggedUser(VALID_TOKEN)).willReturn(new LoggedUser());
    }

    /**
     * Should return an UNAUTHORIZED error response when not token
     *
     * @param url to be called with GET and not token
     */
    public void getNotToken(String url) throws Exception {
        evaluateNotToken(MockMvcRequestBuilders.get(url));
    }

    /**
     * Should return an UNAUTHORIZED error response when not token
     *
     * @param url to be called with POST and not token
     */
    public void postNotToken(String url) throws Exception {
        evaluateNotToken(MockMvcRequestBuilders.post(url));
    }

    /**
     * Should return an UNAUTHORIZED error response when not token
     *
     * @param url to be called with PUT and not token
     */
    public void putNotToken(String url) throws Exception {
        evaluateNotToken(MockMvcRequestBuilders.put(url));
    }

    /**
     * Should return an UNAUTHORIZED error response when not token
     *
     * @param url to be called with DELETE and not token
     */
    public void deleteNotToken(String url) throws Exception {
        evaluateNotToken(MockMvcRequestBuilders.delete(url));
    }

    /**
     * Should return an UNAUTHORIZED error response when token invalid
     *
     * @param url to be called with GET and Invalid token
     */
    public void getInvalid(String url) throws Exception {
        evaluateInvalid(MockMvcRequestBuilders.get(url));
    }

    /**
     * Should return an UNAUTHORIZED error response when token invalid
     *
     * @param url to be called with POST and Invalid token
     */
    public void postInvalid(String url) throws Exception {
        evaluateInvalid(MockMvcRequestBuilders.post(url));
    }

    /**
     * Should return an UNAUTHORIZED error response when token invalid
     *
     * @param url to be called with PUT and Invalid token
     */
    public void putInvalid(String url) throws Exception {
        evaluateInvalid(MockMvcRequestBuilders.put(url));
    }

    /**
     * Should return an UNAUTHORIZED error response when token invalid
     *
     * @param url to be called with DELETE and Invalid token
     */
    public void deleteInvalid(String url) throws Exception {
        evaluateInvalid(MockMvcRequestBuilders.delete(url));
    }

    /**
     * Should return an UNAUTHORIZED error response when not permissions
     *
     * @param url to be called with GET and token without permissions
     */
    public void getNotPermission(String url) throws Exception {
        evaluateNotPermission(MockMvcRequestBuilders.get(url));
    }

    /**
     * Should return an UNAUTHORIZED error response when not permissions
     *
     * @param url  to be called with POST and token without permissions
     * @param body correct body that passes @Valid requirements or "" if not needed
     */
    public void postNotPermission(String url, String body) throws Exception {
        evaluateNotPermission(MockMvcRequestBuilders.post(url).content(body));
    }

    /**
     * Should return an UNAUTHORIZED error response when not permissions
     *
     * @param url  to be called with PUT and token without permissions
     * @param body correct body that passes @Valid requirements or "" if not needed
     */
    public void putNotPermission(String url, String body) throws Exception {
        evaluateNotPermission(MockMvcRequestBuilders.put(url).content(body));
    }

    /**
     * Should return an UNAUTHORIZED error response when not permissions
     *
     * @param url to be called with DELETE and token without permissions
     */
    public void deleteNotPermission(String url) throws Exception {
        evaluateNotPermission(MockMvcRequestBuilders.delete(url));
    }

    /**
     * Should return an UNAUTHORIZED error response when not token
     *
     * @param builder request to be called
     */
    private void evaluateNotToken(MockHttpServletRequestBuilder builder) throws Exception {
        // call without token
        MockHttpServletRequestBuilder builder1 = builder.contentType(MediaType.APPLICATION_JSON);

        mvc.perform(builder1)
                .andExpect(status().isUnauthorized());

        // call with token without Bearer type
        MockHttpServletRequestBuilder builder2 = builder.header("Authorization", "NoBearer test")
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(builder2)
                .andExpect(status().isUnauthorized());

        verify(tokenService, never()).getLoggedUser(any());
        verify(responseFactory, never()).error(any());
    }

    /**
     * Should return an UNAUTHORIZED error response when token invalid
     *
     * @param builder request to be called
     */
    private void evaluateInvalid(MockHttpServletRequestBuilder builder) throws Exception {
        builder = builder.header("Authorization", "Bearer " + INVALID_TOKEN)
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(builder)
                .andExpect(status().isUnauthorized());

        verify(tokenService, times(1)).getLoggedUser(INVALID_TOKEN);
        verify(responseFactory, never()).error(any());
    }

    /**
     * Should return an UNAUTHORIZED error response when not permissions
     *
     * @param builder request to be called
     */
    private void evaluateNotPermission(MockHttpServletRequestBuilder builder) throws Exception {
        builder = builder.header("Authorization", "Bearer " + VALID_TOKEN)
                .contentType(MediaType.APPLICATION_JSON);

        given(responseFactory.error(any(Exception.class))).willReturn(new ResponseEntity(ACCESS_DENIED_EXPECTED, HttpStatus.UNAUTHORIZED));

        final String bodyResult = mvc.perform(builder)
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse().getContentAsString();

        assertNotSame(ACCESS_DENIED_EXPECTED, bodyResult);
        assertEquals(ACCESS_DENIED_EXPECTED, bodyResult);
        verify(tokenService, times(1)).getLoggedUser(VALID_TOKEN);
        verify(responseFactory, times(1)).error(captor.capture());
        assertTrue(captor.getValue() instanceof AppAuthenticationException);
    }
}