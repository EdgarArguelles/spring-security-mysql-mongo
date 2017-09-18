package app.models;

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
public class AuthProviderTest {

    @Autowired
    private ObjectMapper mapper;

    /**
     * Should create default constructor
     */
    @Test
    public void constructorDefault() {
        final AuthProvider authProvider = new AuthProvider();

        assertNull(authProvider.getId());
        assertNull(authProvider.getName());
        assertNull(authProvider.getDescription());
        assertNull(authProvider.getUrl());
        assertNull(authProvider.getAuthKey());
        assertNull(authProvider.getAuthSecret());
        assertNull(authProvider.getAuthentications());
    }

    /**
     * Should create Id constructor
     */
    @Test
    public void constructorId() {
        final String ID = "ID";
        final AuthProvider authProvider = new AuthProvider(ID);

        assertSame(ID, authProvider.getId());
        assertNull(authProvider.getName());
        assertNull(authProvider.getDescription());
        assertNull(authProvider.getUrl());
        assertNull(authProvider.getAuthKey());
        assertNull(authProvider.getAuthSecret());
        assertNull(authProvider.getAuthentications());
    }

    /**
     * Should create complete constructor
     */
    @Test
    public void constructorComplete() {
        final String NAME = "name";
        final String DESCRIPTION = "description";
        final String URL = "url";
        final String AUTH_KEY = "key";
        final String AUTH_SECRET = "secret";
        final AuthProvider authProvider = new AuthProvider(NAME, DESCRIPTION, URL, AUTH_KEY, AUTH_SECRET);

        assertNull(authProvider.getId());
        assertSame(NAME, authProvider.getName());
        assertSame(DESCRIPTION, authProvider.getDescription());
        assertSame(URL, authProvider.getUrl());
        assertSame(AUTH_KEY, authProvider.getAuthKey());
        assertSame(AUTH_SECRET, authProvider.getAuthSecret());
        assertNull(authProvider.getAuthentications());
    }

    /**
     * Should set and get id
     */
    @Test
    public void setGetID() {
        final AuthProvider authProvider = new AuthProvider();
        final String ID = "ID";
        authProvider.setId(ID);

        assertSame(ID, authProvider.getId());
    }

    /**
     * Should set and get name
     */
    @Test
    public void setGetName() {
        final AuthProvider authProvider = new AuthProvider();
        final String NAME = "name";
        authProvider.setName(NAME);

        assertSame(NAME, authProvider.getName());
    }

    /**
     * Should set and get description
     */
    @Test
    public void setGetDescription() {
        final AuthProvider authProvider = new AuthProvider();
        final String DESCRIPTION = "description";
        authProvider.setDescription(DESCRIPTION);

        assertSame(DESCRIPTION, authProvider.getDescription());
    }

    /**
     * Should set and get url
     */
    @Test
    public void setGetUrl() {
        final AuthProvider authProvider = new AuthProvider();
        final String URL = "url";
        authProvider.setUrl(URL);

        assertSame(URL, authProvider.getUrl());
    }

    /**
     * Should set and get authKey
     */
    @Test
    public void setGetAuthKey() {
        final AuthProvider authProvider = new AuthProvider();
        final String AUTH_KEY = "key";
        authProvider.setAuthKey(AUTH_KEY);

        assertSame(AUTH_KEY, authProvider.getAuthKey());
    }

    /**
     * Should set and get authSecret
     */
    @Test
    public void setGetAuthSecret() {
        final AuthProvider authProvider = new AuthProvider();
        final String AUTH_SECRET = "secret";
        authProvider.setAuthSecret(AUTH_SECRET);

        assertSame(AUTH_SECRET, authProvider.getAuthSecret());
    }

    /**
     * Should set and get authentications
     */
    @Test
    public void setGetAuthentications() {
        final AuthProvider authProvider = new AuthProvider();
        final List<Authentication> AUTHENTICATION = Arrays.asList(new Authentication("A1"), new Authentication("A2"));
        authProvider.setAuthentications(AUTHENTICATION);

        assertSame(AUTHENTICATION, authProvider.getAuthentications());
    }

