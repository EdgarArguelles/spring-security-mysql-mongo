package app.pojos.pages;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Filter Request pojo
 */
public class FilterRequest {

    @NotNull
    @Size(min = 1, max = 255)
    @Getter
    private String field;

    @Getter
    private String value;

    @NotNull
    @Size(min = 2, max = 255)
    @Getter
    @Setter
    private String operation;

    /**
     * Default constructor needed when deserialize
     */
    public FilterRequest() {
    }

    /**
     * Create an instance
     *
     * @param field     field to be filtered
     * @param value     field value
     * @param operation operation to be performed
     */
    public FilterRequest(String field, String value, String operation) {
        this.field = field;
        this.value = value;
        this.operation = operation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FilterRequest)) return false;

        FilterRequest that = (FilterRequest) o;

        if (getField() != null ? !getField().equals(that.getField()) : that.getField() != null) return false;
        if (getValue() != null ? !getValue().equals(that.getValue()) : that.getValue() != null) return false;
        return getOperation() != null ? getOperation().equals(that.getOperation()) : that.getOperation() == null;
    }

    @Override
    public int hashCode() {
        int result = getField() != null ? getField().hashCode() : 0;
        result = 31 * result + (getValue() != null ? getValue().hashCode() : 0);
        result = 31 * result + (getOperation() != null ? getOperation().hashCode() : 0);
        return result;
    }
}