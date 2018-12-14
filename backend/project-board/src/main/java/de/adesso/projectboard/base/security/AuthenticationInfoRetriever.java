package de.adesso.projectboard.base.security;

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

}
