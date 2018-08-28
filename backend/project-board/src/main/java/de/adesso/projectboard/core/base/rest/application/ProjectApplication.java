package de.adesso.projectboard.core.base.rest.application;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectApplication {

    private Long projectId;

    private String comment;

}
