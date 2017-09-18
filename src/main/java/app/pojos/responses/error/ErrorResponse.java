package app.pojos.responses.error;

import app.pojos.responses.error.nesteds.NestedError;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.util.List;

/**
 * Error Response pojo
 */
public class ErrorResponse {

    @Getter
    private Error error;

    /**
     * Default constructor needed when deserialize
     */
    public ErrorResponse() {
    }

    /**
     * Create an instance without specific developer message
     *
     * @param message message displayed to users
     */
    public ErrorResponse(String message) {
        this(message, null);
    }

    /**
     * Create an instance with specific developer message
     *
     * @param message    message displayed to users
     * @param devMessage message displayed to developers
     */
    public ErrorResponse(String message, String devMessage) {
        this(message, devMessage, null);
    }

    /**
     * Create an instance with specific developer message and nested errors
     *
     * @param message      message displayed to users
     * @param devMessage   message displayed to developers
     * @param nestedErrors nested errors displayed to users
     */
    public ErrorResponse(String message, String devMessage, List<NestedError> nestedErrors) {
        this.error = new Error(message, devMessage, nestedErrors);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ErrorResponse)) return false;

        ErrorResponse that = (ErrorResponse) o;

        return getError() != null ? getError().equals(that.getError()) : that.getError() == null;
    }

    @Override
    public int hashCode() {
        return getError() != null ? getError().hashCode() : 0;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private class Error {

        @Getter
        private String message;

        @Getter
        private String devMessage;

        @Getter
        private List<NestedError> nestedErrors;

        public Error() {
        }

        public Error(String message, String devMessage, List<NestedError> nestedErrors) {
            this.message = message;
            this.devMessage = devMessage;
            this.nestedErrors = nestedErrors;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Error)) return false;

            Error that = (Error) o;

            if (getMessage() != null ? !getMessage().equals(that.getMessage()) : that.getMessage() != null)
                return false;
            if (getDevMessage() != null ? !getDevMessage().equals(that.getDevMessage()) : that.getDevMessage() != null)
                return false;
            return getNestedErrors() != null ? getNestedErrors().equals(that.getNestedErrors()) : that.getNestedErrors() == null;
        }

        @Override
        public int hashCode() {
            int result = getMessage() != null ? getMessage().hashCode() : 0;
            result = 31 * result + (getDevMessage() != null ? getDevMessage().hashCode() : 0);
            result = 31 * result + (getNestedErrors() != null ? getNestedErrors().hashCode() : 0);
            return result;
        }
    }
}