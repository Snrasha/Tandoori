package tandoori.neo4jBolt;

import java.io.IOException;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;

public class ARGB8888Query extends Query {
    private ARGB8888Query(QueryEngineBolt queryEngine) {
        super(queryEngine);
    }

    public static ARGB8888Query createARGB8888Query(QueryEngineBolt queryEngine) {
        return new ARGB8888Query(queryEngine);
    }

    @Override
    public void execute(boolean details) throws IOException {
        try (Transaction tx = this.session.beginTransaction()) {
            String query = ("MATCH (e: ExternalArgument  {app_key:" + (queryEngine.getKeyApp())) + "}) WHERE EXISTS(e.is_argb_8888) RETURN e as nod,e.name as name";
            if (details) {
                query += ", count(e) as ARGB8888";
            }
            StatementResult result = tx.run(query);
            queryEngine.resultToCSV(result, "ARGB8888");
            tx.success();
        }
    }
}

