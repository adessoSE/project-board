package de.adesso.projectboard.core.base.rest.user.bookmark;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookmarkClientDTO {

    private long projectId;

}
