package app.services.implementations;

import app.models.Authentication;
import app.models.Model;
import app.services.PresentationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PresentationServiceImpl implements PresentationService {

    @Override
    public void prepare(List<? extends Model> models, boolean shouldLoadComplete, boolean shouldLoadAllRelations) {
        if (models == null) {
            return;
        }

        models.forEach(m -> prepare(m, shouldLoadComplete, shouldLoadAllRelations));
    }

    @Override
    public void prepare(Model model, boolean shouldLoadComplete, boolean shouldLoadAllRelations) {
        if (model == null) {
            return;
        }

        // if shouldLoadAllRelations is true, shouldLoadComplete is true as well
        shouldLoadComplete = shouldLoadAllRelations || shouldLoadComplete;
        if (!shouldLoadComplete || !shouldLoadAllRelations) {
            model.cleanRelations(!shouldLoadComplete);
        }

        if (model instanceof Authentication) {
            ((Authentication) model).cleanAuthData();
        }
    }
}