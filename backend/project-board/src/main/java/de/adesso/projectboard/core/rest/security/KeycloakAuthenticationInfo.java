package de.adesso.projectboard.core.rest.security;

import de.adesso.projectboard.core.base.rest.security.AuthenticationInfo;
import org.keycloak.KeycloakPrincipal;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;

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
