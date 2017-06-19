package tandoori.metrics;

import tandoori.entities.Entity;
import tandoori.entities.PaprikaClass;

public class IsService extends UnaryMetric<Boolean> {
    private IsService(PaprikaClass entity, boolean value) {
        this.value = value;
        this.entity = entity;
        this.name = "is_service";
    }

    public static IsService createIsService(PaprikaClass entity, boolean value) {
        IsService isService = new IsService(entity, value);
        isService.updateEntity();
        return isService;
    }
}

