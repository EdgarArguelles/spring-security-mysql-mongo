package app.controllers;

import app.factories.ResponseFactory;
import app.security.services.TokenService;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class InfoCtrlTest {

    @Autowired
    private MockMvc mvc;

    @Captor
    private ArgumentCaptor<Exception> captor;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private ResponseFactory responseFactory;

    private final String INVALID_TOKEN = "invalid";

    private final String VALID_TOKEN = "valid";

    private CtrlTest ctrlTest;

    @Before
    public void setup() throws Exception {
        ctrlTest = new CtrlTest(mvc, captor, tokenService, responseFactory, INVALID_TOKEN, VALID_TOKEN);
    }

    /**
     * Should return version when not token
     */
    @Test
    public void versionNotToken() throws Exception {
        final MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.get("/info/version")
                        .contentType(MediaType.APPLICATION_JSON);

        final String bodyExpected = "test";
        given(responseFactory.success(any(String.class))).willReturn(new ResponseEntity(bodyExpected, HttpStatus.OK));

        final String bodyResult = mvc.perform(builder)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertNotSame(bodyExpected, bodyResult);
        assertEquals(bodyExpected, bodyResult);
        verify(tokenService, never()).getLoggedUser(any());
        verify(responseFactory, times(1)).success(any(String.class));
    }

    /**
     * Should return an UNAUTHORIZED error response when token invalid
     */
    @Test
    public void versionInvalid() throws Exception {
        ctrlTest.getInvalid("/info/version");
        verify(responseFactory, never()).success(any());
    }

    /**
     * Should return version when not permissions
     */
    @Test
    public void versionNotPermission() throws Exception {
        final MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.get("/info/version")
                        .header("Authorization", "Bearer " + VALID_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON);

        final String bodyExpected = "test";
        given(responseFactory.success(any(String.class))).willReturn(new ResponseEntity(bodyExpected, HttpStatus.OK));

        final String bodyResult = mvc.perform(builder)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertNotSame(bodyExpected, bodyResult);
        assertEquals(bodyExpected, bodyResult);
        verify(tokenService, times(1)).getLoggedUser(VALID_TOKEN);
        verify(responseFactory, times(1)).success(any(String.class));
    }
}