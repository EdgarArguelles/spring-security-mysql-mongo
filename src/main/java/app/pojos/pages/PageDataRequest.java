package app.pojos.pages;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Page Request pojo
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageDataRequest {

    @NotNull
    @Getter
    @Setter
    private Integer page;

    @NotNull
    @Getter
    @Setter
    private Integer size;

    @Getter
    @Setter
    private String direction;

    @Getter
    @Setter
    private List<String> sort;

    @Valid
    @Getter
    private List<FilterRequest> filters;

    /**
     * Default constructor needed when deserialize
     */
    public PageDataRequest() {
    }

    /**
     * Create an instance
     *
     * @param page      current page
     * @param size      page size
     * @param direction page sort direction (could be ASC or DESC)
     * @param sort      page sort values
     * @param filters   filters to be performed
     */
    public PageDataRequest(Integer page, Integer size, String direction, List<String> sort, List<FilterRequest> filters) {
        this.page = page;
        this.size = size;
        this.direction = direction;
        this.sort = sort;
        this.filters = filters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PageDataRequest)) return false;

        PageDataRequest that = (PageDataRequest) o;

        if (getPage() != null ? !getPage().equals(that.getPage()) : that.getPage() != null) return false;
        if (getSize() != null ? !getSize().equals(that.getSize()) : that.getSize() != null) return false;
        if (getDirection() != null ? !getDirection().equals(that.getDirection()) : that.getDirection() != null)
            return false;
        if (getSort() != null ? !getSort().equals(that.getSort()) : that.getSort() != null) return false;
        return getFilters() != null ? getFilters().equals(that.getFilters()) : that.getFilters() == null;
    }

    @Override
    public int hashCode() {
        int result = getPage() != null ? getPage().hashCode() : 0;
        result = 31 * result + (getSize() != null ? getSize().hashCode() : 0);
        result = 31 * result + (getDirection() != null ? getDirection().hashCode() : 0);
        result = 31 * result + (getSort() != null ? getSort().hashCode() : 0);
        result = 31 * result + (getFilters() != null ? getFilters().hashCode() : 0);
        return result;
    }
}