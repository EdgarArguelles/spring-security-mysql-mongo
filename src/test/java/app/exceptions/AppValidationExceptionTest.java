package app.exceptions;

import app.pojos.responses.error.nesteds.NestedError;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class AppValidationExceptionTest {

    /**
     * Should create basic constructor
     */
    @Test
    public void constructorBasic() {
        final String MESSAGE = "test";
        final AppException exception = new AppValidationException(MESSAGE);

        assertSame(MESSAGE, exception.getMessage());
        assertNull(exception.getNestedErrors());
    }

    /**
     * Should create complete constructor
     */
    @Test
    public void constructorComplete() {
        final String MESSAGE = "test";
        final List<NestedError> NESTED_ERRORS = Collections.EMPTY_LIST;
        final AppException exception = new AppValidationException(MESSAGE, NESTED_ERRORS);

        assertSame(MESSAGE, exception.getMessage());
        assertSame(NESTED_ERRORS, exception.getNestedErrors());
    }
}