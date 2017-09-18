package app.controllers;

import app.factories.PageFactory;
import app.factories.ResponseFactory;
import app.models.AuthProvider;
import app.models.Authentication;
import app.models.Person;
import app.pojos.pages.PageDataRequest;
import app.pojos.pages.PageDataResponse;
import app.security.pojos.LoggedUser;
import app.security.services.TokenService;
import app.services.AuthenticationService;
import app.services.PresentationService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
public class AuthenticationCtrlTest {

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
    private PresentationService presentationService;

    @MockBean
    private PageFactory pageFactory;

    @MockBean
    private AuthenticationService authenticationService;

    private final String INVALID_TOKEN = "invalid";

    private final String VALID_TOKEN = "valid";

    private final String VIEW_USERS_TOKEN = "view";

    private final String CREATE_USERS_TOKEN = "create";

    private final String REMOVE_USERS_TOKEN = "remove";

    private final String BASE_URL = "/authentications";

    private CtrlTest ctrlTest;

    @Before
    public void setup() throws Exception {
        ctrlTest = new CtrlTest(mvc, captor, tokenService, responseFactory, INVALID_TOKEN, VALID_TOKEN);

        given(tokenService.getLoggedUser(VIEW_USERS_TOKEN)).willReturn(
                new LoggedUser(null, null, null, new HashSet<>(Arrays.asList("VIEW_USERS"))));
        given(tokenService.getLoggedUser(CREATE_USERS_TOKEN)).willReturn(
                new LoggedUser(null, null, null, new HashSet<>(Arrays.asList("CREATE_USERS"))));
        given(tokenService.getLoggedUser(REMOVE_USERS_TOKEN)).willReturn(
                new LoggedUser(null, null, null, new HashSet<>(Arrays.asList("REMOVE_USERS"))));
    }

    /**
     * Should return an UNAUTHORIZED error response when not token
     */
    @Test
    public void listNotToken() throws Exception {
        ctrlTest.getNotToken(BASE_URL);
        verify(authenticationService, never()).findAll();
        verify(responseFactory, never()).success(any());
    }

    /**
     * Should return an UNAUTHORIZED error response when token invalid
     */
    @Test
    public void listInvalid() throws Exception {
        ctrlTest.getInvalid(BASE_URL);
        verify(authenticationService, never()).findAll();
        verify(responseFactory, never()).success(any());
    }

    /**
     * Should return an UNAUTHORIZED error response when not permissions
     */
    @Test
    public void listNotPermission() throws Exception {
        ctrlTest.getNotPermission(BASE_URL);
        verify(authenticationService, never()).findAll();
        verify(responseFactory, never()).success(any());
    }

    /**
     * Should return authentications list when permission
     */
    @Test
    public void listSuccess() throws Exception {
        final MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.get(BASE_URL)
                        .header("Authorization", "Bearer " + VIEW_USERS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON);

        final String bodyExpected = "test";
        final List<Authentication> authenticationsExpected = Arrays.asList(
                new Authentication("N", "P", new AuthProvider("N", "D", "U", "AK", "AS"), new Person("P")),
                new Authentication("N2", "P2", new AuthProvider("N2", "D2", "U2", "AK2", "AS2"), new Person("P2"))
        );
        final List<Authentication> authentications = Arrays.asList(
                new Authentication("N", "P", new AuthProvider("N", "D", "U", "AK", "AS"), new Person("P")),
                new Authentication("N2", "P2", new AuthProvider("N2", "D2", "U2", "AK2", "AS2"), new Person("P2"))
        );
        given(authenticationService.findAll()).willReturn(authentications);
        doNothing().when(presentationService).prepare(authentications, false, false);
        given(responseFactory.success(authentications)).willReturn(new ResponseEntity(bodyExpected, HttpStatus.OK));

        final String bodyResult = mvc.perform(builder)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertNotSame(bodyExpected, bodyResult);
        assertEquals(bodyExpected, bodyResult);
        assertNotSame(authenticationsExpected, authentications);
        assertEquals(authenticationsExpected, authentications);
        verify(tokenService, times(1)).getLoggedUser(VIEW_USERS_TOKEN);
        verify(authenticationService, times(1)).findAll();
        verify(presentationService, times(1)).prepare(authentications, false, false);
        verify(responseFactory, times(1)).success(authentications);
    }

