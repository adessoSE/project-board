package de.adesso.projectboard.base.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.persistence.EntityNotFoundException;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Application not found!")
public class ApplicationNotFoundException extends EntityNotFoundException {

}
