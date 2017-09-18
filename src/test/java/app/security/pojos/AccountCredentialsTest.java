package app.security.pojos;

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
public class AccountCredentialsTest {

    @Autowired
    private ObjectMapper mapper;

    /**
     * Should create default constructor
     */
    @Test
    public void constructorDefault() {
        final AccountCredentials credentials = new AccountCredentials();

        assertNull(credentials.getUsername());
        assertNull(credentials.getPassword());
    }

    /**
     * Should create complete constructor
     */
    @Test
    public void constructorComplete() {
        final String USERNAME = "test";
        final String PASSWORD = "pass";
        final AccountCredentials credentials = new AccountCredentials(USERNAME, PASSWORD);

        assertSame(USERNAME, credentials.getUsername());
        assertSame(PASSWORD, credentials.getPassword());
    }

    /**
     * Should serialize and deserialize
     */
    @Test
    public void serialize() throws IOException {
        final String USERNAME = "test";
        final String PASSWORD = "pass";
        final AccountCredentials credentialsExpected = new AccountCredentials(USERNAME, PASSWORD);

        final String json = mapper.writeValueAsString(credentialsExpected);
        final AccountCredentials credentialsResult = mapper.readValue(json, AccountCredentials.class);

        assertNotSame(credentialsExpected, credentialsResult);
        assertEquals(credentialsExpected, credentialsResult);
    }

    /**
     * Should equals instances
     */
    @Test
    public void equalsInstance() {
        final AccountCredentials credentials = new AccountCredentials("U1", "P1");

        assertTrue(credentials.equals(credentials));
        assertFalse(credentials.equals(null));
        assertFalse(credentials.equals(new String()));
    }

    /**
     * Should fail equals due username
     */
    @Test
    public void noEqualsUsername() {
        final AccountCredentials credentials1 = new AccountCredentials("U1", "P1");
        final AccountCredentials credentials2 = new AccountCredentials("U2", "P1");
        final AccountCredentials credentialsNull = new AccountCredentials(null, "P1");

        assertNotEquals(credentials1, credentials2);
        assertNotEquals(credentials1, credentialsNull);
        assertNotEquals(credentialsNull, credentials1);
    }

    /**
     * Should fail equals due password
     */
    @Test
    public void noEqualsPassword() {
        final AccountCredentials credentials1 = new AccountCredentials("U1", "P1");
        final AccountCredentials credentials2 = new AccountCredentials("U1", "P2");
        final AccountCredentials credentialsNull = new AccountCredentials("U1", null);

        assertNotEquals(credentials1, credentials2);
        assertNotEquals(credentials1, credentialsNull);
        assertNotEquals(credentialsNull, credentials1);
    }

    /**
     * Should be equals
     */
    @Test
    public void testEquals() {
        final AccountCredentials credentials1 = new AccountCredentials("U1", "P1");
        final AccountCredentials credentials2 = new AccountCredentials("U1", "P1");
        final AccountCredentials credentialsNull1 = new AccountCredentials();
        final AccountCredentials credentialsNull2 = new AccountCredentials();

        assertNotSame(credentials1, credentials2);
        assertEquals(credentials1, credentials2);
        assertNotSame(credentialsNull1, credentialsNull2);
        assertEquals(credentialsNull1, credentialsNull2);
    }

    /**
     * Should have hashCode
     */
    @Test
    public void testHashCode() {
        final String USERNAME = "test";
        final String PASSWORD = "pass";
        final AccountCredentials credentials = new AccountCredentials(USERNAME, PASSWORD);
        final AccountCredentials credentialsNull = new AccountCredentials();

        int hashExpected = USERNAME.hashCode();
        hashExpected = 31 * hashExpected + (PASSWORD.hashCode());

        final int hashResult = credentials.hashCode();

        assertEquals(hashExpected, hashResult);
        assertEquals(0, credentialsNull.hashCode());
    }
}