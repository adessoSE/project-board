package de.adesso.projectboard.base.application.projection;

import de.adesso.projectboard.base.project.projection.ReducedProjectProjection;
import org.springframework.beans.factory.annotation.Value;

public interface ReducedApplicationProjection extends BaseApplicationProjection {

    @Value("#{@baseProjectionFactory.createProjection(target.project, T(de.adesso.projectboard.base.project.projection.ReducedProjectProjection))}")
    ReducedProjectProjection getProject();

}
