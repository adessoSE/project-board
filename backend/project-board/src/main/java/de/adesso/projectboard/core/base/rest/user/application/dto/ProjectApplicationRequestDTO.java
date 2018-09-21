package de.adesso.projectboard.core.base.rest.user.application.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.adesso.projectboard.core.base.rest.user.ApplicationController;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * The DTO send by the user to apply for a project.
 *
 * @see ApplicationController
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectApplicationRequestDTO implements Serializable {

    @NotNull
    private Long projectId;

    private String comment;

    /**
     * Constructs a new instance. The {@link #comment} is initialized
     * with a empty string.
     */
    public ProjectApplicationRequestDTO() {
        this.comment = "";
    }

}
