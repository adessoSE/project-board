package de.adesso.projectboard.base.projection;

import de.adesso.projectboard.base.user.service.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BaseProjectionFactory {

    private final ProjectionFactory projectionFactory;

    private final UserAuthService userAuthService;

    @Autowired
    public BaseProjectionFactory(ProjectionFactory projectionFactory, UserAuthService userAuthService) {
        this.projectionFactory = projectionFactory;
        this.userAuthService = userAuthService;
    }

    public <T> T createProjection(@NotNull Object object, @NotNull Class<T> projectionType) {
        return projectionFactory.createProjection(projectionType, object);
    }

    public <T> List<T> createProjections(@NotNull Collection<?> objects, @NotNull Class<T> projectionType) {
        return objects.stream()
                .map(obj -> projectionFactory.createProjection(projectionType, obj))
                .collect(Collectors.toList());
    }

    public Object createProjectionForAuthenticatedUser(@NotNull Object object, @NotNull Class<?> normalProjectionType, @NotNull Class<?> managerProjectionType) {
        var usedProjectionType = getProjectionType(normalProjectionType, managerProjectionType);

        return projectionFactory.createProjection(usedProjectionType, object);
    }

    public List<?> createProjectionsForAuthenticatedUser(@NotNull Collection<?> objects, @NotNull Class<?> normalProjectionType, @NotNull Class<?> managerProjectionType) {
        var usedProjectionType = getProjectionType(normalProjectionType, managerProjectionType);

        return objects.parallelStream()
                .map(obj -> projectionFactory.createProjection(usedProjectionType, obj))
                .collect(Collectors.toList());
    }

    public Page<?> createProjectionsForAuthenticatedUser(@NotNull Page<?> page, @NotNull Class<?> normalProjectionType, @NotNull Class<?> managerProjectionType) {
        var usedProjectionType = getProjectionType(normalProjectionType, managerProjectionType);

        return page.map(pageElement -> projectionFactory.createProjection(usedProjectionType, pageElement));
    }

    Class<?> getProjectionType(Class<?> normalProjectionType, Class<?> managerProjectionType) {
        var authenticatedUser = userAuthService.getAuthenticatedUser();

        return userAuthService.userIsEffectivelyAManager(authenticatedUser) ?
                managerProjectionType :
                normalProjectionType;
    }

}
