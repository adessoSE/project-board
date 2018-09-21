package de.adesso.projectboard.core.base.rest.user.useraccess.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.adesso.projectboard.core.base.rest.user.useraccess.persistence.UserAccessInfo;
import de.adesso.projectboard.core.base.rest.user.UserAccessController;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * The DTO of a {@link UserAccessInfo} object send by the user..
 *
 * @see UserAccessController
 * @see UserAccessInfo
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserAccessInfoRequestDTO {

    @NotNull
    private LocalDateTime accessEnd;

}
