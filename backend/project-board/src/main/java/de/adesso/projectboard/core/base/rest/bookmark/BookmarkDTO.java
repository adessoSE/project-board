package de.adesso.projectboard.core.base.rest.bookmark;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookmarkDTO {

    private long projectId;

}
