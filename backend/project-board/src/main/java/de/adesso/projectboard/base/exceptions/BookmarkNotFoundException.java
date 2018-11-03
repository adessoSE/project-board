package de.adesso.projectboard.base.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.persistence.EntityNotFoundException;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Bookmark not found!")
public class BookmarkNotFoundException extends EntityNotFoundException {

}