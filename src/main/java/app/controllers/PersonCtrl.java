package app.controllers;

import app.factories.PageFactory;
import app.factories.ResponseFactory;
import app.models.Person;
import app.pojos.pages.PageDataRequest;
import app.services.PersonService;
import app.services.PresentationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/people")
public class PersonCtrl {

    @Autowired
    private ResponseFactory responseFactory;

    @Autowired
    private PresentationService presentationService;

    @Autowired
    private PageFactory pageFactory;

    @Autowired
    private PersonService personService;

    @RequestMapping(method = RequestMethod.GET)
    @PreAuthorize("hasRole('VIEW_USERS')")
    public ResponseEntity list(@RequestParam(value = "complete", defaultValue = "false") boolean shouldLoadComplete,
                               @RequestParam(value = "all_relations", defaultValue = "false") boolean shouldLoadAllRelations) {
        List<Person> people = personService.findAll();
        presentationService.prepare(people, shouldLoadComplete, shouldLoadAllRelations);
        return responseFactory.success(people);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @PreAuthorize("hasRole('VIEW_USERS')")
    public ResponseEntity get(@PathVariable String id, @RequestParam(value = "complete", defaultValue = "false") boolean shouldLoadComplete,
                              @RequestParam(value = "all_relations", defaultValue = "false") boolean shouldLoadAllRelations) {
        Person person = personService.findById(id);
        presentationService.prepare(person, shouldLoadComplete, shouldLoadAllRelations);
        return responseFactory.successNotNull(person);
    }

    @RequestMapping(method = RequestMethod.POST)
    @PreAuthorize("hasRole('CREATE_USERS')")
    public ResponseEntity create(@Valid @RequestBody Person p) {
        Person person = personService.save(p);
        return responseFactory.successNotNull(person);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @PreAuthorize("hasRole('CREATE_USERS')")
    public ResponseEntity edit(@PathVariable String id, @Valid @RequestBody Person p) {
        p.setId(id);
        Person person = personService.update(p);
        return responseFactory.successNotNull(person);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @PreAuthorize("hasRole('REMOVE_USERS')")
    public ResponseEntity delete(@PathVariable String id) {
        Person person = personService.delete(id);
        return responseFactory.successNotNull(person);
    }

    @RequestMapping(value = "/{id}/authentications", method = RequestMethod.GET)
    @PreAuthorize("hasRole('VIEW_USERS')")
    public ResponseEntity getAuthentications(@PathVariable String id, @RequestParam(value = "complete", defaultValue = "false") boolean shouldLoadComplete,
                                             @RequestParam(value = "all_relations", defaultValue = "false") boolean shouldLoadAllRelations) {
        Person person = personService.findByIdNotNull(id);
        presentationService.prepare(person.getAuthentications(), shouldLoadComplete, shouldLoadAllRelations);
        return responseFactory.successNotNull(person.getAuthentications());
    }

    @RequestMapping(value = "/Page", method = RequestMethod.POST)
    @PreAuthorize("hasRole('VIEW_USERS')")
    public ResponseEntity page(@Valid @RequestBody PageDataRequest pageDataRequest,
                               @RequestParam(value = "complete", defaultValue = "false") boolean shouldLoadComplete,
                               @RequestParam(value = "all_relations", defaultValue = "false") boolean shouldLoadAllRelations) {
        Page<Person> people = personService.page(pageDataRequest);
        presentationService.prepare(people.getContent(), shouldLoadComplete, shouldLoadAllRelations);
        return responseFactory.success(people.getContent(), pageFactory.pageResponse(people, pageDataRequest));
    }
}