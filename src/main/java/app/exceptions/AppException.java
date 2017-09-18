package app.exceptions;

import app.pojos.responses.error.nesteds.NestedError;
import lombok.Getter;

import java.util.List;

/**
 * Custom exception with user readable message
 */
public abstract class AppException extends RuntimeException {

    @Getter
    private List<NestedError> nestedErrors;

    /**
     * Constructs a new exception with the specified user readable message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for later retrieval by the {@link #getMessage()} method.
     */
    public AppException(String message) {
        this(message, null);
    }

    /**
     * Constructs a new exception with the specified user readable message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message      the detail message. The detail message is saved for later retrieval by the {@link #getMessage()} method.
     * @param nestedErrors nested errors displayed to users
     */
    public AppException(String message, List<NestedError> nestedErrors) {
        super(message);
        this.nestedErrors = nestedErrors;
    }
}