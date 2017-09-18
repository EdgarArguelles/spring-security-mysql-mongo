package app.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.DBRef;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "authentication", uniqueConstraints = {@UniqueConstraint(columnNames = {"auth_provider_id", "person_id"})})
public class Authentication extends Model {

    @Size(min = 1, max = 255)
    @Column(unique = true)
    @Getter
    @Setter
    private String username;

    // is not possible to use @JsonIgnore to hide password, cause create and edit will ignore it as well
    @NotNull
    @Size(min = 3, max = 255)
    @Column(nullable = false)
    @Getter
    @Setter
    private String password;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "auth_provider_id", nullable = false)
    @DBRef // all foreign keys need @DBRef to notify Mongo about relationship and ownership
    @Getter
    @Setter
    private AuthProvider authProvider;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "person_id", nullable = false)
    @DBRef // all foreign keys need @DBRef to notify Mongo about relationship and ownership
    @Getter
    @Setter
    private Person person;

    public Authentication() {
    }

    public Authentication(String id) {
        this.id = id;
    }

    public Authentication(String username, String password, AuthProvider authProvider, Person person) {
        this.username = username;
        this.password = password;
        this.authProvider = authProvider;
        this.person = person;
    }

    // cause is not possible to use @JsonIgnore to hide password, in order to
    // keep important information secret, all JSON responses must clean data before being sent
    public void cleanAuthData() {
        setPassword(null);

        // the next three are not needed because @JsonIgnore in AuthProvider, but it is a extra security
        if (authProvider != null) {
            authProvider.setUrl(null);
            authProvider.setAuthKey(null);
            authProvider.setAuthSecret(null);
        }
    }

    @Override
    public void cleanRelations(boolean cleanAll) {
        // is not needed to clean data with @JsonIgnore
        if (authProvider != null) {
            authProvider.cleanRelations(cleanAll);
        }
        if (person != null) {
            person.cleanRelations(cleanAll);
        }
    }

    @Override
    public String toString() {
        return "{" + id + ", " + username + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Authentication)) return false;

        Authentication that = (Authentication) o;

        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) return false;
        if (getUsername() != null ? !getUsername().equals(that.getUsername()) : that.getUsername() != null)
            return false;
        if (getPassword() != null ? !getPassword().equals(that.getPassword()) : that.getPassword() != null)
            return false;
        if (getAuthProvider() != null ? !getAuthProvider().equals(that.getAuthProvider()) : that.getAuthProvider() != null)
            return false;
        return getPerson() != null ? getPerson().equals(that.getPerson()) : that.getPerson() == null;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getUsername() != null ? getUsername().hashCode() : 0);
        result = 31 * result + (getPassword() != null ? getPassword().hashCode() : 0);
        result = 31 * result + (getAuthProvider() != null ? getAuthProvider().hashCode() : 0);
        result = 31 * result + (getPerson() != null ? getPerson().hashCode() : 0);
        return result;
    }
}