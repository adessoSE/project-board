package de.adesso.projectboard.core.rest.security;

import de.adesso.projectboard.core.base.rest.security.ExpressionEvaluator;
import de.adesso.projectboard.core.rest.security.persistence.UserProjectsAccessInfo;
import de.adesso.projectboard.core.rest.security.persistence.UserProjectsAccessInfoRepository;
import de.adesso.projectboard.core.security.KeycloakAuthorizationInfo;
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
 *     Activated via the <i>keyclaok-adesso</i> profile.
 * </p>
 *
 * @see ExpressionEvaluator
 */
@Profile("adesso-keycloak")
@Service
public class UserAccessExpressionEvaluator implements ExpressionEvaluator {

    private final KeycloakAuthorizationInfo authInfo;

    private final UserProjectsAccessInfoRepository userAccessInfoRepo;

    @Autowired
    public UserAccessExpressionEvaluator(KeycloakAuthorizationInfo authInfo, UserProjectsAccessInfoRepository userAccessInfoRepo) {
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
     *          is present in the {@link UserProjectsAccessInfoRepository} and the
     *          {@link UserProjectsAccessInfo#accessEnd access end date} is after
     *          the current {@link LocalDateTime}, <i>false</i> otherwise.
     *
     * @see UserProjectsAccessInfoRepository#findFirstByUserIdOrderByAccessEndDesc(String)
     * @see KeycloakAuthorizationInfo#getUsername()
     */
    @Override
    public boolean hasAccessToProjects(Authentication authentication) {
        Optional<UserProjectsAccessInfo> accessInfo =
                userAccessInfoRepo.findFirstByUserIdOrderByAccessEndDesc(authInfo.getUsername());

        if(accessInfo.isPresent()) {
            LocalDateTime accessEnd = accessInfo.get().getAccessEnd();

            return accessEnd.isAfter(LocalDateTime.now());
        }

        return false;
    }

}
