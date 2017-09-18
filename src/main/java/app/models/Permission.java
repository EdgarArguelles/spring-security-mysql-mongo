package app.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Table(name = "permission")
public class Permission extends Model {

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

    // this entity doesn't have the ownership, so this List must be Ignored in JSON to avoid cyclical references
    @JsonIgnore
    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
    @Getter
    @Setter
    private List<Role> roles;

    public Permission() {
    }

    public Permission(String id) {
        this.id = id;
    }

    public Permission(String name, String description) {
        this.name = name;
        this.description = description;
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
        if (!(o instanceof Permission)) return false;

        Permission that = (Permission) o;

        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) return false;
        if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null) return false;
        if (getDescription() != null ? !getDescription().equals(that.getDescription()) : that.getDescription() != null)
            return false;
        return getRoles() != null ? getRoles().equals(that.getRoles()) : that.getRoles() == null;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        result = 31 * result + (getRoles() != null ? getRoles().hashCode() : 0);
        return result;
    }
}