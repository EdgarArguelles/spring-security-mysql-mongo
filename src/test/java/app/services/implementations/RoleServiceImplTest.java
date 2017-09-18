package app.services.implementations;

import app.exceptions.AppDontFoundException;
import app.exceptions.AppValidationException;
import app.models.Permission;
import app.models.Person;
import app.models.Role;
import app.pojos.pages.PageDataRequest;
import app.repositories.PersonRepository;
import app.repositories.RoleRepository;
import app.services.RoleService;
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

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RoleServiceImplTest {

    @Autowired
    private RoleService roleService;

    @MockBean
    private RoleRepository roleRepository;

    @MockBean
    private PersonRepository personRepository;

    @Captor
    ArgumentCaptor<Role> captor;

    /**
     * Should call findAll function
     */
    @Test
    public void findAll() {
        final List<Role> rolesMocked = Arrays.asList(
                new Role("ID1"), new Role("ID2"), null, new Role("ID4"));
        rolesMocked.get(0).setPeople(Arrays.asList(new Person("Per1")));
        rolesMocked.get(0).setPermissions(new HashSet<>(Arrays.asList(new Permission("P1"), new Permission("P2"))));
        rolesMocked.get(1).setPeople(Collections.EMPTY_LIST);
        rolesMocked.get(1).setPermissions(new HashSet<>());
        given(roleRepository.findAll()).willReturn(rolesMocked);
        given(personRepository.findByRoles(any(Role.class)))
                .willReturn(Arrays.asList(new Person("Per2"), new Person("Per3")));

        final List<Role> rolesExpected = Arrays.asList(
                new Role("ID1"), new Role("ID2"), null, new Role("ID4"));
        rolesExpected.get(0).setPeople(Arrays.asList(new Person("Per1")));
        rolesExpected.get(0).setPermissions(new HashSet<>(Arrays.asList(new Permission("P1"), new Permission("P2"))));
        rolesExpected.get(1).setPeople(Collections.EMPTY_LIST);
        rolesExpected.get(1).setPermissions(new HashSet<>());
        rolesExpected.get(3).setPeople(Arrays.asList(new Person("Per2"), new Person("Per3")));

        final List<Role> rolesResult = roleService.findAll();

        assertSame(rolesMocked, rolesResult);
        assertNotSame(rolesExpected, rolesResult);
        assertEquals(rolesExpected, rolesResult);
        verify(roleRepository, times(1)).findAll();
        verify(personRepository, times(1)).findByRoles(captor.capture());
        assertEquals(rolesMocked.get(3), captor.getAllValues().get(0));
    }

    /**
     * Should call findOne function
     */
    @Test
    public void findById() {
        final String ID = "ID";
        final Role roleMocked = new Role(ID);
        roleMocked.setPermissions(new HashSet<>(Arrays.asList(new Permission("P1"), new Permission("P2"))));
        given(roleRepository.findOne(ID)).willReturn(roleMocked);
        given(personRepository.findByRoles(roleMocked))
                .willReturn(Arrays.asList(new Person("Per2"), new Person("Per3")));

        final Role roleExpected = new Role(ID);
        roleExpected.setPermissions(new HashSet<>(Arrays.asList(new Permission("P1"), new Permission("P2"))));
        roleExpected.setPeople(Arrays.asList(new Person("Per2"), new Person("Per3")));

        final Role roleResult = roleService.findById(ID);

        assertSame(roleMocked, roleResult);
        assertNotSame(roleExpected, roleResult);
        assertEquals(roleExpected, roleResult);
        verify(roleRepository, times(1)).findOne(ID);
        verify(personRepository, times(1)).findByRoles(roleMocked);
    }

    /**
     * Should throw AppDontFoundException when null
     */
    @Test(expected = AppDontFoundException.class)
    public void findByIdNotNullWhenNull() {
        final String ID = "ID";
        given(roleRepository.findOne(ID)).willReturn(null);

        roleService.findByIdNotNull(ID);
    }

    /**
     * Should return a role when not null
     */
    @Test
    public void findByIdNotNullWhenNotNull() {
        final String ID = "ID";
        final Role roleMocked = new Role(ID);
        roleMocked.setPermissions(new HashSet<>(Arrays.asList(new Permission("P1"), new Permission("P2"))));
        given(roleRepository.findOne(ID)).willReturn(roleMocked);
        given(personRepository.findByRoles(roleMocked))
                .willReturn(Arrays.asList(new Person("Per2"), new Person("Per3")));

        final Role roleExpected = new Role(ID);
        roleExpected.setPermissions(new HashSet<>(Arrays.asList(new Permission("P1"), new Permission("P2"))));
        roleExpected.setPeople(Arrays.asList(new Person("Per2"), new Person("Per3")));

        final Role roleResult = roleService.findByIdNotNull(ID);

        assertSame(roleMocked, roleResult);
        assertNotSame(roleExpected, roleResult);
        assertEquals(roleExpected, roleResult);
        verify(roleRepository, times(1)).findOne(ID);
        verify(personRepository, times(1)).findByRoles(roleMocked);
    }

    /**
     * Should call findByName function
     */
    @Test
    public void findByName() {
        final String NAME = "test";
        final Role roleMocked = new Role(NAME, null, null);
        given(roleRepository.findByName(NAME)).willReturn(roleMocked);
        given(personRepository.findByRoles(roleMocked))
                .willReturn(Arrays.asList(new Person("Per2"), new Person("Per3")));

        final Role roleExpected = new Role(NAME, null, null);
        roleExpected.setPeople(Arrays.asList(new Person("Per2"), new Person("Per3")));

        final Role roleResult = roleService.findByName(NAME);

        assertSame(roleMocked, roleResult);
        assertNotSame(roleExpected, roleResult);
        assertEquals(roleExpected, roleResult);
        verify(roleRepository, times(1)).findByName(NAME);
        verify(personRepository, times(1)).findByRoles(roleMocked);
    }

    /**
     * Should throw AppValidationException when name duplicated
     */
    @Test(expected = AppValidationException.class)
    public void saveDuplicate() {
        final String NAME = "test";
        final Role role = new Role(NAME, null, null);
        given(roleRepository.findByName(NAME)).willReturn(role);
        given(personRepository.findByRoles(role)).willReturn(null);

        roleService.save(role);
    }

    /**
     * Should return a role when save successfully
     */
    @Test
    public void saveSuccessfully() {
        final String NAME = "test";
        final String DESC = "desc";
        final Set<Permission> PERMISSIONS = new HashSet<>(Arrays.asList(new Permission("P1"), new Permission("P2")));
        final Role role = new Role(NAME, DESC, PERMISSIONS);
        given(roleRepository.findByName(NAME)).willReturn(null);
        given(roleRepository.save(role)).willReturn(role);

        final Role roleExpected = new Role(NAME, DESC, PERMISSIONS);

        final Role roleResult = roleService.save(role);

        assertSame(role, roleResult);
        assertNotSame(roleExpected, roleResult);
        assertEquals(roleExpected, roleResult);
        verify(roleRepository, times(1)).findByName(NAME);
        verify(roleRepository, times(1)).save(role);
    }

    /**
     * Should throw AppDontFoundException when role doesn't exist
     */
    @Test(expected = AppDontFoundException.class)
    public void updateDontFound() {
        final String ID = "ID";
        final Role role = new Role(ID);
        given(roleRepository.findOne(ID)).willReturn(null);

        roleService.update(role);
    }

    /**
     * Should return a role when update successfully
     */
    @Test
    public void updateSuccessfully() {
        final String ID = "ID";
        final String NAME_ROLE = "name after";
        final String NAME_ORIGINAL = "name before";
        final String DESC_ROLE = "desc after";
        final String DESC_ORIGINAL = "desc before";
        final Set<Permission> PERMISSIONS_ROLE = new HashSet<>(Arrays.asList(new Permission("P1")));
        final Set<Permission> PERMISSIONS_ORIGINAL = new HashSet<>(Arrays.asList(new Permission("P2"), new Permission("P3")));
        final List<Person> PEOPLE_ROLE = Arrays.asList(new Person("Per1"));
        final List<Person> PEOPLE_ORIGINAL = Arrays.asList(new Person("Per2"), new Person("Per3"));
        final Role role = new Role(NAME_ROLE, DESC_ROLE, PERMISSIONS_ROLE);
        role.setId(ID);
        role.setPeople(PEOPLE_ROLE);
        final Role roleOriginal = new Role(NAME_ORIGINAL, DESC_ORIGINAL, PERMISSIONS_ORIGINAL);
        roleOriginal.setId(ID);
        roleOriginal.setPeople(PEOPLE_ORIGINAL);
        //only change desc
        final Role roleMocked = new Role(NAME_ORIGINAL, DESC_ROLE, PERMISSIONS_ROLE);
        roleMocked.setId(ID);
        roleMocked.setPeople(PEOPLE_ORIGINAL);
        given(roleRepository.findOne(ID)).willReturn(roleOriginal);
        given(roleRepository.save(roleOriginal)).willReturn(roleMocked);

        final Role roleExpected = new Role(NAME_ORIGINAL, DESC_ROLE, PERMISSIONS_ROLE);
        roleExpected.setId(ID);
        roleExpected.setPeople(PEOPLE_ORIGINAL);

        final Role roleResult = roleService.update(role);

        assertSame(roleMocked, roleResult);
        assertNotSame(role, roleResult);
        assertNotSame(roleOriginal, roleResult);
        assertNotSame(roleExpected, roleResult);
        assertEquals(roleExpected, roleResult);
        verify(roleRepository, times(1)).findOne(ID);
        verify(roleRepository, times(1)).save(roleOriginal);
    }

    /**
     * Should throw AppDontFoundException when role doesn't exist
     */
    @Test(expected = AppDontFoundException.class)
    public void deleteDontFound() {
        final String ID = "ID";
        given(roleRepository.findOne(ID)).willReturn(null);

        roleService.delete(ID);
    }

    /**
     * Should throw AppValidationException when role is being used
     */
    @Test(expected = AppValidationException.class)
    public void deleteUsed() {
        final String ID = "ID";
        final Role role = new Role(ID);
        role.setPeople(Arrays.asList(new Person("Per1")));
        given(roleRepository.findOne(ID)).willReturn(role);

        roleService.delete(ID);
    }

    /**
     * Should return a role when delete successfully
     */
    @Test
    public void deleteSuccessfully() {
        final String ID = "ID";
        final String NAME = "test";
        final String DESC = "desc";
        final Set<Permission> PERMISSIONS = new HashSet<>(Arrays.asList(new Permission("P1"), new Permission("P2")));
        final Role role = new Role(NAME, DESC, PERMISSIONS);
        role.setId(ID);
        given(roleRepository.findOne(ID)).willReturn(role);
        given(personRepository.findByRoles(role)).willReturn(null);
        doNothing().when(roleRepository).delete(role);

        //clean permissions
        final Role roleExpected = new Role(NAME, DESC, null);
        roleExpected.setId(ID);

        final Role roleResult = roleService.delete(ID);

        assertSame(role, roleResult);
        assertNotSame(roleExpected, roleResult);
        assertEquals(roleExpected, roleResult);
        verify(roleRepository, times(1)).findOne(ID);
        verify(personRepository, times(1)).findByRoles(role);
        verify(roleRepository, times(1)).delete(role);
    }

    /**
     * Should call page function
     */
    @Test
    public void page() {
        final PageDataRequest pageDataRequest = new PageDataRequest();
        final Page<Role> rolesMocked = new PageImpl(Arrays.asList(new Role("ID1"), new Role("ID2")));
        given(roleRepository.page(pageDataRequest)).willReturn(rolesMocked);

        final Page<Role> rolesExpected = new PageImpl(Arrays.asList(new Role("ID1"), new Role("ID2")));

        final Page<Role> rolesResult = roleService.page(pageDataRequest);

        assertSame(rolesMocked, rolesResult);
        assertNotSame(rolesExpected, rolesResult);
        assertEquals(rolesExpected, rolesResult);
        verify(roleRepository, times(1)).page(pageDataRequest);
    }
}