    /**
     * Should return an UNAUTHORIZED error response when not token
     */
    @Test
    public void getNotToken() throws Exception {
        ctrlTest.getNotToken(BASE_URL + "/1");
        verify(authenticationService, never()).findById(any());
        verify(responseFactory, never()).successNotNull(any());
    }

    /**
     * Should return an UNAUTHORIZED error response when token invalid
     */
    @Test
    public void getInvalid() throws Exception {
        ctrlTest.getInvalid(BASE_URL + "/1");
        verify(authenticationService, never()).findById(any());
        verify(responseFactory, never()).successNotNull(any());
    }

    /**
     * Should return an UNAUTHORIZED error response when not permissions
     */
    @Test
    public void getNotPermission() throws Exception {
        ctrlTest.getNotPermission(BASE_URL + "/1");
        verify(authenticationService, never()).findById(any());
        verify(responseFactory, never()).successNotNull(any());
    }

    /**
     * Should return null when permission and any entry was found
     */
    @Test
    public void getSuccessNull() throws Exception {
        final String ID = "ID";
        final MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.get(BASE_URL + "/" + ID)
                        .header("Authorization", "Bearer " + VIEW_USERS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON);

        final String bodyExpected = "test";
        given(authenticationService.findById(ID)).willReturn(null);
        doNothing().when(presentationService).prepare((Authentication) null, false, false);
        given(responseFactory.successNotNull(null)).willReturn(new ResponseEntity(bodyExpected, HttpStatus.OK));

        final String bodyResult = mvc.perform(builder)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertNotSame(bodyExpected, bodyResult);
        assertEquals(bodyExpected, bodyResult);
        verify(tokenService, times(1)).getLoggedUser(VIEW_USERS_TOKEN);
        verify(authenticationService, times(1)).findById(ID);
        verify(presentationService, times(1)).prepare((Authentication) null, false, false);
        verify(responseFactory, times(1)).successNotNull(null);
    }

    /**
     * Should return an authentication when permission
     */
    @Test
    public void getSuccess() throws Exception {
        final String ID = "ID";
        final MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.get(BASE_URL + "/" + ID)
                        .header("Authorization", "Bearer " + VIEW_USERS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON);

        final String bodyExpected = "test";
        final Authentication authenticationExpected = new Authentication("N", "P", new AuthProvider("N", "D", "U", "AK", "AS"), new Person("P"));
        final Authentication authentication = new Authentication("N", "P", new AuthProvider("N", "D", "U", "AK", "AS"), new Person("P"));
        given(authenticationService.findById(ID)).willReturn(authentication);
        doNothing().when(presentationService).prepare(authentication, false, false);
        given(responseFactory.successNotNull(authentication)).willReturn(new ResponseEntity(bodyExpected, HttpStatus.OK));

        final String bodyResult = mvc.perform(builder)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertNotSame(bodyExpected, bodyResult);
        assertEquals(bodyExpected, bodyResult);
        assertNotSame(authenticationExpected, authentication);
        assertEquals(authenticationExpected, authentication);
        verify(tokenService, times(1)).getLoggedUser(VIEW_USERS_TOKEN);
        verify(authenticationService, times(1)).findById(ID);
        verify(presentationService, times(1)).prepare(authentication, false, false);
        verify(responseFactory, times(1)).successNotNull(authentication);
    }

    /**
     * Should return an UNAUTHORIZED error response when not token
     */
    @Test
    public void createNotToken() throws Exception {
        ctrlTest.postNotToken(BASE_URL);
        verify(authenticationService, never()).save(any());
        verify(responseFactory, never()).successNotNull(any());
    }

    /**
     * Should return an UNAUTHORIZED error response when token invalid
     */
    @Test
    public void createInvalid() throws Exception {
        ctrlTest.postInvalid(BASE_URL);
        verify(authenticationService, never()).save(any());
        verify(responseFactory, never()).successNotNull(any());
    }

    /**
     * Should return an UNAUTHORIZED error response when not permissions
     */
    @Test
    public void createNotPermission() throws Exception {
        final Authentication a = new Authentication("N", "PPP", new AuthProvider("ID"), new Person("P"));
        final String requestJson = mapper.writeValueAsString(a);

        ctrlTest.postNotPermission(BASE_URL, requestJson);
        verify(authenticationService, never()).save(any());
        verify(responseFactory, never()).successNotNull(any());
    }

