package app.controllers;

import app.factories.PageFactory;
import app.factories.ResponseFactory;
import app.models.Authentication;
import app.pojos.pages.PageDataRequest;
import app.services.AuthenticationService;
import app.services.PresentationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/authentications")
public class AuthenticationCtrl {

    @Autowired
    private ResponseFactory responseFactory;

    @Autowired
    private PresentationService presentationService;

    @Autowired
    private PageFactory pageFactory;

    @Autowired
    private AuthenticationService authenticationService;

    @RequestMapping(method = RequestMethod.GET)
    @PreAuthorize("hasRole('VIEW_USERS')")
    public ResponseEntity list(@RequestParam(value = "complete", defaultValue = "false") boolean shouldLoadComplete,
                               @RequestParam(value = "all_relations", defaultValue = "false") boolean shouldLoadAllRelations) {
        List<Authentication> authentications = authenticationService.findAll();
        presentationService.prepare(authentications, shouldLoadComplete, shouldLoadAllRelations);
        return responseFactory.success(authentications);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @PreAuthorize("hasRole('VIEW_USERS')")
    public ResponseEntity get(@PathVariable String id, @RequestParam(value = "complete", defaultValue = "false") boolean shouldLoadComplete,
                              @RequestParam(value = "all_relations", defaultValue = "false") boolean shouldLoadAllRelations) {
        Authentication authentication = authenticationService.findById(id);
        presentationService.prepare(authentication, shouldLoadComplete, shouldLoadAllRelations);
        return responseFactory.successNotNull(authentication);
    }

    @RequestMapping(method = RequestMethod.POST)
    @PreAuthorize("hasRole('CREATE_USERS')")
    public ResponseEntity create(@Valid @RequestBody Authentication a) {
        Authentication authentication = authenticationService.save(a);
        if (authentication != null) {
            authentication.cleanAuthData();
        }
        return responseFactory.successNotNull(authentication);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @PreAuthorize("hasRole('CREATE_USERS')")
    public ResponseEntity edit(@PathVariable String id, @Valid @RequestBody Authentication a) {
        a.setId(id);
        Authentication authentication = authenticationService.update(a);
        if (authentication != null) {
            authentication.cleanAuthData();
        }
        return responseFactory.successNotNull(authentication);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @PreAuthorize("hasRole('REMOVE_USERS')")
    public ResponseEntity delete(@PathVariable String id) {
        Authentication authentication = authenticationService.delete(id);
        if (authentication != null) {
            authentication.cleanAuthData();
        }
        return responseFactory.successNotNull(authentication);
    }

    @RequestMapping(value = "/Page", method = RequestMethod.POST)
    @PreAuthorize("hasRole('VIEW_USERS')")
    public ResponseEntity page(@Valid @RequestBody PageDataRequest pageDataRequest,
                               @RequestParam(value = "complete", defaultValue = "false") boolean shouldLoadComplete,
                               @RequestParam(value = "all_relations", defaultValue = "false") boolean shouldLoadAllRelations) {
        Page<Authentication> authentications = authenticationService.page(pageDataRequest);
        presentationService.prepare(authentications.getContent(), shouldLoadComplete, shouldLoadAllRelations);
        return responseFactory.success(authentications.getContent(), pageFactory.pageResponse(authentications, pageDataRequest));
    }
}