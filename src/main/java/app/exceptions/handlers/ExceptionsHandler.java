package app.exceptions.handlers;

import app.exceptions.AppAuthenticationException;
import app.factories.ResponseFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.ProviderNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Handle all exceptions that happens in Application
 */
@ControllerAdvice
public class ExceptionsHandler {

    @Autowired
    private ResponseFactory responseFactory;

    @ExceptionHandler(Exception.class)
    public ResponseEntity handleException(Exception e) {
        if (e instanceof ProviderNotFoundException || e instanceof AccessDeniedException) {
            return responseFactory.error(new AppAuthenticationException("Access is denied."));
        }
        return responseFactory.error(e);
    }
}