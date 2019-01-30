package de.adesso.projectboard.base.application.projection;

import org.springframework.beans.factory.annotation.Value;

public interface FullApplicationProjection extends BaseApplicationProjection {

    @Value("#{@projectProjectionFactory.createProjection(target.project, T(de.adesso.projectboard.base.project.projection.FullProjectProjection))}")
    FullApplicationProjection getProject();

}
