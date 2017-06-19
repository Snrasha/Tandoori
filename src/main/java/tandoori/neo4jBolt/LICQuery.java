package tandoori.neo4jBolt;

import java.io.IOException;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;

public class LICQuery extends Query {
    private LICQuery(QueryEngineBolt queryEngine) {
        super(queryEngine);
    }

    public static LICQuery createLICQuery(QueryEngineBolt queryEngine) {
        return new LICQuery(queryEngine);
    }

    @Override
    public void execute(boolean details) throws IOException {
        try (Transaction tx = this.session.beginTransaction()) {
            String query = ("MATCH (cl:Class  {app_key:" + (queryEngine.getKeyApp())) + "}) WHERE EXISTS(cl.is_inner_class) AND NOT EXISTS(cl.is_static) RETURN cl as nod,cl.app_key as app_key";
            if (details) {
                query += ",cl.name as full_name";
            }else {
                query += ",count(cl) as LIC";
            }
            StatementResult result = tx.run(query);
            queryEngine.resultToCSV(result, "LIC");
            tx.success();
        }
    }
}

