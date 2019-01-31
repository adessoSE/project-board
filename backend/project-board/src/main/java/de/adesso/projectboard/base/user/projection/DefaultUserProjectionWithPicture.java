package de.adesso.projectboard.base.user.projection;

import de.adesso.projectboard.base.projection.NamedProjection;
import org.springframework.beans.factory.annotation.Value;

@NamedProjection(
        name = "withpicture",
        target = UserProjectionSource.class
)
public interface DefaultUserProjectionWithPicture extends DefaultUserProjection {

    @Value("#{target.data.picture}")
    byte[] getPhoto();

}
