package de.adesso.projectboard.rest.security;

import de.adesso.projectboard.base.security.AuthenticationInfo;
import org.keycloak.KeycloakPrincipal;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * {@link AuthenticationInfo} implementation that retrieves the user id from
 * the {@link SecurityContext}.
 *
 * @see AuthenticationInfo
 */
@Profile("adesso-keycloak")
@Service
public class KeycloakAuthenticationInfo implements AuthenticationInfo {

    @Override
    public String getUserId() {
        KeycloakPrincipal principal = (KeycloakPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal.getName();
    }

}
