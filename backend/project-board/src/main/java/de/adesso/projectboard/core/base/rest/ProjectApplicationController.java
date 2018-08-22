package de.adesso.projectboard.core.base.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/application")
public class ProjectApplicationController {

    private final ProjectApplicationHandler applicationHandler;

    @Autowired
    public ProjectApplicationController(ProjectApplicationHandler applicationHandler) {
        this.applicationHandler = applicationHandler;
    }

    @PostMapping(path= "/", consumes = "application/json", produces = "application/json")
    public void applyForProject(@RequestBody ProjectApplication projectApplication) {
        applicationHandler.onApplicationReceived(projectApplication);
    }

}
