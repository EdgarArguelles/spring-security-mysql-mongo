package app.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.DBRef;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "role")
public class Role extends Model {

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

    // in @ManyToMany the Owner Entity must use Set to notify MySQL that new relational table will have a combine Primary Key
    // if List is used instead the new relational table won't have a combine Primary key so data could be duplicated
    @ManyToMany(fetch = FetchType.LAZY)
    @DBRef // all foreign keys need @DBRef to notify Mongo about relationship and ownership
    @Getter
    @Setter
    private Set<Permission> permissions;

    // this entity doesn't have the ownership, so this List must be Ignored in JSON to avoid cyclical references
    @JsonIgnore
    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    @Getter
    @Setter
    private List<Person> people;

    public Role() {
    }

    public Role(String id) {
        this.id = id;
    }

    public Role(String name, String description, Set<Permission> permissions) {
        this.name = name;
        this.description = description;
        this.permissions = permissions;
    }

    @Override
    public void cleanRelations(boolean cleanAll) {
        // is not needed to clean data with @JsonIgnore
        if (cleanAll) {
            permissions = null;
        } else {
            permissions.forEach(p -> p.cleanRelations(true));
        }
    }

    @Override
    public String toString() {
        return "{" + id + ", " + name + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Role)) return false;

        Role that = (Role) o;

        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) return false;
        if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null) return false;
        if (getDescription() != null ? !getDescription().equals(that.getDescription()) : that.getDescription() != null)
            return false;
        if (getPermissions() != null ? !getPermissions().equals(that.getPermissions()) : that.getPermissions() != null)
            return false;
        return getPeople() != null ? getPeople().equals(that.getPeople()) : that.getPeople() == null;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        result = 31 * result + (getPermissions() != null ? getPermissions().hashCode() : 0);
        result = 31 * result + (getPeople() != null ? getPeople().hashCode() : 0);
        return result;
    }
}