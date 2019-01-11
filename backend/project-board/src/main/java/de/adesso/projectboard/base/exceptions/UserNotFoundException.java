package de.adesso.projectboard.base.exceptions;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "User not found!")
@NoArgsConstructor
public class UserNotFoundException extends NoSuchEntityException {

    public UserNotFoundException(String userId) {
        super("User", userId);
    }

}
