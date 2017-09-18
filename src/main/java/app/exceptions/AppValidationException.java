package app.exceptions;

import app.pojos.responses.error.nesteds.NestedError;

import java.util.List;

public class AppValidationException extends AppException {

    /**
     * Constructs a new exception with the specified user readable message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for later retrieval by the {@link #getMessage()} method.
     */
    public AppValidationException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified user readable message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message      the detail message. The detail message is saved for later retrieval by the {@link #getMessage()} method.
     * @param nestedErrors nested errors displayed to users
     */
    public AppValidationException(String message, List<NestedError> nestedErrors) {
        super(message, nestedErrors);
    }
}