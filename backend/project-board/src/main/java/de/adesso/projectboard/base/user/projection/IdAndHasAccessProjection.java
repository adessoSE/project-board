package de.adesso.projectboard.base.user.projection;

import de.adesso.projectboard.base.projection.NamedProjection;
import org.springframework.beans.factory.annotation.Value;

@NamedProjection(
        name = "hasAccess",
        target = UserProjectionSource.class
)
public interface IdAndHasAccessProjection {
    @Value("#{target.user.id}")
    String getId();

    @Value("#{@repositoryUserAccessService.userHasActiveAccessInterval(target.user)}")
    boolean getHasAccess();
}