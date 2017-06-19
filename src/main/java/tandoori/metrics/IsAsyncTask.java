package tandoori.metrics;

import tandoori.entities.Entity;
import tandoori.entities.PaprikaClass;

public class IsAsyncTask extends UnaryMetric<Boolean> {
    private IsAsyncTask(PaprikaClass entity, boolean value) {
        this.value = value;
        this.entity = entity;
        this.name = "is_async_task";
    }

    public static IsAsyncTask createIsAsyncTask(PaprikaClass entity, boolean value) {
        IsAsyncTask isAsyncTask = new IsAsyncTask(entity, value);
        isAsyncTask.updateEntity();
        return isAsyncTask;
    }
}