    /**
     * Should not throw an exception
     */
    @Test
    public void cleanRelations() {
        final AuthProvider authProvider = new AuthProvider();
        authProvider.cleanRelations(true);
    }

    /**
     * Should get toString
     */
    @Test
    public void toStringValid() {
        final String ID = "ID";
        final String NAME = "name";
        final AuthProvider authProvider = new AuthProvider(ID);
        authProvider.setName(NAME);

        assertEquals("{" + ID + ", " + NAME + "}", authProvider.toString());
    }

    /**
     * Should serialize and deserialize
     */
    @Test
    public void serialize() throws IOException {
        final String ID = "ID";
        final String NAME = "name";
        final String DESCRIPTION = "description";
        final AuthProvider authProviderExpected = new AuthProvider(NAME, DESCRIPTION, null, null, null);
        authProviderExpected.setId(ID);

        final String json = mapper.writeValueAsString(authProviderExpected);
        final AuthProvider authProviderResult = mapper.readValue(json, AuthProvider.class);

        assertNotSame(authProviderExpected, authProviderResult);
        assertEquals(authProviderExpected, authProviderResult);
    }

    /**
     * Should ignore null value on json
     */
    @Test
    public void JsonNotIncludeNull() throws JsonProcessingException {
        final AuthProvider authProvider = new AuthProvider();
        final AuthProvider authProviderFull = new AuthProvider("N", "D", "U", "AK", "AS");
        authProviderFull.setId("ID");
        authProviderFull.setAuthentications(Arrays.asList(new Authentication("A1"), new Authentication("A2")));

        final String json = mapper.writeValueAsString(authProvider);
        final String jsonFull = mapper.writeValueAsString(authProviderFull);

        assertFalse(json.contains("id"));
        assertFalse(json.contains("name"));
        assertFalse(json.contains("description"));
        assertFalse(json.contains("url"));
        assertFalse(json.contains("authKey"));
        assertFalse(json.contains("authSecret"));
        assertFalse(json.contains("authentications"));
        assertTrue(jsonFull.contains("id"));
        assertTrue(jsonFull.contains("name"));
        assertTrue(jsonFull.contains("description"));
        assertFalse(jsonFull.contains("url"));
        assertFalse(jsonFull.contains("authKey"));
        assertFalse(jsonFull.contains("authSecret"));
        assertFalse(jsonFull.contains("authentications"));
    }

    /**
     * Should equals instances
     */
    @Test
    public void equalsInstance() {
        final AuthProvider authProvider = new AuthProvider("ID");

        assertTrue(authProvider.equals(authProvider));
        assertFalse(authProvider.equals(null));
        assertFalse(authProvider.equals(new String()));
    }

    /**
     * Should fail equals due ID
     */
    @Test
    public void noEqualsID() {
        final AuthProvider authProvider1 = new AuthProvider("N", "D", "U", "AK", "AS");
        authProvider1.setId("ID");
        authProvider1.setAuthentications(Arrays.asList(new Authentication("A1"), new Authentication("A2")));
        final AuthProvider authProvider2 = new AuthProvider("N", "D", "U", "AK", "AS");
        authProvider2.setId("ID1");
        authProvider2.setAuthentications(Arrays.asList(new Authentication("A1"), new Authentication("A2")));
        final AuthProvider authProviderNull = new AuthProvider("N", "D", "U", "AK", "AS");
        authProviderNull.setId(null);
        authProviderNull.setAuthentications(Arrays.asList(new Authentication("A1"), new Authentication("A2")));

        assertNotEquals(authProvider1, authProvider2);
        assertNotEquals(authProvider1, authProviderNull);
        assertNotEquals(authProviderNull, authProvider1);
    }

