package de.adesso.projectboard.core.base.rest.user.useraccess.dto;

import de.adesso.projectboard.core.base.rest.user.UserAccessController;
import de.adesso.projectboard.core.base.rest.user.useraccess.persistence.UserAccessInfo;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * The DTO of a {@link UserAccessInfo} object sent back to the user.
 *
 * @see UserAccessController
 */
@Data
public class UserAccessInfoResponseDTO implements Serializable {

    private boolean hasAccess;

    private LocalDateTime accessStart;

    private LocalDateTime accessEnd;

    /**
     *
     * @param info
     *          The {@link UserAccessInfo} object.
     *
     * @return
     *          A new {@link UserAccessInfoResponseDTO} with the information of
     *          the given {@link UserAccessInfo} object.
     *
     */
    public static UserAccessInfoResponseDTO fromAccessInfo(UserAccessInfo info) {
        UserAccessInfoResponseDTO responseDTO = new UserAccessInfoResponseDTO();

        responseDTO.setAccessStart(info.getAccessStart());
        responseDTO.setAccessEnd(info.getAccessEnd());

        boolean hasAccess = LocalDateTime.now().isBefore(info.getAccessEnd());
        responseDTO.setHasAccess(hasAccess);

        return responseDTO;
    }

    /**
     *
     * @return
     *          A new {@link UserAccessInfoResponseDTO} with {@link #hasAccess}
     *          set to {@code false}.
     */
    public static UserAccessInfoResponseDTO noAccess() {
        UserAccessInfoResponseDTO responseDTO = new UserAccessInfoResponseDTO();

        responseDTO.setHasAccess(false);

        return responseDTO;
    }

}
