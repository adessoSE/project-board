package de.adesso.projectboard.base.application.projection;

import de.adesso.projectboard.base.project.projection.ReducedProjectProjection;
import de.adesso.projectboard.base.user.projection.NameAndIdProjection;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;

public interface ApplicationProjection {

    Long getId();

    @Value("#{target.applicationDate}")
    LocalDateTime getDate();

    String getComment();

    @Value("#{@projectProjectionFactory.createProjectionForUser(target.project, target.user)}")
    ReducedProjectProjection getProject();

    @Value("#{@userProjectionFactory.createProjection(target.user, T(de.adesso.projectboard.base.user.projection.NameAndIdProjection))}")
    NameAndIdProjection getUser();

}