    /**
     * Should fail equals due name
     */
    @Test
    public void noEqualsName() {
        final AuthProvider authProvider1 = new AuthProvider("N", "D", "U", "AK", "AS");
        authProvider1.setId("ID");
        authProvider1.setAuthentications(Arrays.asList(new Authentication("A1"), new Authentication("A2")));
        final AuthProvider authProvider2 = new AuthProvider("N1", "D", "U", "AK", "AS");
        authProvider2.setId("ID");
        authProvider2.setAuthentications(Arrays.asList(new Authentication("A1"), new Authentication("A2")));
        final AuthProvider authProviderNull = new AuthProvider(null, "D", "U", "AK", "AS");
        authProviderNull.setId("ID");
        authProviderNull.setAuthentications(Arrays.asList(new Authentication("A1"), new Authentication("A2")));

        assertNotEquals(authProvider1, authProvider2);
        assertNotEquals(authProvider1, authProviderNull);
        assertNotEquals(authProviderNull, authProvider1);
    }

    /**
     * Should fail equals due description
     */
    @Test
    public void noEqualsDescription() {
        final AuthProvider authProvider1 = new AuthProvider("N", "D", "U", "AK", "AS");
        authProvider1.setId("ID");
        authProvider1.setAuthentications(Arrays.asList(new Authentication("A1"), new Authentication("A2")));
        final AuthProvider authProvider2 = new AuthProvider("N", "D1", "U", "AK", "AS");
        authProvider2.setId("ID");
        authProvider2.setAuthentications(Arrays.asList(new Authentication("A1"), new Authentication("A2")));
        final AuthProvider authProviderNull = new AuthProvider("N", null, "U", "AK", "AS");
        authProviderNull.setId("ID");
        authProviderNull.setAuthentications(Arrays.asList(new Authentication("A1"), new Authentication("A2")));

        assertNotEquals(authProvider1, authProvider2);
        assertNotEquals(authProvider1, authProviderNull);
        assertNotEquals(authProviderNull, authProvider1);
    }

    /**
     * Should fail equals due url
     */
    @Test
    public void noEqualsUrl() {
        final AuthProvider authProvider1 = new AuthProvider("N", "D", "U", "AK", "AS");
        authProvider1.setId("ID");
        authProvider1.setAuthentications(Arrays.asList(new Authentication("A1"), new Authentication("A2")));
        final AuthProvider authProvider2 = new AuthProvider("N", "D", "U1", "AK", "AS");
        authProvider2.setId("ID");
        authProvider2.setAuthentications(Arrays.asList(new Authentication("A1"), new Authentication("A2")));
        final AuthProvider authProviderNull = new AuthProvider("N", "D", null, "AK", "AS");
        authProviderNull.setId("ID");
        authProviderNull.setAuthentications(Arrays.asList(new Authentication("A1"), new Authentication("A2")));

        assertNotEquals(authProvider1, authProvider2);
        assertNotEquals(authProvider1, authProviderNull);
        assertNotEquals(authProviderNull, authProvider1);
    }

    /**
     * Should fail equals due authKey
     */
    @Test
    public void noEqualsAuthKey() {
        final AuthProvider authProvider1 = new AuthProvider("N", "D", "U", "AK", "AS");
        authProvider1.setId("ID");
        authProvider1.setAuthentications(Arrays.asList(new Authentication("A1"), new Authentication("A2")));
        final AuthProvider authProvider2 = new AuthProvider("N", "D", "U", "AK1", "AS");
        authProvider2.setId("ID");
        authProvider2.setAuthentications(Arrays.asList(new Authentication("A1"), new Authentication("A2")));
        final AuthProvider authProviderNull = new AuthProvider("N", "D", "U", null, "AS");
        authProviderNull.setId("ID");
        authProviderNull.setAuthentications(Arrays.asList(new Authentication("A1"), new Authentication("A2")));

        assertNotEquals(authProvider1, authProvider2);
        assertNotEquals(authProvider1, authProviderNull);
        assertNotEquals(authProviderNull, authProvider1);
    }

