package de.adesso.projectboard.core.rest.security;

import de.adesso.projectboard.core.base.rest.security.ExpressionEvaluator;
import de.adesso.projectboard.core.rest.security.persistence.UserAccessInfo;
import de.adesso.projectboard.core.rest.security.persistence.UserAccessInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * A {@link ExpressionEvaluator} implementation that is used to authorize access
 * to the REST interface.
 *
 * <p>
 *     Activated via the <i>adesso-keycloak</i> profile.
 * </p>
 *
 * @see ExpressionEvaluator
 */
@Profile("adesso-keycloak")
@Service
public class UserAccessExpressionEvaluator implements ExpressionEvaluator {

    private final KeycloakAuthenticationInfo authInfo;

    private final UserAccessInfoRepository userAccessInfoRepo;

    @Autowired
    public UserAccessExpressionEvaluator(KeycloakAuthenticationInfo authInfo, UserAccessInfoRepository userAccessInfoRepo) {
        this.authInfo = authInfo;
        this.userAccessInfoRepo = userAccessInfoRepo;
    }

    /**
     *
     * @param authentication
     *          The {@link Authentication} object.
     *
     * @return
     *          <i>true</i>, if the username of the currently authenticated user
     *          is present in the {@link UserAccessInfoRepository} and the
     *          {@link UserAccessInfo#accessEnd access end date} is after
     *          the current {@link LocalDateTime}, <i>false</i> otherwise.
     *
     * @see UserAccessInfoRepository#findFirstByUserIdOrderByAccessEndDesc(String)
     * @see KeycloakAuthenticationInfo#getUsername()
     */
    @Override
    public boolean hasAccessToProjects(Authentication authentication) {
        Optional<UserAccessInfo> accessInfo =
                userAccessInfoRepo.findFirstByUserIdOrderByAccessEndDesc(authInfo.getUsername());

        if(accessInfo.isPresent()) {
            LocalDateTime accessEnd = accessInfo.get().getAccessEnd();

            return accessEnd.isAfter(LocalDateTime.now());
        }

        return false;
    }

    /**
     *
     * @param authentication
     *          The {@link Authentication} object.
     *
     * @param projectId
     *          The id of the {@link de.adesso.projectboard.core.base.project.persistence.AbstractProject}
     *          the user wants to access.
     *
     * @return
     *          <i>true</i>, if
     *
     */
    @Override
    public boolean hasAccessToProject(Authentication authentication, long projectId) {
        // TODO implement
        return false;
    }

    /**
     *
     * @param authentication
     *          The {@link Authentication} object.
     *
     * @return
     *          The result of {@link #hasAccessToProjects(Authentication)}
     *
     * @see #hasAccessToProjects(Authentication)
     */
    @Override
    public boolean hasPermissionToApply(Authentication authentication) {
        return hasAccessToProjects(authentication);
    }

}
