package app.controllers;

import app.factories.PageFactory;
import app.factories.ResponseFactory;
import app.models.Permission;
import app.models.Role;
import app.pojos.pages.PageDataRequest;
import app.pojos.pages.PageDataResponse;
import app.security.pojos.LoggedUser;
import app.security.services.TokenService;
import app.services.PermissionService;
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
public class PermissionCtrlTest {

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
    private PermissionService permissionService;

    private final String INVALID_TOKEN = "invalid";

    private final String VALID_TOKEN = "valid";

    private final String VIEW_ROLES_TOKEN = "view";

    private final String CREATE_ROLES_TOKEN = "create";

    private final String REMOVE_ROLES_TOKEN = "remove";

    private final String BASE_URL = "/permissions";

    private CtrlTest ctrlTest;

    @Before
    public void setup() throws Exception {
        ctrlTest = new CtrlTest(mvc, captor, tokenService, responseFactory, INVALID_TOKEN, VALID_TOKEN);

        given(tokenService.getLoggedUser(VIEW_ROLES_TOKEN)).willReturn(
                new LoggedUser(null, null, null, new HashSet<>(Arrays.asList("VIEW_ROLES"))));
        given(tokenService.getLoggedUser(CREATE_ROLES_TOKEN)).willReturn(
                new LoggedUser(null, null, null, new HashSet<>(Arrays.asList("CREATE_ROLES"))));
        given(tokenService.getLoggedUser(REMOVE_ROLES_TOKEN)).willReturn(
                new LoggedUser(null, null, null, new HashSet<>(Arrays.asList("REMOVE_ROLES"))));
    }

    /**
     * Should return an UNAUTHORIZED error response when not token
     */
    @Test
    public void listNotToken() throws Exception {
        ctrlTest.getNotToken(BASE_URL);
        verify(permissionService, never()).findAll();
        verify(responseFactory, never()).success(any());
    }

    /**
     * Should return an UNAUTHORIZED error response when token invalid
     */
    @Test
    public void listInvalid() throws Exception {
        ctrlTest.getInvalid(BASE_URL);
        verify(permissionService, never()).findAll();
        verify(responseFactory, never()).success(any());
    }

    /**
     * Should return an UNAUTHORIZED error response when not permissions
     */
    @Test
    public void listNotPermission() throws Exception {
        ctrlTest.getNotPermission(BASE_URL);
        verify(permissionService, never()).findAll();
        verify(responseFactory, never()).success(any());
    }

    /**
     * Should return permissions list when permission
     */
    @Test
    public void listSuccess() throws Exception {
        final MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.get(BASE_URL)
                        .header("Authorization", "Bearer " + VIEW_ROLES_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON);

        final String bodyExpected = "test";
        final List<Permission> permissions = Arrays.asList(new Permission("P1"), new Permission("P2"));
        given(permissionService.findAll()).willReturn(permissions);
        doNothing().when(presentationService).prepare(permissions, false, false);
        given(responseFactory.success(permissions)).willReturn(new ResponseEntity(bodyExpected, HttpStatus.OK));

        final String bodyResult = mvc.perform(builder)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertNotSame(bodyExpected, bodyResult);
        assertEquals(bodyExpected, bodyResult);
        verify(tokenService, times(1)).getLoggedUser(VIEW_ROLES_TOKEN);
        verify(permissionService, times(1)).findAll();
        verify(presentationService, times(1)).prepare(permissions, false, false);
        verify(responseFactory, times(1)).success(permissions);
    }

    /**
     * Should return an UNAUTHORIZED error response when not token
     */
    @Test
    public void getNotToken() throws Exception {
        ctrlTest.getNotToken(BASE_URL + "/1");
        verify(permissionService, never()).findById(any());
        verify(responseFactory, never()).successNotNull(any());
    }

    /**
     * Should return an UNAUTHORIZED error response when token invalid
     */
    @Test
    public void getInvalid() throws Exception {
        ctrlTest.getInvalid(BASE_URL + "/1");
        verify(permissionService, never()).findById(any());
        verify(responseFactory, never()).successNotNull(any());
    }

