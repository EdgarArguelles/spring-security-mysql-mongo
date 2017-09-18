package app.controllers;

import app.factories.PageFactory;
import app.factories.ResponseFactory;
import app.models.AuthProvider;
import app.models.Authentication;
import app.models.Person;
import app.models.Role;
import app.pojos.pages.PageDataRequest;
import app.pojos.pages.PageDataResponse;
import app.security.pojos.LoggedUser;
import app.security.services.TokenService;
import app.services.PersonService;
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

import java.time.LocalDate;
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
public class PersonCtrlTest {

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
    private PersonService personService;

    private final String INVALID_TOKEN = "invalid";

    private final String VALID_TOKEN = "valid";

    private final String VIEW_USERS_TOKEN = "view";

    private final String CREATE_USERS_TOKEN = "create";

    private final String REMOVE_USERS_TOKEN = "remove";

    private final String BASE_URL = "/people";

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
        verify(personService, never()).findAll();
        verify(responseFactory, never()).success(any());
    }

    /**
     * Should return an UNAUTHORIZED error response when token invalid
     */
    @Test
    public void listInvalid() throws Exception {
        ctrlTest.getInvalid(BASE_URL);
        verify(personService, never()).findAll();
        verify(responseFactory, never()).success(any());
    }

    /**
     * Should return an UNAUTHORIZED error response when not permissions
     */
    @Test
    public void listNotPermission() throws Exception {
        ctrlTest.getNotPermission(BASE_URL);
        verify(personService, never()).findAll();
        verify(responseFactory, never()).success(any());
    }

    /**
     * Should return people list when permission
     */
    @Test
    public void listSuccess() throws Exception {
        final MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.get(BASE_URL)
                        .header("Authorization", "Bearer " + VIEW_USERS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON);

        final String bodyExpected = "test";
        final List<Person> people = Arrays.asList(new Person("P1"), new Person("P2"));
        given(personService.findAll()).willReturn(people);
        doNothing().when(presentationService).prepare(people, false, false);
        given(responseFactory.success(people)).willReturn(new ResponseEntity(bodyExpected, HttpStatus.OK));

        final String bodyResult = mvc.perform(builder)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertNotSame(bodyExpected, bodyResult);
        assertEquals(bodyExpected, bodyResult);
        verify(tokenService, times(1)).getLoggedUser(VIEW_USERS_TOKEN);
        verify(personService, times(1)).findAll();
        verify(presentationService, times(1)).prepare(people, false, false);
        verify(responseFactory, times(1)).success(people);
    }

    /**
     * Should return an UNAUTHORIZED error response when not token
     */
    @Test
    public void getNotToken() throws Exception {
        ctrlTest.getNotToken(BASE_URL + "/1");
        verify(personService, never()).findById(any());
        verify(responseFactory, never()).successNotNull(any());
    }

    /**
     * Should return an UNAUTHORIZED error response when token invalid
     */
    @Test
    public void getInvalid() throws Exception {
        ctrlTest.getInvalid(BASE_URL + "/1");
        verify(personService, never()).findById(any());
        verify(responseFactory, never()).successNotNull(any());
    }

    /**
     * Should return an UNAUTHORIZED error response when not permissions
     */
    @Test
    public void getNotPermission() throws Exception {
        ctrlTest.getNotPermission(BASE_URL + "/1");
        verify(personService, never()).findById(any());
        verify(responseFactory, never()).successNotNull(any());
    }

    /**
     * Should return a person when permission
     */
    @Test
    public void getSuccess() throws Exception {
        final String ID = "ID";
        final MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.get(BASE_URL + "/" + ID)
                        .header("Authorization", "Bearer " + VIEW_USERS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON);

        final String bodyExpected = "test";
        final Person person = new Person("P1");
        given(personService.findById(ID)).willReturn(person);
        doNothing().when(presentationService).prepare(person, false, false);
        given(responseFactory.successNotNull(person)).willReturn(new ResponseEntity(bodyExpected, HttpStatus.OK));

        final String bodyResult = mvc.perform(builder)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertNotSame(bodyExpected, bodyResult);
        assertEquals(bodyExpected, bodyResult);
        verify(tokenService, times(1)).getLoggedUser(VIEW_USERS_TOKEN);
        verify(personService, times(1)).findById(ID);
        verify(presentationService, times(1)).prepare(person, false, false);
        verify(responseFactory, times(1)).successNotNull(person);
    }

    /**
     * Should return an UNAUTHORIZED error response when not token
     */
    @Test
    public void createNotToken() throws Exception {
        ctrlTest.postNotToken(BASE_URL);
        verify(personService, never()).save(any());
        verify(responseFactory, never()).successNotNull(any());
    }

    /**
     * Should return an UNAUTHORIZED error response when token invalid
     */
    @Test
    public void createInvalid() throws Exception {
        ctrlTest.postInvalid(BASE_URL);
        verify(personService, never()).save(any());
        verify(responseFactory, never()).successNotNull(any());
    }

    /**
     * Should return an UNAUTHORIZED error response when not permissions
     */
    @Test
    public void createNotPermission() throws Exception {
        final Person p = new Person("name", "last name", LocalDate.now(), Person.CIVIL_STATUS.SINGLE, Person.SEX.F, "aa@aa.com", new HashSet<>(Arrays.asList(new Role("R1"), new Role("R2"))));
        final String requestJson = mapper.writeValueAsString(p);

        ctrlTest.postNotPermission(BASE_URL, requestJson);
        verify(personService, never()).save(any());
        verify(responseFactory, never()).successNotNull(any());
    }

    /**
     * Should return a person when permission and valid
     */
    @Test
    public void createSuccess() throws Exception {
        final Person p = new Person("name", "last name", LocalDate.now(), Person.CIVIL_STATUS.SINGLE, Person.SEX.F, "aa@aa.com", new HashSet<>(Arrays.asList(new Role("R1"), new Role("R2"))));
        final String requestJson = mapper.writeValueAsString(p);
        final MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.post(BASE_URL)
                        .header("Authorization", "Bearer " + CREATE_USERS_TOKEN)
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON);

        final String bodyExpected = "test";
        final Person person = new Person("P1");
        given(personService.save(p)).willReturn(person);
        given(responseFactory.successNotNull(person)).willReturn(new ResponseEntity(bodyExpected, HttpStatus.OK));

        final String bodyResult = mvc.perform(builder)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertNotSame(bodyExpected, bodyResult);
        assertEquals(bodyExpected, bodyResult);
        verify(tokenService, times(1)).getLoggedUser(CREATE_USERS_TOKEN);
        verify(personService, times(1)).save(p);
        verify(responseFactory, times(1)).successNotNull(person);
    }

    /**
     * Should return an UNAUTHORIZED error response when not token
     */
    @Test
    public void editNotToken() throws Exception {
        ctrlTest.putNotToken(BASE_URL + "/1");
        verify(personService, never()).update(any());
        verify(responseFactory, never()).successNotNull(any());
    }

    /**
     * Should return an UNAUTHORIZED error response when token invalid
     */
    @Test
    public void editInvalid() throws Exception {
        ctrlTest.putInvalid(BASE_URL + "/1");
        verify(personService, never()).update(any());
        verify(responseFactory, never()).successNotNull(any());
    }

    /**
     * Should return an UNAUTHORIZED error response when not permissions
     */
    @Test
    public void editNotPermission() throws Exception {
        final Person p = new Person("name", "last name", LocalDate.now(), Person.CIVIL_STATUS.SINGLE, Person.SEX.F, "aa@aa.com", new HashSet<>(Arrays.asList(new Role("R1"), new Role("R2"))));
        final String requestJson = mapper.writeValueAsString(p);

        ctrlTest.putNotPermission(BASE_URL + "/1", requestJson);
        verify(personService, never()).update(any());
        verify(responseFactory, never()).successNotNull(any());
    }

    /**
     * Should return a person when permission and valid
     */
    @Test
    public void editSuccess() throws Exception {
        final String ID = "ID";
        final Person p = new Person("name", "last name", LocalDate.now(), Person.CIVIL_STATUS.SINGLE, Person.SEX.F, "aa@aa.com", new HashSet<>(Arrays.asList(new Role("R1"), new Role("R2"))));
        final String requestJson = mapper.writeValueAsString(p);
        final MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.put(BASE_URL + "/" + ID)
                        .header("Authorization", "Bearer " + CREATE_USERS_TOKEN)
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON);

        final String bodyExpected = "test";
        final Person person = new Person("P1");
        p.setId(ID);
        given(personService.update(p)).willReturn(person);
        given(responseFactory.successNotNull(person)).willReturn(new ResponseEntity(bodyExpected, HttpStatus.OK));

        final String bodyResult = mvc.perform(builder)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertNotSame(bodyExpected, bodyResult);
        assertEquals(bodyExpected, bodyResult);
        verify(tokenService, times(1)).getLoggedUser(CREATE_USERS_TOKEN);
        verify(personService, times(1)).update(p);
        verify(responseFactory, times(1)).successNotNull(person);
    }

    /**
     * Should return an UNAUTHORIZED error response when not token
     */
    @Test
    public void deleteNotToken() throws Exception {
        ctrlTest.deleteNotToken(BASE_URL + "/1");
        verify(personService, never()).delete(any());
        verify(responseFactory, never()).successNotNull(any());
    }

    /**
     * Should return an UNAUTHORIZED error response when token invalid
     */
    @Test
    public void deleteInvalid() throws Exception {
        ctrlTest.deleteInvalid(BASE_URL + "/1");
        verify(personService, never()).delete(any());
        verify(responseFactory, never()).successNotNull(any());
    }

    /**
     * Should return an UNAUTHORIZED error response when not permission
     */
    @Test
    public void deleteNotPermission() throws Exception {
        ctrlTest.deleteNotPermission(BASE_URL + "/1");
        verify(personService, never()).delete(any());
        verify(responseFactory, never()).successNotNull(any());
    }

    /**
     * Should return a person when permission
     */
    @Test
    public void deleteSuccess() throws Exception {
        final String ID = "ID";
        final MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.delete(BASE_URL + "/" + ID)
                        .header("Authorization", "Bearer " + REMOVE_USERS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON);

        final String bodyExpected = "test";
        final Person person = new Person("P1");
        given(personService.delete(ID)).willReturn(person);
        given(responseFactory.successNotNull(person)).willReturn(new ResponseEntity(bodyExpected, HttpStatus.OK));

        final String bodyResult = mvc.perform(builder)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertNotSame(bodyExpected, bodyResult);
        assertEquals(bodyExpected, bodyResult);
        verify(tokenService, times(1)).getLoggedUser(REMOVE_USERS_TOKEN);
        verify(personService, times(1)).delete(ID);
        verify(responseFactory, times(1)).successNotNull(person);
    }

    /**
     * Should return an UNAUTHORIZED error response when not token
     */
    @Test
    public void getAuthenticationsNotToken() throws Exception {
        ctrlTest.getNotToken(BASE_URL + "/1/authentications");
        verify(personService, never()).findByIdNotNull(any());
        verify(responseFactory, never()).successNotNull(any());
    }

    /**
     * Should return an UNAUTHORIZED error response when token invalid
     */
    @Test
    public void getAuthenticationsInvalid() throws Exception {
        ctrlTest.getInvalid(BASE_URL + "/1/authentications");
        verify(personService, never()).findByIdNotNull(any());
        verify(responseFactory, never()).successNotNull(any());
    }

    /**
     * Should return an UNAUTHORIZED error response when not permission
     */
    @Test
    public void getAuthenticationsNotPermission() throws Exception {
        ctrlTest.getNotPermission(BASE_URL + "/1/authentications");
        verify(personService, never()).findByIdNotNull(any());
        verify(responseFactory, never()).successNotNull(any());
    }

    /**
     * Should return authentications list when permission
     */
    @Test
    public void getAuthenticationsSuccess() throws Exception {
        final String ID = "ID";
        final MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.get(BASE_URL + "/" + ID + "/authentications")
                        .header("Authorization", "Bearer " + VIEW_USERS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON);

        final String bodyExpected = "test";
        final Person personExpected = new Person("P1");
        personExpected.setAuthentications(Arrays.asList(
                new Authentication("U", "P", new AuthProvider("N", "D", "U", "AK", "AS"), new Person("P1")),
                new Authentication("U2", "P2", new AuthProvider("N1", "D1", "U1", "AK1", "AS1"), new Person("P1"))
        ));
        final Person person = new Person("P1");
        person.setAuthentications(Arrays.asList(
                new Authentication("U", "P", new AuthProvider("N", "D", "U", "AK", "AS"), new Person("P1")),
                new Authentication("U2", "P2", new AuthProvider("N1", "D1", "U1", "AK1", "AS1"), new Person("P1"))
        ));
        given(personService.findByIdNotNull(ID)).willReturn(person);
        doNothing().when(presentationService).prepare(person.getAuthentications(), false, false);
        given(responseFactory.successNotNull(person.getAuthentications())).willReturn(new ResponseEntity(bodyExpected, HttpStatus.OK));

        final String bodyResult = mvc.perform(builder)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertNotSame(bodyExpected, bodyResult);
        assertEquals(bodyExpected, bodyResult);
        assertNotSame(personExpected, person);
        assertEquals(personExpected, person);
        verify(tokenService, times(1)).getLoggedUser(VIEW_USERS_TOKEN);
        verify(personService, times(1)).findByIdNotNull(ID);
        verify(presentationService, times(1)).prepare(person.getAuthentications(), false, false);
        verify(responseFactory, times(1)).successNotNull(person.getAuthentications());
    }

    /**
     * Should return an UNAUTHORIZED error response when not token
     */
    @Test
    public void pageNotToken() throws Exception {
        ctrlTest.postNotToken(BASE_URL + "/Page");
        verify(personService, never()).page(any());
        verify(pageFactory, never()).pageResponse(any(), any());
        verify(responseFactory, never()).success(any());
    }

    /**
     * Should return an UNAUTHORIZED error response when token invalid
     */
    @Test
    public void pageInvalid() throws Exception {
        ctrlTest.postInvalid(BASE_URL + "/Page");
        verify(personService, never()).page(any());
        verify(pageFactory, never()).pageResponse(any(), any());
        verify(responseFactory, never()).success(any());
    }

    /**
     * Should return an UNAUTHORIZED error response when not permission
     */
    @Test
    public void pageNotPermission() throws Exception {
        final PageDataRequest pageDataRequest = new PageDataRequest(1, 3, null, null, null);
        final String requestJson = mapper.writeValueAsString(pageDataRequest);

        ctrlTest.postNotPermission(BASE_URL + "/Page", requestJson);
        verify(personService, never()).page(any());
        verify(pageFactory, never()).pageResponse(any(), any());
        verify(responseFactory, never()).success(any());
    }

    /**
     * Should return people list when permission
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
        final Page<Person> people = new PageImpl<>(Arrays.asList(new Person("P1"), new Person("P2")));
        final PageDataResponse pageDataResponse = new PageDataResponse();
        given(personService.page(pageDataRequest)).willReturn(people);
        doNothing().when(presentationService).prepare(people.getContent(), false, false);
        given(pageFactory.pageResponse(people, pageDataRequest)).willReturn(pageDataResponse);
        given(responseFactory.success(people.getContent(), pageDataResponse)).willReturn(new ResponseEntity(bodyExpected, HttpStatus.OK));

        final String bodyResult = mvc.perform(builder)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertNotSame(bodyExpected, bodyResult);
        assertEquals(bodyExpected, bodyResult);
        verify(tokenService, times(1)).getLoggedUser(VIEW_USERS_TOKEN);
        verify(personService, times(1)).page(pageDataRequest);
        verify(presentationService, times(1)).prepare(people.getContent(), false, false);
        verify(pageFactory, times(1)).pageResponse(people, pageDataRequest);
        verify(responseFactory, times(1)).success(people.getContent(), pageDataResponse);
    }
}