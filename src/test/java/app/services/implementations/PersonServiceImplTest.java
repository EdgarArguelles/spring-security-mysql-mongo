package app.services.implementations;

import app.exceptions.AppDontFoundException;
import app.exceptions.AppValidationException;
import app.models.Authentication;
import app.models.Person;
import app.models.Role;
import app.pojos.pages.PageDataRequest;
import app.repositories.AuthenticationRepository;
import app.repositories.PersonRepository;
import app.services.PersonService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PersonServiceImplTest {

    @Autowired
    private PersonService personService;

    @MockBean
    private PersonRepository personRepository;

    @MockBean
    private AuthenticationRepository authenticationRepository;

    @Captor
    ArgumentCaptor<Person> captor;

    /**
     * Should call findAll function
     */
    @Test
    public void findAll() {
        final List<Person> peopleMocked = Arrays.asList(
                new Person("ID1"), new Person("ID2"), null, new Person("ID4"));
        peopleMocked.get(0).setAuthentications(Arrays.asList(new Authentication("A1")));
        peopleMocked.get(0).setRoles(new HashSet<>(Arrays.asList(new Role("R1"), new Role("R2"))));
        peopleMocked.get(1).setAuthentications(Collections.EMPTY_LIST);
        peopleMocked.get(1).setRoles(new HashSet<>());
        given(personRepository.findAll()).willReturn(peopleMocked);
        given(authenticationRepository.findByPerson(any(Person.class)))
                .willReturn(Arrays.asList(new Authentication("A2"), new Authentication("A3")));

        final List<Person> peopleExpected = Arrays.asList(
                new Person("ID1"), new Person("ID2"), null, new Person("ID4"));
        peopleExpected.get(0).setAuthentications(Arrays.asList(new Authentication("A1")));
        peopleExpected.get(0).setRoles(new HashSet<>(Arrays.asList(new Role("R1"), new Role("R2"))));
        peopleExpected.get(1).setAuthentications(Collections.EMPTY_LIST);
        peopleExpected.get(1).setRoles(new HashSet<>());
        peopleExpected.get(3).setAuthentications(Arrays.asList(new Authentication("A2"), new Authentication("A3")));

        final List<Person> peopleResult = personService.findAll();

        assertSame(peopleMocked, peopleResult);
        assertNotSame(peopleExpected, peopleResult);
        assertEquals(peopleExpected, peopleResult);
        verify(personRepository, times(1)).findAll();
        verify(authenticationRepository, times(1)).findByPerson(captor.capture());
        assertEquals(peopleMocked.get(3), captor.getAllValues().get(0));
    }

    /**
     * Should call findOne function
     */
    @Test
    public void findById() {
        final String ID = "ID";
        final Person personMocked = new Person(ID);
        personMocked.setRoles(new HashSet<>(Arrays.asList(new Role("R1"), new Role("R2"))));
        given(personRepository.findOne(ID)).willReturn(personMocked);
        given(authenticationRepository.findByPerson(personMocked))
                .willReturn(Arrays.asList(new Authentication("A2"), new Authentication("A3")));

        final Person personExpected = new Person(ID);
        personExpected.setRoles(new HashSet<>(Arrays.asList(new Role("R1"), new Role("R2"))));
        personExpected.setAuthentications(Arrays.asList(new Authentication("A2"), new Authentication("A3")));

        final Person personResult = personService.findById(ID);

        assertSame(personMocked, personResult);
        assertNotSame(personExpected, personResult);
        assertEquals(personExpected, personResult);
        verify(personRepository, times(1)).findOne(ID);
        verify(authenticationRepository, times(1)).findByPerson(personMocked);
    }

    /**
     * Should throw AppDontFoundException when null
     */
    @Test(expected = AppDontFoundException.class)
    public void findByIdNotNullWhenNull() {
        final String ID = "ID";
        given(personRepository.findOne(ID)).willReturn(null);

        personService.findByIdNotNull(ID);
    }

    /**
     * Should return a person when not null
     */
    @Test
    public void findByIdNotNullWhenNotNull() {
        final String ID = "ID";
        final Person personMocked = new Person(ID);
        personMocked.setRoles(new HashSet<>(Arrays.asList(new Role("R1"), new Role("R2"))));
        given(personRepository.findOne(ID)).willReturn(personMocked);
        given(authenticationRepository.findByPerson(personMocked))
                .willReturn(Arrays.asList(new Authentication("A2"), new Authentication("A3")));

        final Person personExpected = new Person(ID);
        personExpected.setRoles(new HashSet<>(Arrays.asList(new Role("R1"), new Role("R2"))));
        personExpected.setAuthentications(Arrays.asList(new Authentication("A2"), new Authentication("A3")));

        final Person personResult = personService.findByIdNotNull(ID);

        assertSame(personMocked, personResult);
        assertNotSame(personExpected, personResult);
        assertEquals(personExpected, personResult);
        verify(personRepository, times(1)).findOne(ID);
        verify(authenticationRepository, times(1)).findByPerson(personMocked);
    }

    /**
     * Should throw AppValidationException when Civil Status is invalid
     */
    @Test(expected = AppValidationException.class)
    public void saveCivilStatusInvalid() {
        final Person person = new Person("ID");
        person.setCivilStatus(-1);

        personService.save(person);
    }

    /**
     * Should throw AppValidationException when Sex is invalid
     */
    @Test(expected = AppValidationException.class)
    public void saveSexInvalid() {
        final Person person = new Person("ID");
        person.setSex("A");

        personService.save(person);
    }

    /**
     * Should return a person when save successfully
     */
    @Test
    public void saveSuccessfully() {
        final String NAME = "test";
        final String LAST_NAME = "last name";
        final LocalDate BIRTHDAY = LocalDate.now();
        final Integer CIVIL_STATUS = Person.CIVIL_STATUS.SINGLE;
        final String SEX = Person.SEX.M;
        final String EMAIL = "test@test.com";
        final Set<Role> ROLES = new HashSet<>(Arrays.asList(new Role("R1"), new Role("R2")));
        final Person person = new Person(NAME, LAST_NAME, BIRTHDAY, CIVIL_STATUS, SEX, EMAIL, ROLES);
        given(personRepository.save(person)).willReturn(person);

        final Person personExpected = new Person(NAME, LAST_NAME, BIRTHDAY, CIVIL_STATUS, SEX, EMAIL, ROLES);

        final Person personResult = personService.save(person);

        assertSame(person, personResult);
        assertNotSame(personExpected, personResult);
        assertEquals(personExpected, personResult);
        verify(personRepository, times(1)).save(person);
    }

    /**
     * Should throw AppValidationException when Civil Status is invalid
     */
    @Test(expected = AppValidationException.class)
    public void updateCivilStatusInvalid() {
        final Person person = new Person("ID");
        person.setCivilStatus(-1);

        personService.update(person);
    }

    /**
     * Should throw AppValidationException when Sex is invalid
     */
    @Test(expected = AppValidationException.class)
    public void updateSexInvalid() {
        final Person person = new Person("ID");
        person.setSex("A");

        personService.update(person);
    }

    /**
     * Should throw AppDontFoundException when person doesn't exist
     */
    @Test(expected = AppDontFoundException.class)
    public void updateDontFound() {
        final String ID = "ID";
        final Person person = new Person(ID);
        person.setCivilStatus(Person.CIVIL_STATUS.SINGLE);
        person.setSex(Person.SEX.M);
        given(personRepository.findOne(ID)).willReturn(null);

        personService.update(person);
    }

    /**
     * Should return a person when update successfully
     */
    @Test
    public void updateSuccessfully() {
        final String ID = "ID";
        final String NAME_PERSON = "name after";
        final String NAME_ORIGINAL = "name before";
        final String LAST_NAME_PERSON = "last name after";
        final String LAST_NAME_ORIGINAL = "last name before";
        final LocalDate BIRTHDAY_PERSON = LocalDate.now();
        final LocalDate BIRTHDAY_ORIGINAL = LocalDate.now();
        final Integer CIVIL_STATUS_PERSON = Person.CIVIL_STATUS.SINGLE;
        final Integer CIVIL_STATUS_ORIGINAL = Person.CIVIL_STATUS.MARRIED;
        final String SEX_PERSON = Person.SEX.M;
        final String SEX_ORIGINAL = Person.SEX.F;
        final String EMAIL_PERSON = "mail after";
        final String EMAIL_ORIGINAL = "mail before";
        final Set<Role> ROLES_PERSON = new HashSet<>(Arrays.asList(new Role("R1")));
        final Set<Role> ROLES_ORIGINAL = new HashSet<>(Arrays.asList(new Role("R2"), new Role("R3")));
        final List<Authentication> AUTHENTICATIONS_PERSON = Arrays.asList(new Authentication("A1"));
        final List<Authentication> AUTHENTICATIONS_ORIGINAL = Arrays.asList(new Authentication("A2"), new Authentication("A3"));
        final Person person = new Person(NAME_PERSON, LAST_NAME_PERSON, BIRTHDAY_PERSON, CIVIL_STATUS_PERSON, SEX_PERSON, EMAIL_PERSON, ROLES_PERSON);
        person.setId(ID);
        person.setAuthentications(AUTHENTICATIONS_PERSON);
        final Person personOriginal = new Person(NAME_ORIGINAL, LAST_NAME_ORIGINAL, BIRTHDAY_ORIGINAL, CIVIL_STATUS_ORIGINAL, SEX_ORIGINAL, EMAIL_ORIGINAL, ROLES_ORIGINAL);
        personOriginal.setId(ID);
        personOriginal.setAuthentications(AUTHENTICATIONS_ORIGINAL);
        //only change name, last name, birthday, civil status, sex and email
        final Person personMocked = new Person(NAME_PERSON, LAST_NAME_PERSON, BIRTHDAY_PERSON, CIVIL_STATUS_PERSON, SEX_PERSON, EMAIL_PERSON, ROLES_PERSON);
        personMocked.setId(ID);
        personMocked.setAuthentications(AUTHENTICATIONS_ORIGINAL);
        given(personRepository.findOne(ID)).willReturn(personOriginal);
        given(personRepository.save(personOriginal)).willReturn(personMocked);

        final Person personExpected = new Person(NAME_PERSON, LAST_NAME_PERSON, BIRTHDAY_PERSON, CIVIL_STATUS_PERSON, SEX_PERSON, EMAIL_PERSON, ROLES_PERSON);
        personExpected.setId(ID);
        personExpected.setAuthentications(AUTHENTICATIONS_ORIGINAL);

        final Person personResult = personService.update(person);

        assertSame(personMocked, personResult);
        assertNotSame(person, personResult);
        assertNotSame(personOriginal, personResult);
        assertNotSame(personExpected, personResult);
        assertEquals(personExpected, personResult);
        verify(personRepository, times(1)).findOne(ID);
        verify(personRepository, times(1)).save(personOriginal);
    }

    /**
     * Should throw AppDontFoundException when person doesn't exist
     */
    @Test(expected = AppDontFoundException.class)
    public void deleteDontFound() {
        final String ID = "ID";
        given(personRepository.findOne(ID)).willReturn(null);

        personService.delete(ID);
    }

    /**
     * Should throw AppValidationException when person is being used
     */
    @Test(expected = AppValidationException.class)
    public void deleteUsed() {
        final String ID = "ID";
        final Person person = new Person(ID);
        person.setAuthentications(Arrays.asList(new Authentication("A1")));
        given(personRepository.findOne(ID)).willReturn(person);

        personService.delete(ID);
    }

    /**
     * Should return a person when delete successfully
     */
    @Test
    public void deleteSuccessfully() {
        final String ID = "ID";
        final String NAME = "test";
        final String LAST_NAME = "last name";
        final LocalDate BIRTHDAY = LocalDate.now();
        final Integer CIVIL_STATUS = Person.CIVIL_STATUS.SINGLE;
        final String SEX = Person.SEX.M;
        final String EMAIL = "test@test.com";
        final Set<Role> ROLES = new HashSet<>(Arrays.asList(new Role("R1"), new Role("R2")));
        final Person person = new Person(NAME, LAST_NAME, BIRTHDAY, CIVIL_STATUS, SEX, EMAIL, ROLES);
        person.setId(ID);
        given(personRepository.findOne(ID)).willReturn(person);
        given(authenticationRepository.findByPerson(person)).willReturn(null);
        doNothing().when(personRepository).delete(person);

        final Person personExpected = new Person(NAME, LAST_NAME, BIRTHDAY, CIVIL_STATUS, SEX, EMAIL, null);
        personExpected.setId(ID);

        final Person personResult = personService.delete(ID);

        assertSame(person, personResult);
        assertNotSame(personExpected, personResult);
        assertEquals(personExpected, personResult);
        verify(personRepository, times(1)).findOne(ID);
        verify(authenticationRepository, times(1)).findByPerson(person);
        verify(personRepository, times(1)).delete(person);
    }

    /**
     * Should call page function
     */
    @Test
    public void page() {
        final PageDataRequest pageDataRequest = new PageDataRequest();
        final Page<Person> peopleMocked = new PageImpl(Arrays.asList(new Person("ID1"), new Person("ID2")));
        given(personRepository.page(pageDataRequest)).willReturn(peopleMocked);

        final Page<Person> peopleExpected = new PageImpl(Arrays.asList(new Person("ID1"), new Person("ID2")));

        final Page<Person> peopleResult = personService.page(pageDataRequest);

        assertSame(peopleMocked, peopleResult);
        assertNotSame(peopleExpected, peopleResult);
        assertEquals(peopleExpected, peopleResult);
        verify(personRepository, times(1)).page(pageDataRequest);
    }
}