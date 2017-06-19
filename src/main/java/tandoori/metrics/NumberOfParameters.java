package tandoori.metrics;

import tandoori.entities.Entity;
import tandoori.entities.PaprikaMethod;

public class NumberOfParameters extends UnaryMetric<Integer> {
    private NumberOfParameters(PaprikaMethod paprikaMethod, int value) {
        this.value = value;
        this.entity = paprikaMethod;
        this.name = "number_of_parameters";
    }

    public static NumberOfParameters createNumberOfParameters(PaprikaMethod paprikaMethod, int value) {
        NumberOfParameters numberOfParameters = new NumberOfParameters(paprikaMethod, value);
        numberOfParameters.updateEntity();
        return numberOfParameters;
    }
}

