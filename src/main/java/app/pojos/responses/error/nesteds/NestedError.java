package app.pojos.responses.error.nesteds;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;

/**
 * Nested Error
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({@JsonSubTypes.Type(value = ValidationNestedError.class)})
public abstract class NestedError {

    @Getter
    private String message;

    /**
     * Default constructor needed when deserialize
     */
    public NestedError() {
    }

    /**
     * Create a nested error instance
     *
     * @param message nested message displayed to users
     */
    public NestedError(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NestedError)) return false;

        NestedError that = (NestedError) o;

        return getMessage() != null ? getMessage().equals(that.getMessage()) : that.getMessage() == null;
    }

    @Override
    public int hashCode() {
        return getMessage() != null ? getMessage().hashCode() : 0;
    }
}