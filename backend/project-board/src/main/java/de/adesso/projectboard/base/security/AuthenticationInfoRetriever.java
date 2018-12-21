package de.adesso.projectboard.base.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;

/**
 * Interface used to retrieve authentication information.
 */
public interface AuthenticationInfoRetriever {

    /**
     *
     * @return
     *          The id of the currently authenticated user.
     */
    String getUserId();

    /**
     *
     * @return
     *          {@code true}, iff the currently authenticated
     *          user has the {@code admin} role.
     */
    @SuppressWarnings("unchecked")
    default boolean hasAdminRole() {
        Collection<GrantedAuthority> authorities = (Collection<GrantedAuthority>)
                SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        return authorities.stream().anyMatch(authority -> "ROLE_admin".equals(authority.getAuthority()));
    }

}
