package app.exceptions.handlers;

import app.exceptions.AppAuthenticationException;
import app.factories.ResponseFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.ProviderNotFoundException;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ExceptionsHandlerTest {

    @Autowired
    private ExceptionsHandler exceptionsHandler;

    @MockBean
    private ResponseFactory responseFactory;

    @Captor
    private ArgumentCaptor<Exception> captor;

    /**
     * Should handle ProviderNotFoundException
     */
    @Test
    public void handleProviderNotFoundException() {
        final Exception exception = new ProviderNotFoundException("");

        final ResponseEntity responseMocked = new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        given(responseFactory.error(any(Exception.class))).willReturn(responseMocked);

        final ResponseEntity responseResult = exceptionsHandler.handleException(exception);

        assertSame(responseMocked, responseResult);
        verify(responseFactory, times(1)).error(captor.capture());
        assertTrue(captor.getValue() instanceof AppAuthenticationException);
    }

    /**
     * Should handle AccessDeniedException
     */
    @Test
    public void handleAccessDeniedException() {
        final Exception exception = new AccessDeniedException("");

        final ResponseEntity responseMocked = new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        given(responseFactory.error(any(Exception.class))).willReturn(responseMocked);

        final ResponseEntity responseResult = exceptionsHandler.handleException(exception);

        assertSame(responseMocked, responseResult);
        verify(responseFactory, times(1)).error(captor.capture());
        assertTrue(captor.getValue() instanceof AppAuthenticationException);
    }

    /**
     * Should handle Exception
     */
    @Test
    public void handleException() {
        final Exception exception = new RuntimeException("");

        final ResponseEntity responseMocked = new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        given(responseFactory.error(exception)).willReturn(responseMocked);

        final ResponseEntity responseResult = exceptionsHandler.handleException(exception);

        assertSame(responseMocked, responseResult);
        verify(responseFactory, times(1)).error(exception);
    }
}