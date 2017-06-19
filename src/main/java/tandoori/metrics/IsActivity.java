package tandoori.metrics;

import tandoori.entities.Entity;
import tandoori.entities.PaprikaClass;

public class IsActivity extends UnaryMetric<Boolean> {
    private IsActivity(PaprikaClass entity, boolean value) {
        this.value = value;
        this.entity = entity;
        this.name = "is_activity";
    }

    public static IsActivity createIsActivity(PaprikaClass entity, boolean value) {
        IsActivity isActivity = new IsActivity(entity, value);
        isActivity.updateEntity();
        return isActivity;
    }
}

