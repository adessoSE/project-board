package de.adesso.projectboard.core.base.rest.application;

import de.adesso.projectboard.core.base.rest.application.persistence.ProjectApplicationLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/application")
public class ProjectApplicationController {

    private final ProjectApplicationHandler applicationHandler;

    private final ProjectApplicationLogRepository logRepository;

    @Autowired
    public ProjectApplicationController(ProjectApplicationHandler applicationHandler, ProjectApplicationLogRepository logRepository) {
        this.applicationHandler = applicationHandler;
        this.logRepository = logRepository;
    }

    @PostMapping(path= "/", consumes = "application/json", produces = "application/json")
    public void applyForProject(@RequestBody ProjectApplication projectApplication) {
        logRepository.save(applicationHandler.onApplicationReceived(projectApplication));
    }

}
