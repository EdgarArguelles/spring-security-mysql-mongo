package app.services.implementations;

import app.models.*;
import app.services.PresentationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PresentationServiceImplTest {

    @Autowired
    private PresentationService presentationService;

    /**
     * Should not throw an Exception when list null
     */
    @Test
    public void prepareListWhenNull() {
        final List<Model> models = null;
        presentationService.prepare(models, false, false);
    }

    /**
     * Should not throw an Exception when list is empty
     */
    @Test
    public void prepareListWhenEmpty() {
        presentationService.prepare(Collections.EMPTY_LIST, true, true);
    }

    /**
     * Should not clean any relationship
     */
    @Test
    public void prepareListWhenCompleteAndAllRelations() {
        final AuthProvider AUTH_PROVIDER = new AuthProvider("N", "D", "U", "AK", "AS");
        final AuthProvider AUTH_PROVIDER_2 = new AuthProvider("N", "D", null, null, null);
        final Person PERSON = new Person("PE");
        PERSON.setRoles(new HashSet<>(Arrays.asList(
                new Role("R1", "DR1", new HashSet<>(Arrays.asList(new Permission("P1"))))
        )));
        final Person PERSON_2 = new Person("PE");
        PERSON_2.setRoles(new HashSet<>(Arrays.asList(
                new Role("R1", "DR1", new HashSet<>(Arrays.asList(new Permission("P1"))))
        )));
        final List<Model> modelsResult = Arrays.asList(
                new Role("N", "D", new HashSet<>(Arrays.asList(new Permission("P")))),
                null,
                new Authentication("U", "P", AUTH_PROVIDER, PERSON)
        );
        final List<Model> modelsExpected = Arrays.asList(
                new Role("N", "D", new HashSet<>(Arrays.asList(new Permission("P")))),
                null,
                new Authentication("U", null, AUTH_PROVIDER_2, PERSON_2)
        );

        presentationService.prepare(modelsResult, true, true);

        assertNotSame(modelsExpected, modelsResult);
        assertEquals(modelsExpected, modelsResult);
    }

    /**
     * Should not clean any relationship
     */
    @Test
    public void prepareListWhenNotCompleteAndAllRelations() {
        final AuthProvider AUTH_PROVIDER = new AuthProvider("N", "D", "U", "AK", "AS");
        final AuthProvider AUTH_PROVIDER_2 = new AuthProvider("N", "D", null, null, null);
        final Person PERSON = new Person("PE");
        PERSON.setRoles(new HashSet<>(Arrays.asList(
                new Role("R1", "DR1", new HashSet<>(Arrays.asList(new Permission("P1"))))
        )));
        final Person PERSON_2 = new Person("PE");
        PERSON_2.setRoles(new HashSet<>(Arrays.asList(
                new Role("R1", "DR1", new HashSet<>(Arrays.asList(new Permission("P1"))))
        )));
        final List<Model> modelsResult = Arrays.asList(
                new Role("N", "D", new HashSet<>(Arrays.asList(new Permission("P")))),
                null,
                new Authentication("U", "P", AUTH_PROVIDER, PERSON)
        );
        final List<Model> modelsExpected = Arrays.asList(
                new Role("N", "D", new HashSet<>(Arrays.asList(new Permission("P")))),
                null,
                new Authentication("U", null, AUTH_PROVIDER_2, PERSON_2)
        );

        presentationService.prepare(modelsResult, false, true);

        assertNotSame(modelsExpected, modelsResult);
        assertEquals(modelsExpected, modelsResult);
    }

    /**
     * Should load only first level relationship
     */
    @Test
    public void prepareListWhenCompleteAndNotAllRelations() {
        final AuthProvider AUTH_PROVIDER = new AuthProvider("N", "D", "U", "AK", "AS");
        final AuthProvider AUTH_PROVIDER_2 = new AuthProvider("N", "D", null, null, null);
        final Person PERSON = new Person("PE");
        PERSON.setRoles(new HashSet<>(Arrays.asList(
                new Role("R1", "DR1", new HashSet<>(Arrays.asList(new Permission("P1"))))
        )));
        final Person PERSON_2 = new Person("PE");
        PERSON_2.setRoles(new HashSet<>(Arrays.asList(
                new Role("R1", "DR1", null)
        )));
        final List<Model> modelsResult = Arrays.asList(
                new Role("N", "D", new HashSet<>(Arrays.asList(new Permission("P")))),
                null,
                new Authentication("U", "P", AUTH_PROVIDER, PERSON)
        );
        final List<Model> modelsExpected = Arrays.asList(
                new Role("N", "D", new HashSet<>(Arrays.asList(new Permission("P")))),
                null,
                new Authentication("U", null, AUTH_PROVIDER_2, PERSON_2)
        );

        presentationService.prepare(modelsResult, true, false);

        assertNotSame(modelsExpected, modelsResult);
        assertEquals(modelsExpected, modelsResult);
    }

    /**
     * Should load without any relationship
     */
    @Test
    public void prepareListWhenNotCompleteAndNotAllRelations() {
        final AuthProvider AUTH_PROVIDER = new AuthProvider("N", "D", "U", "AK", "AS");
        final AuthProvider AUTH_PROVIDER_2 = new AuthProvider("N", "D", null, null, null);
        final Person PERSON = new Person("PE");
        PERSON.setRoles(new HashSet<>(Arrays.asList(new Role("R"))));
        final Person PERSON_2 = new Person("PE");
        final List<Model> modelsResult = Arrays.asList(
                new Role("N", "D", new HashSet<>(Arrays.asList(new Permission("P")))),
                null,
                new Authentication("U", "P", AUTH_PROVIDER, PERSON)
        );
        final List<Model> modelsExpected = Arrays.asList(
                new Role("N", "D", null),
                null,
                new Authentication("U", null, AUTH_PROVIDER_2, PERSON_2)
        );

        presentationService.prepare(modelsResult, false, false);

        assertNotSame(modelsExpected, modelsResult);
        assertEquals(modelsExpected, modelsResult);
    }

    /**
     * Should not throw an Exception when model null
     */
    @Test
    public void prepareWhenNull() {
        final Model model = null;
        presentationService.prepare(model, false, false);
    }

    /**
     * Should not clean any relationship
     */
    @Test
    public void prepareWhenCompleteAndAllRelations() {
        final AuthProvider AUTH_PROVIDER = new AuthProvider("N", "D", "U", "AK", "AS");
        final AuthProvider AUTH_PROVIDER_2 = new AuthProvider("N", "D", null, null, null);
        final Person PERSON = new Person("PE");
        PERSON.setRoles(new HashSet<>(Arrays.asList(
                new Role("R1", "DR1", new HashSet<>(Arrays.asList(new Permission("P1"))))
        )));
        final Person PERSON_2 = new Person("PE");
        PERSON_2.setRoles(new HashSet<>(Arrays.asList(
                new Role("R1", "DR1", new HashSet<>(Arrays.asList(new Permission("P1"))))
        )));
        final Authentication authenticationResult = new Authentication("U", "P", AUTH_PROVIDER, PERSON);
        final Authentication authenticationExpected = new Authentication("U", null, AUTH_PROVIDER_2, PERSON_2);

        presentationService.prepare(authenticationResult, true, true);

        assertNotSame(authenticationExpected, authenticationResult);
        assertEquals(authenticationExpected, authenticationResult);
    }

    /**
     * Should not clean any relationship
     */
    @Test
    public void prepareWhenNotCompleteAndAllRelations() {
        final AuthProvider AUTH_PROVIDER = new AuthProvider("N", "D", "U", "AK", "AS");
        final AuthProvider AUTH_PROVIDER_2 = new AuthProvider("N", "D", null, null, null);
        final Person PERSON = new Person("PE");
        PERSON.setRoles(new HashSet<>(Arrays.asList(
                new Role("R1", "DR1", new HashSet<>(Arrays.asList(new Permission("P1"))))
        )));
        final Person PERSON_2 = new Person("PE");
        PERSON_2.setRoles(new HashSet<>(Arrays.asList(
                new Role("R1", "DR1", new HashSet<>(Arrays.asList(new Permission("P1"))))
        )));
        final Authentication authenticationResult = new Authentication("U", "P", AUTH_PROVIDER, PERSON);
        final Authentication authenticationExpected = new Authentication("U", null, AUTH_PROVIDER_2, PERSON_2);

        presentationService.prepare(authenticationResult, false, true);

        assertNotSame(authenticationExpected, authenticationResult);
        assertEquals(authenticationExpected, authenticationResult);
    }

    /**
     * Should load only first level relationship
     */
    @Test
    public void prepareWhenCompleteAndNotAllRelations() {
        final AuthProvider AUTH_PROVIDER = new AuthProvider("N", "D", "U", "AK", "AS");
        final AuthProvider AUTH_PROVIDER_2 = new AuthProvider("N", "D", null, null, null);
        final Person PERSON = new Person("PE");
        PERSON.setRoles(new HashSet<>(Arrays.asList(
                new Role("R1", "DR1", new HashSet<>(Arrays.asList(new Permission("P1"))))
        )));
        final Person PERSON_2 = new Person("PE");
        PERSON_2.setRoles(new HashSet<>(Arrays.asList(
                new Role("R1", "DR1", null)
        )));
        final Authentication authenticationResult = new Authentication("U", "P", AUTH_PROVIDER, PERSON);
        final Authentication authenticationExpected = new Authentication("U", null, AUTH_PROVIDER_2, PERSON_2);

        presentationService.prepare(authenticationResult, true, false);

        assertNotSame(authenticationExpected, authenticationResult);
        assertEquals(authenticationExpected, authenticationResult);
    }

    /**
     * Should load without any relationship
     */
    @Test
    public void prepareWhenNotCompleteAndNotAllRelations() {
        final AuthProvider AUTH_PROVIDER = new AuthProvider("N", "D", "U", "AK", "AS");
        final AuthProvider AUTH_PROVIDER_2 = new AuthProvider("N", "D", null, null, null);
        final Person PERSON = new Person("PE");
        PERSON.setRoles(new HashSet<>(Arrays.asList(new Role("R"))));
        final Person PERSON_2 = new Person("PE");
        final Authentication authenticationResult = new Authentication("U", "P", AUTH_PROVIDER, PERSON);
        final Authentication authenticationExpected = new Authentication("U", null, AUTH_PROVIDER_2, PERSON_2);

        presentationService.prepare(authenticationResult, false, false);

        assertNotSame(authenticationExpected, authenticationResult);
        assertEquals(authenticationExpected, authenticationResult);
    }
}