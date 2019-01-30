package de.adesso.projectboard.base.access.payload;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Value;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Value
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserAccessPayload {

    @Future
    @NotNull
    LocalDateTime accessEnd;

}
