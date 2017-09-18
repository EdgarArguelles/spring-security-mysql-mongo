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
public class PermissionTest {

    @Autowired
    private ObjectMapper mapper;

    /**
     * Should create default constructor
     */
    @Test
    public void constructorDefault() {
        final Permission permission = new Permission();

        assertNull(permission.getId());
        assertNull(permission.getName());
        assertNull(permission.getDescription());
        assertNull(permission.getRoles());
    }

    /**
     * Should create Id constructor
     */
    @Test
    public void constructorId() {
        final String ID = "ID";
        final Permission permission = new Permission(ID);

        assertSame(ID, permission.getId());
        assertNull(permission.getName());
        assertNull(permission.getDescription());
        assertNull(permission.getRoles());
    }

    /**
     * Should create complete constructor
     */
    @Test
    public void constructorComplete() {
        final String NAME = "name";
        final String DESCRIPTION = "description";
        final Permission permission = new Permission(NAME, DESCRIPTION);

        assertNull(permission.getId());
        assertSame(NAME, permission.getName());
        assertSame(DESCRIPTION, permission.getDescription());
        assertNull(permission.getRoles());
    }

    /**
     * Should set and get id
     */
    @Test
    public void setGetID() {
        final Permission permission = new Permission();
        final String ID = "ID";
        permission.setId(ID);

        assertSame(ID, permission.getId());
    }

    /**
     * Should set and get name
     */
    @Test
    public void setGetName() {
        final Permission permission = new Permission();
        final String NAME = "name";
        permission.setName(NAME);

        assertSame(NAME, permission.getName());
    }

    /**
     * Should set and get description
     */
    @Test
    public void setGetDescription() {
        final Permission permission = new Permission();
        final String DESCRIPTION = "description";
        permission.setDescription(DESCRIPTION);

        assertSame(DESCRIPTION, permission.getDescription());
    }

    /**
     * Should set and get roles
     */
    @Test
    public void setGetRoles() {
        final Permission permission = new Permission();
        final List<Role> ROLES = Arrays.asList(new Role("R1"), new Role("R2"));
        permission.setRoles(ROLES);

        assertSame(ROLES, permission.getRoles());
    }

    /**
     * Should not throw an exception
     */
    @Test
    public void cleanRelations() {
        final Permission permission = new Permission();
        permission.cleanRelations(false);
    }

    /**
     * Should get toString
     */
    @Test
    public void toStringValid() {
        final String ID = "ID";
        final String NAME = "name";
        final Permission permission = new Permission(ID);
        permission.setName(NAME);

        assertEquals("{" + ID + ", " + NAME + "}", permission.toString());
    }

    /**
     * Should serialize and deserialize
     */
    @Test
    public void serialize() throws IOException {
        final String ID = "ID";
        final String NAME = "name";
        final String DESCRIPTION = "description";
        final Permission permissionExpected = new Permission(NAME, DESCRIPTION);
        permissionExpected.setId(ID);

        final String json = mapper.writeValueAsString(permissionExpected);
        final Permission permissionResult = mapper.readValue(json, Permission.class);

        assertNotSame(permissionExpected, permissionResult);
        assertEquals(permissionExpected, permissionResult);
    }

    /**
     * Should ignore null value on json
     */
    @Test
    public void JsonNotIncludeNull() throws JsonProcessingException {
        final Permission permission = new Permission();
        final Permission permissionFull = new Permission("N", "D");
        permissionFull.setId("ID");
        permissionFull.setRoles(Arrays.asList(new Role("R1"), new Role("R2")));

        final String json = mapper.writeValueAsString(permission);
        final String jsonFull = mapper.writeValueAsString(permissionFull);

        assertTrue(json.contains("id"));
        assertTrue(json.contains("name"));
        assertTrue(json.contains("description"));
        assertFalse(json.contains("roles"));
        assertTrue(jsonFull.contains("id"));
        assertTrue(jsonFull.contains("name"));
        assertTrue(jsonFull.contains("description"));
        assertFalse(jsonFull.contains("roles"));
    }

    /**
     * Should equals instances
     */
    @Test
    public void equalsInstance() {
        final Permission permission = new Permission("ID");

        assertTrue(permission.equals(permission));
        assertFalse(permission.equals(null));
        assertFalse(permission.equals(new String()));
    }

