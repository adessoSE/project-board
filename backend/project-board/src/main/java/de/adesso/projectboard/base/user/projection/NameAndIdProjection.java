package de.adesso.projectboard.base.user.projection;

import de.adesso.projectboard.base.projection.NamedProjection;
import org.springframework.beans.factory.annotation.Value;

@NamedProjection(target = UserProjectionSource.class)
public interface NameAndIdProjection {

    @Value("#{target.user.id}")
    String getId();

    @Value("#{target.data.firstName}")
    String getFirstName();

    @Value("#{target.data.lastName}")
    String getLastName();

}
