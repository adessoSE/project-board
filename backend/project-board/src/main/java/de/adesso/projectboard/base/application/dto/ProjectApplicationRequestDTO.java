package de.adesso.projectboard.base.application.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.adesso.projectboard.base.application.rest.ApplicationController;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * The DTO send by the user to apply for a project.
 *
 * @see ApplicationController
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectApplicationRequestDTO implements Serializable {

    @NotNull
    private String projectId;

    @Size(max = 4096)
    private String comment = "";

}
