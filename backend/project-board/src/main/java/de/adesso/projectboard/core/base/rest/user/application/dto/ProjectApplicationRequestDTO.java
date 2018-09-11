package de.adesso.projectboard.core.base.rest.user.application.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * The DTO send by the user to apply for a project.
 *
 * @see de.adesso.projectboard.core.base.rest.user.UserController
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectApplicationRequestDTO {

    @NotNull
    private Long projectId;

    private String comment;

}
