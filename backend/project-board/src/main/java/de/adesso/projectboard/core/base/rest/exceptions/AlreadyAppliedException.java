package de.adesso.projectboard.core.base.rest.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Already applied for this project!")
public class AlreadyAppliedException extends RuntimeException {

}
