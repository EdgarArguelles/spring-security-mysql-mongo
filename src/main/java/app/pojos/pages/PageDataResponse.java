package app.pojos.pages;

import lombok.Getter;

/**
 * Page Response pojo
 */
public class PageDataResponse {

    @Getter
    private Integer totalPages;

    @Getter
    private Long totalElements;

    @Getter
    private PageDataRequest dataRequest;

    /**
     * Default constructor needed when deserialize
     */
    public PageDataResponse() {
    }

    /**
     * Create an instance
     *
     * @param totalPages    max number of pages
     * @param totalElements max number of elements
     * @param dataRequest   pagination data request
     */
    public PageDataResponse(Integer totalPages, Long totalElements, PageDataRequest dataRequest) {
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.dataRequest = dataRequest;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PageDataResponse)) return false;

        PageDataResponse that = (PageDataResponse) o;

        if (getTotalPages() != null ? !getTotalPages().equals(that.getTotalPages()) : that.getTotalPages() != null)
            return false;
        if (getTotalElements() != null ? !getTotalElements().equals(that.getTotalElements()) : that.getTotalElements() != null)
            return false;
        return getDataRequest() != null ? getDataRequest().equals(that.getDataRequest()) : that.getDataRequest() == null;
    }

    @Override
    public int hashCode() {
        int result = getTotalPages() != null ? getTotalPages().hashCode() : 0;
        result = 31 * result + (getTotalElements() != null ? getTotalElements().hashCode() : 0);
        result = 31 * result + (getDataRequest() != null ? getDataRequest().hashCode() : 0);
        return result;
    }
}