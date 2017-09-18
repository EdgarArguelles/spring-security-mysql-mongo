package app.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AuthenticationTest {

    @Autowired
    private ObjectMapper mapper;

    /**
     * Should create default constructor
     */
    @Test
    public void constructorDefault() {
        final Authentication authentication = new Authentication();

        assertNull(authentication.getId());
        assertNull(authentication.getUsername());
        assertNull(authentication.getPassword());
        assertNull(authentication.getAuthProvider());
        assertNull(authentication.getPerson());
    }

    /**
     * Should create Id constructor
     */
    @Test
    public void constructorId() {
        final String ID = "ID";
        final Authentication authentication = new Authentication(ID);

        assertSame(ID, authentication.getId());
        assertNull(authentication.getUsername());
        assertNull(authentication.getPassword());
        assertNull(authentication.getAuthProvider());
        assertNull(authentication.getPerson());
    }

    /**
     * Should create complete constructor
     */
    @Test
    public void constructorComplete() {
        final String USERNAME = "username";
        final String PASSWORD = "password";
        final AuthProvider AUTH_PROVIDER = new AuthProvider("AP1");
        final Person PERSON = new Person("P1");
        final Authentication authentication = new Authentication(USERNAME, PASSWORD, AUTH_PROVIDER, PERSON);

        assertNull(authentication.getId());
        assertSame(USERNAME, authentication.getUsername());
        assertSame(PASSWORD, authentication.getPassword());
        assertSame(AUTH_PROVIDER, authentication.getAuthProvider());
        assertSame(PERSON, authentication.getPerson());
    }

    /**
     * Should set and get id
     */
    @Test
    public void setGetID() {
        final Authentication authentication = new Authentication();
        final String ID = "ID";
        authentication.setId(ID);

        assertSame(ID, authentication.getId());
    }

    /**
     * Should set and get username
     */
    @Test
    public void setGetUsername() {
        final Authentication authentication = new Authentication();
        final String USERNAME = "username";
        authentication.setUsername(USERNAME);

        assertSame(USERNAME, authentication.getUsername());
    }

    /**
     * Should set and get password
     */
    @Test
    public void setGetPassword() {
        final Authentication authentication = new Authentication();
        final String PASSWORD = "password";
        authentication.setPassword(PASSWORD);

        assertSame(PASSWORD, authentication.getPassword());
    }

    /**
     * Should set and get authProvider
     */
    @Test
    public void setGetAuthProvider() {
        final Authentication authentication = new Authentication();
        final AuthProvider AUTH_PROVIDER = new AuthProvider("AP1");
        authentication.setAuthProvider(AUTH_PROVIDER);

        assertSame(AUTH_PROVIDER, authentication.getAuthProvider());
    }

    /**
     * Should set and get person
     */
    @Test
    public void setGetPerson() {
        final Authentication authentication = new Authentication();
        final Person PERSON = new Person("P1");
        authentication.setPerson(PERSON);

        assertSame(PERSON, authentication.getPerson());
    }

    /**
     * Should call cleanAuthData
     */
    @Test
    public void cleanAuthDataWhenNull() {
        final Authentication authentication = new Authentication("U", "P", null, new Person("P1"));

        authentication.cleanAuthData();

        assertNull(authentication.getPassword());
        assertNull(authentication.getAuthProvider());
    }

    /**
     * Should call cleanAuthData
     */
    @Test
    public void cleanAuthDataWhenNotNull() {
        final Authentication authentication = new Authentication("U", "P", new AuthProvider("N", "D", "U", "AK", "AS"), new Person("P1"));

        authentication.cleanAuthData();

        assertNull(authentication.getPassword());
        assertNull(authentication.getAuthProvider().getUrl());
        assertNull(authentication.getAuthProvider().getAuthKey());
        assertNull(authentication.getAuthProvider().getAuthSecret());
    }

    /**
     * Should not throw an exception
     */
    @Test
    public void cleanRelationsWhenNull() {
        final Authentication authentication = new Authentication();
        authentication.cleanRelations(true);
    }

    /**
     * Should clean all Relations not annotated with @JsonIgnore
     */
    @Test
    public void cleanRelationsWhenNotNullAndCleanAll() {
        final AuthProvider authProviderExpected = new AuthProvider("AP1");
        final AuthProvider AUTH_PROVIDER = new AuthProvider("AP1");
        final Person personExpected = new Person("P1");
        final Person PERSON = new Person("P1");
        PERSON.setRoles(new HashSet<>(Arrays.asList(
                new Role("N", "D", new HashSet<>(Arrays.asList(new Permission("P"))))
        )));
        final Authentication authentication = new Authentication("U", "P", AUTH_PROVIDER, PERSON);
        authentication.cleanRelations(true);

        assertNotSame(authProviderExpected, authentication.getAuthProvider());
        assertEquals(authProviderExpected, authentication.getAuthProvider());
        assertNotSame(personExpected, authentication.getPerson());
        assertEquals(personExpected, authentication.getPerson());
    }

    /**
     * Should clean nested Relations not annotated with @JsonIgnore
     */
    @Test
    public void cleanRelationsWhenNotNullAndNotCleanAll() {
        final AuthProvider authProviderExpected = new AuthProvider("AP1");
        final AuthProvider AUTH_PROVIDER = new AuthProvider("AP1");
        final Person personExpected = new Person("P1");
        personExpected.setRoles(new HashSet<>(Arrays.asList(
                new Role("N", "D", null)
        )));
        final Person PERSON = new Person("P1");
        PERSON.setRoles(new HashSet<>(Arrays.asList(
                new Role("N", "D", new HashSet<>(Arrays.asList(new Permission("P"))))
        )));
        final Authentication authentication = new Authentication("U", "P", AUTH_PROVIDER, PERSON);
        authentication.cleanRelations(false);

        assertNotSame(authProviderExpected, authentication.getAuthProvider());
        assertEquals(authProviderExpected, authentication.getAuthProvider());
        assertNotSame(personExpected, authentication.getPerson());
        assertEquals(personExpected, authentication.getPerson());
    }

    /**
     * Should get toString
     */
    @Test
    public void toStringValid() {
        final String ID = "ID";
        final String USERNAME = "username";
        final Authentication authentication = new Authentication(ID);
        authentication.setUsername(USERNAME);

        assertEquals("{" + ID + ", " + USERNAME + "}", authentication.toString());
    }

    /**
     * Should serialize and deserialize
     */
    @Test
    public void serialize() throws IOException {
        final String ID = "ID";
        final String USERNAME = "username";
        final String PASSWORD = "password";
        final AuthProvider AUTH_PROVIDER = new AuthProvider("N", "D", null, null, null);
        final Person PERSON = new Person("P1");
        final Authentication authenticationExpected = new Authentication(USERNAME, PASSWORD, AUTH_PROVIDER, PERSON);
        authenticationExpected.setId(ID);

        final String json = mapper.writeValueAsString(authenticationExpected);
        final Authentication authenticationResult = mapper.readValue(json, Authentication.class);

        assertNotSame(authenticationExpected, authenticationResult);
        assertEquals(authenticationExpected, authenticationResult);
    }

    /**
     * Should equals instances
     */
    @Test
    public void equalsInstance() {
        final Authentication authentication = new Authentication("ID");

        assertTrue(authentication.equals(authentication));
        assertFalse(authentication.equals(null));
        assertFalse(authentication.equals(new String()));
    }

    /**
     * Should fail equals due ID
     */
    @Test
    public void noEqualsID() {
        final Authentication authentication1 = new Authentication("U", "P", new AuthProvider("AP1"), new Person("P1"));
        authentication1.setId("ID");
        final Authentication authentication2 = new Authentication("U", "P", new AuthProvider("AP1"), new Person("P1"));
        authentication2.setId("ID2");
        final Authentication authenticationNull = new Authentication("U", "P", new AuthProvider("AP1"), new Person("P1"));
        authenticationNull.setId(null);

        assertNotEquals(authentication1, authentication2);
        assertNotEquals(authentication1, authenticationNull);
        assertNotEquals(authenticationNull, authentication1);
    }

    /**
     * Should fail equals due username
     */
    @Test
    public void noEqualsUsername() {
        final Authentication authentication1 = new Authentication("U", "P", new AuthProvider("AP1"), new Person("P1"));
        authentication1.setId("ID");
        final Authentication authentication2 = new Authentication("U2", "P", new AuthProvider("AP1"), new Person("P1"));
        authentication2.setId("ID");
        final Authentication authenticationNull = new Authentication(null, "P", new AuthProvider("AP1"), new Person("P1"));
        authenticationNull.setId("ID");

        assertNotEquals(authentication1, authentication2);
        assertNotEquals(authentication1, authenticationNull);
        assertNotEquals(authenticationNull, authentication1);
    }

    /**
     * Should fail equals due password
     */
    @Test
    public void noEqualsPassword() {
        final Authentication authentication1 = new Authentication("U", "P", new AuthProvider("AP1"), new Person("P1"));
        authentication1.setId("ID");
        final Authentication authentication2 = new Authentication("U", "P1", new AuthProvider("AP1"), new Person("P1"));
        authentication2.setId("ID");
        final Authentication authenticationNull = new Authentication("U", null, new AuthProvider("AP1"), new Person("P1"));
        authenticationNull.setId("ID");

        assertNotEquals(authentication1, authentication2);
        assertNotEquals(authentication1, authenticationNull);
        assertNotEquals(authenticationNull, authentication1);
    }

    /**
     * Should fail equals due authProvider
     */
    @Test
    public void noEqualsAuthProvider() {
        final Authentication authentication1 = new Authentication("U", "P", new AuthProvider("AP1"), new Person("P1"));
        authentication1.setId("ID");
        final Authentication authentication2 = new Authentication("U", "P", new AuthProvider("AP2"), new Person("P1"));
        authentication2.setId("ID");
        final Authentication authenticationNull = new Authentication("U", "P", null, new Person("P1"));
        authenticationNull.setId("ID");

        assertNotEquals(authentication1, authentication2);
        assertNotEquals(authentication1, authenticationNull);
        assertNotEquals(authenticationNull, authentication1);
    }

    /**
     * Should fail equals due person
     */
    @Test
    public void noEqualsPerson() {
        final Authentication authentication1 = new Authentication("U", "P", new AuthProvider("AP1"), new Person("P1"));
        authentication1.setId("ID");
        final Authentication authentication2 = new Authentication("U", "P", new AuthProvider("AP1"), new Person("P2"));
        authentication2.setId("ID");
        final Authentication authenticationNull = new Authentication("U", "P", new AuthProvider("AP1"), null);
        authenticationNull.setId("ID");

        assertNotEquals(authentication1, authentication2);
        assertNotEquals(authentication1, authenticationNull);
        assertNotEquals(authenticationNull, authentication1);
    }

    /**
     * Should be equals
     */
    @Test
    public void testEquals() {
        final Authentication authentication1 = new Authentication("U", "P", new AuthProvider("AP1"), new Person("P1"));
        authentication1.setId("ID");
        final Authentication authentication2 = new Authentication("U", "P", new AuthProvider("AP1"), new Person("P1"));
        authentication2.setId("ID");
        final Authentication authenticationNull1 = new Authentication();
        final Authentication authenticationNull2 = new Authentication();

        assertNotSame(authentication1, authentication2);
        assertEquals(authentication1, authentication2);
        assertNotSame(authenticationNull1, authenticationNull2);
        assertEquals(authenticationNull1, authenticationNull2);
    }

    /**
     * Should have hashCode
     */
    @Test
    public void testHashCode() {
        final String ID = "ID";
        final String USERNAME = "username";
        final String PASSWORD = "password";
        final AuthProvider AUTH_PROVIDER = new AuthProvider("AP1");
        final Person PERSON = new Person("P1");
        final Authentication authentication = new Authentication(USERNAME, PASSWORD, AUTH_PROVIDER, PERSON);
        authentication.setId(ID);
        final Authentication authenticationNull = new Authentication();

        int hashExpected = ID.hashCode();
        hashExpected = 31 * hashExpected + (USERNAME.hashCode());
        hashExpected = 31 * hashExpected + (PASSWORD.hashCode());
        hashExpected = 31 * hashExpected + (AUTH_PROVIDER.hashCode());
        hashExpected = 31 * hashExpected + (PERSON.hashCode());

        final int hashResult = authentication.hashCode();

        assertEquals(hashExpected, hashResult);
        assertEquals(0, authenticationNull.hashCode());
    }
}