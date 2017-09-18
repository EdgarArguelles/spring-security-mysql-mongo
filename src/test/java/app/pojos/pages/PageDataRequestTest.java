package app.pojos.pages;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PageDataRequestTest {

    @Autowired
    private ObjectMapper mapper;

    /**
     * Should create default constructor
     */
    @Test
    public void constructorDefault() {
        final PageDataRequest pageDataRequest = new PageDataRequest();

        assertNull(pageDataRequest.getPage());
        assertNull(pageDataRequest.getSize());
        assertNull(pageDataRequest.getDirection());
        assertNull(pageDataRequest.getSort());
        assertNull(pageDataRequest.getFilters());
    }

    /**
     * Should create complete constructor
     */
    @Test
    public void constructorComplete() {
        final Integer PAGE = 1;
        final Integer SIZE = 2;
        final String DIRECTION = "direction";
        final List<String> SORT = Arrays.asList("S1", "S2");
        final List<FilterRequest> FILTERS = Arrays.asList(new FilterRequest("FR1", null, null), new FilterRequest("FR2", null, null));
        final PageDataRequest pageDataRequest = new PageDataRequest(PAGE, SIZE, DIRECTION, SORT, FILTERS);

        assertSame(PAGE, pageDataRequest.getPage());
        assertSame(SIZE, pageDataRequest.getSize());
        assertSame(DIRECTION, pageDataRequest.getDirection());
        assertSame(SORT, pageDataRequest.getSort());
        assertSame(FILTERS, pageDataRequest.getFilters());
    }

    /**
     * Should set and get page
     */
    @Test
    public void setGetPage() {
        final PageDataRequest pageDataRequest = new PageDataRequest();
        final Integer PAGE = 1;
        pageDataRequest.setPage(PAGE);

        assertSame(PAGE, pageDataRequest.getPage());
    }

    /**
     * Should set and get size
     */
    @Test
    public void setGetSize() {
        final PageDataRequest pageDataRequest = new PageDataRequest();
        final Integer SIZE = 2;
        pageDataRequest.setSize(SIZE);

        assertSame(SIZE, pageDataRequest.getSize());
    }

    /**
     * Should set and get direction
     */
    @Test
    public void setGetDirection() {
        final PageDataRequest pageDataRequest = new PageDataRequest();
        final String DIRECTION = "direction";
        pageDataRequest.setDirection(DIRECTION);

        assertSame(DIRECTION, pageDataRequest.getDirection());
    }

    /**
     * Should set and get sort
     */
    @Test
    public void setGetSort() {
        final PageDataRequest pageDataRequest = new PageDataRequest();
        final List<String> SORT = Arrays.asList("S1", "S2");
        pageDataRequest.setSort(SORT);

        assertSame(SORT, pageDataRequest.getSort());
    }

    /**
     * Should serialize and deserialize
     */
    @Test
    public void serialize() throws IOException {
        final Integer PAGE = 1;
        final Integer SIZE = 2;
        final String DIRECTION = "direction";
        final List<String> SORT = Arrays.asList("S1", "S2");
        final List<FilterRequest> FILTERS = Arrays.asList(new FilterRequest("FR1", "V1", "O1"), new FilterRequest("FR2", "V2", "O2"));
        final PageDataRequest pageDataRequestExpected = new PageDataRequest(PAGE, SIZE, DIRECTION, SORT, FILTERS);

        final String json = mapper.writeValueAsString(pageDataRequestExpected);
        final PageDataRequest pageDataRequestResult = mapper.readValue(json, PageDataRequest.class);

        assertNotSame(pageDataRequestExpected, pageDataRequestResult);
        assertEquals(pageDataRequestExpected, pageDataRequestResult);
    }

    /**
     * Should ignore null value on json
     */
    @Test
    public void JsonNotIncludeNull() throws JsonProcessingException {
        final PageDataRequest pageDataRequest = new PageDataRequest(1, 2, "D", null, null);
        final PageDataRequest pageDataRequestFull = new PageDataRequest(1, 2, "D", Arrays.asList("A"), Arrays.asList(new FilterRequest("F", "V", "O")));

        final String json = mapper.writeValueAsString(pageDataRequest);
        final String jsonFull = mapper.writeValueAsString(pageDataRequestFull);

        assertFalse(json.contains("sort"));
        assertFalse(json.contains("filter"));
        assertTrue(jsonFull.contains("sort"));
        assertTrue(jsonFull.contains("filter"));
    }

    /**
     * Should equals instances
     */
    @Test
    public void equalsInstance() {
        final PageDataRequest pageDataRequest = new PageDataRequest(1, 2, "D", null, null);

        assertTrue(pageDataRequest.equals(pageDataRequest));
        assertFalse(pageDataRequest.equals(null));
        assertFalse(pageDataRequest.equals(new String()));
    }

    /**
     * Should fail equals due page
     */
    @Test
    public void noEqualsPage() {
        final PageDataRequest pageDataRequest1 = new PageDataRequest(1, 2, "D", Arrays.asList("A", "B"), Arrays.asList(new FilterRequest("A", "B", "C"), new FilterRequest("D", "E", "F")));
        final PageDataRequest pageDataRequest2 = new PageDataRequest(11, 2, "D", Arrays.asList("A", "B"), Arrays.asList(new FilterRequest("A", "B", "C"), new FilterRequest("D", "E", "F")));
        final PageDataRequest pageDataRequestNull = new PageDataRequest(null, 2, "D", Arrays.asList("A", "B"), Arrays.asList(new FilterRequest("A", "B", "C"), new FilterRequest("D", "E", "F")));

        assertNotEquals(pageDataRequest1, pageDataRequest2);
        assertNotEquals(pageDataRequest1, pageDataRequestNull);
        assertNotEquals(pageDataRequestNull, pageDataRequest1);
    }

    /**
     * Should fail equals due size
     */
    @Test
    public void noEqualsSize() {
        final PageDataRequest pageDataRequest1 = new PageDataRequest(1, 2, "D", Arrays.asList("A", "B"), Arrays.asList(new FilterRequest("A", "B", "C"), new FilterRequest("D", "E", "F")));
        final PageDataRequest pageDataRequest2 = new PageDataRequest(1, 21, "D", Arrays.asList("A", "B"), Arrays.asList(new FilterRequest("A", "B", "C"), new FilterRequest("D", "E", "F")));
        final PageDataRequest pageDataRequestNull = new PageDataRequest(1, null, "D", Arrays.asList("A", "B"), Arrays.asList(new FilterRequest("A", "B", "C"), new FilterRequest("D", "E", "F")));

        assertNotEquals(pageDataRequest1, pageDataRequest2);
        assertNotEquals(pageDataRequest1, pageDataRequestNull);
        assertNotEquals(pageDataRequestNull, pageDataRequest1);
    }

    /**
     * Should fail equals due direction
     */
    @Test
    public void noEqualsDirection() {
        final PageDataRequest pageDataRequest1 = new PageDataRequest(1, 2, "D", Arrays.asList("A", "B"), Arrays.asList(new FilterRequest("A", "B", "C"), new FilterRequest("D", "E", "F")));
        final PageDataRequest pageDataRequest2 = new PageDataRequest(1, 2, "D2", Arrays.asList("A", "B"), Arrays.asList(new FilterRequest("A", "B", "C"), new FilterRequest("D", "E", "F")));
        final PageDataRequest pageDataRequestNull = new PageDataRequest(1, 2, null, Arrays.asList("A", "B"), Arrays.asList(new FilterRequest("A", "B", "C"), new FilterRequest("D", "E", "F")));

        assertNotEquals(pageDataRequest1, pageDataRequest2);
        assertNotEquals(pageDataRequest1, pageDataRequestNull);
        assertNotEquals(pageDataRequestNull, pageDataRequest1);
    }

    /**
     * Should fail equals due sort
     */
    @Test
    public void noEqualsSort() {
        final PageDataRequest pageDataRequest1 = new PageDataRequest(1, 2, "D", Arrays.asList("A", "B"), Arrays.asList(new FilterRequest("A", "B", "C"), new FilterRequest("D", "E", "F")));
        final PageDataRequest pageDataRequest2 = new PageDataRequest(1, 2, "D", Arrays.asList("A"), Arrays.asList(new FilterRequest("A", "B", "C"), new FilterRequest("D", "E", "F")));
        final PageDataRequest pageDataRequestNull = new PageDataRequest(1, 2, "D", null, Arrays.asList(new FilterRequest("A", "B", "C"), new FilterRequest("D", "E", "F")));

        assertNotEquals(pageDataRequest1, pageDataRequest2);
        assertNotEquals(pageDataRequest1, pageDataRequestNull);
        assertNotEquals(pageDataRequestNull, pageDataRequest1);
    }

    /**
     * Should fail equals due filters
     */
    @Test
    public void noEqualsFilters() {
        final PageDataRequest pageDataRequest1 = new PageDataRequest(1, 2, "D", Arrays.asList("A", "B"), Arrays.asList(new FilterRequest("A", "B", "C"), new FilterRequest("D", "E", "F")));
        final PageDataRequest pageDataRequest2 = new PageDataRequest(1, 2, "D", Arrays.asList("A", "B"), Arrays.asList(new FilterRequest("A", "B", "C")));
        final PageDataRequest pageDataRequestNull = new PageDataRequest(1, 2, "D", Arrays.asList("A", "B"), null);

        assertNotEquals(pageDataRequest1, pageDataRequest2);
        assertNotEquals(pageDataRequest1, pageDataRequestNull);
        assertNotEquals(pageDataRequestNull, pageDataRequest1);
    }

    /**
     * Should be equals
     */
    @Test
    public void testEquals() {
        final PageDataRequest pageDataRequest1 = new PageDataRequest(1, 2, "D", Arrays.asList("A", "B"), Arrays.asList(new FilterRequest("A", "B", "C"), new FilterRequest("D", "E", "F")));
        final PageDataRequest pageDataRequest2 = new PageDataRequest(1, 2, "D", Arrays.asList("A", "B"), Arrays.asList(new FilterRequest("A", "B", "C"), new FilterRequest("D", "E", "F")));
        final PageDataRequest pageDataRequestNull1 = new PageDataRequest();
        final PageDataRequest pageDataRequestNull2 = new PageDataRequest();

        assertNotSame(pageDataRequest1, pageDataRequest2);
        assertEquals(pageDataRequest1, pageDataRequest2);
        assertNotSame(pageDataRequestNull1, pageDataRequestNull2);
        assertEquals(pageDataRequestNull1, pageDataRequestNull2);
    }

    /**
     * Should have hashCode
     */
    @Test
    public void testHashCode() {
        final Integer PAGE = 1;
        final Integer SIZE = 2;
        final String DIRECTION = "direction";
        final List<String> SORT = Arrays.asList("S1", "S2");
        final List<FilterRequest> FILTERS = Arrays.asList(new FilterRequest("FR1", "V1", "O1"), new FilterRequest("FR2", "V2", "O2"));
        final PageDataRequest pageDataRequest = new PageDataRequest(PAGE, SIZE, DIRECTION, SORT, FILTERS);
        final PageDataRequest pageDataRequestNull = new PageDataRequest();

        int hashExpected = PAGE.hashCode();
        hashExpected = 31 * hashExpected + (SIZE.hashCode());
        hashExpected = 31 * hashExpected + (DIRECTION.hashCode());
        hashExpected = 31 * hashExpected + (SORT.hashCode());
        hashExpected = 31 * hashExpected + (FILTERS.hashCode());

        final int hashResult = pageDataRequest.hashCode();

        assertEquals(hashExpected, hashResult);
        assertEquals(0, pageDataRequestNull.hashCode());
    }
}