package app.integration_test.validations;

import app.integration_test.IntegrationTest;
import app.models.AuthProvider;
import app.models.Authentication;
import app.models.Person;
import app.pojos.pages.FilterRequest;
import app.pojos.pages.PageDataRequest;
import app.pojos.responses.error.nesteds.NestedError;
import app.pojos.responses.error.nesteds.ValidationNestedError;
import app.security.services.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationCtrlValidationsTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private TokenService tokenService;

    private final String BASE_URL = "/authentications";

    private IntegrationTest integrationTest;

    @Before
    public void setup() throws Exception {
        integrationTest = new IntegrationTest(mvc, mapper, tokenService);
    }

    /**
     * Should return a BAD_REQUEST error response with 3 errors when parameters null
     */
    @Test
    public void validateWhenNull() throws Exception {
        final Authentication a = new Authentication();
        final String requestJson = mapper.writeValueAsString(a);

        final List<NestedError> nestedErrorsExpected = Arrays.asList(
                new ValidationNestedError("password", "may not be null"),
                new ValidationNestedError("authProvider", "may not be null"),
                new ValidationNestedError("person", "may not be null")
        );

        integrationTest.failPostModeValidation(BASE_URL, requestJson, nestedErrorsExpected);
        integrationTest.failPutModeValidation(BASE_URL + "/5", requestJson, nestedErrorsExpected);
    }

    /**
     * Should return a BAD_REQUEST error response with 2 errors when parameters empty
     */
    @Test
    public void validateWhenEmpty() throws Exception {
        final Authentication a = new Authentication("", "PP", new AuthProvider("AP"), new Person("P"));
        final String requestJson = mapper.writeValueAsString(a);

        final List<NestedError> nestedErrorsExpected = Arrays.asList(
                new ValidationNestedError("username", "size must be between 1 and 255"),
                new ValidationNestedError("password", "size must be between 3 and 255")
        );

        integrationTest.failPostModeValidation(BASE_URL, requestJson, nestedErrorsExpected);
        integrationTest.failPutModeValidation(BASE_URL + "/5", requestJson, nestedErrorsExpected);
    }

    /**
     * Should return a BAD_REQUEST error response with 2 errors when parameters are bigger than max
     */
    @Test
    public void validateWhenMax() throws Exception {
        final StringBuffer longText = new StringBuffer();
        IntStream.range(0, 256).forEach(i -> longText.append("a"));
        final Authentication a = new Authentication(longText.toString(), longText.toString(), new AuthProvider("AP"), new Person("P"));
        final String requestJson = mapper.writeValueAsString(a);

        final List<NestedError> nestedErrorsExpected = Arrays.asList(
                new ValidationNestedError("username", "size must be between 1 and 255"),
                new ValidationNestedError("password", "size must be between 3 and 255")
        );

        integrationTest.failPostModeValidation(BASE_URL, requestJson, nestedErrorsExpected);
        integrationTest.failPutModeValidation(BASE_URL + "/5", requestJson, nestedErrorsExpected);
    }

    /**
     * Should return a BAD_REQUEST error response with 2 errors when PageDataRequest has errors
     */
    @Test
    public void validatePageWhenPageDataRequestError() throws Exception {
        final PageDataRequest p = new PageDataRequest(null, null, "", null, Collections.EMPTY_LIST);
        final String requestJson = mapper.writeValueAsString(p);

        final List<NestedError> nestedErrorsExpected = Arrays.asList(
                new ValidationNestedError("page", "may not be null"),
                new ValidationNestedError("size", "may not be null")
        );

        integrationTest.failPostModeValidation(BASE_URL + "/Page", requestJson, nestedErrorsExpected);
    }

    /**
     * Should return a BAD_REQUEST error response with 9 errors when FilterRequest has errors
     */
    @Test
    public void validatePageWhenFilterRequestError() throws Exception {
        final StringBuffer longText = new StringBuffer();
        IntStream.range(0, 256).forEach(i -> longText.append("a"));
        final List<FilterRequest> filters = Arrays.asList(
                new FilterRequest("F", "V", "OO"),
                new FilterRequest(),
                new FilterRequest("", "", "O"),
                new FilterRequest(longText.toString(), null, longText.toString()),
                new FilterRequest(null, "aaaaa", "OP"),
                new FilterRequest("fie", "bbb", null),
                new FilterRequest("F2", "V2", "OO2")
        );
        final PageDataRequest p = new PageDataRequest(null, -1, null, null, filters);
        final String requestJson = mapper.writeValueAsString(p);

        final List<NestedError> nestedErrorsExpected = Arrays.asList(
                new ValidationNestedError("page", "may not be null"),
                new ValidationNestedError("filters[1].field", "may not be null"),
                new ValidationNestedError("filters[1].operation", "may not be null"),
                new ValidationNestedError("filters[2].field", "size must be between 1 and 255"),
                new ValidationNestedError("filters[2].operation", "size must be between 2 and 255"),
                new ValidationNestedError("filters[3].field", "size must be between 1 and 255"),
                new ValidationNestedError("filters[3].operation", "size must be between 2 and 255"),
                new ValidationNestedError("filters[4].field", "may not be null"),
                new ValidationNestedError("filters[5].operation", "may not be null")
        );

        integrationTest.failPostModeValidation(BASE_URL + "/Page", requestJson, nestedErrorsExpected);
    }
}