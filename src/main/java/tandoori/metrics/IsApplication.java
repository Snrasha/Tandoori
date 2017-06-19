package tandoori.metrics;

import tandoori.entities.Entity;
import tandoori.entities.PaprikaClass;

public class IsApplication extends UnaryMetric<Boolean> {
    private IsApplication(PaprikaClass entity, boolean value) {
        this.value = value;
        this.entity = entity;
        this.name = "is_application";
    }

    public static IsApplication createIsApplication(PaprikaClass entity, boolean value) {
        IsApplication isApplication = new IsApplication(entity, value);
        isApplication.updateEntity();
        return isApplication;
    }
}

