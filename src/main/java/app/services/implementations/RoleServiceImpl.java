package app.services.implementations;

import app.exceptions.AppDontFoundException;
import app.exceptions.AppValidationException;
import app.models.Role;
import app.pojos.pages.PageDataRequest;
import app.repositories.PersonRepository;
import app.repositories.RoleRepository;
import app.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PersonRepository personRepository;

    @Override
    public List<Role> findAll() {
        List<Role> roles = roleRepository.findAll();
        roles.forEach(r -> loadRelatedData(r));
        return roles;
    }

    @Override
    public Role findById(String id) {
        Role role = roleRepository.findOne(id);
        loadRelatedData(role);
        return role;
    }

    @Override
    public Role findByIdNotNull(String id) throws AppDontFoundException {
        Role role = findById(id);
        if (role == null) {
            throw new AppDontFoundException("Data don't found.");
        }

        return role;
    }

    @Override
    public Role findByName(String name) {
        Role role = roleRepository.findByName(name);
        loadRelatedData(role);
        return role;
    }

    @Override
    @Transactional
    public Role save(Role role) {
        if (findByName(role.getName()) != null) {
            throw new AppValidationException("Role name '" + role.getName() + "' is already used.");
        }

        return roleRepository.save(role);
    }

    @Override
    @Transactional
    public Role update(Role role) {
        Role original = findByIdNotNull(role.getId());
        original.setDescription(role.getDescription());
        original.setPermissions(role.getPermissions());

        // mongo validation that avoid adding people list to Role table
        if (roleRepository instanceof MongoRepository) {
            original.setPeople(null);
        }
        return roleRepository.save(original);
    }

    @Override
    @Transactional
    public Role delete(String id) {
        Role role = findByIdNotNull(id);
        if (role.getPeople() != null && !role.getPeople().isEmpty()) {
            throw new AppValidationException("There are some people using the Role '" + role.getName() + "'.");
        }

        roleRepository.delete(role);
        role.setPermissions(null);
        return role;
    }

    @Override
    public Page<Role> page(PageDataRequest pageDataRequest) {
        return roleRepository.page(pageDataRequest);
    }

    /**
     * Load related data (only used with mongo or jpa which doesn't implement bi-directional relationship)
     *
     * @param role role where related data is loaded
     */
    private void loadRelatedData(Role role) {
        if (role == null) {
            return;
        }

        if (role.getPeople() == null) {
            role.setPeople(personRepository.findByRoles(role));
        }
    }
}