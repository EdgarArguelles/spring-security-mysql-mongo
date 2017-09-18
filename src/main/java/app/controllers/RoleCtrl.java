package app.controllers;

import app.factories.PageFactory;
import app.factories.ResponseFactory;
import app.models.Role;
import app.pojos.pages.PageDataRequest;
import app.services.PresentationService;
import app.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/roles")
public class RoleCtrl {

    @Autowired
    private ResponseFactory responseFactory;

    @Autowired
    private PresentationService presentationService;

    @Autowired
    private PageFactory pageFactory;

    @Autowired
    private RoleService roleService;

    @RequestMapping(method = RequestMethod.GET)
    @PreAuthorize("hasRole('VIEW_ROLES')")
    public ResponseEntity list(@RequestParam(value = "complete", defaultValue = "false") boolean shouldLoadComplete,
                               @RequestParam(value = "all_relations", defaultValue = "false") boolean shouldLoadAllRelations) {
        List<Role> roles = roleService.findAll();
        presentationService.prepare(roles, shouldLoadComplete, shouldLoadAllRelations);
        return responseFactory.success(roles);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @PreAuthorize("hasRole('VIEW_ROLES')")
    public ResponseEntity get(@PathVariable String id, @RequestParam(value = "complete", defaultValue = "false") boolean shouldLoadComplete,
                              @RequestParam(value = "all_relations", defaultValue = "false") boolean shouldLoadAllRelations) {
        Role role = roleService.findById(id);
        presentationService.prepare(role, shouldLoadComplete, shouldLoadAllRelations);
        return responseFactory.successNotNull(role);
    }

    @RequestMapping(method = RequestMethod.POST)
    @PreAuthorize("hasRole('CREATE_ROLES')")
    public ResponseEntity create(@Valid @RequestBody Role r) {
        Role role = roleService.save(r);
        return responseFactory.successNotNull(role);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @PreAuthorize("hasRole('CREATE_ROLES')")
    public ResponseEntity edit(@PathVariable String id, @Valid @RequestBody Role r) {
        r.setId(id);
        Role role = roleService.update(r);
        return responseFactory.successNotNull(role);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @PreAuthorize("hasRole('REMOVE_ROLES')")
    public ResponseEntity delete(@PathVariable String id) {
        Role role = roleService.delete(id);
        return responseFactory.successNotNull(role);
    }

    @RequestMapping(value = "/{id}/people", method = RequestMethod.GET)
    @PreAuthorize("hasRole('VIEW_ROLES') and hasRole('VIEW_USERS')")
    public ResponseEntity getPeople(@PathVariable String id, @RequestParam(value = "complete", defaultValue = "false") boolean shouldLoadComplete,
                                    @RequestParam(value = "all_relations", defaultValue = "false") boolean shouldLoadAllRelations) {
        Role role = roleService.findByIdNotNull(id);
        presentationService.prepare(role.getPeople(), shouldLoadComplete, shouldLoadAllRelations);
        return responseFactory.successNotNull(role.getPeople());
    }

    @RequestMapping(value = "/Page", method = RequestMethod.POST)
    @PreAuthorize("hasRole('VIEW_ROLES')")
    public ResponseEntity page(@Valid @RequestBody PageDataRequest pageDataRequest,
                               @RequestParam(value = "complete", defaultValue = "false") boolean shouldLoadComplete,
                               @RequestParam(value = "all_relations", defaultValue = "false") boolean shouldLoadAllRelations) {
        Page<Role> roles = roleService.page(pageDataRequest);
        presentationService.prepare(roles.getContent(), shouldLoadComplete, shouldLoadAllRelations);
        return responseFactory.success(roles.getContent(), pageFactory.pageResponse(roles, pageDataRequest));
    }
}