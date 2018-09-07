package de.adesso.projectboard.core.rest.useraccess;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserAccessInfoClientDTO {

    @NotEmpty
    private String userId;

    @NotNull
    @Future
    private LocalDateTime accessEnd;

}