    /**
     * Should return an UNAUTHORIZED error response when not permissions
     */
    @Test
    public void getNotPermission() throws Exception {
        ctrlTest.getNotPermission(BASE_URL + "/1");
        verify(permissionService, never()).findById(any());
        verify(responseFactory, never()).successNotNull(any());
    }

    /**
     * Should return a permission when permission
     */
    @Test
    public void getSuccess() throws Exception {
        final String ID = "ID";
        final MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.get(BASE_URL + "/" + ID)
                        .header("Authorization", "Bearer " + VIEW_ROLES_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON);

        final String bodyExpected = "test";
        final Permission permission = new Permission("P1");
        given(permissionService.findById(ID)).willReturn(permission);
        doNothing().when(presentationService).prepare(permission, false, false);
        given(responseFactory.successNotNull(permission)).willReturn(new ResponseEntity(bodyExpected, HttpStatus.OK));

        final String bodyResult = mvc.perform(builder)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertNotSame(bodyExpected, bodyResult);
        assertEquals(bodyExpected, bodyResult);
        verify(tokenService, times(1)).getLoggedUser(VIEW_ROLES_TOKEN);
        verify(permissionService, times(1)).findById(ID);
        verify(presentationService, times(1)).prepare(permission, false, false);
        verify(responseFactory, times(1)).successNotNull(permission);
    }

    /**
     * Should return an UNAUTHORIZED error response when not token
     */
    @Test
    public void createNotToken() throws Exception {
        ctrlTest.postNotToken(BASE_URL);
        verify(permissionService, never()).save(any());
        verify(responseFactory, never()).successNotNull(any());
    }

    /**
     * Should return an UNAUTHORIZED error response when token invalid
     */
    @Test
    public void createInvalid() throws Exception {
        ctrlTest.postInvalid(BASE_URL);
        verify(permissionService, never()).save(any());
        verify(responseFactory, never()).successNotNull(any());
    }

    /**
     * Should return an UNAUTHORIZED error response when not permissions
     */
    @Test
    public void createNotPermission() throws Exception {
        final Permission p = new Permission("name", "description");
        final String requestJson = mapper.writeValueAsString(p);

        ctrlTest.postNotPermission(BASE_URL, requestJson);
        verify(permissionService, never()).save(any());
        verify(responseFactory, never()).successNotNull(any());
    }

    /**
     * Should return a permission when permission and valid
     */
    @Test
    public void createSuccess() throws Exception {
        final Permission p = new Permission("name", "description");
        final String requestJson = mapper.writeValueAsString(p);
        final MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.post(BASE_URL)
                        .header("Authorization", "Bearer " + CREATE_ROLES_TOKEN)
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON);

        final String bodyExpected = "test";
        final Permission permission = new Permission("P1");
        given(permissionService.save(p)).willReturn(permission);
        given(responseFactory.successNotNull(permission)).willReturn(new ResponseEntity(bodyExpected, HttpStatus.OK));

