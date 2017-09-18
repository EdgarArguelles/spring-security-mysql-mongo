package app.controllers;

import app.factories.PageFactory;
import app.factories.ResponseFactory;
import app.models.Permission;
import app.pojos.pages.PageDataRequest;
import app.services.PermissionService;
import app.services.PresentationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/permissions")
public class PermissionCtrl {

    @Autowired
    private ResponseFactory responseFactory;

    @Autowired
    private PresentationService presentationService;

    @Autowired
    private PageFactory pageFactory;

    @Autowired
    private PermissionService permissionService;

    @RequestMapping(method = RequestMethod.GET)
    @PreAuthorize("hasRole('VIEW_ROLES')")
    public ResponseEntity list(@RequestParam(value = "complete", defaultValue = "false") boolean shouldLoadComplete,
                               @RequestParam(value = "all_relations", defaultValue = "false") boolean shouldLoadAllRelations) {
        List<Permission> permissions = permissionService.findAll();
        presentationService.prepare(permissions, shouldLoadComplete, shouldLoadAllRelations);
        return responseFactory.success(permissions);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @PreAuthorize("hasRole('VIEW_ROLES')")
    public ResponseEntity get(@PathVariable String id, @RequestParam(value = "complete", defaultValue = "false") boolean shouldLoadComplete,
                              @RequestParam(value = "all_relations", defaultValue = "false") boolean shouldLoadAllRelations) {
        Permission permission = permissionService.findById(id);
        presentationService.prepare(permission, shouldLoadComplete, shouldLoadAllRelations);
        return responseFactory.successNotNull(permission);
    }

    @RequestMapping(method = RequestMethod.POST)
    @PreAuthorize("hasRole('CREATE_ROLES')")
    public ResponseEntity create(@Valid @RequestBody Permission p) {
        Permission permission = permissionService.save(p);
        return responseFactory.successNotNull(permission);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @PreAuthorize("hasRole('CREATE_ROLES')")
    public ResponseEntity edit(@PathVariable String id, @Valid @RequestBody Permission p) {
        p.setId(id);
        Permission permission = permissionService.update(p);
        return responseFactory.successNotNull(permission);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @PreAuthorize("hasRole('REMOVE_ROLES')")
    public ResponseEntity delete(@PathVariable String id) {
        Permission permission = permissionService.delete(id);
        return responseFactory.successNotNull(permission);
    }

    @RequestMapping(value = "/{id}/roles", method = RequestMethod.GET)
    @PreAuthorize("hasRole('VIEW_ROLES')")
    public ResponseEntity getRoles(@PathVariable String id, @RequestParam(value = "complete", defaultValue = "false") boolean shouldLoadComplete,
                                   @RequestParam(value = "all_relations", defaultValue = "false") boolean shouldLoadAllRelations) {
        Permission permission = permissionService.findByIdNotNull(id);
        presentationService.prepare(permission.getRoles(), shouldLoadComplete, shouldLoadAllRelations);
        return responseFactory.successNotNull(permission.getRoles());
    }

    @RequestMapping(value = "/Page", method = RequestMethod.POST)
    @PreAuthorize("hasRole('VIEW_ROLES')")
    public ResponseEntity page(@Valid @RequestBody PageDataRequest pageDataRequest,
                               @RequestParam(value = "complete", defaultValue = "false") boolean shouldLoadComplete,
                               @RequestParam(value = "all_relations", defaultValue = "false") boolean shouldLoadAllRelations) {
        Page<Permission> permissions = permissionService.page(pageDataRequest);
        presentationService.prepare(permissions.getContent(), shouldLoadComplete, shouldLoadAllRelations);
        return responseFactory.success(permissions.getContent(), pageFactory.pageResponse(permissions, pageDataRequest));
    }
}