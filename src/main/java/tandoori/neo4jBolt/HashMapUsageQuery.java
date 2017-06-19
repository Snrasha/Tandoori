package tandoori.neo4jBolt;

import java.io.IOException;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;

public class HashMapUsageQuery extends Query {
    private HashMapUsageQuery(QueryEngineBolt queryEngine) {
        super(queryEngine);
    }

    public static HashMapUsageQuery createHashMapUsageQuery(QueryEngineBolt queryEngine) {
        return new HashMapUsageQuery(queryEngine);
    }

    @Override
    public void execute(boolean details) throws IOException {
        StatementResult result;
        try (Transaction tx = this.session.beginTransaction()) {
            String query = ("MATCH (m:Method  {app_key:" + (queryEngine.getKeyApp())) + "})-[:CALLS]->(e:ExternalMethod{full_name:'<init>#java.util.HashMap'}) return m as nod,m.app_key";
            if (details) {
                query += ",m.full_name as full_name";
            }else {
                query += ", count(m) as HMU";
            }
            result = tx.run(query);
            queryEngine.resultToCSV(result, "HMU");
            tx.success();
        }
    }
}

