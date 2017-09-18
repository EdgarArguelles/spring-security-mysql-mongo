package app.pojos.responses.success;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

/**
 * Success Response pojo
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SuccessResponse {

    @Getter
    private Object data;

    @Getter
    private Object metaData;

    @Getter
    private String newToken;

    /**
     * Default constructor needed when deserialize
     */
    public SuccessResponse() {
    }

    /**
     * Create an instance without metadata
     *
     * @param data     main info returned on Response body
     * @param newToken refreshed token
     */
    public SuccessResponse(Object data, String newToken) {
        this(data, null, newToken);
    }

    /**
     * Create an instance with metadata
     *
     * @param data     main info returned on Response body
     * @param metaData extra info returned on Response body
     * @param newToken refreshed token
     */
    public SuccessResponse(Object data, Object metaData, String newToken) {
        this.data = data;
        this.metaData = metaData;
        this.newToken = newToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SuccessResponse)) return false;

        SuccessResponse that = (SuccessResponse) o;

        if (getData() != null ? !getData().equals(that.getData()) : that.getData() != null) return false;
        if (getMetaData() != null ? !getMetaData().equals(that.getMetaData()) : that.getMetaData() != null)
            return false;
        return getNewToken() != null ? getNewToken().equals(that.getNewToken()) : that.getNewToken() == null;
    }

    @Override
    public int hashCode() {
        int result = getData() != null ? getData().hashCode() : 0;
        result = 31 * result + (getMetaData() != null ? getMetaData().hashCode() : 0);
        result = 31 * result + (getNewToken() != null ? getNewToken().hashCode() : 0);
        return result;
    }
}