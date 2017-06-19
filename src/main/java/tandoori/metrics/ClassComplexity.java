package tandoori.metrics;

import tandoori.entities.Entity;
import tandoori.entities.PaprikaClass;

public class ClassComplexity extends UnaryMetric<Integer> {
    private ClassComplexity(PaprikaClass paprikaClass) {
        this.value = paprikaClass.computeComplexity();
        this.entity = paprikaClass;
        this.name = "class_complexity";
    }

    public static ClassComplexity createClassComplexity(PaprikaClass paprikaClass) {
        ClassComplexity classComplexity = new ClassComplexity(paprikaClass);
        classComplexity.updateEntity();
        return classComplexity;
    }
}

