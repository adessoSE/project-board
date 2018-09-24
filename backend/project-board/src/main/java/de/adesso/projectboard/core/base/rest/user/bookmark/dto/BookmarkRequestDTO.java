package de.adesso.projectboard.core.base.rest.user.bookmark.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookmarkRequestDTO {

    @NotNull
    private String projectId;

}