    /**
     * Should return null when permission and any entry was found
     */
    @Test
    public void createSuccessNull() throws Exception {
        final Authentication a = new Authentication("N", "PPP", new AuthProvider("ID"), new Person("P"));
        final String requestJson = mapper.writeValueAsString(a);
        final MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.post(BASE_URL)
                        .header("Authorization", "Bearer " + CREATE_USERS_TOKEN)
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON);

        final String bodyExpected = "test";
        given(authenticationService.save(a)).willReturn(null);
        given(responseFactory.successNotNull(null)).willReturn(new ResponseEntity(bodyExpected, HttpStatus.OK));

        final String bodyResult = mvc.perform(builder)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertNotSame(bodyExpected, bodyResult);
        assertEquals(bodyExpected, bodyResult);
        verify(tokenService, times(1)).getLoggedUser(CREATE_USERS_TOKEN);
        verify(authenticationService, times(1)).save(a);
        verify(responseFactory, times(1)).successNotNull(null);
    }

    /**
     * Should return an authentication when permission and valid
     */
    @Test
    public void createSuccess() throws Exception {
        final Authentication a = new Authentication("N", "PPP", new AuthProvider("ID"), new Person("P"));
        final String requestJson = mapper.writeValueAsString(a);
        final MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.post(BASE_URL)
                        .header("Authorization", "Bearer " + CREATE_USERS_TOKEN)
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON);

        final String bodyExpected = "test";
        final Authentication authenticationExpected = new Authentication("N", null, new AuthProvider("N", "D", null, null, null), new Person("P"));
        final Authentication authentication = new Authentication("N", "P", new AuthProvider("N", "D", "U", "AK", "AS"), new Person("P"));
        given(authenticationService.save(a)).willReturn(authentication);
        given(responseFactory.successNotNull(authentication)).willReturn(new ResponseEntity(bodyExpected, HttpStatus.OK));

        final String bodyResult = mvc.perform(builder)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertNotSame(bodyExpected, bodyResult);
        assertEquals(bodyExpected, bodyResult);
        assertNotSame(authenticationExpected, authentication);
        assertEquals(authenticationExpected, authentication);
        verify(tokenService, times(1)).getLoggedUser(CREATE_USERS_TOKEN);
        verify(authenticationService, times(1)).save(a);
        verify(responseFactory, times(1)).successNotNull(authentication);
    }

    /**
     * Should return an UNAUTHORIZED error response when not token
     */
    @Test
    public void editNotToken() throws Exception {
        ctrlTest.putNotToken(BASE_URL + "/1");
        verify(authenticationService, never()).update(any());
        verify(responseFactory, never()).successNotNull(any());
    }

    /**
     * Should return an UNAUTHORIZED error response when token invalid
     */
    @Test
    public void editInvalid() throws Exception {
        ctrlTest.putInvalid(BASE_URL + "/1");
        verify(authenticationService, never()).update(any());
        verify(responseFactory, never()).successNotNull(any());
    }

    /**
     * Should return an UNAUTHORIZED error response when not permissions
     */
    @Test
    public void editNotPermission() throws Exception {
        final Authentication a = new Authentication("N", "PPP", new AuthProvider("ID"), new Person("P"));
        final String requestJson = mapper.writeValueAsString(a);

        ctrlTest.putNotPermission(BASE_URL + "/1", requestJson);
        verify(authenticationService, never()).update(any());
        verify(responseFactory, never()).successNotNull(any());
    }

    /**
     * Should return null when permission and any entry was found
     */
    @Test
    public void editSuccessNull() throws Exception {
        final String ID = "ID";
        final Authentication a = new Authentication("N", "PPP", new AuthProvider("ID"), new Person("P"));
        final String requestJson = mapper.writeValueAsString(a);
        final MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.put(BASE_URL + "/" + ID)
                        .header("Authorization", "Bearer " + CREATE_USERS_TOKEN)
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON);

        final String bodyExpected = "test";
        a.setId(ID);
        given(authenticationService.update(a)).willReturn(null);
        given(responseFactory.successNotNull(null)).willReturn(new ResponseEntity(bodyExpected, HttpStatus.OK));

        final String bodyResult = mvc.perform(builder)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertNotSame(bodyExpected, bodyResult);
        assertEquals(bodyExpected, bodyResult);
        verify(tokenService, times(1)).getLoggedUser(CREATE_USERS_TOKEN);
        verify(authenticationService, times(1)).update(a);
        verify(responseFactory, times(1)).successNotNull(null);
    }

    /**
     * Should return an authentication when permission and valid
     */
    @Test
    public void editSuccess() throws Exception {
        final String ID = "ID";
        final Authentication a = new Authentication("N", "PPP", new AuthProvider("ID"), new Person("P"));
        final String requestJson = mapper.writeValueAsString(a);
        final MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.put(BASE_URL + "/" + ID)
                        .header("Authorization", "Bearer " + CREATE_USERS_TOKEN)
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON);

        final String bodyExpected = "test";
        final Authentication authenticationExpected = new Authentication("N", null, new AuthProvider("N", "D", null, null, null), new Person("P"));
        final Authentication authentication = new Authentication("N", "P", new AuthProvider("N", "D", "U", "AK", "AS"), new Person("P"));
        a.setId(ID);
        given(authenticationService.update(a)).willReturn(authentication);
        given(responseFactory.successNotNull(authentication)).willReturn(new ResponseEntity(bodyExpected, HttpStatus.OK));

        final String bodyResult = mvc.perform(builder)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertNotSame(bodyExpected, bodyResult);
        assertEquals(bodyExpected, bodyResult);
        assertNotSame(authenticationExpected, authentication);
        assertEquals(authenticationExpected, authentication);
        verify(tokenService, times(1)).getLoggedUser(CREATE_USERS_TOKEN);
        verify(authenticationService, times(1)).update(a);
        verify(responseFactory, times(1)).successNotNull(authentication);
    }

    /**
     * Should return an UNAUTHORIZED error response when not token
     */
    @Test
    public void deleteNotToken() throws Exception {
        ctrlTest.deleteNotToken(BASE_URL + "/1");
        verify(authenticationService, never()).delete(any());
        verify(responseFactory, never()).successNotNull(any());
    }

    /**
     * Should return an UNAUTHORIZED error response when token invalid
     */
    @Test
    public void deleteInvalid() throws Exception {
        ctrlTest.deleteInvalid(BASE_URL + "/1");
        verify(authenticationService, never()).delete(any());
        verify(responseFactory, never()).successNotNull(any());
    }

    /**
     * Should return an UNAUTHORIZED error response when not permission
     */
    @Test
    public void deleteNotPermission() throws Exception {
        ctrlTest.deleteNotPermission(BASE_URL + "/1");
        verify(authenticationService, never()).delete(any());
        verify(responseFactory, never()).successNotNull(any());
    }

    /**
     * Should return null when permission and any entry was found
     */
    @Test
    public void deleteSuccessNull() throws Exception {
        final String ID = "ID";
        final MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.delete(BASE_URL + "/" + ID)
                        .header("Authorization", "Bearer " + REMOVE_USERS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON);

        final String bodyExpected = "test";
        given(authenticationService.delete(ID)).willReturn(null);
        given(responseFactory.successNotNull(null)).willReturn(new ResponseEntity(bodyExpected, HttpStatus.OK));

        final String bodyResult = mvc.perform(builder)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertNotSame(bodyExpected, bodyResult);
        assertEquals(bodyExpected, bodyResult);
        verify(tokenService, times(1)).getLoggedUser(REMOVE_USERS_TOKEN);
        verify(authenticationService, times(1)).delete(ID);
        verify(responseFactory, times(1)).successNotNull(null);
    }

    /**
     * Should return an authentication when permission
     */
    @Test
    public void deleteSuccess() throws Exception {
        final String ID = "ID";
        final MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.delete(BASE_URL + "/" + ID)
                        .header("Authorization", "Bearer " + REMOVE_USERS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON);

        final String bodyExpected = "test";
        final Authentication authenticationExpected = new Authentication("N", null, new AuthProvider("N", "D", null, null, null), new Person("P"));
        final Authentication authentication = new Authentication("N", "P", new AuthProvider("N", "D", "U", "AK", "AS"), new Person("P"));
        given(authenticationService.delete(ID)).willReturn(authentication);
        given(responseFactory.successNotNull(authentication)).willReturn(new ResponseEntity(bodyExpected, HttpStatus.OK));

        final String bodyResult = mvc.perform(builder)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertNotSame(bodyExpected, bodyResult);
        assertEquals(bodyExpected, bodyResult);
        assertNotSame(authenticationExpected, authentication);
        assertEquals(authenticationExpected, authentication);
        verify(tokenService, times(1)).getLoggedUser(REMOVE_USERS_TOKEN);
        verify(authenticationService, times(1)).delete(ID);
        verify(responseFactory, times(1)).successNotNull(authentication);
    }

    /**
     * Should return an UNAUTHORIZED error response when not token
     */
    @Test
    public void pageNotToken() throws Exception {
        ctrlTest.postNotToken(BASE_URL + "/Page");
        verify(authenticationService, never()).page(any());
        verify(pageFactory, never()).pageResponse(any(), any());
        verify(responseFactory, never()).success(any());
    }

    /**
     * Should return an UNAUTHORIZED error response when token invalid
     */
    @Test
    public void pageInvalid() throws Exception {
        ctrlTest.postInvalid(BASE_URL + "/Page");
        verify(authenticationService, never()).page(any());
        verify(pageFactory, never()).pageResponse(any(), any());
        verify(responseFactory, never()).success(any());
    }

    /**
     * Should return an UNAUTHORIZED error response when not permission
     */
    @Test
    public void pageNotPermission() throws Exception {
        final PageDataRequest pageDataRequest = new PageDataRequest(1, 5, null, null, null);
        final String requestJson = mapper.writeValueAsString(pageDataRequest);

        ctrlTest.postNotPermission(BASE_URL + "/Page", requestJson);
        verify(authenticationService, never()).page(any());
        verify(pageFactory, never()).pageResponse(any(), any());
        verify(responseFactory, never()).success(any());
    }

    /**
     * Should return authentications list when permission
     */
    @Test
    public void pageSuccess() throws Exception {
        final PageDataRequest pageDataRequest = new PageDataRequest(1, 3, null, null, null);
        final String requestJson = mapper.writeValueAsString(pageDataRequest);
        final MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.post(BASE_URL + "/Page")
                        .header("Authorization", "Bearer " + VIEW_USERS_TOKEN)
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON);

        final String bodyExpected = "test";
        final Page<Authentication> authenticationsExpected = new PageImpl<>(Arrays.asList(
                new Authentication("N", "P", new AuthProvider("N", "D", "U", "AK", "AS"), new Person("P")),
                new Authentication("N2", "P2", new AuthProvider("N2", "D2", "U2", "AK2", "AS2"), new Person("P2"))
        ));
        final Page<Authentication> authentications = new PageImpl<>(Arrays.asList(
                new Authentication("N", "P", new AuthProvider("N", "D", "U", "AK", "AS"), new Person("P")),
                new Authentication("N2", "P2", new AuthProvider("N2", "D2", "U2", "AK2", "AS2"), new Person("P2"))
        ));
        final PageDataResponse pageDataResponse = new PageDataResponse();
        given(authenticationService.page(pageDataRequest)).willReturn(authentications);
        doNothing().when(presentationService).prepare(authentications.getContent(), false, false);
        given(pageFactory.pageResponse(authentications, pageDataRequest)).willReturn(pageDataResponse);
        given(responseFactory.success(authentications.getContent(), pageDataResponse)).willReturn(new ResponseEntity(bodyExpected, HttpStatus.OK));

        final String bodyResult = mvc.perform(builder)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertNotSame(bodyExpected, bodyResult);
        assertEquals(bodyExpected, bodyResult);
        assertNotSame(authenticationsExpected, authentications);
        assertEquals(authenticationsExpected, authentications);
        verify(tokenService, times(1)).getLoggedUser(VIEW_USERS_TOKEN);
        verify(authenticationService, times(1)).page(pageDataRequest);
        verify(presentationService, times(1)).prepare(authentications.getContent(), false, false);
        verify(pageFactory, times(1)).pageResponse(authentications, pageDataRequest);
        verify(responseFactory, times(1)).success(authentications.getContent(), pageDataResponse);
    }
}