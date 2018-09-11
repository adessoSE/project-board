package de.adesso.projectboard.core.rest.useraccess.dto;

import de.adesso.projectboard.core.base.rest.user.dto.UserResponseDTO;
import de.adesso.projectboard.core.base.rest.user.persistence.User;
import de.adesso.projectboard.core.rest.useraccess.persistence.UserAccessInfo;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * The DTO of a {@link de.adesso.projectboard.core.rest.useraccess.persistence.UserAccessInfo}
 * object send back to the user.
 *
 * @see de.adesso.projectboard.core.rest.useraccess.UserAccessController
 */
@Data
public class UserAccessInfoResponseDTO implements Serializable {

    private UserResponseDTO user;

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

        responseDTO.setUser(UserResponseDTO.fromUser(info.getUser()));
        responseDTO.setAccessStart(info.getAccessStart());
        responseDTO.setAccessEnd(info.getAccessEnd());

        boolean hasAccess = LocalDateTime.now().isBefore(info.getAccessEnd());
        responseDTO.setHasAccess(hasAccess);

        return responseDTO;
    }

    /**
     *
     * @param user
     *          The {@link User} object.
     *
     * @return
     *          A new {@link UserAccessInfoResponseDTO} with {@link #hasAccess} set to false.
     */
    public static UserAccessInfoResponseDTO noAccess(User user) {
        UserAccessInfoResponseDTO responseDTO = new UserAccessInfoResponseDTO();

        responseDTO.setUser(UserResponseDTO.fromUser(user));
        responseDTO.setHasAccess(false);

        return responseDTO;
    }

}
