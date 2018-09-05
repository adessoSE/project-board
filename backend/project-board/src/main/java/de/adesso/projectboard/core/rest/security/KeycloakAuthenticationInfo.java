package de.adesso.projectboard.core.rest.security;

import de.adesso.projectboard.core.base.rest.security.AuthenticationInfo;
import org.keycloak.KeycloakPrincipal;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;

@Profile("adesso-keycloak")
@Service
public class KeycloakAuthenticationInfo implements AuthenticationInfo {

    // TODO: implement
    // Note: auth context is set when calling -> SecurityContextHolder.getContext() returns a context

    @Override
    public String getUserId() {
        KeycloakPrincipal principal = (KeycloakPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal.getName();
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
     *          The full name in A <i>firstName lastName</i>
     *          pattern.
     *
     * @see #getFirstName()
     * @see #getLastName()
     */
    public String getName() {
        return getFirstName() + ' ' + getLastName();
    }

    /**
     *
     * @return
     *          The first name of the current user.
     */
    public String getFirstName() {
        return "Dieter";
    }

    /**
     *
     * @return
     *          The last name of the current user.
     */
    public String getLastName() {
        return "Pete";
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

    /**
     *
     * @return
     *          A {@link Set} of strings containing all user IDs of
     *          the employees. (alle user IDs der unterstellten mitarbeiter)
     */
    public Set<String> getEmployeeSet() {
        return Collections.emptySet();
    }

}
