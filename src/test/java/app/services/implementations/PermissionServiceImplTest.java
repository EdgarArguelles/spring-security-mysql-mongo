package app.services.implementations;

import app.exceptions.AppDontFoundException;
import app.exceptions.AppValidationException;
import app.models.Permission;
import app.models.Role;
import app.pojos.pages.PageDataRequest;
import app.repositories.PermissionRepository;
import app.repositories.RoleRepository;
import app.services.PermissionService;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PermissionServiceImplTest {

    @Autowired
    private PermissionService permissionService;

    @MockBean
    private PermissionRepository permissionRepository;

    @MockBean
    private RoleRepository roleRepository;

    @Captor
    ArgumentCaptor<Permission> captor;

    /**
     * Should call findAll function
     */
    @Test
    public void findAll() {
        final List<Permission> permissionsMocked = Arrays.asList(
                new Permission("ID1"), new Permission("ID2"), null, new Permission("ID4"));
        permissionsMocked.get(0).setRoles(Arrays.asList(new Role("ROLE1")));
        permissionsMocked.get(1).setRoles(Collections.EMPTY_LIST);
        given(permissionRepository.findAll()).willReturn(permissionsMocked);
        given(roleRepository.findByPermissions(any(Permission.class)))
                .willReturn(Arrays.asList(new Role("ROLE2"), new Role("ROLE3")));

        final List<Permission> permissionsExpected = Arrays.asList(
                new Permission("ID1"), new Permission("ID2"), null, new Permission("ID4"));
        permissionsExpected.get(0).setRoles(Arrays.asList(new Role("ROLE1")));
        permissionsExpected.get(1).setRoles(Collections.EMPTY_LIST);
        permissionsExpected.get(3).setRoles(Arrays.asList(new Role("ROLE2"), new Role("ROLE3")));

        final List<Permission> permissionsResult = permissionService.findAll();

        assertSame(permissionsMocked, permissionsResult);
        assertNotSame(permissionsExpected, permissionsResult);
        assertEquals(permissionsExpected, permissionsResult);
        verify(permissionRepository, times(1)).findAll();
        verify(roleRepository, times(1)).findByPermissions(captor.capture());
        assertEquals(permissionsMocked.get(3), captor.getAllValues().get(0));
    }

    /**
     * Should call findOne function
     */
    @Test
    public void findById() {
        final String ID = "ID";
        final Permission permissionMocked = new Permission(ID);
        given(permissionRepository.findOne(ID)).willReturn(permissionMocked);
        given(roleRepository.findByPermissions(permissionMocked))
                .willReturn(Arrays.asList(new Role("ROLE2"), new Role("ROLE3")));

        final Permission permissionExpected = new Permission(ID);
        permissionExpected.setRoles(Arrays.asList(new Role("ROLE2"), new Role("ROLE3")));

        final Permission permissionResult = permissionService.findById(ID);

        assertSame(permissionMocked, permissionResult);
        assertNotSame(permissionExpected, permissionResult);
        assertEquals(permissionExpected, permissionResult);
        verify(permissionRepository, times(1)).findOne(ID);
        verify(roleRepository, times(1)).findByPermissions(permissionMocked);
    }

    /**
     * Should throw AppDontFoundException when null
     */
    @Test(expected = AppDontFoundException.class)
    public void findByIdNotNullWhenNull() {
        final String ID = "ID";
        given(permissionRepository.findOne(ID)).willReturn(null);

        permissionService.findByIdNotNull(ID);
    }

    /**
     * Should return a permission when not null
     */
    @Test
    public void findByIdNotNullWhenNotNull() {
        final String ID = "ID";
        final Permission permissionMocked = new Permission(ID);
        given(permissionRepository.findOne(ID)).willReturn(permissionMocked);
        given(roleRepository.findByPermissions(permissionMocked))
                .willReturn(Arrays.asList(new Role("ROLE2"), new Role("ROLE3")));

        final Permission permissionExpected = new Permission(ID);
        permissionExpected.setRoles(Arrays.asList(new Role("ROLE2"), new Role("ROLE3")));

        final Permission permissionResult = permissionService.findByIdNotNull(ID);

        assertSame(permissionMocked, permissionResult);
        assertNotSame(permissionExpected, permissionResult);
        assertEquals(permissionExpected, permissionResult);
        verify(permissionRepository, times(1)).findOne(ID);
        verify(roleRepository, times(1)).findByPermissions(permissionMocked);
    }

    /**
     * Should call findByName function
     */
    @Test
    public void findByName() {
        final String NAME = "test";
        final Permission permissionMocked = new Permission(NAME, null);
        given(permissionRepository.findByName(NAME)).willReturn(permissionMocked);
        given(roleRepository.findByPermissions(permissionMocked))
                .willReturn(Arrays.asList(new Role("ROLE2"), new Role("ROLE3")));

        final Permission permissionExpected = new Permission(NAME, null);
        permissionExpected.setRoles(Arrays.asList(new Role("ROLE2"), new Role("ROLE3")));

        final Permission permissionResult = permissionService.findByName(NAME);

        assertSame(permissionMocked, permissionResult);
        assertNotSame(permissionExpected, permissionResult);
        assertEquals(permissionExpected, permissionResult);
        verify(permissionRepository, times(1)).findByName(NAME);
        verify(roleRepository, times(1)).findByPermissions(permissionMocked);
    }

    /**
     * Should throw AppValidationException when name duplicated
     */
    @Test(expected = AppValidationException.class)
    public void saveDuplicate() {
        final String NAME = "test";
        final Permission permission = new Permission(NAME, null);
        given(permissionRepository.findByName(NAME)).willReturn(permission);
        given(roleRepository.findByPermissions(permission)).willReturn(null);

        permissionService.save(permission);
    }

    /**
     * Should return a permission when save successfully
     */
    @Test
    public void saveSuccessfully() {
        final String NAME = "test";
        final String DESC = "desc";
        final Permission permission = new Permission(NAME, DESC);
        given(permissionRepository.findByName(NAME)).willReturn(null);
        given(permissionRepository.save(permission)).willReturn(permission);

        final Permission permissionExpected = new Permission(NAME, DESC);

        final Permission permissionResult = permissionService.save(permission);

        assertSame(permission, permissionResult);
        assertNotSame(permissionExpected, permissionResult);
        assertEquals(permissionExpected, permissionResult);
        verify(permissionRepository, times(1)).findByName(NAME);
        verify(permissionRepository, times(1)).save(permission);
    }

    /**
     * Should throw AppDontFoundException when permission doesn't exist
     */
    @Test(expected = AppDontFoundException.class)
    public void updateDontFound() {
        final String ID = "ID";
        final Permission permission = new Permission(ID);
        given(permissionRepository.findOne(ID)).willReturn(null);

        permissionService.update(permission);
    }

    /**
     * Should return a permission when update successfully
     */
    @Test
    public void updateSuccessfully() {
        final String ID = "ID";
        final String NAME_PERMISSION = "name after";
        final String NAME_ORIGINAL = "name before";
        final String DESC_PERMISSION = "desc after";
        final String DESC_ORIGINAL = "desc before";
        final List<Role> ROLES_PERMISSION = Arrays.asList(new Role("ID1"));
        final List<Role> ROLES_ORIGINAL = Arrays.asList(new Role("ID2"), new Role("ID3"));
        final Permission permission = new Permission(NAME_PERMISSION, DESC_PERMISSION);
        permission.setId(ID);
        permission.setRoles(ROLES_PERMISSION);
        final Permission permissionOriginal = new Permission(NAME_ORIGINAL, DESC_ORIGINAL);
        permissionOriginal.setId(ID);
        permissionOriginal.setRoles(ROLES_ORIGINAL);
        //only change desc
        final Permission permissionMocked = new Permission(NAME_ORIGINAL, DESC_PERMISSION);
        permissionMocked.setId(ID);
        permissionMocked.setRoles(ROLES_ORIGINAL);
        given(permissionRepository.findOne(ID)).willReturn(permissionOriginal);
        given(permissionRepository.save(permissionOriginal)).willReturn(permissionMocked);

        final Permission permissionExpected = new Permission(NAME_ORIGINAL, DESC_PERMISSION);
        permissionExpected.setId(ID);
        permissionExpected.setRoles(ROLES_ORIGINAL);

        final Permission permissionResult = permissionService.update(permission);

        assertSame(permissionMocked, permissionResult);
        assertNotSame(permission, permissionResult);
        assertNotSame(permissionOriginal, permissionResult);
        assertNotSame(permissionExpected, permissionResult);
        assertEquals(permissionExpected, permissionResult);
        verify(permissionRepository, times(1)).findOne(ID);
        verify(permissionRepository, times(1)).save(permissionOriginal);
    }

    /**
     * Should throw AppDontFoundException when permission doesn't exist
     */
    @Test(expected = AppDontFoundException.class)
    public void deleteDontFound() {
        final String ID = "ID";
        given(permissionRepository.findOne(ID)).willReturn(null);

        permissionService.delete(ID);
    }

    /**
     * Should throw AppValidationException when permission is being used
     */
    @Test(expected = AppValidationException.class)
    public void deleteUsed() {
        final String ID = "ID";
        final String NAME = "test";
        final String DESC = "desc";
        final Permission permission = new Permission(NAME, DESC);
        permission.setId(ID);
        permission.setRoles(Arrays.asList(new Role("ID1")));
        given(permissionRepository.findOne(ID)).willReturn(permission);

        permissionService.delete(ID);
    }

    /**
     * Should return a permission when delete successfully
     */
    @Test
    public void deleteSuccessfully() {
        final String ID = "ID";
        final String NAME = "test";
        final String DESC = "desc";
        final Permission permission = new Permission(NAME, DESC);
        permission.setId(ID);
        given(permissionRepository.findOne(ID)).willReturn(permission);
        given(roleRepository.findByPermissions(permission)).willReturn(null);
        doNothing().when(permissionRepository).delete(permission);

        final Permission permissionExpected = new Permission(NAME, DESC);
        permissionExpected.setId(ID);

        final Permission permissionResult = permissionService.delete(ID);

        assertSame(permission, permissionResult);
        assertNotSame(permissionExpected, permissionResult);
        assertEquals(permissionExpected, permissionResult);
        verify(permissionRepository, times(1)).findOne(ID);
        verify(roleRepository, times(1)).findByPermissions(permission);
        verify(permissionRepository, times(1)).delete(permission);
    }

    /**
     * Should call page function
     */
    @Test
    public void page() {
        final PageDataRequest pageDataRequest = new PageDataRequest();
        final Page<Permission> permissionsMocked = new PageImpl(Arrays.asList(new Permission("ID1"), new Permission("ID2")));
        given(permissionRepository.page(pageDataRequest)).willReturn(permissionsMocked);

        final Page<Permission> permissionsExpected = new PageImpl(Arrays.asList(new Permission("ID1"), new Permission("ID2")));

        final Page<Permission> permissionsResult = permissionService.page(pageDataRequest);

        assertSame(permissionsMocked, permissionsResult);
        assertNotSame(permissionsExpected, permissionsResult);
        assertEquals(permissionsExpected, permissionsResult);
        verify(permissionRepository, times(1)).page(pageDataRequest);
    }
}