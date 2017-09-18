package app.pojos.responses.success;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SuccessResponseTest {

    @Autowired
    private ObjectMapper mapper;

    /**
     * Should create default constructor
     */
    @Test
    public void constructorDefault() {
        final SuccessResponse response = new SuccessResponse();

        assertNull(response.getData());
        assertNull(response.getMetaData());
        assertNull(response.getNewToken());
    }

    /**
     * Should create Data constructor
     */
    @Test
    public void constructorData() {
        final Object DATA = "test";
        final String NEW_TOKEN = "token";
        final SuccessResponse response = new SuccessResponse(DATA, NEW_TOKEN);

        assertSame(DATA, response.getData());
        assertNull(response.getMetaData());
        assertSame(NEW_TOKEN, response.getNewToken());
    }

    /**
     * Should create complete constructor
     */
    @Test
    public void constructorComplete() {
        final Object DATA = "test";
        final Object META_DATA = "value";
        final String NEW_TOKEN = "token";
        final SuccessResponse response = new SuccessResponse(DATA, META_DATA, NEW_TOKEN);

        assertSame(DATA, response.getData());
        assertSame(META_DATA, response.getMetaData());
        assertSame(NEW_TOKEN, response.getNewToken());
    }

    /**
     * Should serialize and deserialize
     */
    @Test
    public void serialize() throws IOException {
        final Object DATA = "test";
        final Object META_DATA = "value";
        final String NEW_TOKEN = "token";
        final SuccessResponse responseExpected = new SuccessResponse(DATA, META_DATA, NEW_TOKEN);

        final String json = mapper.writeValueAsString(responseExpected);
        final SuccessResponse responseResult = mapper.readValue(json, SuccessResponse.class);

        assertNotSame(responseExpected, responseResult);
        assertEquals(responseExpected, responseResult);
    }

    /**
     * Should ignore null value on json
     */
    @Test
    public void JsonNotIncludeNull() throws JsonProcessingException {
        final SuccessResponse response = new SuccessResponse("D", "NT");
        final SuccessResponse responseFull = new SuccessResponse("D", "MD", "NT");

        final String json = mapper.writeValueAsString(response);
        final String jsonFull = mapper.writeValueAsString(responseFull);

        assertFalse(json.contains("metaData"));
        assertTrue(jsonFull.contains("metaData"));
    }

    /**
     * Should equals instances
     */
    @Test
    public void equalsInstance() {
        final SuccessResponse response = new SuccessResponse("D", "NT");

        assertTrue(response.equals(response));
        assertFalse(response.equals(null));
        assertFalse(response.equals(new String()));
    }

    /**
     * Should fail equals due data
     */
    @Test
    public void noEqualsData() {
        final SuccessResponse response1 = new SuccessResponse("DATA", "META", "TOKEN");
        final SuccessResponse response2 = new SuccessResponse("DATA1", "META", "TOKEN");
        final SuccessResponse responseNull = new SuccessResponse(null, "META", "TOKEN");

        assertNotEquals(response1, response2);
        assertNotEquals(response1, responseNull);
        assertNotEquals(responseNull, response1);
    }

    /**
     * Should fail equals due metaData
     */
    @Test
    public void noEqualsMetaData() {
        final SuccessResponse response1 = new SuccessResponse("DATA", "META", "TOKEN");
        final SuccessResponse response2 = new SuccessResponse("DATA", "META1", "TOKEN");
        final SuccessResponse responseNull = new SuccessResponse("DATA", null, "TOKEN");

        assertNotEquals(response1, response2);
        assertNotEquals(response1, responseNull);
        assertNotEquals(responseNull, response1);
    }

    /**
     * Should fail equals due newToken
     */
    @Test
    public void noEqualsNewToken() {
        final SuccessResponse response1 = new SuccessResponse("DATA", "META", "TOKEN");
        final SuccessResponse response2 = new SuccessResponse("DATA", "META", "TOKEN1");
        final SuccessResponse responseNull = new SuccessResponse("DATA", "META", null);

        assertNotEquals(response1, response2);
        assertNotEquals(response1, responseNull);
        assertNotEquals(responseNull, response1);
    }

    /**
     * Should be equals
     */
    @Test
    public void testEquals() {
        final SuccessResponse response1 = new SuccessResponse("DATA", "META", "TOKEN");
        final SuccessResponse response2 = new SuccessResponse("DATA", "META", "TOKEN");
        final SuccessResponse responseNull1 = new SuccessResponse();
        final SuccessResponse responseNull2 = new SuccessResponse();

        assertNotSame(response1, response2);
        assertEquals(response1, response2);
        assertNotSame(responseNull1, responseNull2);
        assertEquals(responseNull1, responseNull2);
    }

    /**
     * Should have hashCode
     */
    @Test
    public void testHashCode() {
        final Object DATA = "test";
        final Object META_DATA = "value";
        final String NEW_TOKEN = "token";
        final SuccessResponse response = new SuccessResponse(DATA, META_DATA, NEW_TOKEN);
        final SuccessResponse responseNull = new SuccessResponse();

        int hashExpected = DATA.hashCode();
        hashExpected = 31 * hashExpected + (META_DATA.hashCode());
        hashExpected = 31 * hashExpected + (NEW_TOKEN.hashCode());

        final int hashResult = response.hashCode();

        assertEquals(hashExpected, hashResult);
        assertEquals(0, responseNull.hashCode());
    }
}