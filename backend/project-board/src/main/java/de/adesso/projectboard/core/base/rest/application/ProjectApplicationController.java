package de.adesso.projectboard.core.base.rest.application;

import de.adesso.projectboard.core.base.rest.application.persistence.ProjectApplicationLog;
import de.adesso.projectboard.core.base.rest.application.persistence.ProjectApplicationLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@PreAuthorize("hasPermissionToApply()")
@RestController
@RequestMapping("/projects/applications")
public class ProjectApplicationController {

    private final ProjectApplicationHandler applicationHandler;

    private final ProjectApplicationLogRepository logRepository;

    @Autowired
    public ProjectApplicationController(ProjectApplicationHandler applicationHandler, ProjectApplicationLogRepository logRepository) {
        this.applicationHandler = applicationHandler;
        this.logRepository = logRepository;
    }

    @PostMapping(path = "/", consumes = "application/json", produces = "application/json")
    public ProjectApplicationLog applyForProject(@Valid @RequestBody ProjectApplication projectApplication) {
        return logRepository.save(applicationHandler.onApplicationReceived(projectApplication));
    }

}
