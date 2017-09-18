package app.controllers;

import app.factories.ResponseFactory;
import app.models.AuthProvider;
import app.services.AuthProviderService;
import app.services.PresentationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/auth_providers")
public class AuthProviderCtrl {

    @Autowired
    private ResponseFactory responseFactory;

    @Autowired
    private PresentationService presentationService;

    @Autowired
    private AuthProviderService authProviderService;

    @RequestMapping(method = RequestMethod.GET)
    @PreAuthorize("hasRole('CREATE_USERS')")
    public ResponseEntity list() {
        List<AuthProvider> providers = authProviderService.findAll();
        presentationService.prepare(providers, false, false);
        return responseFactory.success(providers);
    }
}