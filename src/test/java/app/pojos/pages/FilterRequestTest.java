package app.pojos.pages;

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
public class FilterRequestTest {

    @Autowired
    private ObjectMapper mapper;

    /**
     * Should create default constructor
     */
    @Test
    public void constructorDefault() {
        final FilterRequest filterRequest = new FilterRequest();

        assertNull(filterRequest.getField());
        assertNull(filterRequest.getValue());
        assertNull(filterRequest.getOperation());
    }

    /**
     * Should create complete constructor
     */
    @Test
    public void constructorComplete() {
        final String FIELD = "test";
        final String VALUE = "value";
        final String OPERATION = "operation";
        final FilterRequest filterRequest = new FilterRequest(FIELD, VALUE, OPERATION);

        assertSame(FIELD, filterRequest.getField());
        assertSame(VALUE, filterRequest.getValue());
        assertSame(OPERATION, filterRequest.getOperation());
    }

    /**
     * Should set and get operation
     */
    @Test
    public void setGetOperation() {
        final FilterRequest filterRequest = new FilterRequest();
        final String OPERATION = "operation";
        filterRequest.setOperation(OPERATION);

        assertSame(OPERATION, filterRequest.getOperation());
    }

    /**
     * Should serialize and deserialize
     */
    @Test
    public void serialize() throws IOException {
        final String FIELD = "test";
        final String VALUE = "value";
        final String OPERATION = "operation";
        final FilterRequest filterRequestExpected = new FilterRequest(FIELD, VALUE, OPERATION);

        final String json = mapper.writeValueAsString(filterRequestExpected);
        final FilterRequest filterRequestResult = mapper.readValue(json, FilterRequest.class);

        assertNotSame(filterRequestExpected, filterRequestResult);
        assertEquals(filterRequestExpected, filterRequestResult);
    }

    /**
     * Should equals instances
     */
    @Test
    public void equalsInstance() {
        final FilterRequest filterRequest = new FilterRequest("F", "V", "O");

        assertTrue(filterRequest.equals(filterRequest));
        assertFalse(filterRequest.equals(null));
        assertFalse(filterRequest.equals(new String()));
    }

    /**
     * Should fail equals due field
     */
    @Test
    public void noEqualsField() {
        final FilterRequest filterRequest1 = new FilterRequest("Field1", "value", "OP");
        final FilterRequest filterRequest2 = new FilterRequest("Field2", "value", "OP");
        final FilterRequest filterRequestNull = new FilterRequest(null, "value", "OP");

        assertNotEquals(filterRequest1, filterRequest2);
        assertNotEquals(filterRequest1, filterRequestNull);
        assertNotEquals(filterRequestNull, filterRequest1);
    }

    /**
     * Should fail equals due value
     */
    @Test
    public void noEqualsValue() {
        final FilterRequest filterRequest1 = new FilterRequest("Field1", "value", "OP");
        final FilterRequest filterRequest2 = new FilterRequest("Field1", "value2", "OP");
        final FilterRequest filterRequestNull = new FilterRequest("Field1", null, "OP");

        assertNotEquals(filterRequest1, filterRequest2);
        assertNotEquals(filterRequest1, filterRequestNull);
        assertNotEquals(filterRequestNull, filterRequest1);
    }

    /**
     * Should fail equals due operation
     */
    @Test
    public void noEqualsOperation() {
        final FilterRequest filterRequest1 = new FilterRequest("Field1", "value", "OP");
        final FilterRequest filterRequest2 = new FilterRequest("Field1", "value", "OP2");
        final FilterRequest filterRequestNull = new FilterRequest("Field1", "value", null);

        assertNotEquals(filterRequest1, filterRequest2);
        assertNotEquals(filterRequest1, filterRequestNull);
        assertNotEquals(filterRequestNull, filterRequest1);
    }

    /**
     * Should be equals
     */
    @Test
    public void testEquals() {
        final FilterRequest filterRequest1 = new FilterRequest("Field1", "value", "OP");
        final FilterRequest filterRequest2 = new FilterRequest("Field1", "value", "OP");
        final FilterRequest filterRequestNull1 = new FilterRequest();
        final FilterRequest filterRequestNull2 = new FilterRequest();

        assertNotSame(filterRequest1, filterRequest2);
        assertEquals(filterRequest1, filterRequest2);
        assertNotSame(filterRequestNull1, filterRequestNull2);
        assertEquals(filterRequestNull1, filterRequestNull2);
    }

    /**
     * Should have hashCode
     */
    @Test
    public void testHashCode() {
        final String FIELD = "test";
        final String VALUE = "value";
        final String OPERATION = "operation";
        final FilterRequest filterRequest = new FilterRequest(FIELD, VALUE, OPERATION);
        final FilterRequest filterRequestNull = new FilterRequest();

        int hashExpected = FIELD.hashCode();
        hashExpected = 31 * hashExpected + (VALUE.hashCode());
        hashExpected = 31 * hashExpected + (OPERATION.hashCode());

        final int hashResult = filterRequest.hashCode();

        assertEquals(hashExpected, hashResult);
        assertEquals(0, filterRequestNull.hashCode());
    }
}