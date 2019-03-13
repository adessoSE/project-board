package de.adesso.projectboard.rest.security;

import de.adesso.projectboard.base.security.AuthenticationInfoRetriever;
import org.keycloak.KeycloakPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * {@link AuthenticationInfoRetriever} implementation that retrieves the user id from
 * the {@link SecurityContext}.
 *
 * @see AuthenticationInfoRetriever
 */
@Service
public class KeycloakAuthenticationInfoRetriever implements AuthenticationInfoRetriever {

    @Override
    public String getUserId() {
        KeycloakPrincipal principal = (KeycloakPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal.getName();
    }

}
