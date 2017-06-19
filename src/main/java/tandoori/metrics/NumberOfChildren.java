package tandoori.metrics;

import tandoori.entities.Entity;
import tandoori.entities.PaprikaClass;

public class NumberOfChildren extends UnaryMetric<Integer> {
    private NumberOfChildren(PaprikaClass paprikaClass) {
        this.value = paprikaClass.getChildren();
        this.entity = paprikaClass;
        this.name = "number_of_children";
    }

    public static NumberOfChildren createNumberOfChildren(PaprikaClass paprikaClass) {
        NumberOfChildren numberOfChildren = new NumberOfChildren(paprikaClass);
        numberOfChildren.updateEntity();
        return numberOfChildren;
    }
}

