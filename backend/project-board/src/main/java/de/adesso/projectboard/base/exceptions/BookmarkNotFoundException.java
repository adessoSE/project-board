package de.adesso.projectboard.base.exceptions;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Bookmark not found!")
@NoArgsConstructor
public class BookmarkNotFoundException extends NoSuchEntityException {

    public BookmarkNotFoundException(long bookmarkId) {
        super("Bookmark", bookmarkId);
    }

}
