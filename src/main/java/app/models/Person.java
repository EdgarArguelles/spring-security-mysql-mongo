package app.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Email;
import org.springframework.data.mongodb.core.mapping.DBRef;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "person")
public class Person extends Model {

    public interface CIVIL_STATUS {
        Integer SINGLE = 1;
        Integer MARRIED = 2;
    }

    public interface SEX {
        String M = "M";
        String F = "F";
    }

    @NotNull
    @Size(min = 1, max = 255)
    @Column(nullable = false)
    @Getter
    @Setter
    private String name;

    @NotNull
    @Size(min = 1, max = 255)
    @Column(nullable = false)
    @Getter
    @Setter
    private String lastName;

    @NotNull
    @Column(nullable = false)
    @Getter
    @Setter
    private LocalDate birthday;

    @NotNull
    @Column(nullable = false, columnDefinition = "smallint")
    @Getter
    @Setter
    private Integer civilStatus;

    @NotNull
    @Size(min = 1, max = 1)
    @Column(nullable = false, length = 1)
    @Getter
    @Setter
    private String sex;

    @Email
    @Size(min = 3, max = 255)
    @Column()
    @Getter
    @Setter
    private String email;

    // in @ManyToMany the Owner Entity must use Set to notify MySQL that new relational table will have a combine Primary Key
    // if List is used instead the new relational table won't have a combine Primary key so data could be duplicated
    @ManyToMany(fetch = FetchType.LAZY)
    @DBRef // all foreign keys need @DBRef to notify Mongo about relationship and ownership
    @Getter
    @Setter
    private Set<Role> roles;

    // this entity doesn't have the ownership, so this List must be Ignored in JSON to avoid cyclical references
    @JsonIgnore
    @OneToMany(mappedBy = "person", fetch = FetchType.LAZY)
    @Getter
    @Setter
    private List<Authentication> authentications;

    public Person() {
    }

    public Person(String id) {
        this.id = id;
    }

    public Person(String name, String lastName, LocalDate birthday, Integer civilStatus, String sex, String email, Set<Role> roles) {
        this.name = name;
        this.lastName = lastName;
        this.birthday = birthday;
        this.civilStatus = civilStatus;
        this.sex = sex;
        this.email = email;
        this.roles = roles;
    }

    @JsonIgnore
    public String getFullName() {
        return name + " " + lastName;
    }

    @Override
    public void cleanRelations(boolean cleanAll) {
        // is not needed to clean data with @JsonIgnore
        if (cleanAll) {
            roles = null;
        } else {
            roles.forEach(r -> r.cleanRelations(true));
        }
    }

    @Override
    public String toString() {
        return "{" + id + ", " + getFullName() + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;

        Person that = (Person) o;

        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) return false;
        if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null) return false;
        if (getLastName() != null ? !getLastName().equals(that.getLastName()) : that.getLastName() != null)
            return false;
        if (getBirthday() != null ? !getBirthday().equals(that.getBirthday()) : that.getBirthday() != null)
            return false;
        if (getCivilStatus() != null ? !getCivilStatus().equals(that.getCivilStatus()) : that.getCivilStatus() != null)
            return false;
        if (getSex() != null ? !getSex().equals(that.getSex()) : that.getSex() != null) return false;
        if (getEmail() != null ? !getEmail().equals(that.getEmail()) : that.getEmail() != null) return false;
        if (getRoles() != null ? !getRoles().equals(that.getRoles()) : that.getRoles() != null) return false;
        return getAuthentications() != null ? getAuthentications().equals(that.getAuthentications()) : that.getAuthentications() == null;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getLastName() != null ? getLastName().hashCode() : 0);
        result = 31 * result + (getBirthday() != null ? getBirthday().hashCode() : 0);
        result = 31 * result + (getCivilStatus() != null ? getCivilStatus().hashCode() : 0);
        result = 31 * result + (getSex() != null ? getSex().hashCode() : 0);
        result = 31 * result + (getEmail() != null ? getEmail().hashCode() : 0);
        result = 31 * result + (getRoles() != null ? getRoles().hashCode() : 0);
        result = 31 * result + (getAuthentications() != null ? getAuthentications().hashCode() : 0);
        return result;
    }
}