package tandoori.metrics;

import tandoori.entities.Entity;
import tandoori.entities.PaprikaMethod;

public class NumberOfLines extends UnaryMetric<Integer> {
    private NumberOfLines(PaprikaMethod paprikaMethod, int value) {
        this.value = value;
        this.entity = paprikaMethod;
        this.name = "number_of_lines";
    }

    public static NumberOfLines createNumberOfLines(PaprikaMethod paprikaMethod, int value) {
        NumberOfLines numberOfLines = new NumberOfLines(paprikaMethod, value);
        numberOfLines.updateEntity();
        return numberOfLines;
    }
}

