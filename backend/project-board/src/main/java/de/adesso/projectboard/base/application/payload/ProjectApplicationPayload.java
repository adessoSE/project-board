package de.adesso.projectboard.base.application.payload;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectApplicationPayload {

    @NotEmpty
    private String projectId;

    private String comment;

}
