package app.services;

import app.exceptions.AppDontFoundException;
import app.models.Model;
import app.pojos.pages.PageDataRequest;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Basic operations for all JPA services
 */
public interface JpaService<T extends Model> {

    /**
     * Retrieves all entities.
     *
     * @return list of entities.
     */
    List<T> findAll();

    /**
     * Retrieves an entity by its id.
     *
     * @param id value to search.
     * @return the entity with the given id or null if none found.
     */
    T findById(String id);

    /**
     * Retrieves an entity by its id or throws AppDontFoundException if entity not found.
     *
     * @param id value to search.
     * @return the entity with the given id.
     * @throws AppDontFoundException if entity not found
     */
    T findByIdNotNull(String id) throws AppDontFoundException;

    /**
     * Create an entity.
     *
     * @param entity entity to be created.
     * @return the entity created.
     */
    T save(T entity);

    /**
     * Update an entity.
     *
     * @param entity entity to be updated.
     * @return the entity updated.
     */
    T update(T entity);

    /**
     * Delete an entity.
     *
     * @param id entity id to be deleted.
     * @return the entity that was deleted.
     */
    T delete(String id);

    /**
     * Retrieves all requested entities.
     *
     * @param pageDataRequest Page data.
     * @return list of entities with metadata.
     */
    Page<T> page(PageDataRequest pageDataRequest);
}