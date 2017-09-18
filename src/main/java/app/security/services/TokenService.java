package app.security.services;

import app.security.pojos.LoggedUser;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;

/**
 * Deals with the creation and verification of tokens
 */
public interface TokenService {

    /**
     * Creates user's token
     *
     * @param loggedUser loggedUser entity.
     * @return generated token.
     * @throws JsonProcessingException if token is not generated.
     */
    String createToken(LoggedUser loggedUser) throws JsonProcessingException;

    /**
     * Refreshes token's expiration time
     *
     * @return refreshed token.
     * @throws JsonProcessingException if token is not generated.
     */
    String refreshToken() throws JsonProcessingException;

    /**
     * Gets user info from requested token
     *
     * @param token requested token.
     * @return LoggedUser entity or null if token invalid.
     * @throws IOException if token invalid.
     */
    LoggedUser getLoggedUser(String token) throws IOException;
}