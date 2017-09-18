package app.security.pojos;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/**
 * Essential Logged User info pojo
 */
public class LoggedUser {

    @Getter
    private String id;

    @Getter
    private String fullName;

    @Getter
    private String role;

    @Getter
    @Setter
    private Set<String> permissions;

    /**
     * Default constructor needed when deserialize
     */
    public LoggedUser() {
    }

    /**
     * Create an instance
     *
     * @param id   person data base id
     * @param role role data base id (In which facet is the user)
     */
    public LoggedUser(String id, String role) {
        this(id, null, role, null);
    }

    /**
     * Create an instance
     *
     * @param id          person data base id
     * @param fullName    display name
     * @param role        role data base id (In which facet is the user)
     * @param permissions permissions name list
     */
    public LoggedUser(String id, String fullName, String role, Set<String> permissions) {
        this.id = id;
        this.fullName = fullName;
        this.role = role;
        this.permissions = permissions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LoggedUser)) return false;

        LoggedUser that = (LoggedUser) o;

        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) return false;
        if (getFullName() != null ? !getFullName().equals(that.getFullName()) : that.getFullName() != null)
            return false;
        if (getRole() != null ? !getRole().equals(that.getRole()) : that.getRole() != null) return false;
        return getPermissions() != null ? getPermissions().equals(that.getPermissions()) : that.getPermissions() == null;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getFullName() != null ? getFullName().hashCode() : 0);
        result = 31 * result + (getRole() != null ? getRole().hashCode() : 0);
        result = 31 * result + (getPermissions() != null ? getPermissions().hashCode() : 0);
        return result;
    }
}