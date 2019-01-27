package de.adesso.projectboard.base.project.rest;

import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.project.projection.FullProjectProjection;
import de.adesso.projectboard.base.project.projection.ReducedProjectProjection;
import de.adesso.projectboard.base.user.service.UserAuthService;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.http.ResponseEntity;

import java.util.Collection;
import java.util.stream.Collectors;

public abstract class BaseProjectController {

    private final ProjectionFactory projectionFactory;

    private final UserAuthService userAuthService;

    protected BaseProjectController(ProjectionFactory projectionFactory, UserAuthService userAuthService) {
        this.projectionFactory = projectionFactory;
        this.userAuthService = userAuthService;
    }

    public abstract ResponseEntity<? extends ReducedProjectProjection> getById(String projectId);

    protected Collection<? extends ReducedProjectProjection> getProjectionOfProjects(@NonNull Collection<Project> projects) {
        var projectionType = getProjectionTypeForAuthenticatedUser();

        return projects.stream()
                .map(project -> projectionFactory.createProjection(projectionType, project))
                .collect(Collectors.toList());
    }

    protected Page<? extends ReducedProjectProjection> mapToProjection(@NonNull Page<Project> projectPage) {
        var projectionType = getProjectionTypeForAuthenticatedUser();

        return projectPage
                .map(project -> projectionFactory.createProjection(projectionType, project));
    }

    Class<? extends ReducedProjectProjection> getProjectionTypeForAuthenticatedUser() {
        var authenticatedUser = userAuthService.getAuthenticatedUser();

        return userAuthService.userHasAccessToAllProjectFields(authenticatedUser)
                ? FullProjectProjection.class : ReducedProjectProjection.class;
    }

}
