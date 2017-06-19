package tandoori.metrics;

import tandoori.entities.Entity;
import tandoori.entities.PaprikaClass;

public class NPathComplexity extends UnaryMetric<Integer> {
    private NPathComplexity(PaprikaClass paprikaClass) {
        this.value = paprikaClass.computeNPathComplexity();
        this.entity = paprikaClass;
        this.name = "npath_complexity";
    }

    public static NPathComplexity createNPathComplexity(PaprikaClass paprikaClass) {
        NPathComplexity npath_complexity = new NPathComplexity(paprikaClass);
        npath_complexity.updateEntity();
        return npath_complexity;
    }
}