    /**
     * Should fail equals due authSecret
     */
    @Test
    public void noEqualsAuthSecret() {
        final AuthProvider authProvider1 = new AuthProvider("N", "D", "U", "AK", "AS");
        authProvider1.setId("ID");
        authProvider1.setAuthentications(Arrays.asList(new Authentication("A1"), new Authentication("A2")));
        final AuthProvider authProvider2 = new AuthProvider("N", "D", "U", "AK", "AS1");
        authProvider2.setId("ID");
        authProvider2.setAuthentications(Arrays.asList(new Authentication("A1"), new Authentication("A2")));
        final AuthProvider authProviderNull = new AuthProvider("N", "D", "U", "AK", null);
        authProviderNull.setId("ID");
        authProviderNull.setAuthentications(Arrays.asList(new Authentication("A1"), new Authentication("A2")));

        assertNotEquals(authProvider1, authProvider2);
        assertNotEquals(authProvider1, authProviderNull);
        assertNotEquals(authProviderNull, authProvider1);
    }

    /**
     * Should fail equals due authentications
     */
    @Test
    public void noEqualsAuthentications() {
        final AuthProvider authProvider1 = new AuthProvider("N", "D", "U", "AK", "AS");
        authProvider1.setId("ID");
        authProvider1.setAuthentications(Arrays.asList(new Authentication("A1"), new Authentication("A2")));
        final AuthProvider authProvider2 = new AuthProvider("N", "D", "U", "AK", "AS");
        authProvider2.setId("ID");
        authProvider2.setAuthentications(Arrays.asList(new Authentication("A1")));
        final AuthProvider authProviderNull = new AuthProvider("N", "D", "U", "AK", "AS");
        authProviderNull.setId("ID");
        authProviderNull.setAuthentications(null);

        assertNotEquals(authProvider1, authProvider2);
        assertNotEquals(authProvider1, authProviderNull);
        assertNotEquals(authProviderNull, authProvider1);
    }

    /**
     * Should be equals
     */
    @Test
    public void testEquals() {
        final AuthProvider authProvider1 = new AuthProvider("N", "D", "U", "AK", "AS");
        authProvider1.setId("ID");
        authProvider1.setAuthentications(Arrays.asList(new Authentication("A1"), new Authentication("A2")));
        final AuthProvider authProvider2 = new AuthProvider("N", "D", "U", "AK", "AS");
        authProvider2.setId("ID");
        authProvider2.setAuthentications(Arrays.asList(new Authentication("A1"), new Authentication("A2")));
        final AuthProvider authProviderNull1 = new AuthProvider();
        final AuthProvider authProviderNull2 = new AuthProvider();

        assertNotSame(authProvider1, authProvider2);
        assertEquals(authProvider1, authProvider2);
        assertNotSame(authProviderNull1, authProviderNull2);
        assertEquals(authProviderNull1, authProviderNull2);
    }

    /**
     * Should have hashCode
     */
    @Test
    public void testHashCode() {
        final String ID = "ID";
        final String NAME = "name";
        final String DESCRIPTION = "description";
        final String URL = "url";
        final String AUTH_KEY = "key";
        final String AUTH_SECRET = "secret";
        final List<Authentication> AUTHENTICATION = Arrays.asList(new Authentication("A1"), new Authentication("A2"));
        final AuthProvider authProvider = new AuthProvider(NAME, DESCRIPTION, URL, AUTH_KEY, AUTH_SECRET);
        authProvider.setId(ID);
        authProvider.setAuthentications(AUTHENTICATION);
        final AuthProvider authProviderNull = new AuthProvider();

        int hashExpected = ID.hashCode();
        hashExpected = 31 * hashExpected + (NAME.hashCode());
        hashExpected = 31 * hashExpected + (DESCRIPTION.hashCode());
        hashExpected = 31 * hashExpected + (URL.hashCode());
        hashExpected = 31 * hashExpected + (AUTH_KEY.hashCode());
        hashExpected = 31 * hashExpected + (AUTH_SECRET.hashCode());
        hashExpected = 31 * hashExpected + (AUTHENTICATION.hashCode());

        final int hashResult = authProvider.hashCode();

        assertEquals(hashExpected, hashResult);
        assertEquals(0, authProviderNull.hashCode());
    }
}