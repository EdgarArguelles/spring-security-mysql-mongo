package app.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PersonTest {

    @Autowired
    private ObjectMapper mapper;

    /**
     * Should have CIVIL_STATUS and SEX constants
     */
    @Test
    public void constants() {
        final Integer SINGLE = 1;
        final Integer MARRIED = 2;
        final String M = "M";
        final String F = "F";

        assertEquals(SINGLE, Person.CIVIL_STATUS.SINGLE);
        assertEquals(MARRIED, Person.CIVIL_STATUS.MARRIED);
        assertEquals(M, Person.SEX.M);
        assertEquals(F, Person.SEX.F);
    }

    /**
     * Should create default constructor
     */
    @Test
    public void constructorDefault() {
        final Person person = new Person();

        assertNull(person.getId());
        assertNull(person.getName());
        assertNull(person.getLastName());
        assertNull(person.getBirthday());
        assertNull(person.getCivilStatus());
        assertNull(person.getSex());
        assertNull(person.getEmail());
        assertNull(person.getRoles());
        assertNull(person.getAuthentications());
    }

    /**
     * Should create Id constructor
     */
    @Test
    public void constructorId() {
        final String ID = "ID";
        final Person person = new Person(ID);

        assertSame(ID, person.getId());
        assertNull(person.getName());
        assertNull(person.getLastName());
        assertNull(person.getBirthday());
        assertNull(person.getCivilStatus());
        assertNull(person.getSex());
        assertNull(person.getEmail());
        assertNull(person.getRoles());
        assertNull(person.getAuthentications());
    }

    /**
     * Should create complete constructor
     */
    @Test
    public void constructorComplete() {
        final String NAME = "name";
        final String LAST_NAME = "last name";
        final LocalDate BIRTHDAY = LocalDate.now();
        final Integer CIVIL_STATUS = 1;
        final String SEX = "A";
        final String EMAIL = "emailtest";
        final Set<Role> ROLES = new HashSet<>(Arrays.asList(new Role("R1"), new Role("R2")));
        final Person person = new Person(NAME, LAST_NAME, BIRTHDAY, CIVIL_STATUS, SEX, EMAIL, ROLES);

        assertNull(person.getId());
        assertSame(NAME, person.getName());
        assertSame(LAST_NAME, person.getLastName());
        assertSame(BIRTHDAY, person.getBirthday());
        assertSame(CIVIL_STATUS, person.getCivilStatus());
        assertSame(SEX, person.getSex());
        assertSame(EMAIL, person.getEmail());
        assertSame(ROLES, person.getRoles());
        assertNull(person.getAuthentications());
    }

    /**
     * Should set and get id
     */
    @Test
    public void setGetID() {
        final Person person = new Person();
        final String ID = "ID";
        person.setId(ID);

        assertSame(ID, person.getId());
    }

    /**
     * Should set and get name
     */
    @Test
    public void setGetName() {
        final Person person = new Person();
        final String NAME = "name";
        person.setName(NAME);

        assertSame(NAME, person.getName());
    }

    /**
     * Should set and get lastName
     */
    @Test
    public void setGetLastName() {
        final Person person = new Person();
        final String LAST_NAME = "last name";
        person.setLastName(LAST_NAME);

        assertSame(LAST_NAME, person.getLastName());
    }

    /**
     * Should set and get birthday
     */
    @Test
    public void setGetBirthday() {
        final Person person = new Person();
        final LocalDate BIRTHDAY = LocalDate.now();
        person.setBirthday(BIRTHDAY);

        assertSame(BIRTHDAY, person.getBirthday());
    }

    /**
     * Should set and get civilStatus
     */
    @Test
    public void setGetCivilStatus() {
        final Person person = new Person();
        final Integer CIVIL_STATUS = 1;
        person.setCivilStatus(CIVIL_STATUS);

        assertSame(CIVIL_STATUS, person.getCivilStatus());
    }

    /**
     * Should set and get sex
     */
    @Test
    public void setGetSex() {
        final Person person = new Person();
        final String SEX = "A";
        person.setSex(SEX);

        assertSame(SEX, person.getSex());
    }

    /**
     * Should set and get email
     */
    @Test
    public void setGetEmail() {
        final Person person = new Person();
        final String EMAIL = "emailtest";
        person.setEmail(EMAIL);

        assertSame(EMAIL, person.getEmail());
    }

    /**
     * Should set and get roles
     */
    @Test
    public void setGetRoles() {
        final Person person = new Person();
        final Set<Role> ROLES = new HashSet<>(Arrays.asList(new Role("R1"), new Role("R2")));
        person.setRoles(ROLES);

        assertSame(ROLES, person.getRoles());
    }

    /**
     * Should set and get authentications
     */
    @Test
    public void setGetAuthentications() {
        final Person person = new Person();
        final List<Authentication> AUTHENTICATIONS = Arrays.asList(new Authentication("A1"), new Authentication("A2"));
        person.setAuthentications(AUTHENTICATIONS);

        assertSame(AUTHENTICATIONS, person.getAuthentications());
    }

    /**
     * Should clean all Relations not annotated with @JsonIgnore
     */
    @Test
    public void cleanRelationsWhenCleanAll() {
        final Set<Role> ROLES = new HashSet<>(Arrays.asList(
                new Role("N", "D", new HashSet<>(Arrays.asList(new Permission("P"))))
        ));
        final Person person = new Person("N", "LN", LocalDate.now(), 0, "S", "E", ROLES);
        person.cleanRelations(true);

        assertNull(person.getRoles());
    }

    /**
     * Should clean nested Relations not annotated with @JsonIgnore
     */
    @Test
    public void cleanRelationsWhenNotCleanAll() {
        final Set<Role> rolesExpected = new HashSet<>(Arrays.asList(
                new Role("N", "D", null)
        ));
        final Set<Role> ROLES = new HashSet<>(Arrays.asList(
                new Role("N", "D", new HashSet<>(Arrays.asList(new Permission("P"))))
        ));
        final Person person = new Person("N", "LN", LocalDate.now(), 0, "S", "E", ROLES);
        person.cleanRelations(false);

        assertNotSame(rolesExpected, person.getRoles());
        assertEquals(rolesExpected, person.getRoles());
    }

    /**
     * Should get toString
     */
    @Test
    public void toStringValid() {
        final String ID = "ID";
        final String NAME = "name";
        final String LAST_NAME = "last name";
        final Person person = new Person(ID);
        person.setName(NAME);
        person.setLastName(LAST_NAME);

        assertEquals("{" + ID + ", " + NAME + " " + LAST_NAME + "}", person.toString());
    }

    /**
     * Should get Full Name
     */
    @Test
    public void getFullName() {
        final String NAME = "name";
        final String LAST_NAME = "last name";
        final Person person = new Person();
        person.setName(NAME);
        person.setLastName(LAST_NAME);

        assertEquals(NAME + " " + LAST_NAME, person.getFullName());
    }

    /**
     * Should serialize and deserialize
     */
    @Test
    public void serialize() throws IOException {
        final String NAME = "name";
        final String LAST_NAME = "last name";
        final LocalDate BIRTHDAY = LocalDate.now();
        final Integer CIVIL_STATUS = 1;
        final String SEX = "A";
        final String EMAIL = "emailtest";
        final Set<Role> ROLES = new HashSet<>(Arrays.asList(new Role("R1"), new Role("R2")));
        final Person personExpected = new Person(NAME, LAST_NAME, BIRTHDAY, CIVIL_STATUS, SEX, EMAIL, ROLES);
        personExpected.setId("ID");

        final String json = mapper.writeValueAsString(personExpected);
        final Person personResult = mapper.readValue(json, Person.class);

        assertNotSame(personExpected, personResult);
        assertEquals(personExpected, personResult);
    }

    /**
     * Should ignore null value on json
     */
    @Test
    public void JsonNotIncludeNull() throws JsonProcessingException {
        final Person person = new Person();
        final Person personFull = new Person("N", "LN", LocalDate.now(), 1, "A", "E", new HashSet<>(Arrays.asList(new Role("R1"))));
        personFull.setId("ID");
        personFull.setAuthentications(Arrays.asList(new Authentication("A1"), new Authentication("A2")));

        final String json = mapper.writeValueAsString(person);
        final String jsonFull = mapper.writeValueAsString(personFull);

        assertTrue(json.contains("id"));
        assertTrue(json.contains("name"));
        assertTrue(json.contains("lastName"));
        assertTrue(json.contains("birthday"));
        assertTrue(json.contains("civilStatus"));
        assertTrue(json.contains("sex"));
        assertTrue(json.contains("email"));
        assertTrue(json.contains("roles"));
        assertFalse(json.contains("authentications"));
        assertFalse(json.contains("fullName"));
        assertTrue(jsonFull.contains("id"));
        assertTrue(jsonFull.contains("name"));
        assertTrue(jsonFull.contains("lastName"));
        assertTrue(jsonFull.contains("birthday"));
        assertTrue(jsonFull.contains("civilStatus"));
        assertTrue(jsonFull.contains("sex"));
        assertTrue(jsonFull.contains("email"));
        assertTrue(jsonFull.contains("roles"));
        assertFalse(jsonFull.contains("authentications"));
        assertFalse(jsonFull.contains("fullName"));
    }

    /**
     * Should equals instances
     */
    @Test
    public void equalsInstance() {
        final Person person = new Person("ID");

        assertTrue(person.equals(person));
        assertFalse(person.equals(null));
        assertFalse(person.equals(new String()));
    }

    /**
     * Should fail equals due ID
     */
    @Test
    public void noEqualsID() {
        final LocalDate BIRTHDAY = LocalDate.now();
        final Person person1 = new Person("N", "LN", BIRTHDAY, 1, "A", "E", new HashSet<>(Arrays.asList(new Role("R1"), new Role("R2"))));
        person1.setId("ID");
        person1.setAuthentications(Arrays.asList(new Authentication("A1"), new Authentication("A2")));
        final Person person2 = new Person("N", "LN", BIRTHDAY, 1, "A", "E", new HashSet<>(Arrays.asList(new Role("R1"), new Role("R2"))));
        person2.setId("ID2");
        person2.setAuthentications(Arrays.asList(new Authentication("A1"), new Authentication("A2")));
        final Person person1Null = new Person("N", "LN", BIRTHDAY, 1, "A", "E", new HashSet<>(Arrays.asList(new Role("R1"), new Role("R2"))));
        person1Null.setId(null);
        person1Null.setAuthentications(Arrays.asList(new Authentication("A1"), new Authentication("A2")));

        assertNotEquals(person1, person2);
        assertNotEquals(person1, person1Null);
        assertNotEquals(person1Null, person1);
    }

    /**
     * Should fail equals due name
     */
    @Test
    public void noEqualsName() {
        final LocalDate BIRTHDAY = LocalDate.now();
        final Person person1 = new Person("N", "LN", BIRTHDAY, 1, "A", "E", new HashSet<>(Arrays.asList(new Role("R1"), new Role("R2"))));
        person1.setId("ID");
        person1.setAuthentications(Arrays.asList(new Authentication("A1"), new Authentication("A2")));
        final Person person2 = new Person("N1", "LN", BIRTHDAY, 1, "A", "E", new HashSet<>(Arrays.asList(new Role("R1"), new Role("R2"))));
        person2.setId("ID");
        person2.setAuthentications(Arrays.asList(new Authentication("A1"), new Authentication("A2")));
        final Person person1Null = new Person(null, "LN", BIRTHDAY, 1, "A", "E", new HashSet<>(Arrays.asList(new Role("R1"), new Role("R2"))));
        person1Null.setId("ID");
        person1Null.setAuthentications(Arrays.asList(new Authentication("A1"), new Authentication("A2")));

        assertNotEquals(person1, person2);
        assertNotEquals(person1, person1Null);
        assertNotEquals(person1Null, person1);
    }

    /**
     * Should fail equals due lastName
     */
    @Test
    public void noEqualsLastName() {
        final LocalDate BIRTHDAY = LocalDate.now();
        final Person person1 = new Person("N", "LN", BIRTHDAY, 1, "A", "E", new HashSet<>(Arrays.asList(new Role("R1"), new Role("R2"))));
        person1.setId("ID");
        person1.setAuthentications(Arrays.asList(new Authentication("A1"), new Authentication("A2")));
        final Person person2 = new Person("N", "LN2", BIRTHDAY, 1, "A", "E", new HashSet<>(Arrays.asList(new Role("R1"), new Role("R2"))));
        person2.setId("ID");
        person2.setAuthentications(Arrays.asList(new Authentication("A1"), new Authentication("A2")));
        final Person person1Null = new Person("N", null, BIRTHDAY, 1, "A", "E", new HashSet<>(Arrays.asList(new Role("R1"), new Role("R2"))));
        person1Null.setId("ID");
        person1Null.setAuthentications(Arrays.asList(new Authentication("A1"), new Authentication("A2")));

        assertNotEquals(person1, person2);
        assertNotEquals(person1, person1Null);
        assertNotEquals(person1Null, person1);
    }

    /**
     * Should fail equals due birthday
     */
    @Test
    public void noEqualsBirthday() {
        final LocalDate BIRTHDAY = LocalDate.now();
        final LocalDate BIRTHDAY2 = BIRTHDAY.plusDays(1);
        final Person person1 = new Person("N", "LN", BIRTHDAY, 1, "A", "E", new HashSet<>(Arrays.asList(new Role("R1"), new Role("R2"))));
        person1.setId("ID");
        person1.setAuthentications(Arrays.asList(new Authentication("A1"), new Authentication("A2")));
        final Person person2 = new Person("N", "LN", BIRTHDAY2, 1, "A", "E", new HashSet<>(Arrays.asList(new Role("R1"), new Role("R2"))));
        person2.setId("ID");
        person2.setAuthentications(Arrays.asList(new Authentication("A1"), new Authentication("A2")));
        final Person person1Null = new Person("N", "LN", null, 1, "A", "E", new HashSet<>(Arrays.asList(new Role("R1"), new Role("R2"))));
        person1Null.setId("ID");
        person1Null.setAuthentications(Arrays.asList(new Authentication("A1"), new Authentication("A2")));

        assertNotEquals(person1, person2);
        assertNotEquals(person1, person1Null);
        assertNotEquals(person1Null, person1);
    }

    /**
     * Should fail equals due civilStatus
     */
    @Test
    public void noEqualsCivilStatus() {
        final LocalDate BIRTHDAY = LocalDate.now();
        final Person person1 = new Person("N", "LN", BIRTHDAY, 1, "A", "E", new HashSet<>(Arrays.asList(new Role("R1"), new Role("R2"))));
        person1.setId("ID");
        person1.setAuthentications(Arrays.asList(new Authentication("A1"), new Authentication("A2")));
        final Person person2 = new Person("N", "LN", BIRTHDAY, 2, "A", "E", new HashSet<>(Arrays.asList(new Role("R1"), new Role("R2"))));
        person2.setId("ID");
        person2.setAuthentications(Arrays.asList(new Authentication("A1"), new Authentication("A2")));
        final Person person1Null = new Person("N", "LN", BIRTHDAY, null, "A", "E", new HashSet<>(Arrays.asList(new Role("R1"), new Role("R2"))));
        person1Null.setId("ID");
        person1Null.setAuthentications(Arrays.asList(new Authentication("A1"), new Authentication("A2")));

        assertNotEquals(person1, person2);
        assertNotEquals(person1, person1Null);
        assertNotEquals(person1Null, person1);
    }

    /**
     * Should fail equals due sex
     */
    @Test
    public void noEqualsSex() {
        final LocalDate BIRTHDAY = LocalDate.now();
        final Person person1 = new Person("N", "LN", BIRTHDAY, 1, "A", "E", new HashSet<>(Arrays.asList(new Role("R1"), new Role("R2"))));
        person1.setId("ID");
        person1.setAuthentications(Arrays.asList(new Authentication("A1"), new Authentication("A2")));
        final Person person2 = new Person("N", "LN", BIRTHDAY, 1, "B", "E", new HashSet<>(Arrays.asList(new Role("R1"), new Role("R2"))));
        person2.setId("ID");
        person2.setAuthentications(Arrays.asList(new Authentication("A1"), new Authentication("A2")));
        final Person person1Null = new Person("N", "LN", BIRTHDAY, 1, null, "E", new HashSet<>(Arrays.asList(new Role("R1"), new Role("R2"))));
        person1Null.setId("ID");
        person1Null.setAuthentications(Arrays.asList(new Authentication("A1"), new Authentication("A2")));

        assertNotEquals(person1, person2);
        assertNotEquals(person1, person1Null);
        assertNotEquals(person1Null, person1);
    }

    /**
     * Should fail equals due email
     */
    @Test
    public void noEqualsEmail() {
        final LocalDate BIRTHDAY = LocalDate.now();
        final Person person1 = new Person("N", "LN", BIRTHDAY, 1, "A", "E", new HashSet<>(Arrays.asList(new Role("R1"), new Role("R2"))));
        person1.setId("ID");
        person1.setAuthentications(Arrays.asList(new Authentication("A1"), new Authentication("A2")));
        final Person person2 = new Person("N", "LN", BIRTHDAY, 1, "A", "E2", new HashSet<>(Arrays.asList(new Role("R1"), new Role("R2"))));
        person2.setId("ID");
        person2.setAuthentications(Arrays.asList(new Authentication("A1"), new Authentication("A2")));
        final Person person1Null = new Person("N", "LN", BIRTHDAY, 1, "A", null, new HashSet<>(Arrays.asList(new Role("R1"), new Role("R2"))));
        person1Null.setId("ID");
        person1Null.setAuthentications(Arrays.asList(new Authentication("A1"), new Authentication("A2")));

        assertNotEquals(person1, person2);
        assertNotEquals(person1, person1Null);
        assertNotEquals(person1Null, person1);
    }

    /**
     * Should fail equals due roles
     */
    @Test
    public void noEqualsRoles() {
        final LocalDate BIRTHDAY = LocalDate.now();
        final Person person1 = new Person("N", "LN", BIRTHDAY, 1, "A", "E", new HashSet<>(Arrays.asList(new Role("R1"), new Role("R2"))));
        person1.setId("ID");
        person1.setAuthentications(Arrays.asList(new Authentication("A1"), new Authentication("A2")));
        final Person person2 = new Person("N", "LN", BIRTHDAY, 1, "A", "E", new HashSet<>(Arrays.asList(new Role("R1"))));
        person2.setId("ID");
        person2.setAuthentications(Arrays.asList(new Authentication("A1"), new Authentication("A2")));
        final Person person1Null = new Person("N", "LN", BIRTHDAY, 1, "A", "E", null);
        person1Null.setId("ID");
        person1Null.setAuthentications(Arrays.asList(new Authentication("A1"), new Authentication("A2")));

        assertNotEquals(person1, person2);
        assertNotEquals(person1, person1Null);
        assertNotEquals(person1Null, person1);
    }

    /**
     * Should fail equals due authentications
     */
    @Test
    public void noEqualsAuthentications() {
        final LocalDate BIRTHDAY = LocalDate.now();
        final Person person1 = new Person("N", "LN", BIRTHDAY, 1, "A", "E", new HashSet<>(Arrays.asList(new Role("R1"), new Role("R2"))));
        person1.setId("ID");
        person1.setAuthentications(Arrays.asList(new Authentication("A1"), new Authentication("A2")));
        final Person person2 = new Person("N", "LN", BIRTHDAY, 1, "A", "E", new HashSet<>(Arrays.asList(new Role("R1"), new Role("R2"))));
        person2.setId("ID");
        person2.setAuthentications(Arrays.asList(new Authentication("A1")));
        final Person person1Null = new Person("N", "LN", BIRTHDAY, 1, "A", "E", new HashSet<>(Arrays.asList(new Role("R1"), new Role("R2"))));
        person1Null.setId("ID");
        person1Null.setAuthentications(null);

        assertNotEquals(person1, person2);
        assertNotEquals(person1, person1Null);
        assertNotEquals(person1Null, person1);
    }

    /**
     * Should be equals
     */
    @Test
    public void testEquals() {
        final LocalDate BIRTHDAY = LocalDate.now();
        final Person person1 = new Person("N", "LN", BIRTHDAY, 1, "A", "E", new HashSet<>(Arrays.asList(new Role("R1"), new Role("R2"))));
        person1.setId("ID");
        person1.setAuthentications(Arrays.asList(new Authentication("A1"), new Authentication("A2")));
        final Person person2 = new Person("N", "LN", BIRTHDAY, 1, "A", "E", new HashSet<>(Arrays.asList(new Role("R1"), new Role("R2"))));
        person2.setId("ID");
        person2.setAuthentications(Arrays.asList(new Authentication("A1"), new Authentication("A2")));
        final Person personNull1 = new Person();
        final Person personNull2 = new Person();

        assertNotSame(person1, person2);
        assertEquals(person1, person2);
        assertNotSame(personNull1, personNull2);
        assertEquals(personNull1, personNull2);
    }

    /**
     * Should have hashCode
     */
    @Test
    public void testHashCode() {
        final String ID = "ID";
        final String NAME = "name";
        final String LAST_NAME = "last name";
        final LocalDate BIRTHDAY = LocalDate.now();
        final Integer CIVIL_STATUS = 1;
        final String SEX = "A";
        final String EMAIL = "emailtest";
        final Set<Role> ROLES = new HashSet<>(Arrays.asList(new Role("R1"), new Role("R2")));
        final List<Authentication> AUTHENTICATIONS = Arrays.asList(new Authentication("A1"), new Authentication("A2"));
        final Person person = new Person(NAME, LAST_NAME, BIRTHDAY, CIVIL_STATUS, SEX, EMAIL, ROLES);
        person.setId(ID);
        person.setAuthentications(AUTHENTICATIONS);
        final Person personNull = new Person();

        int hashExpected = ID.hashCode();
        hashExpected = 31 * hashExpected + (NAME.hashCode());
        hashExpected = 31 * hashExpected + (LAST_NAME.hashCode());
        hashExpected = 31 * hashExpected + (BIRTHDAY.hashCode());
        hashExpected = 31 * hashExpected + (CIVIL_STATUS.hashCode());
        hashExpected = 31 * hashExpected + (SEX.hashCode());
        hashExpected = 31 * hashExpected + (EMAIL.hashCode());
        hashExpected = 31 * hashExpected + (ROLES.hashCode());
        hashExpected = 31 * hashExpected + (AUTHENTICATIONS.hashCode());

        final int hashResult = person.hashCode();

        assertEquals(hashExpected, hashResult);
        assertEquals(0, personNull.hashCode());
    }
}