package tandoori.metrics;

import tandoori.entities.Entity;
import tandoori.entities.PaprikaApp;

public class NumberOfActivities extends UnaryMetric<Integer> {
    private NumberOfActivities(PaprikaApp paprikaApp, int value) {
        this.value = value;
        this.entity = paprikaApp;
        this.name = "number_of_activities";
    }

    public static NumberOfActivities createNumberOfActivities(PaprikaApp paprikaApp, int value) {
        NumberOfActivities numberOfActivities = new NumberOfActivities(paprikaApp, value);
        numberOfActivities.updateEntity();
        return numberOfActivities;
    }
}

