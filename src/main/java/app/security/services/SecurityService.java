package app.security.services;

import app.security.pojos.AccountCredentials;
import app.security.pojos.LoggedUser;

/**
 * Deals with security process
 */
public interface SecurityService {

    /**
     * Try to authenticate an user
     *
     * @param credentials to authenticate.
     * @return LoggedUser instance associated with credentials
     */
    LoggedUser authenticate(AccountCredentials credentials);

    /**
     * Creates a new LoggedUser instance with the requested role
     *
     * @param roleId requested role id.
     * @return new LoggedUser instance
     */
    LoggedUser changeRole(String roleId);

    /**
     * Gets info from logged user (LoggedUser instance)
     *
     * @return LoggedUser instance
     */
    LoggedUser getLoggedUser();

    /**
     * Hashes a value
     *
     * @param value value to be hashed.
     * @return hashed value
     */
    String hashValue(String value);
}