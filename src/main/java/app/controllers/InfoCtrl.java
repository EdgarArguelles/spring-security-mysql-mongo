package app.controllers;

import app.factories.ResponseFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/info")
public class InfoCtrl {

    @Autowired
    private ResponseFactory responseFactory;

    @Value("${api-version}")
    private String API_VERSION;

    @RequestMapping(value = "/version", method = RequestMethod.GET)
    public ResponseEntity version() {
        return responseFactory.success(API_VERSION);
    }
}