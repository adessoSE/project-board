package de.adesso.projectboard.base.access.dto;

import de.adesso.projectboard.base.access.persistence.AccessInfo;
import de.adesso.projectboard.base.access.rest.UserAccessController;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * The DTO of a {@link AccessInfo} object sent back to the user.
 *
 * @see UserAccessController
 */
@Data
public class AccessInfoResponseDTO implements Serializable {

    private boolean hasAccess;

    private LocalDateTime accessStart;

    private LocalDateTime accessEnd;

    /**
     *
     * @param info
     *          The {@link AccessInfo} object.
     *
     * @return
     *          A new {@link AccessInfoResponseDTO} with the information of
     *          the given {@link AccessInfo} object.
     *
     */
    public static AccessInfoResponseDTO fromAccessInfo(AccessInfo info) {
        AccessInfoResponseDTO responseDTO = new AccessInfoResponseDTO();

        responseDTO.setAccessStart(info.getAccessStart());
        responseDTO.setAccessEnd(info.getAccessEnd());

        boolean hasAccess = LocalDateTime.now().isBefore(info.getAccessEnd());
        responseDTO.setHasAccess(hasAccess);

        return responseDTO;
    }

    /**
     *
     * @return
     *          A new {@link AccessInfoResponseDTO} with {@link #hasAccess}
     *          set to {@code false}.
     */
    public static AccessInfoResponseDTO noAccess() {
        AccessInfoResponseDTO responseDTO = new AccessInfoResponseDTO();

        responseDTO.setHasAccess(false);

        return responseDTO;
    }

}
