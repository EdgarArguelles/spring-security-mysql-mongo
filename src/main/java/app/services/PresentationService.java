package app.services;

import app.models.Model;

import java.util.List;

public interface PresentationService {

    /**
     * Prepare models before be sent to front end (don't send unnecessary data or call unnecessary sql).<br/>
     * (shouldLoadComplete = true and shouldLoadAllRelations = true) load data with all nested relations.<br/>
     * (shouldLoadComplete = false and shouldLoadAllRelations = true) load data with all nested relations.<br/>
     * (shouldLoadComplete = true and shouldLoadAllRelations = false) load data with only first level relations.<br/>
     * (shouldLoadComplete = false and shouldLoadAllRelations = false) load data without relations.
     *
     * @param models                 list of models that will be prepared.
     * @param shouldLoadComplete     if true load data with only first level relations
     * @param shouldLoadAllRelations if true load data with all nested relations
     */
    void prepare(List<? extends Model> models, boolean shouldLoadComplete, boolean shouldLoadAllRelations);

    /**
     * Prepare a model before be sent to front end (don't send unnecessary data or call unnecessary sql).<br/>
     * (shouldLoadComplete = true and shouldLoadAllRelations = true) load data with all nested relations.<br/>
     * (shouldLoadComplete = false and shouldLoadAllRelations = true) load data with all nested relations.<br/>
     * (shouldLoadComplete = true and shouldLoadAllRelations = false) load data with only first level relations.<br/>
     * (shouldLoadComplete = false and shouldLoadAllRelations = false) load data without relations.
     *
     * @param model                  model that will be prepared.
     * @param shouldLoadComplete     if true load data with only first level relations
     * @param shouldLoadAllRelations if true load data with all nested relations
     */
    void prepare(Model model, boolean shouldLoadComplete, boolean shouldLoadAllRelations);
}