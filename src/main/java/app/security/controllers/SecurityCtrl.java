package app.security.controllers;

import app.factories.ResponseFactory;
import app.security.pojos.AccountCredentials;
import app.security.pojos.LoggedUser;
import app.security.services.SecurityService;
import app.security.services.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class SecurityCtrl {

    @Autowired
    private SecurityService securityService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ResponseFactory responseFactory;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity login(@Valid @RequestBody AccountCredentials c) throws IOException {
        LoggedUser loggedUser = securityService.authenticate(c);
        String token = tokenService.createToken(loggedUser);
        loggedUser = tokenService.getLoggedUser(token);

        Map<String, Object> map = new HashMap<>();
        map.put("loggedUser", loggedUser);
        map.put("token", token);
        return new ResponseEntity(map, HttpStatus.OK);
    }

    @RequestMapping(value = "/change_role/{id}", method = RequestMethod.POST)
    public ResponseEntity changeRole(@PathVariable String id) throws IOException {
        LoggedUser loggedUser = securityService.changeRole(id);
        String token = tokenService.createToken(loggedUser);
        loggedUser = tokenService.getLoggedUser(token);

        Map<String, Object> map = new HashMap<>();
        map.put("loggedUser", loggedUser);
        map.put("token", token);
        return new ResponseEntity(map, HttpStatus.OK);
    }

    @RequestMapping(value = "/ping", method = RequestMethod.GET)
    public ResponseEntity ping() {
        return responseFactory.success(securityService.getLoggedUser());
    }
}