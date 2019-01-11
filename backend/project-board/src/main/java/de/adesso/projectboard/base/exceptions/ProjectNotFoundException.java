package de.adesso.projectboard.base.exceptions;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Project not found!")
@NoArgsConstructor
public class ProjectNotFoundException extends NoSuchEntityException {

    public ProjectNotFoundException(long projectId) {
        super("Project", projectId);
    }

}
