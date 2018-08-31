package de.adesso.projectboard.core.base.rest.application;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectApplication {

    @NotNull
    private Long projectId;

    private String comment;

}
