package app.services.implementations;

import app.exceptions.AppDontFoundException;
import app.exceptions.AppValidationException;
import app.models.Person;
import app.pojos.pages.PageDataRequest;
import app.pojos.responses.error.nesteds.NestedError;
import app.pojos.responses.error.nesteds.ValidationNestedError;
import app.repositories.AuthenticationRepository;
import app.repositories.PersonRepository;
import app.services.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class PersonServiceImpl implements PersonService {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private AuthenticationRepository authenticationRepository;

    @Override
    public List<Person> findAll() {
        List<Person> people = personRepository.findAll();
        people.forEach(p -> loadRelatedData(p));
        return people;
    }

    @Override
    public Person findById(String id) {
        Person person = personRepository.findOne(id);
        loadRelatedData(person);
        return person;
    }

    @Override
    public Person findByIdNotNull(String id) throws AppDontFoundException {
        Person person = findById(id);
        if (person == null) {
            throw new AppDontFoundException("Data don't found.");
        }

        return person;
    }

    @Override
    @Transactional
    public Person save(Person person) {
        validateData(person);
        return personRepository.save(person);
    }

    @Override
    @Transactional
    public Person update(Person person) {
        validateData(person);

        Person original = findByIdNotNull(person.getId());
        original.setName(person.getName());
        original.setLastName(person.getLastName());
        original.setBirthday(person.getBirthday());
        original.setCivilStatus(person.getCivilStatus());
        original.setSex(person.getSex());
        original.setEmail(person.getEmail());
        original.setRoles(person.getRoles());

        // mongo validation that avoid adding authentications list to Person table
        if (personRepository instanceof MongoRepository) {
            original.setAuthentications(null);
        }
        return personRepository.save(original);
    }

    @Override
    @Transactional
    public Person delete(String id) {
        Person person = findByIdNotNull(id);
        if (person.getAuthentications() != null && !person.getAuthentications().isEmpty()) {
            throw new AppValidationException("Person '" + person.getFullName() + "' has one or more authentications associated.");
        }
        personRepository.delete(person);
        person.setRoles(null);
        return person;
    }

    @Override
    public Page<Person> page(PageDataRequest pageDataRequest) {
        return personRepository.page(pageDataRequest);
    }

    /**
     * Validates data integrity
     *
     * @param person entity to be validated
     * @throws AppValidationException
     */
    private void validateData(Person person) throws AppValidationException {
        List<NestedError> nestedErrors = new ArrayList<>();
        nestedErrors.add(validateCivilStatus(person.getCivilStatus()));
        nestedErrors.add(validateSex(person.getSex()));

        // remove null from list
        nestedErrors.removeAll(Collections.singleton(null));
        if (!nestedErrors.isEmpty()) {
            throw new AppValidationException("Some data aren't valid.", nestedErrors);
        }
    }

    /**
     * Validates if civil status is an allowed value
     *
     * @param civilStatus value to be validated
     * @return NestedError or null if value is valid
     */
    private NestedError validateCivilStatus(Integer civilStatus) {
        List<Integer> allowed = new ArrayList<>();

        //iterate all interface properties
        Arrays.asList(Person.CIVIL_STATUS.class.getDeclaredFields()).forEach((field -> {
            try {
                allowed.add((Integer) field.get(Integer.class));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }));

        if (!allowed.contains(civilStatus)) {
            return new ValidationNestedError("civilStatus", "'" + civilStatus + "' is not a valid Civil Status value, it only allows " + Arrays.toString(allowed.toArray()));
        }
        return null;
    }

    /**
     * Validates if sex is an allowed value
     *
     * @param sex value to be validated
     * @return NestedError or null if value is valid
     */
    private NestedError validateSex(String sex) {
        List<String> allowed = new ArrayList<>();

        //iterate all interface properties
        Arrays.asList(Person.SEX.class.getDeclaredFields()).forEach((field -> {
            try {
                allowed.add((String) field.get(String.class));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }));

        if (!allowed.contains(sex)) {
            return new ValidationNestedError("sex", "'" + sex + "' is not a valid Sex value, it only allows " + Arrays.toString(allowed.toArray()));
        }
        return null;
    }

    /**
     * Load related data (only used with mongo or jpa which doesn't implement bi-directional relationship)
     *
     * @param person person where related data is loaded
     */
    private void loadRelatedData(Person person) {
        if (person == null) {
            return;
        }

        if (person.getAuthentications() == null) {
            person.setAuthentications(authenticationRepository.findByPerson(person));
        }
    }
}