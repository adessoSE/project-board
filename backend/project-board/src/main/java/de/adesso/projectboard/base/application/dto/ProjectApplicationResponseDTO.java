package de.adesso.projectboard.base.application.dto;

import de.adesso.projectboard.base.application.persistence.ProjectApplication;
import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.user.dto.UserResponseDTO;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * The DTO of a {@link ProjectApplication} send by the REST endpoint back to the user.
 *
 * @see ApplicationDtoFactory
 */
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectApplicationResponseDTO implements Serializable {

    private long id;

    private UserResponseDTO user;

    private Project project;

    private String comment;

    private LocalDateTime date;

}
