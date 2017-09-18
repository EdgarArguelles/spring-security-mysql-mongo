package app.services.implementations;

import app.exceptions.AppDontFoundException;
import app.exceptions.AppValidationException;
import app.models.Permission;
import app.pojos.pages.PageDataRequest;
import app.repositories.PermissionRepository;
import app.repositories.RoleRepository;
import app.services.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public List<Permission> findAll() {
        List<Permission> permissions = permissionRepository.findAll();
        permissions.forEach(p -> loadRelatedData(p));
        return permissions;
    }

    @Override
    public Permission findById(String id) {
        Permission permission = permissionRepository.findOne(id);
        loadRelatedData(permission);
        return permission;
    }

    @Override
    public Permission findByIdNotNull(String id) throws AppDontFoundException {
        Permission permission = findById(id);
        if (permission == null) {
            throw new AppDontFoundException("Data don't found.");
        }

        return permission;
    }

    @Override
    public Permission findByName(String name) {
        Permission permission = permissionRepository.findByName(name);
        loadRelatedData(permission);
        return permission;
    }

    @Override
    @Transactional
    public Permission save(Permission permission) {
        if (findByName(permission.getName()) != null) {
            throw new AppValidationException("Permission name '" + permission.getName() + "' is already used.");
        }

        return permissionRepository.save(permission);
    }

    @Override
    @Transactional
    public Permission update(Permission permission) {
        Permission original = findByIdNotNull(permission.getId());
        original.setDescription(permission.getDescription());

        // mongo validation that avoid adding roles list to Permission table
        if (permissionRepository instanceof MongoRepository) {
            original.setRoles(null);
        }
        return permissionRepository.save(original);
    }

    @Override
    @Transactional
    public Permission delete(String id) {
        Permission permission = findByIdNotNull(id);
        if (permission.getRoles() != null && !permission.getRoles().isEmpty()) {
            throw new AppValidationException("There are some roles using the Permission '" + permission.getName() + "'.");
        }

        permissionRepository.delete(permission);
        return permission;
    }

    @Override
    public Page<Permission> page(PageDataRequest pageDataRequest) {
        return permissionRepository.page(pageDataRequest);
    }

    /**
     * Load related data (only used with mongo or jpa which doesn't implement bi-directional relationship)
     *
     * @param permission permission where related data is loaded
     */
    private void loadRelatedData(Permission permission) {
        if (permission == null) {
            return;
        }

        if (permission.getRoles() == null) {
            permission.setRoles(roleRepository.findByPermissions(permission));
        }
    }
}