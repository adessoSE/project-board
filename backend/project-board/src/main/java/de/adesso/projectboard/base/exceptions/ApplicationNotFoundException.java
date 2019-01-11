package de.adesso.projectboard.base.exceptions;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Application not found!")
@NoArgsConstructor
public class ApplicationNotFoundException extends NoSuchEntityException {

    public ApplicationNotFoundException(long applicationId) {
        super("Application", applicationId);
    }

}
