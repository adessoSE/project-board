package de.adesso.projectboard.core.base.rest.user.application.dto;

import de.adesso.projectboard.core.base.rest.project.persistence.AbstractProject;
import de.adesso.projectboard.core.base.rest.user.dto.UserResponseDTO;
import de.adesso.projectboard.core.base.rest.user.application.persistence.ProjectApplication;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * The DTO of a {@link de.adesso.projectboard.core.base.rest.user.application.persistence.ProjectApplication}
 * send by the REST endpoint back to the user.
 */
@Data
public class ProjectApplicationResponseDTO implements Serializable {

    private long id;

    private UserResponseDTO user;

    private AbstractProject project;

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
    public static ProjectApplicationResponseDTO fromApplication(ProjectApplication application) {
        ProjectApplicationResponseDTO dto = new ProjectApplicationResponseDTO();

        dto.setId(application.getId());
        dto.setUser(UserResponseDTO.fromUser(application.getUser()));
        dto.setProject(application.getProject());
        dto.setComment(application.getComment());
        dto.setDate(application.getApplicationDate());

        return dto;
    }

}
