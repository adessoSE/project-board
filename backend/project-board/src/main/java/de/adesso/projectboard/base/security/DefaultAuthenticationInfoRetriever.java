package de.adesso.projectboard.base.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * {@link AuthenticationInfoRetriever} implementation that returns a the {@link Authentication#getPrincipal() principal}
 * of the {@link Authentication} instance of the current {@link SecurityContext} in case it is a {@link String}.
 *
 * <p>
 *     Auto-configured when no other {@link AuthenticationInfoRetriever} bean is supplied.
 * </p>
 *
 * @see AuthenticationInfoRetriever
 */
public class DefaultAuthenticationInfoRetriever implements AuthenticationInfoRetriever {

    /**
     *
     * @return
     *          The {@link Authentication#getPrincipal() principal} of the current {@link Authentication}
     *          instance in case it is a string or a empty string otherwise.
     */
    @Override
    public String getUserId() {
        SecurityContext context = SecurityContextHolder.getContext();
        Object principal = context.getAuthentication().getPrincipal();

        return (principal instanceof String) ? (String) principal : "";
    }

}
