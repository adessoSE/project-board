package de.adesso.projectboard.base.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "The project's status prevents applications!")
public class ProjectStatusPreventsApplicationException extends RuntimeException {

}
