package app.factories.implementations;

import app.exceptions.AppAuthenticationException;
import app.exceptions.AppDontFoundException;
import app.exceptions.AppException;
import app.factories.ResponseFactory;
import app.pojos.responses.error.ErrorResponse;
import app.pojos.responses.error.nesteds.NestedError;
import app.pojos.responses.error.nesteds.ValidationNestedError;
import app.pojos.responses.success.SuccessResponse;
import app.security.services.TokenService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.ArrayList;
import java.util.List;

@Component
public class ResponseFactoryImpl implements ResponseFactory {

    @Autowired
    private TokenService tokenService;

    @Override
    public ResponseEntity success(Object data) {
        return success(data, null);
    }

    @Override
    public ResponseEntity success(Object data, Object metaData) {
        String newToken = null;
        try {
            newToken = tokenService.refreshToken();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return new ResponseEntity(new SuccessResponse(data, metaData, newToken), HttpStatus.OK);
    }

    @Override
    public ResponseEntity successNotNull(Object data) {
        if (data == null) {
            return error(new AppDontFoundException("Data don't found."));
        }
        return success(data);
    }

    @Override
    public ResponseEntity error(Exception e) {
        if (e instanceof AppException) {
            return errorByException((AppException) e);
        } else if (e instanceof MethodArgumentNotValidException) {
            return errorByException((MethodArgumentNotValidException) e);
        }
        return error("An error has occurred.", e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Create an Error Response with specific developer message and nested errors
     *
     * @param message      message displayed to users
     * @param devMessage   message displayed to developers
     * @param nestedErrors nested errors displayed to users
     * @param status       the status code
     * @return Error ResponseEntity
     */
    private ResponseEntity error(String message, String devMessage, List<NestedError> nestedErrors, HttpStatus status) {
        return new ResponseEntity(new ErrorResponse(message, devMessage, nestedErrors), status);
    }

    /**
     * Create an Error Response caused by a AppException
     *
     * @param e AppException that causes the error
     * @return Error ResponseEntity
     */
    private ResponseEntity errorByException(AppException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        if (e instanceof AppDontFoundException) {
            status = HttpStatus.NOT_FOUND;
        } else if (e instanceof AppAuthenticationException) {
            status = HttpStatus.UNAUTHORIZED;
        }

        return error(e.getMessage(), null, e.getNestedErrors(), status);
    }

    /**
     * Create a BAD_REQUEST Error Response caused by a MethodArgumentNotValidException
     *
     * @param e MethodArgumentNotValidException that causes the error
     * @return Error ResponseEntity
     */
    private ResponseEntity errorByException(MethodArgumentNotValidException e) {
        List<NestedError> nestedErrors = new ArrayList<>();
        e.getBindingResult().getFieldErrors().forEach(fieldError -> nestedErrors.add(new ValidationNestedError(fieldError.getField(), fieldError.getDefaultMessage())));
        return error("Some data aren't valid.", e.getMessage(), nestedErrors, HttpStatus.BAD_REQUEST);
    }
}