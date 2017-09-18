package app.factories.implementations;

import app.exceptions.AppAuthenticationException;
import app.exceptions.AppDontFoundException;
import app.exceptions.AppValidationException;
import app.factories.ResponseFactory;
import app.pojos.responses.error.ErrorResponse;
import app.pojos.responses.error.nesteds.NestedError;
import app.pojos.responses.error.nesteds.ValidationNestedError;
import app.pojos.responses.success.SuccessResponse;
import app.security.services.TokenService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ResponseFactoryImplTest {

    @Autowired
    private ResponseFactory responseFactory;

    @MockBean
    private TokenService tokenService;

    /**
     * Should get ResponseEntity with only data without token
     */
    @Test
    public void successDefaultConstructorWithoutToken() throws JsonProcessingException {
        final String DATA = "test";
        final HttpStatus STATUS_EXPECTED = HttpStatus.OK;
        final SuccessResponse RESPONSE_EXPECTED = new SuccessResponse(DATA, null, null);

        given(tokenService.refreshToken()).willThrow(new JsonParseException(null, ""));

        final ResponseEntity response = responseFactory.success(DATA);

        assertEquals(STATUS_EXPECTED, response.getStatusCode());
        assertNotSame(RESPONSE_EXPECTED, response.getBody());
        assertEquals(RESPONSE_EXPECTED, response.getBody());
        verify(tokenService, times(1)).refreshToken();
    }

    /**
     * Should get ResponseEntity with only data with token
     */
    @Test
    public void successDefaultConstructorWithToken() throws JsonProcessingException {
        final String DATA = "test";
        final String NEW_TOKEN = "new token";
        final HttpStatus STATUS_EXPECTED = HttpStatus.OK;
        final SuccessResponse RESPONSE_EXPECTED = new SuccessResponse(DATA, null, NEW_TOKEN);

        given(tokenService.refreshToken()).willReturn(NEW_TOKEN);

        final ResponseEntity response = responseFactory.success(DATA);

        assertEquals(STATUS_EXPECTED, response.getStatusCode());
        assertNotSame(RESPONSE_EXPECTED, response.getBody());
        assertEquals(RESPONSE_EXPECTED, response.getBody());
        verify(tokenService, times(1)).refreshToken();
    }

    /**
     * Should get ResponseEntity with data and metaData
     */
    @Test
    public void successCompleteConstructor() throws JsonProcessingException {
        final String DATA = "test";
        final String META_DATA = "meta data";
        final String NEW_TOKEN = "new token";
        final HttpStatus STATUS_EXPECTED = HttpStatus.OK;
        final SuccessResponse RESPONSE_EXPECTED = new SuccessResponse(DATA, META_DATA, NEW_TOKEN);

        given(tokenService.refreshToken()).willReturn(NEW_TOKEN);

        final ResponseEntity response = responseFactory.success(DATA, META_DATA);

        assertEquals(STATUS_EXPECTED, response.getStatusCode());
        assertNotSame(RESPONSE_EXPECTED, response.getBody());
        assertEquals(RESPONSE_EXPECTED, response.getBody());
        verify(tokenService, times(1)).refreshToken();
    }

    /**
     * Should get an error ResponseEntity when null
     */
    @Test
    public void successNotNullWhenNull() {
        final String MESSAGE = "Data don't found.";
        final HttpStatus STATUS_EXPECTED = HttpStatus.NOT_FOUND;
        final ErrorResponse RESPONSE_EXPECTED = new ErrorResponse(MESSAGE, null, null);

        final ResponseEntity response = responseFactory.successNotNull(null);

        assertEquals(STATUS_EXPECTED, response.getStatusCode());
        assertNotSame(RESPONSE_EXPECTED, response.getBody());
        assertEquals(RESPONSE_EXPECTED, response.getBody());
    }

    /**
     * Should get ResponseEntity when not null
     */
    @Test
    public void successNotNullWhenNotNull() throws JsonProcessingException {
        final String DATA = "test";
        final String NEW_TOKEN = "new token";
        final HttpStatus STATUS_EXPECTED = HttpStatus.OK;
        final SuccessResponse RESPONSE_EXPECTED = new SuccessResponse(DATA, null, NEW_TOKEN);

        given(tokenService.refreshToken()).willReturn(NEW_TOKEN);

        final ResponseEntity response = responseFactory.successNotNull(DATA);

        assertEquals(STATUS_EXPECTED, response.getStatusCode());
        assertNotSame(RESPONSE_EXPECTED, response.getBody());
        assertEquals(RESPONSE_EXPECTED, response.getBody());
        verify(tokenService, times(1)).refreshToken();
    }

    /**
     * Should get an error ResponseEntity when AppDontFoundException with NOT_FOUND code
     */
    @Test
    public void errorAppDontFoundException() {
        final String MESSAGE = "test";
        final Exception EXCEPTION = new AppDontFoundException(MESSAGE);
        final HttpStatus STATUS_EXPECTED = HttpStatus.NOT_FOUND;
        final ErrorResponse RESPONSE_EXPECTED = new ErrorResponse(MESSAGE, null, null);

        final ResponseEntity response = responseFactory.error(EXCEPTION);

        assertEquals(STATUS_EXPECTED, response.getStatusCode());
        assertNotSame(RESPONSE_EXPECTED, response.getBody());
        assertEquals(RESPONSE_EXPECTED, response.getBody());
    }

    /**
     * Should get an error ResponseEntity when AppAuthenticationException with UNAUTHORIZED code
     */
    @Test
    public void errorAppAuthenticationException() {
        final String MESSAGE = "test";
        final Exception EXCEPTION = new AppAuthenticationException(MESSAGE);
        final HttpStatus STATUS_EXPECTED = HttpStatus.UNAUTHORIZED;
        final ErrorResponse RESPONSE_EXPECTED = new ErrorResponse(MESSAGE, null, null);

        final ResponseEntity response = responseFactory.error(EXCEPTION);

        assertEquals(STATUS_EXPECTED, response.getStatusCode());
        assertNotSame(RESPONSE_EXPECTED, response.getBody());
        assertEquals(RESPONSE_EXPECTED, response.getBody());
    }

    /**
     * Should get an error ResponseEntity when AppValidationException with BAD_REQUEST code
     */
    @Test
    public void errorAppValidationException() {
        final String MESSAGE = "test";
        final List<NestedError> NESTED_ERROR = Arrays.asList(new ValidationNestedError("F1", "M1"), new ValidationNestedError("F2", "M2"));
        final Exception EXCEPTION = new AppValidationException(MESSAGE, NESTED_ERROR);
        final HttpStatus STATUS_EXPECTED = HttpStatus.BAD_REQUEST;
        final ErrorResponse RESPONSE_EXPECTED = new ErrorResponse(MESSAGE, null, NESTED_ERROR);

        final ResponseEntity response = responseFactory.error(EXCEPTION);

        assertEquals(STATUS_EXPECTED, response.getStatusCode());
        assertNotSame(RESPONSE_EXPECTED, response.getBody());
        assertEquals(RESPONSE_EXPECTED, response.getBody());
    }

    /**
     * Should get an error ResponseEntity when MethodArgumentNotValidException with BAD_REQUEST code
     */
    @Test
    public void errorMethodArgumentNotValidException() {
        final String MESSAGE = "Some data aren't valid.";
        final String DEV_MESSAGE = "dev message";
        final String FIELD_1 = "field1";
        final String MESSAGE_1 = "message 1";
        final String FIELD_2 = "field2";
        final String MESSAGE_2 = "message 2";
        final List<NestedError> NESTED_ERROR = Arrays.asList(new ValidationNestedError(FIELD_1, MESSAGE_1), new ValidationNestedError(FIELD_2, MESSAGE_2));
        final List<FieldError> FIELD_ERROR = Arrays.asList(new FieldError("", FIELD_1, MESSAGE_1), new FieldError("", FIELD_2, MESSAGE_2));
        final BindingResult BINDING_RESULT = mock(BindingResult.class);
        final MethodArgumentNotValidException EXCEPTION = mock(MethodArgumentNotValidException.class);
        final HttpStatus STATUS_EXPECTED = HttpStatus.BAD_REQUEST;
        final ErrorResponse RESPONSE_EXPECTED = new ErrorResponse(MESSAGE, DEV_MESSAGE, NESTED_ERROR);

        given(BINDING_RESULT.getFieldErrors()).willReturn(FIELD_ERROR);
        given(EXCEPTION.getBindingResult()).willReturn(BINDING_RESULT);
        given(EXCEPTION.getMessage()).willReturn(DEV_MESSAGE);

        final ResponseEntity response = responseFactory.error(EXCEPTION);

        assertEquals(STATUS_EXPECTED, response.getStatusCode());
        assertNotSame(RESPONSE_EXPECTED, response.getBody());
        assertEquals(RESPONSE_EXPECTED, response.getBody());
        verify(BINDING_RESULT, times(1)).getFieldErrors();
        verify(EXCEPTION, times(1)).getBindingResult();
        verify(EXCEPTION, times(1)).getMessage();
    }

    /**
     * Should get an error ResponseEntity when Exception with INTERNAL_SERVER_ERROR code
     */
    @Test
    public void errorException() {
        final String MESSAGE = "An error has occurred.";
        final String EXCEPTION_MESSAGE = "test";
        final Exception EXCEPTION = new Exception(EXCEPTION_MESSAGE);
        final HttpStatus STATUS_EXPECTED = HttpStatus.INTERNAL_SERVER_ERROR;
        final ErrorResponse RESPONSE_EXPECTED = new ErrorResponse(MESSAGE, EXCEPTION_MESSAGE, null);

        final ResponseEntity response = responseFactory.error(EXCEPTION);

        assertEquals(STATUS_EXPECTED, response.getStatusCode());
        assertNotSame(RESPONSE_EXPECTED, response.getBody());
        assertEquals(RESPONSE_EXPECTED, response.getBody());
    }
}