        final String bodyResult = mvc.perform(builder)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertNotSame(bodyExpected, bodyResult);
        assertEquals(bodyExpected, bodyResult);
        verify(tokenService, times(1)).getLoggedUser(CREATE_ROLES_TOKEN);
        verify(permissionService, times(1)).save(p);
        verify(responseFactory, times(1)).successNotNull(permission);
    }

    /**
     * Should return an UNAUTHORIZED error response when not token
     */
    @Test
    public void editNotToken() throws Exception {
        ctrlTest.putNotToken(BASE_URL + "/1");
        verify(permissionService, never()).update(any());
        verify(responseFactory, never()).successNotNull(any());
    }

    /**
     * Should return an UNAUTHORIZED error response when token invalid
     */
    @Test
    public void editInvalid() throws Exception {
        ctrlTest.putInvalid(BASE_URL + "/1");
        verify(permissionService, never()).update(any());
        verify(responseFactory, never()).successNotNull(any());
    }

    /**
     * Should return an UNAUTHORIZED error response when not permissions
     */
    @Test
    public void editNotPermission() throws Exception {
        final Permission p = new Permission("name", "description");
        final String requestJson = mapper.writeValueAsString(p);

        ctrlTest.putNotPermission(BASE_URL + "/1", requestJson);
        verify(permissionService, never()).update(any());
        verify(responseFactory, never()).successNotNull(any());
    }

    /**
     * Should return a permission when permission and valid
     */
    @Test
    public void editSuccess() throws Exception {
        final String ID = "ID";
        final Permission p = new Permission("name", "description");
        final String requestJson = mapper.writeValueAsString(p);
        final MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.put(BASE_URL + "/" + ID)
                        .header("Authorization", "Bearer " + CREATE_ROLES_TOKEN)
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON);

        final String bodyExpected = "test";
        final Permission permission = new Permission("P1");
        p.setId(ID);
        given(permissionService.update(p)).willReturn(permission);
        given(responseFactory.successNotNull(permission)).willReturn(new ResponseEntity(bodyExpected, HttpStatus.OK));

        final String bodyResult = mvc.perform(builder)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertNotSame(bodyExpected, bodyResult);
        assertEquals(bodyExpected, bodyResult);
        verify(tokenService, times(1)).getLoggedUser(CREATE_ROLES_TOKEN);
        verify(permissionService, times(1)).update(p);
        verify(responseFactory, times(1)).successNotNull(permission);
    }

    /**
     * Should return an UNAUTHORIZED error response when not token
     */
    @Test
    public void deleteNotToken() throws Exception {
        ctrlTest.deleteNotToken(BASE_URL + "/1");
        verify(permissionService, never()).delete(any());
        verify(responseFactory, never()).successNotNull(any());
    }

    /**
     * Should return an UNAUTHORIZED error response when token invalid
     */
    @Test
    public void deleteInvalid() throws Exception {
        ctrlTest.deleteInvalid(BASE_URL + "/1");
        verify(permissionService, never()).delete(any());
        verify(responseFactory, never()).successNotNull(any());
    }

    /**
     * Should return an UNAUTHORIZED error response when not permission
     */
    @Test
    public void deleteNotPermission() throws Exception {
        ctrlTest.deleteNotPermission(BASE_URL + "/1");
        verify(permissionService, never()).delete(any());
        verify(responseFactory, never()).successNotNull(any());
    }

    /**
     * Should return a permission when permission
     */
    @Test
    public void deleteSuccess() throws Exception {
        final String ID = "ID";
        final MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.delete(BASE_URL + "/" + ID)
                        .header("Authorization", "Bearer " + REMOVE_ROLES_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON);

        final String bodyExpected = "test";
        final Permission permission = new Permission("P1");
        given(permissionService.delete(ID)).willReturn(permission);
        given(responseFactory.successNotNull(permission)).willReturn(new ResponseEntity(bodyExpected, HttpStatus.OK));

        final String bodyResult = mvc.perform(builder)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertNotSame(bodyExpected, bodyResult);
        assertEquals(bodyExpected, bodyResult);
        verify(tokenService, times(1)).getLoggedUser(REMOVE_ROLES_TOKEN);
        verify(permissionService, times(1)).delete(ID);
        verify(responseFactory, times(1)).successNotNull(permission);
    }

    /**
     * Should return an UNAUTHORIZED error response when not token
     */
    @Test
    public void getRolesNotToken() throws Exception {
        ctrlTest.getNotToken(BASE_URL + "/1/roles");
        verify(permissionService, never()).findByIdNotNull(any());
        verify(responseFactory, never()).successNotNull(any());
    }

    /**
     * Should return an UNAUTHORIZED error response when token invalid
     */
    @Test
    public void getRolesInvalid() throws Exception {
        ctrlTest.getInvalid(BASE_URL + "/1/roles");
        verify(permissionService, never()).findByIdNotNull(any());
        verify(responseFactory, never()).successNotNull(any());
    }

    /**
     * Should return an UNAUTHORIZED error response when not permission
     */
    @Test
    public void getRolesNotPermission() throws Exception {
        ctrlTest.getNotPermission(BASE_URL + "/1/roles");
        verify(permissionService, never()).findByIdNotNull(any());
        verify(responseFactory, never()).successNotNull(any());
    }

    /**
     * Should return roles list when permission
     */
    @Test
    public void getRolesSuccess() throws Exception {
        final String ID = "ID";
        final MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.get(BASE_URL + "/" + ID + "/roles")
                        .header("Authorization", "Bearer " + VIEW_ROLES_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON);

        final String bodyExpected = "test";
        final Permission permission = new Permission("P1");
        permission.setRoles(Arrays.asList(new Role("R1"), new Role("R2")));
        given(permissionService.findByIdNotNull(ID)).willReturn(permission);
        doNothing().when(presentationService).prepare(permission.getRoles(), false, false);
        given(responseFactory.successNotNull(permission.getRoles())).willReturn(new ResponseEntity(bodyExpected, HttpStatus.OK));

        final String bodyResult = mvc.perform(builder)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertNotSame(bodyExpected, bodyResult);
        assertEquals(bodyExpected, bodyResult);
        verify(tokenService, times(1)).getLoggedUser(VIEW_ROLES_TOKEN);
        verify(permissionService, times(1)).findByIdNotNull(ID);
        verify(presentationService, times(1)).prepare(permission.getRoles(), false, false);
        verify(responseFactory, times(1)).successNotNull(permission.getRoles());
    }

    /**
     * Should return an UNAUTHORIZED error response when not token
     */
    @Test
    public void pageNotToken() throws Exception {
        ctrlTest.postNotToken(BASE_URL + "/Page");
        verify(permissionService, never()).page(any());
        verify(pageFactory, never()).pageResponse(any(), any());
        verify(responseFactory, never()).success(any());
    }

    /**
     * Should return an UNAUTHORIZED error response when token invalid
     */
    @Test
    public void pageInvalid() throws Exception {
        ctrlTest.postInvalid(BASE_URL + "/Page");
        verify(permissionService, never()).page(any());
        verify(pageFactory, never()).pageResponse(any(), any());
        verify(responseFactory, never()).success(any());
    }

    /**
     * Should return an UNAUTHORIZED error response when not permission
     */
    @Test
    public void pageNotPermission() throws Exception {
        final PageDataRequest pageDataRequest = new PageDataRequest(1, 2, null, null, null);
        final String requestJson = mapper.writeValueAsString(pageDataRequest);

        ctrlTest.postNotPermission(BASE_URL + "/Page", requestJson);
        verify(permissionService, never()).page(any());
        verify(pageFactory, never()).pageResponse(any(), any());
        verify(responseFactory, never()).success(any());
    }

    /**
     * Should return permissions list when permission
     */
    @Test
    public void pageSuccess() throws Exception {
        final PageDataRequest pageDataRequest = new PageDataRequest(1, 3, null, null, null);
        final String requestJson = mapper.writeValueAsString(pageDataRequest);
        final MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.post(BASE_URL + "/Page")
                        .header("Authorization", "Bearer " + VIEW_ROLES_TOKEN)
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON);

        final String bodyExpected = "test";
        final Page<Permission> permissions = new PageImpl<>(Arrays.asList(new Permission("P1"), new Permission("P2")));
        final PageDataResponse pageDataResponse = new PageDataResponse();
        given(permissionService.page(pageDataRequest)).willReturn(permissions);
        doNothing().when(presentationService).prepare(permissions.getContent(), false, false);
        given(pageFactory.pageResponse(permissions, pageDataRequest)).willReturn(pageDataResponse);
        given(responseFactory.success(permissions.getContent(), pageDataResponse)).willReturn(new ResponseEntity(bodyExpected, HttpStatus.OK));

        final String bodyResult = mvc.perform(builder)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertNotSame(bodyExpected, bodyResult);
        assertEquals(bodyExpected, bodyResult);
        verify(tokenService, times(1)).getLoggedUser(VIEW_ROLES_TOKEN);
        verify(permissionService, times(1)).page(pageDataRequest);
        verify(presentationService, times(1)).prepare(permissions.getContent(), false, false);
        verify(pageFactory, times(1)).pageResponse(permissions, pageDataRequest);
        verify(responseFactory, times(1)).success(permissions.getContent(), pageDataResponse);
    }
}