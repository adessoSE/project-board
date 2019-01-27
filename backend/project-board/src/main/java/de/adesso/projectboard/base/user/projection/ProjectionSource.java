package de.adesso.projectboard.base.user.projection;

import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.persistence.data.UserData;
import lombok.Value;

@Value
public class ProjectionSource {

    User user;

    UserData data;

    boolean manager;

}
