package tandoori.metrics;

import tandoori.entities.Entity;
import tandoori.entities.PaprikaApp;

public class NumberOfAbstractClasses extends UnaryMetric<Integer> {
    private NumberOfAbstractClasses(PaprikaApp paprikaApp, int value) {
        this.value = value;
        this.entity = paprikaApp;
        this.name = "number_of_abstract_classes";
    }

    public static NumberOfAbstractClasses createNumberOfAbstractClasses(PaprikaApp paprikaApp, int value) {
        NumberOfAbstractClasses numberOfAbstractClasses = new NumberOfAbstractClasses(paprikaApp, value);
        numberOfAbstractClasses.updateEntity();
        return numberOfAbstractClasses;
    }
}

