package app.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "auth_provider")
public class AuthProvider extends Model {

    @NotNull
    @Size(min = 1, max = 255)
    @Column(nullable = false, unique = true)
    @Getter
    @Setter
    private String name;

    @NotNull
    @Size(min = 1, max = 255)
    @Column(nullable = false)
    @Getter
    @Setter
    private String description;

    // because is not possible to create, edit or remove AuthProvider in this app,
    // @JsonIgnore can be used to hide information (url, authKey and authSecret)
    @JsonIgnore
    @Size(min = 1, max = 255)
    @Column()
    @Getter
    @Setter
    private String url;

    @JsonIgnore
    @Size(min = 1, max = 255)
    @Column()
    @Getter
    @Setter
    private String authKey;

    @JsonIgnore
    @Size(min = 1, max = 255)
    @Column()
    @Getter
    @Setter
    private String authSecret;

    // this entity doesn't have the ownership, so this List must be Ignored in JSON to avoid cyclical references
    @JsonIgnore
    @OneToMany(mappedBy = "authProvider", fetch = FetchType.LAZY)
    @Getter
    @Setter
    private List<Authentication> authentications;

    public AuthProvider() {
    }

    public AuthProvider(String id) {
        this.id = id;
    }

    public AuthProvider(String name, String description, String url, String authKey, String authSecret) {
        this.name = name;
        this.description = description;
        this.url = url;
        this.authKey = authKey;
        this.authSecret = authSecret;
    }

    @Override
    public void cleanRelations(boolean cleanAll) {
        // is not needed to clean data with @JsonIgnore
    }

    @Override
    public String toString() {
        return "{" + id + ", " + name + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AuthProvider)) return false;

        AuthProvider that = (AuthProvider) o;

        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) return false;
        if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null) return false;
        if (getDescription() != null ? !getDescription().equals(that.getDescription()) : that.getDescription() != null)
            return false;
        if (getUrl() != null ? !getUrl().equals(that.getUrl()) : that.getUrl() != null) return false;
        if (getAuthKey() != null ? !getAuthKey().equals(that.getAuthKey()) : that.getAuthKey() != null) return false;
        if (getAuthSecret() != null ? !getAuthSecret().equals(that.getAuthSecret()) : that.getAuthSecret() != null)
            return false;
        return getAuthentications() != null ? getAuthentications().equals(that.getAuthentications()) : that.getAuthentications() == null;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        result = 31 * result + (getUrl() != null ? getUrl().hashCode() : 0);
        result = 31 * result + (getAuthKey() != null ? getAuthKey().hashCode() : 0);
        result = 31 * result + (getAuthSecret() != null ? getAuthSecret().hashCode() : 0);
        result = 31 * result + (getAuthentications() != null ? getAuthentications().hashCode() : 0);
        return result;
    }
}