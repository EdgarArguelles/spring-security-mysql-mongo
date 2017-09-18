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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RoleTest {

    @Autowired
    private ObjectMapper mapper;

    /**
     * Should create default constructor
     */
    @Test
    public void constructorDefault() {
        final Role role = new Role();

        assertNull(role.getId());
        assertNull(role.getName());
        assertNull(role.getDescription());
        assertNull(role.getPermissions());
        assertNull(role.getPeople());
    }

    /**
     * Should create Id constructor
     */
    @Test
    public void constructorId() {
        final String ID = "ID";
        final Role role = new Role(ID);

        assertSame(ID, role.getId());
        assertNull(role.getName());
        assertNull(role.getDescription());
        assertNull(role.getPermissions());
        assertNull(role.getPeople());
    }

    /**
     * Should create complete constructor
     */
    @Test
    public void constructorComplete() {
        final String NAME = "name";
        final String DESCRIPTION = "description";
        final Set<Permission> PERMISSIONS = new HashSet<>(Arrays.asList(new Permission("P1"), new Permission("P2")));
        final Role role = new Role(NAME, DESCRIPTION, PERMISSIONS);

        assertNull(role.getId());
        assertSame(NAME, role.getName());
        assertSame(DESCRIPTION, role.getDescription());
        assertSame(PERMISSIONS, role.getPermissions());
        assertNull(role.getPeople());
    }

    /**
     * Should set and get id
     */
    @Test
    public void setGetID() {
        final Role role = new Role();
        final String ID = "ID";
        role.setId(ID);

        assertSame(ID, role.getId());
    }

    /**
     * Should set and get name
     */
    @Test
    public void setGetName() {
        final Role role = new Role();
        final String NAME = "name";
        role.setName(NAME);

        assertSame(NAME, role.getName());
    }

    /**
     * Should set and get description
     */
    @Test
    public void setGetDescription() {
        final Role role = new Role();
        final String DESCRIPTION = "description";
        role.setDescription(DESCRIPTION);

        assertSame(DESCRIPTION, role.getDescription());
    }

    /**
     * Should set and get permissions
     */
    @Test
    public void setGetPermissions() {
        final Role role = new Role();
        final Set<Permission> PERMISSIONS = new HashSet<>(Arrays.asList(new Permission("P1"), new Permission("P2")));
        role.setPermissions(PERMISSIONS);

        assertSame(PERMISSIONS, role.getPermissions());
    }

    /**
     * Should set and get people
     */
    @Test
    public void setGetPeople() {
        final Role role = new Role();
        final List<Person> PEOPLE = Arrays.asList(new Person("Per1"), new Person("Per2"));
        role.setPeople(PEOPLE);

        assertSame(PEOPLE, role.getPeople());
    }

    /**
     * Should clean all Relations not annotated with @JsonIgnore
     */
    @Test
    public void cleanRelationsWhenCleanAll() {
        final Role role = new Role("N", "D", new HashSet<>(Arrays.asList(new Permission("P"))));
        role.cleanRelations(true);

        assertNull(role.getPermissions());
    }

    /**
     * Should clean nested Relations not annotated with @JsonIgnore
     */
    @Test
    public void cleanRelationsWhenNotCleanAll() {
        final Set<Permission> permissionsExpected = new HashSet<>(Arrays.asList(new Permission("P")));
        final Role role = new Role("N", "D", new HashSet<>(Arrays.asList(new Permission("P"))));
        role.cleanRelations(false);

        assertNotSame(permissionsExpected, role.getPermissions());
        assertEquals(permissionsExpected, role.getPermissions());
    }

    /**
     * Should get toString
     */
    @Test
    public void toStringValid() {
        final String ID = "ID";
        final String NAME = "name";
        final Role role = new Role(ID);
        role.setName(NAME);

        assertEquals("{" + ID + ", " + NAME + "}", role.toString());
    }

    /**
     * Should serialize and deserialize
     */
    @Test
    public void serialize() throws IOException {
        final String NAME = "name";
        final String DESCRIPTION = "description";
        final Set<Permission> PERMISSIONS = new HashSet<>(Arrays.asList(new Permission("P1"), new Permission("P2")));
        final Role roleExpected = new Role(NAME, DESCRIPTION, PERMISSIONS);
        roleExpected.setId("ID");

        final String json = mapper.writeValueAsString(roleExpected);
        final Role roleResult = mapper.readValue(json, Role.class);

        assertNotSame(roleExpected, roleResult);
        assertEquals(roleExpected, roleResult);
    }

    /**
     * Should ignore null value on json
     */
    @Test
    public void JsonIgnoreUsers() throws JsonProcessingException {
        final Role role = new Role();
        final Role roleFull = new Role("N", "D", new HashSet<>(Arrays.asList(new Permission("P1"), new Permission("P2"))));
        roleFull.setId("ID");
        roleFull.setPeople(Arrays.asList(new Person("Per1"), new Person("Per2")));

        final String json = mapper.writeValueAsString(role);
        final String jsonFull = mapper.writeValueAsString(roleFull);

        assertTrue(json.contains("id"));
        assertTrue(json.contains("name"));
        assertTrue(json.contains("description"));
        assertTrue(json.contains("permissions"));
        assertFalse(json.contains("people"));
        assertTrue(jsonFull.contains("id"));
        assertTrue(jsonFull.contains("name"));
        assertTrue(jsonFull.contains("description"));
        assertTrue(jsonFull.contains("permissions"));
        assertFalse(jsonFull.contains("people"));
    }

    /**
     * Should equals instances
     */
    @Test
    public void equalsInstance() {
        final Role role = new Role("ID");

        assertTrue(role.equals(role));
        assertFalse(role.equals(null));
        assertFalse(role.equals(new String()));
    }

    /**
     * Should fail equals due ID
     */
    @Test
    public void noEqualsID() {
        final Role role1 = new Role("N", "D", new HashSet<>(Arrays.asList(new Permission("P1"), new Permission("P2"))));
        role1.setId("ID");
        role1.setPeople(Arrays.asList(new Person("Per1"), new Person("Per2")));
        final Role role2 = new Role("N", "D", new HashSet<>(Arrays.asList(new Permission("P1"), new Permission("P2"))));
        role2.setId("ID2");
        role2.setPeople(Arrays.asList(new Person("Per1"), new Person("Per2")));
        final Role roleNull = new Role("N", "D", new HashSet<>(Arrays.asList(new Permission("P1"), new Permission("P2"))));
        roleNull.setId(null);
        roleNull.setPeople(Arrays.asList(new Person("Per1"), new Person("Per2")));

        assertNotEquals(role1, role2);
        assertNotEquals(role1, roleNull);
        assertNotEquals(roleNull, role1);
    }

    /**
     * Should fail equals due name
     */
    @Test
    public void noEqualsName() {
        final Role role1 = new Role("N", "D", new HashSet<>(Arrays.asList(new Permission("P1"), new Permission("P2"))));
        role1.setId("ID");
        role1.setPeople(Arrays.asList(new Person("Per1"), new Person("Per2")));
        final Role role2 = new Role("N1", "D", new HashSet<>(Arrays.asList(new Permission("P1"), new Permission("P2"))));
        role2.setId("ID");
        role2.setPeople(Arrays.asList(new Person("Per1"), new Person("Per2")));
        final Role roleNull = new Role(null, "D", new HashSet<>(Arrays.asList(new Permission("P1"), new Permission("P2"))));
        roleNull.setId("ID");
        roleNull.setPeople(Arrays.asList(new Person("Per1"), new Person("Per2")));

        assertNotEquals(role1, role2);
        assertNotEquals(role1, roleNull);
        assertNotEquals(roleNull, role1);
    }

    /**
     * Should fail equals due description
     */
    @Test
    public void noEqualsDescription() {
        final Role role1 = new Role("N", "D", new HashSet<>(Arrays.asList(new Permission("P1"), new Permission("P2"))));
        role1.setId("ID");
        role1.setPeople(Arrays.asList(new Person("Per1"), new Person("Per2")));
        final Role role2 = new Role("N", "D1", new HashSet<>(Arrays.asList(new Permission("P1"), new Permission("P2"))));
        role2.setId("ID");
        role2.setPeople(Arrays.asList(new Person("Per1"), new Person("Per2")));
        final Role roleNull = new Role("N", null, new HashSet<>(Arrays.asList(new Permission("P1"), new Permission("P2"))));
        roleNull.setId("ID");
        roleNull.setPeople(Arrays.asList(new Person("Per1"), new Person("Per2")));

        assertNotEquals(role1, role2);
        assertNotEquals(role1, roleNull);
        assertNotEquals(roleNull, role1);
    }

    /**
     * Should fail equals due permissions
     */
    @Test
    public void noEqualsPermissions() {
        final Role role1 = new Role("N", "D", new HashSet<>(Arrays.asList(new Permission("P1"), new Permission("P2"))));
        role1.setId("ID");
        role1.setPeople(Arrays.asList(new Person("Per1"), new Person("Per2")));
        final Role role2 = new Role("N", "D", new HashSet<>(Arrays.asList(new Permission("P1"))));
        role2.setId("ID");
        role2.setPeople(Arrays.asList(new Person("Per1"), new Person("Per2")));
        final Role roleNull = new Role("N", "D", null);
        roleNull.setId("ID");
        roleNull.setPeople(Arrays.asList(new Person("Per1"), new Person("Per2")));

        assertNotEquals(role1, role2);
        assertNotEquals(role1, roleNull);
        assertNotEquals(roleNull, role1);
    }

    /**
     * Should fail equals due people
     */
    @Test
    public void noEqualsPeople() {
        final Role role1 = new Role("N", "D", new HashSet<>(Arrays.asList(new Permission("P1"), new Permission("P2"))));
        role1.setId("ID");
        role1.setPeople(Arrays.asList(new Person("Per1"), new Person("Per2")));
        final Role role2 = new Role("N", "D", new HashSet<>(Arrays.asList(new Permission("P1"), new Permission("P2"))));
        role2.setId("ID");
        role2.setPeople(Arrays.asList(new Person("Per1")));
        final Role roleNull = new Role("N", "D", new HashSet<>(Arrays.asList(new Permission("P1"), new Permission("P2"))));
        roleNull.setId("ID");
        roleNull.setPeople(null);

        assertNotEquals(role1, role2);
        assertNotEquals(role1, roleNull);
        assertNotEquals(roleNull, role1);
    }

    /**
     * Should be equals
     */
    @Test
    public void testEquals() {
        final Role role1 = new Role("N", "D", new HashSet<>(Arrays.asList(new Permission("P1"), new Permission("P2"))));
        role1.setId("ID");
        role1.setPeople(Arrays.asList(new Person("Per1"), new Person("Per2")));
        final Role role2 = new Role("N", "D", new HashSet<>(Arrays.asList(new Permission("P1"), new Permission("P2"))));
        role2.setId("ID");
        role2.setPeople(Arrays.asList(new Person("Per1"), new Person("Per2")));
        final Role roleNull1 = new Role();
        final Role roleNull2 = new Role();

        assertNotSame(role1, role2);
        assertEquals(role1, role2);
        assertNotSame(roleNull1, roleNull2);
        assertEquals(roleNull1, roleNull2);
    }

    /**
     * Should have hashCode
     */
    @Test
    public void testHashCode() {
        final String ID = "ID";
        final String NAME = "name";
        final String DESCRIPTION = "description";
        final Set<Permission> PERMISSIONS = new HashSet<>(Arrays.asList(new Permission("P1"), new Permission("P2")));
        final List<Person> PEOPLE = Arrays.asList(new Person("Per1"), new Person("Per2"));
        final Role role = new Role(NAME, DESCRIPTION, PERMISSIONS);
        role.setId(ID);
        role.setPeople(PEOPLE);
        final Role roleNull = new Role();

        int hashExpected = ID.hashCode();
        hashExpected = 31 * hashExpected + (NAME.hashCode());
        hashExpected = 31 * hashExpected + (DESCRIPTION.hashCode());
        hashExpected = 31 * hashExpected + (PERMISSIONS.hashCode());
        hashExpected = 31 * hashExpected + (PEOPLE.hashCode());

        final int hashResult = role.hashCode();

        assertEquals(hashExpected, hashResult);
        assertEquals(0, roleNull.hashCode());
    }
}