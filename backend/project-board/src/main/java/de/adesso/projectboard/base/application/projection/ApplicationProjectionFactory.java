package de.adesso.projectboard.base.application.projection;

import de.adesso.projectboard.base.application.persistence.ProjectApplication;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ApplicationProjectionFactory {

    private final ProjectionFactory projectionFactory;

    @Autowired
    public ApplicationProjectionFactory(ProjectionFactory projectionFactory) {
        this.projectionFactory = projectionFactory;
    }

    /**
     *
     * @param applications
     *          The applications to create projections of, not null.
     *
     * @param projectionType
     *          The class instance of the projection, not null.
     *
     * @param <T>
     *          The type of the projection.
     *
     * @return
     *          A list of projections of the given {@code applications} of the given {@code projectionType}.
     */
    public <T> List<T> createProjections(@NonNull Collection<ProjectApplication> applications, @NonNull Class<T> projectionType) {
        return applications.stream()
                .map(application -> projectionFactory.createProjection(projectionType, application))
                .collect(Collectors.toList());
    }

    /**
     *
     * @param application
     *          The application to create a projection of, not null.
     *
     * @param projectionType
     *          The class instance of the projection, not null.
     *
     * @param <T>
     *          The type of the projection.
     *
     * @return
     *          A projection of the given {@code application} of the given {@code projectionType}
     */
    public <T> T createProjection(@NonNull ProjectApplication application, @NonNull Class<T> projectionType) {
        return projectionFactory.createProjection(projectionType, application);
    }

}
