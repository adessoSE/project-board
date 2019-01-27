package de.adesso.projectboard.base.user.projection;

import de.adesso.projectboard.base.projection.NamedProjection;
import de.adesso.projectboard.base.projection.ProjectionTarget;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;

@NamedProjection(
        target = ProjectionTarget.USER,
        defaultProjection = true
)
public interface DefaultProjection {

    @Value("#{target.user.id}")
    String getId();

    @Value("#{target.data.firstName}")
    String getFirstName();

    @Value("#{target.data.lastName}")
    String getLastName();

    @Value("#{target.data.email}")
    String getEmail();

    @Value("#{target.data.lob}")
    String getLob();

    @Value("#{target.manager}")
    boolean getBoss();

    @Value("#{target.user.applications.size()}")
    long getApplications();

    @Value("#{target.user.bookmarks.size()}")
    long getBookmarks();

    @Value("#{target}")
    AccessSummary getAccessInfo();

    interface AccessSummary {

        @Value("#{@repositoryUserAccessService.userHasActiveAccessInfo(target.user)}")
        boolean getHasAccess();

        @Value("#{@repositoryUserAccessService.userHasActiveAccessInfo(target.user) ? target.user.getLatestAccessInfo().get().accessStart : null}")
        LocalDateTime getAccessStart();

        @Value("#{@repositoryUserAccessService.userHasActiveAccessInfo(target.user) ? target.user.getLatestAccessInfo().get().accessEnd : null}")
        LocalDateTime getAccessEnd();

    }

}
