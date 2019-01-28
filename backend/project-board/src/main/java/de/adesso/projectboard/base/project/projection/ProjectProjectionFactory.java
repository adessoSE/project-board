package de.adesso.projectboard.base.project.projection;

import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.service.UserAuthService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProjectProjectionFactory {

    private final ProjectionFactory projectionFactory;

    private final UserAuthService userAuthService;

    @Autowired
    public ProjectProjectionFactory(ProjectionFactory projectionFactory, UserAuthService userAuthService) {
        this.projectionFactory = projectionFactory;
        this.userAuthService = userAuthService;
    }

    /**
     *
     * @param projects
     *          The projects to create projections of, not null.
     *
     * @param projectionType
     *          The class instance of the projection, not null.
     *
     * @param <T>
     *          The projection type.
     *
     * @return
     *          A list projections of the given projects of the given {@code projectionType}.
     */
    public <T> List<T> createProjections(@NonNull Collection<Project> projects, @NonNull Class<T> projectionType) {
        return projects.stream()
                .map(project -> projectionFactory.createProjection(projectionType, project))
                .collect(Collectors.toList());
    }

    /**
     *
     * @param project
     *          The project to create a projection of, not null.
     *
     * @param projectionType
     *          The class instance of the projection, not null.
     *
     * @param <T>
     *          The projection type.
     *
     * @return
     *          A projection of the given {@code project} of the given {@code projectionType}.
     *
     * @see #createProjections(Collection, Class)
     */
    public <T> T createProjection(@NonNull Project project, @NonNull Class<T> projectionType) {
        return projectionFactory.createProjection(projectionType, project);
    }

    /**
     *
     * @param projects
     *          The projects to create projections of, not null.
     *
     * @return
     *          A list of {@link FullProjectProjection} in case the given {@code user} has access to all
     *          project fields or a {@link ReducedProjectProjection} otherwise.
     *
     * @see #createProjectionsForUser(Collection, User)
     */
    public List<? extends ReducedProjectProjection> createProjectionsForUser(@NonNull Collection<Project> projects, @NonNull User user) {
        var projectionType = userAuthService.userHasAccessToAllProjectFields(user) ?
                FullProjectProjection.class :
                ReducedProjectProjection.class;

        return createProjections(projects, projectionType);
    }

    /**
     *
     * @param project
     *          The project to create a projection of, not null.
     *
     * @return
     *          A {@link FullProjectProjection} in case the given {@code user} has access to all
     *          project fields or a {@link ReducedProjectProjection} otherwise.
     *
     * @see #createProjectionForUser(Project, User)
     */
    public ReducedProjectProjection createProjectionForUser(@NonNull Project project, @NonNull User user) {
        var projectionType = userAuthService.userHasAccessToAllProjectFields(user) ?
                FullProjectProjection.class :
                ReducedProjectProjection.class;

        return createProjection(project, projectionType);
    }

}
