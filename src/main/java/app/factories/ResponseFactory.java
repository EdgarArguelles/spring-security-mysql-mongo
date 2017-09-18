package app.factories;

import org.springframework.http.ResponseEntity;

/**
 * Create Response instances
 */
public interface ResponseFactory {

    /**
     * Create a success Response without metadata
     *
     * @param data main info returned on Response body
     * @return Success ResponseEntity
     */
    ResponseEntity success(Object data);

    /**
     * Create a success Response with metadata
     *
     * @param data     main info returned on Response body
     * @param metaData extra info returned on Response body
     * @return Success ResponseEntity
     */
    ResponseEntity success(Object data, Object metaData);

    /**
     * Create a success Response if data is not null otherwise create an NOT_FOUND error Response
     *
     * @param data main info returned on Response body
     * @return Success ResponseEntity or NOT_FOUND Error ResponseEntity if data is null
     */
    ResponseEntity successNotNull(Object data);

    /**
     * Create an Error Response caused by an Exception
     *
     * @param e Exception that causes the error
     * @return Error ResponseEntity
     */
    ResponseEntity error(Exception e);
}