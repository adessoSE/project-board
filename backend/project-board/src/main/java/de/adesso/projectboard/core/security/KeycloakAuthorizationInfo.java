package de.adesso.projectboard.core.security;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("adesso-keycloak")
@Service
public class KeycloakAuthorizationInfo {

    // TODO: implement
    // Note: auth context is set when calling -> SecurityContextHolder.getContext() returns a context

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
        return "Username";
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
