package de.adesso.projectboard.core.rest.useraccess.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * The DTO of a {@link de.adesso.projectboard.core.rest.useraccess.persistence.UserAccessInfo}
 * object send by the user.
 *
 * @see de.adesso.projectboard.core.rest.useraccess.persistence.UserAccessInfo
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserAccessInfoRequestDTO {

    @NotNull
    private LocalDateTime accessEnd;

}
