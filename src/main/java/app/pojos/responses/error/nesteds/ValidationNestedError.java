package app.pojos.responses.error.nesteds;

import lombok.Getter;

/**
 * Validation Error pojo
 */
public class ValidationNestedError extends NestedError {

    @Getter
    private String field;

    /**
     * Default constructor needed when deserialize
     */
    public ValidationNestedError() {
    }

    /**
     * Create an instance
     *
     * @param field   field that causes the error
     * @param message message displayed to users
     */
    public ValidationNestedError(String field, String message) {
        super(message);
        this.field = field;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ValidationNestedError)) return false;
        if (!super.equals(o)) return false;

        ValidationNestedError that = (ValidationNestedError) o;

        return getField() != null ? getField().equals(that.getField()) : that.getField() == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getField() != null ? getField().hashCode() : 0);
        return result;
    }
}