package de.adesso.projectboard.base.application.payload;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.adesso.projectboard.base.application.persistence.ProjectApplication;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectApplicationStatePayload {

    @NotNull
    private ProjectApplication.State state;
}
