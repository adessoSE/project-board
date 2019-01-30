package de.adesso.projectboard.base.application.payload;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Value;

import javax.validation.constraints.NotEmpty;

@Value
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectApplicationPayload {

    @NotEmpty
    String projectId;

    String comment;

}
