package de.adesso.projectboard.base.application.projection;

import de.adesso.projectboard.base.user.projection.NameAndIdProjection;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;

public interface BaseApplicationProjection {

    Long getId();

    @Value("#{target.applicationDate}")
    LocalDateTime getDate();

    String getComment();

    Boolean getOffered();

    @Value("#{@userProjectionFactory.createProjection(target.user, T(de.adesso.projectboard.base.user.projection.NameAndIdProjection))}")
    NameAndIdProjection getUser();
}
