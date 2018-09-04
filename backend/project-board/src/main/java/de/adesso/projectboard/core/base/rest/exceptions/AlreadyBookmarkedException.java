package de.adesso.projectboard.core.base.rest.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Project already bookmarked!")
public class AlreadyBookmarkedException extends RuntimeException {

}
