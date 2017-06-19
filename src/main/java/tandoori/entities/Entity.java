package tandoori.entities;

import java.util.ArrayList;
import java.util.List;

import tandoori.metrics.Metric;
import tandoori.metrics.UnaryMetric;

/**
 * Created by Geoffrey Hecht on 20/05/14.
 */
public abstract class Entity {
    protected String name;
    protected List<Metric> metrics = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Metric> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<Metric> metrics) {
        this.metrics = metrics;
    }

    public void addMetric(UnaryMetric unaryMetric){
        this.metrics.add(unaryMetric);
    }

    @Override
    public String toString() {
        return name;
    }


}
