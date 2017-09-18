package app.integration_test.validations;

import app.integration_test.IntegrationTest;
import app.pojos.responses.error.nesteds.NestedError;
import app.pojos.responses.error.nesteds.ValidationNestedError;
import app.security.pojos.AccountCredentials;
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
import java.util.List;
import java.util.stream.IntStream;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SecurityCtrlValidationsTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private TokenService tokenService;

    private IntegrationTest integrationTest;

    @Before
    public void setup() throws Exception {
        integrationTest = new IntegrationTest(mvc, mapper, tokenService);
    }

    /**
     * Should return a BAD_REQUEST error response with 2 errors when parameters null
     */
    @Test
    public void validateWhenNull() throws Exception {
        final AccountCredentials credentials = new AccountCredentials();
        final String requestJson = mapper.writeValueAsString(credentials);

        final List<NestedError> nestedErrorsExpected = Arrays.asList(
                new ValidationNestedError("username", "may not be null"),
                new ValidationNestedError("password", "may not be null")
        );

        integrationTest.failPostModeValidation("/login", requestJson, nestedErrorsExpected);
    }

    /**
     * Should return a BAD_REQUEST error response with 2 errors when parameters empty
     */
    @Test
    public void validateWhenEmpty() throws Exception {
        final AccountCredentials credentials = new AccountCredentials("", "");
        final String requestJson = mapper.writeValueAsString(credentials);

        final List<NestedError> nestedErrorsExpected = Arrays.asList(
                new ValidationNestedError("username", "size must be between 1 and 255"),
                new ValidationNestedError("password", "size must be between 1 and 255")
        );

        integrationTest.failPostModeValidation("/login", requestJson, nestedErrorsExpected);
    }

    /**
     * Should return a BAD_REQUEST error response with 2 errors when parameters are bigger than max
     */
    @Test
    public void validateWhenMax() throws Exception {
        final StringBuffer longText = new StringBuffer();
        IntStream.range(0, 256).forEach(i -> longText.append("a"));
        final AccountCredentials credentials = new AccountCredentials(longText.toString(), longText.toString());
        final String requestJson = mapper.writeValueAsString(credentials);

        final List<NestedError> nestedErrorsExpected = Arrays.asList(
                new ValidationNestedError("username", "size must be between 1 and 255"),
                new ValidationNestedError("password", "size must be between 1 and 255")
        );

        integrationTest.failPostModeValidation("/login", requestJson, nestedErrorsExpected);
    }
}