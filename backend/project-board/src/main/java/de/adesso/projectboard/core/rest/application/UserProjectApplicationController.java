package de.adesso.projectboard.core.rest.application;

import de.adesso.projectboard.core.base.rest.application.persistence.ProjectApplicationLog;
import de.adesso.projectboard.core.base.rest.application.persistence.ProjectApplicationLogRepository;
import de.adesso.projectboard.core.security.KeycloakAuthorizationInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile("adesso-keycloak")
@RestController
@RequestMapping("/projects/applications/my")
public class UserProjectApplicationController {

    private final ProjectApplicationLogRepository repository;

    private final KeycloakAuthorizationInfo authInfo;

    @Autowired
    public UserProjectApplicationController(ProjectApplicationLogRepository repository, KeycloakAuthorizationInfo authInfo) {
        this.repository = repository;
        this.authInfo = authInfo;
    }

    @GetMapping("/")
    public Iterable<ProjectApplicationLog> getCurrentUserApplications() {
        return repository.findAllByUserId(authInfo.getUsername());
    }

}
