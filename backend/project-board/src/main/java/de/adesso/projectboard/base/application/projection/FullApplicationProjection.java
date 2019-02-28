package de.adesso.projectboard.base.application.projection;

import de.adesso.projectboard.base.project.projection.FullProjectProjection;
import org.springframework.beans.factory.annotation.Value;

public interface FullApplicationProjection extends BaseApplicationProjection {

    @Value("#{@baseProjectionFactory.createProjection(target.project, T(de.adesso.projectboard.base.project.projection.FullProjectProjection))}")
    FullProjectProjection getProject();

    boolean getReadByBoss();
}
