package app.security.pojos;

import lombok.Getter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Account Credentials pojo
 */
public class AccountCredentials {

    @NotNull
    @Size(min = 1, max = 255)
    @Getter
    private String username;

    @NotNull
    @Size(min = 1, max = 255)
    @Getter
    private String password;

    /**
     * Default constructor needed when deserialize
     */
    public AccountCredentials() {
    }

    /**
     * Create an instance
     *
     * @param username credentials username
     * @param password credentials password
     */
    public AccountCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccountCredentials)) return false;

        AccountCredentials that = (AccountCredentials) o;

        if (getUsername() != null ? !getUsername().equals(that.getUsername()) : that.getUsername() != null)
            return false;
        return getPassword() != null ? getPassword().equals(that.getPassword()) : that.getPassword() == null;
    }

    @Override
    public int hashCode() {
        int result = getUsername() != null ? getUsername().hashCode() : 0;
        result = 31 * result + (getPassword() != null ? getPassword().hashCode() : 0);
        return result;
    }
}