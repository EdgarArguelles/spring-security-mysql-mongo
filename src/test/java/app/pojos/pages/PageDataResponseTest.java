package app.pojos.pages;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PageDataResponseTest {

    @Autowired
    private ObjectMapper mapper;

    /**
     * Should create default constructor
     */
    @Test
    public void constructorDefault() {
        final PageDataResponse pageDataResponse = new PageDataResponse();

        assertNull(pageDataResponse.getTotalPages());
        assertNull(pageDataResponse.getTotalElements());
        assertNull(pageDataResponse.getDataRequest());
    }

    /**
     * Should create complete constructor
     */
    @Test
    public void constructorComplete() {
        final Integer TOTAL_PAGES = 1;
        final Long TOTAL_ELEMENTS = 10L;
        final PageDataRequest DATA_REQUEST = new PageDataRequest(1, 2, "direction", Arrays.asList("A", "B"), Arrays.asList(new FilterRequest("F", "V", "O")));
        final PageDataResponse pageDataResponse = new PageDataResponse(TOTAL_PAGES, TOTAL_ELEMENTS, DATA_REQUEST);

        assertSame(TOTAL_PAGES, pageDataResponse.getTotalPages());
        assertSame(TOTAL_ELEMENTS, pageDataResponse.getTotalElements());
        assertSame(DATA_REQUEST, pageDataResponse.getDataRequest());
    }

    /**
     * Should serialize and deserialize
     */
    @Test
    public void serialize() throws IOException {
        final Integer TOTAL_PAGES = 1;
        final Long TOTAL_ELEMENTS = 10L;
        final PageDataRequest DATA_REQUEST = new PageDataRequest(1, 2, "direction", Arrays.asList("A", "B"), Arrays.asList(new FilterRequest("F", "V", "O")));
        final PageDataResponse pageDataResponseExpected = new PageDataResponse(TOTAL_PAGES, TOTAL_ELEMENTS, DATA_REQUEST);

        final String json = mapper.writeValueAsString(pageDataResponseExpected);
        final PageDataResponse pageDataResponseResult = mapper.readValue(json, PageDataResponse.class);

        assertNotSame(pageDataResponseExpected, pageDataResponseResult);
        assertEquals(pageDataResponseExpected, pageDataResponseResult);
    }

    /**
     * Should equals instances
     */
    @Test
    public void equalsInstance() {
        final PageDataResponse pageDataResponse = new PageDataResponse(1, 10L, null);

        assertTrue(pageDataResponse.equals(pageDataResponse));
        assertFalse(pageDataResponse.equals(null));
        assertFalse(pageDataResponse.equals(new String()));
    }

    /**
     * Should fail equals due totalPages
     */
    @Test
    public void noEqualsTotalPages() {
        final PageDataResponse pageDataResponse1 = new PageDataResponse(1, 10L, new PageDataRequest(1, 2, "direction", Arrays.asList("A", "B"), Arrays.asList(new FilterRequest("F", "V", "O"))));
        final PageDataResponse pageDataResponse2 = new PageDataResponse(11, 10L, new PageDataRequest(1, 2, "direction", Arrays.asList("A", "B"), Arrays.asList(new FilterRequest("F", "V", "O"))));
        final PageDataResponse pageDataResponseNull = new PageDataResponse(null, 10L, new PageDataRequest(1, 2, "direction", Arrays.asList("A", "B"), Arrays.asList(new FilterRequest("F", "V", "O"))));

        assertNotEquals(pageDataResponse1, pageDataResponse2);
        assertNotEquals(pageDataResponse1, pageDataResponseNull);
        assertNotEquals(pageDataResponseNull, pageDataResponse1);
    }

    /**
     * Should fail equals due totalElements
     */
    @Test
    public void noEqualsTotalElements() {
        final PageDataResponse pageDataResponse1 = new PageDataResponse(1, 10L, new PageDataRequest(1, 2, "direction", Arrays.asList("A", "B"), Arrays.asList(new FilterRequest("F", "V", "O"))));
        final PageDataResponse pageDataResponse2 = new PageDataResponse(1, 11L, new PageDataRequest(1, 2, "direction", Arrays.asList("A", "B"), Arrays.asList(new FilterRequest("F", "V", "O"))));
        final PageDataResponse pageDataResponseNull = new PageDataResponse(1, null, new PageDataRequest(1, 2, "direction", Arrays.asList("A", "B"), Arrays.asList(new FilterRequest("F", "V", "O"))));

        assertNotEquals(pageDataResponse1, pageDataResponse2);
        assertNotEquals(pageDataResponse1, pageDataResponseNull);
        assertNotEquals(pageDataResponseNull, pageDataResponse1);
    }

    /**
     * Should fail equals due dataRequest
     */
    @Test
    public void noEqualsDataRequest() {
        final PageDataResponse pageDataResponse1 = new PageDataResponse(1, 10L, new PageDataRequest(1, 2, "direction", Arrays.asList("A", "B"), Arrays.asList(new FilterRequest("F", "V", "O"))));
        final PageDataResponse pageDataResponse2 = new PageDataResponse(1, 10L, new PageDataRequest(11, 2, "direction", Arrays.asList("A", "B"), Arrays.asList(new FilterRequest("F", "V", "O"))));
        final PageDataResponse pageDataResponseNull = new PageDataResponse(1, 10L, null);

        assertNotEquals(pageDataResponse1, pageDataResponse2);
        assertNotEquals(pageDataResponse1, pageDataResponseNull);
        assertNotEquals(pageDataResponseNull, pageDataResponse1);
    }

    /**
     * Should be equals
     */
    @Test
    public void testEquals() {
        final PageDataResponse pageDataResponse1 = new PageDataResponse(1, 10L, new PageDataRequest(1, 2, "direction", Arrays.asList("A", "B"), Arrays.asList(new FilterRequest("F", "V", "O"))));
        final PageDataResponse pageDataResponse2 = new PageDataResponse(1, 10L, new PageDataRequest(1, 2, "direction", Arrays.asList("A", "B"), Arrays.asList(new FilterRequest("F", "V", "O"))));
        final PageDataResponse pageDataResponseNull1 = new PageDataResponse();
        final PageDataResponse pageDataResponseNull2 = new PageDataResponse();

        assertNotSame(pageDataResponse1, pageDataResponse2);
        assertEquals(pageDataResponse1, pageDataResponse2);
        assertNotSame(pageDataResponseNull1, pageDataResponseNull2);
        assertEquals(pageDataResponseNull1, pageDataResponseNull2);
    }

    /**
     * Should have hashCode
     */
    @Test
    public void testHashCode() {
        final Integer TOTAL_PAGES = 1;
        final Long TOTAL_ELEMENTS = 10L;
        final PageDataRequest DATA_REQUEST = new PageDataRequest(1, 2, "direction", Arrays.asList("A", "B"), Arrays.asList(new FilterRequest("F", "V", "O")));
        final PageDataResponse pageDataResponse = new PageDataResponse(TOTAL_PAGES, TOTAL_ELEMENTS, DATA_REQUEST);
        final PageDataResponse pageDataResponseNull = new PageDataResponse();

        int hashExpected = TOTAL_PAGES.hashCode();
        hashExpected = 31 * hashExpected + (TOTAL_ELEMENTS.hashCode());
        hashExpected = 31 * hashExpected + (DATA_REQUEST.hashCode());

        final int hashResult = pageDataResponse.hashCode();

        assertEquals(hashExpected, hashResult);
        assertEquals(0, pageDataResponseNull.hashCode());
    }
}