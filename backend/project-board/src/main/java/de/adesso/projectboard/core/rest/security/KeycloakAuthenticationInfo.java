package de.adesso.projectboard.core.rest.security;

import de.adesso.projectboard.core.base.rest.security.AuthenticationInfo;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("adesso-keycloak")
@Service
public class KeycloakAuthenticationInfo implements AuthenticationInfo {

    // TODO: implement
    // Note: auth context is set when calling -> SecurityContextHolder.getContext() returns a context

    @Override
    public String getUserId() {
        return "user";
    }

    /**
     *
     * @return
     *          The CC of the current user.
     */
    public String getCC() {
        return "CC";
    }

    /**
     *
     * @return
     *          The <b>unique</b> username of the current user.
     */
    public String getUsername() {
        return getUserId();
    }

    /**
     *
     * @return
     *          The full name of the current user.
     */
    public String getName() {
        return "Dieter Pete";
    }

    /**
     *
     * @return
     *          The LOB of the current user.
     */
    public String getLOB() {
        return "LOB";
    }

    /**
     *
     * @return
     *          The email of the current user.
     */
    public String getEmail() {
        return "user's email";
    }

    /**
     *
     * @return
     *          The email of the current user's manager.
     */
    public String getManagerEmail() {
        return "manager's email";
    }

}
