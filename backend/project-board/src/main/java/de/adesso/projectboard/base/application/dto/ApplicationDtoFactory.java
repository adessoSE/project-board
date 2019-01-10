package de.adesso.projectboard.base.application.dto;

import de.adesso.projectboard.base.application.persistence.ProjectApplication;
import de.adesso.projectboard.base.user.dto.UserDtoFactory;
import de.adesso.projectboard.base.user.persistence.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Factory to create DTOs for {@link ProjectApplication}s.
 *
 * @see UserDtoFactory
 */
@Component
public class ApplicationDtoFactory {

    private final UserDtoFactory userDtoFactory;

    @Autowired
    public ApplicationDtoFactory(UserDtoFactory userDtoFactory) {
        this.userDtoFactory = userDtoFactory;
    }

    /**
     *
     * @param application
     *          The {@link ProjectApplication} to create a DTO for.
     *
     * @return
     *          The created {@link ProjectApplicationResponseDTO DTO}.
     */
    public ProjectApplicationResponseDTO createDto(ProjectApplication application) {
        User user = application.getUser();

        return new ProjectApplicationResponseDTO()
                .setId(application.getId())
                .setComment(application.getComment())
                .setDate(application.getApplicationDate())
                .setProject(application.getProject())
                .setUser(userDtoFactory.createDto(user, false));
    }

}
