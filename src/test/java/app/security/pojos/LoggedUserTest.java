package app.security.pojos;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LoggedUserTest {

    @Autowired
    private ObjectMapper mapper;

    /**
     * Should create default constructor
     */
    @Test
    public void constructorDefault() {
        final LoggedUser user = new LoggedUser();

        assertNull(user.getId());
        assertNull(user.getFullName());
        assertNull(user.getRole());
        assertNull(user.getPermissions());
    }

    /**
     * Should create id and role constructor
     */
    @Test
    public void constructorIdRole() {
        final String ID = "ID";
        final String ROLE = "ROLE";
        final LoggedUser user = new LoggedUser(ID, ROLE);

        assertSame(ID, user.getId());
        assertNull(user.getFullName());
        assertSame(ROLE, user.getRole());
        assertNull(user.getPermissions());
    }

    /**
     * Should create complete constructor
     */
    @Test
    public void constructorComplete() {
        final String ID = "ID";
        final String FULL_NAME = "full name";
        final String ROLE = "ROLE";
        final Set<String> PERMISSIONS = new HashSet<>(Arrays.asList("PER1", "PER2"));
        final LoggedUser user = new LoggedUser(ID, FULL_NAME, ROLE, PERMISSIONS);

        assertSame(ID, user.getId());
        assertSame(FULL_NAME, user.getFullName());
        assertSame(ROLE, user.getRole());
        assertSame(PERMISSIONS, user.getPermissions());
    }

    /**
     * Should set and get permissions
     */
    @Test
    public void setGetPermissions() {
        final LoggedUser user = new LoggedUser();
        final Set<String> PERMISSIONS = new HashSet<>(Arrays.asList("PER1", "PER2"));
        user.setPermissions(PERMISSIONS);

        assertSame(PERMISSIONS, user.getPermissions());
    }

    /**
     * Should serialize and deserialize
     */
    @Test
    public void serialize() throws IOException {
        final String ID = "ID";
        final String FULL_NAME = "full name";
        final String ROLE = "ROLE";
        final Set<String> PERMISSIONS = new HashSet<>(Arrays.asList("PER1", "PER2"));
        final LoggedUser userExpected = new LoggedUser(ID, FULL_NAME, ROLE, PERMISSIONS);

        final String json = mapper.writeValueAsString(userExpected);
        final LoggedUser userResult = mapper.readValue(json, LoggedUser.class);

        assertNotSame(userExpected, userResult);
        assertEquals(userExpected, userResult);
    }

    /**
     * Should equals instances
     */
    @Test
    public void equalsInstance() {
        final LoggedUser user = new LoggedUser("ID", "Role");

        assertTrue(user.equals(user));
        assertFalse(user.equals(null));
        assertFalse(user.equals(new String()));
    }

    /**
     * Should fail equals due id
     */
    @Test
    public void noEqualsID() {
        final LoggedUser user1 = new LoggedUser("ID1", "full", "ROLE", new HashSet<>(Arrays.asList("PER1", "PER2")));
        final LoggedUser user2 = new LoggedUser("ID2", "full", "ROLE", new HashSet<>(Arrays.asList("PER1", "PER2")));
        final LoggedUser userNull = new LoggedUser(null, "full", "ROLE", new HashSet<>(Arrays.asList("PER1", "PER2")));

        assertNotEquals(user1, user2);
        assertNotEquals(user1, userNull);
        assertNotEquals(userNull, user1);
    }

    /**
     * Should fail equals due fullName
     */
    @Test
    public void noEqualsFullName() {
        final LoggedUser user1 = new LoggedUser("ID1", "full", "ROLE", new HashSet<>(Arrays.asList("PER1", "PER2")));
        final LoggedUser user2 = new LoggedUser("ID1", "full2", "ROLE", new HashSet<>(Arrays.asList("PER1", "PER2")));
        final LoggedUser userNull = new LoggedUser("ID1", null, "ROLE", new HashSet<>(Arrays.asList("PER1", "PER2")));

        assertNotEquals(user1, user2);
        assertNotEquals(user1, userNull);
        assertNotEquals(userNull, user1);
    }

    /**
     * Should fail equals due role
     */
    @Test
    public void noEqualsRole() {
        final LoggedUser user1 = new LoggedUser("ID1", "full", "ROLE", new HashSet<>(Arrays.asList("PER1", "PER2")));
        final LoggedUser user2 = new LoggedUser("ID1", "full", "ROLE2", new HashSet<>(Arrays.asList("PER1", "PER2")));
        final LoggedUser userNull = new LoggedUser("ID1", "full", null, new HashSet<>(Arrays.asList("PER1", "PER2")));

        assertNotEquals(user1, user2);
        assertNotEquals(user1, userNull);
        assertNotEquals(userNull, user1);
    }

    /**
     * Should fail equals due permissions
     */
    @Test
    public void noEqualsPermissions() {
        final LoggedUser user1 = new LoggedUser("ID1", "full", "ROLE", new HashSet<>(Arrays.asList("PER1", "PER2")));
        final LoggedUser user2 = new LoggedUser("ID1", "full", "ROLE", new HashSet<>(Arrays.asList("PER1")));
        final LoggedUser userNull = new LoggedUser("ID1", "full", "ROLE", null);

        assertNotEquals(user1, user2);
        assertNotEquals(user1, userNull);
        assertNotEquals(userNull, user1);
    }

    /**
     * Should be equals
     */
    @Test
    public void testEquals() {
        final LoggedUser user1 = new LoggedUser("ID1", "full", "ROLE", new HashSet<>(Arrays.asList("PER1", "PER2")));
        final LoggedUser user2 = new LoggedUser("ID1", "full", "ROLE", new HashSet<>(Arrays.asList("PER1", "PER2")));
        final LoggedUser userNull1 = new LoggedUser();
        final LoggedUser userNull2 = new LoggedUser();

        assertNotSame(user1, user2);
        assertEquals(user1, user2);
        assertNotSame(userNull1, userNull2);
        assertEquals(userNull1, userNull2);
    }

    /**
     * Should have hashCode
     */
    @Test
    public void testHashCode() {
        final String ID = "ID";
        final String FULL_NAME = "full";
        final String ROLE = "ROLE";
        final Set<String> PERMISSIONS = new HashSet<>(Arrays.asList("PER1", "PER2"));
        final LoggedUser user = new LoggedUser(ID, FULL_NAME, ROLE, PERMISSIONS);
        final LoggedUser userNull = new LoggedUser();

        int hashExpected = ID.hashCode();
        hashExpected = 31 * hashExpected + (FULL_NAME.hashCode());
        hashExpected = 31 * hashExpected + (ROLE.hashCode());
        hashExpected = 31 * hashExpected + (PERMISSIONS.hashCode());

        final int hashResult = user.hashCode();

        assertEquals(hashExpected, hashResult);
        assertEquals(0, userNull.hashCode());
    }
}