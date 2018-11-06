package de.adesso.projectboard.base.application.dto;

import de.adesso.projectboard.base.application.persistence.ProjectApplication;
import de.adesso.projectboard.base.application.rest.ApplicationController;
import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.user.dto.UserResponseDTO;
import de.adesso.projectboard.base.user.persistence.data.UserData;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * The DTO of a {@link ProjectApplication} send by the REST endpoint back to the user.
 *
 * @see ApplicationController
 */
@Data
public class ProjectApplicationResponseDTO implements Serializable {

    private long id;

    private UserResponseDTO user;

    private Project project;

    private String comment;

    private LocalDateTime date;

    /**
     *
     * @param application
     *          The {@link ProjectApplication} to convert.
     *
     * @return
     *          The DTO.
     */
    public static ProjectApplicationResponseDTO fromApplication(ProjectApplication application, UserData userData, boolean isManager) {
        ProjectApplicationResponseDTO dto = new ProjectApplicationResponseDTO();

        dto.setId(application.getId());
        dto.setUser(UserResponseDTO.fromUserData(userData, isManager));
        dto.setProject(application.getProject());
        dto.setComment(application.getComment());
        dto.setDate(application.getApplicationDate());

        return dto;
    }

}
