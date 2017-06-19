package tandoori.metrics;

import tandoori.entities.Entity;
import tandoori.entities.PaprikaClass;

public class IsView extends UnaryMetric<Boolean> {
    private IsView(PaprikaClass entity, boolean value) {
        this.value = value;
        this.entity = entity;
        this.name = "is_view";
    }

    public static IsView createIsView(PaprikaClass entity, boolean value) {
        IsView isView = new IsView(entity, value);
        isView.updateEntity();
        return isView;
    }
}

