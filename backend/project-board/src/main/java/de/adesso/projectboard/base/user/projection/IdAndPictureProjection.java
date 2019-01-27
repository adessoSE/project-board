package de.adesso.projectboard.base.user.projection;

import de.adesso.projectboard.base.projection.NamedProjection;
import de.adesso.projectboard.base.projection.ProjectionTarget;
import org.springframework.beans.factory.annotation.Value;

@NamedProjection(
        name = "idandpicture",
        target = ProjectionTarget.USER
)
public interface IdAndPictureProjection {

    @Value("#{target.user.id}")
    String getId();

    @Value("#{target.data.picture}")
    byte[] getPicture();

}
