package de.adesso.projectboard.core.base.rest.security;

/**
 * {@link AuthenticationInfo} implementation that returns a <i>empty</i> string
 * as the username.
 *
 * <p>
 *     Auto-configured when no other {@link AuthenticationInfo} bean is supplied.
 * </p>
 *
 * @see AuthenticationInfo
 */
public class EmptyUserIdAuthenticationInfo implements AuthenticationInfo {

    /**
     *
     * @return
     *          A empty {@link String}.
     */
    @Override
    public String getUserId() {
        return "";
    }

}