    /**
     * Should fail equals due ID
     */
    @Test
    public void noEqualsID() {
        final Permission permission1 = new Permission("N", "D");
        permission1.setId("ID");
        permission1.setRoles(Arrays.asList(new Role("R1"), new Role("R2")));
        final Permission permission2 = new Permission("N", "D");
        permission2.setId("ID2");
        permission2.setRoles(Arrays.asList(new Role("R1"), new Role("R2")));
        final Permission permissionNull = new Permission("N", "D");
        permissionNull.setId(null);
        permissionNull.setRoles(Arrays.asList(new Role("R1"), new Role("R2")));

        assertNotEquals(permission1, permission2);
        assertNotEquals(permission1, permissionNull);
        assertNotEquals(permissionNull, permission1);
    }

    /**
     * Should fail equals due name
     */
    @Test
    public void noEqualsName() {
        final Permission permission1 = new Permission("N", "D");
        permission1.setId("ID");
        permission1.setRoles(Arrays.asList(new Role("R1"), new Role("R2")));
        final Permission permission2 = new Permission("N1", "D");
        permission2.setId("ID");
        permission2.setRoles(Arrays.asList(new Role("R1"), new Role("R2")));
        final Permission permissionNull = new Permission(null, "D");
        permissionNull.setId("ID");
        permissionNull.setRoles(Arrays.asList(new Role("R1"), new Role("R2")));

        assertNotEquals(permission1, permission2);
        assertNotEquals(permission1, permissionNull);
        assertNotEquals(permissionNull, permission1);
    }

    /**
     * Should fail equals due description
     */
    @Test
    public void noEqualsDescription() {
        final Permission permission1 = new Permission("N", "D");
        permission1.setId("ID");
        permission1.setRoles(Arrays.asList(new Role("R1"), new Role("R2")));
        final Permission permission2 = new Permission("N", "D2");
        permission2.setId("ID");
        permission2.setRoles(Arrays.asList(new Role("R1"), new Role("R2")));
        final Permission permissionNull = new Permission("N", null);
        permissionNull.setId("ID");
        permissionNull.setRoles(Arrays.asList(new Role("R1"), new Role("R2")));

        assertNotEquals(permission1, permission2);
        assertNotEquals(permission1, permissionNull);
        assertNotEquals(permissionNull, permission1);
    }

    /**
     * Should fail equals due roles
     */
    @Test
    public void noEqualsRoles() {
        final Permission permission1 = new Permission("N", "D");
        permission1.setId("ID");
        permission1.setRoles(Arrays.asList(new Role("R1"), new Role("R2")));
        final Permission permission2 = new Permission("N", "D");
        permission2.setId("ID");
        permission2.setRoles(Arrays.asList(new Role("R1")));
        final Permission permissionNull = new Permission("N", "D");
        permissionNull.setId("ID");
        permissionNull.setRoles(null);

        assertNotEquals(permission1, permission2);
        assertNotEquals(permission1, permissionNull);
        assertNotEquals(permissionNull, permission1);
    }

    /**
     * Should be equals
     */
    @Test
    public void testEquals() {
        final Permission permission1 = new Permission("N", "D");
        permission1.setId("ID");
        permission1.setRoles(Arrays.asList(new Role("R1"), new Role("R2")));
        final Permission permission2 = new Permission("N", "D");
        permission2.setId("ID");
        permission2.setRoles(Arrays.asList(new Role("R1"), new Role("R2")));
        final Permission permissionNull1 = new Permission();
        final Permission permissionNull2 = new Permission();

        assertNotSame(permission1, permission2);
        assertEquals(permission1, permission2);
        assertNotSame(permissionNull1, permissionNull2);
        assertEquals(permissionNull1, permissionNull2);
    }

    /**
     * Should have hashCode
     */
    @Test
    public void testHashCode() {
        final String ID = "ID";
        final String NAME = "name";
        final String DESCRIPTION = "description";
        final List<Role> ROLES = Arrays.asList(new Role("R1"), new Role("R2"));
        final Permission permission = new Permission(NAME, DESCRIPTION);
        permission.setId(ID);
        permission.setRoles(ROLES);
        final Permission permissionNull = new Permission();

        int hashExpected = ID.hashCode();
        hashExpected = 31 * hashExpected + (NAME.hashCode());
        hashExpected = 31 * hashExpected + (DESCRIPTION.hashCode());
        hashExpected = 31 * hashExpected + (ROLES.hashCode());

        final int hashResult = permission.hashCode();

        assertEquals(hashExpected, hashResult);
        assertEquals(0, permissionNull.hashCode());
    }
}