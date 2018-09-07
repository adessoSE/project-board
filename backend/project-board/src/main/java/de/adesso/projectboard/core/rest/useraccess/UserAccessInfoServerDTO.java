package de.adesso.projectboard.core.rest.useraccess;

import de.adesso.projectboard.core.base.rest.user.UserDTO;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class UserAccessInfoServerDTO implements Serializable {

    // TODO: implement

    private UserDTO user;

    private boolean hasAccess;

    private LocalDateTime accessStart;

    private LocalDateTime accessEnd;

}
