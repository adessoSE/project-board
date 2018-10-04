package de.adesso.projectboard.core.base.rest.user.useraccess.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.adesso.projectboard.core.base.rest.user.useraccess.persistence.UserAccessInfo;
import de.adesso.projectboard.core.base.rest.user.UserAccessController;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * The DTO of a {@link UserAccessInfo} object send by the user..
 *
 * @see UserAccessController
 * @see UserAccessInfo
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserAccessInfoRequestDTO {

    @Future
    @NotNull
    private LocalDateTime accessEnd;

}
