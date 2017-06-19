package tandoori.neo4jBolt;

import java.io.IOException;
import org.neo4j.driver.v1.Session;

public abstract class Query {
    protected QueryEngineBolt queryEngine;

    protected Session session;

    public Query(QueryEngineBolt queryEngine) {
        this.queryEngine = queryEngine;
        this.session = DriverBolt.getSession();
    }

    public abstract void execute(boolean details) throws IOException;
}

