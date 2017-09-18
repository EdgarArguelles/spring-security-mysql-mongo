package app.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@MappedSuperclass
public abstract class Model {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Getter
    @Setter
    protected String id;

    @Column(name = "created_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    @Getter
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    @Getter
    private LocalDateTime updatedAt;

    @PrePersist
    public void createdAt() {
        // TODO: doesn't work with mongo, try to use Auditable when it supports java 8 LocalDateTime instead of joda time
        this.createdAt = this.updatedAt = LocalDateTime.now(ZoneOffset.UTC);
    }

    @PreUpdate
    public void updatedAt() {
        // TODO: doesn't work with mongo, try to use Auditable when it supports java 8 LocalDateTime instead of joda time
        this.updatedAt = LocalDateTime.now(ZoneOffset.UTC);
    }

    /**
     * Avoid to send unnecessary data to frontend and call unnecessary sql
     *
     * @param cleanAll if true set nested relations to null, otherwise clean nested relations with cleanAll = true
     */
    public abstract void cleanRelations(boolean cleanAll);
}