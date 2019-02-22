package de.adesso.projectboard.base.user.projection;

import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.persistence.data.UserData;
import de.adesso.projectboard.base.user.service.UserService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserProjectionFactory {

    private final UserService userService;

    private final ProjectionFactory projectionFactory;

    @Autowired
    public UserProjectionFactory(UserService userService, ProjectionFactory projectionFactory) {
        this.userService = userService;
        this.projectionFactory = projectionFactory;
    }

    /**
     *
     * @param userData
     *          A collection of {@code UserData} instances to create projections of, not null.
     *
     * @param projectionType
     *          The class instance of the projection, not null.
     *
     * @param <T>
     *          The type of the projection.
     *
     * @return
     *          A list of projections of the given {@code userData}.
     */
    public <T> List<T> createProjections(@NonNull Collection<UserData> userData, @NonNull Class<T> projectionType) {
        var users = userData.stream()
                .map(UserData::getUser)
                .collect(Collectors.toSet());
        var userManagerMap = userService.usersAreManagers(users);

        return userData.stream()
                .map(data -> {
                    var user = data.getUser();
                    var isManager = userManagerMap.get(user);

                    return new UserProjectionSource(user, data, isManager);
                })
                .map(projectionSource -> projectionFactory.createProjection(projectionType, projectionSource))
                .collect(Collectors.toList());
    }

    /**
     *
     * @param user
     *          The user to create a projection of, not null.
     *
     * @param projectionType
     *          The class instance of the projection, not null.
     *
     * @param <T>
     *          The type of the projection.
     *
     * @return
     *          A projection of the given {@code user}.
     */
    public <T> T createProjection(@NonNull User user, @NonNull Class<T> projectionType) {
        var data = userService.getUserData(user);
        var manager = userService.userIsManager(user);

        return projectionFactory.createProjection(projectionType, new UserProjectionSource(user, data, manager));
    }

}
