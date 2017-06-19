package tandoori.neo4jBolt;

import java.io.IOException;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;

public class UnsuitedLRUCacheSizeQuery extends Query {
    private UnsuitedLRUCacheSizeQuery(QueryEngineBolt queryEngine) {
        super(queryEngine);
    }

    public static UnsuitedLRUCacheSizeQuery createUnsuitedLRUCacheSizeQuery(QueryEngineBolt queryEngine) {
        return new UnsuitedLRUCacheSizeQuery(queryEngine);
    }

    @Override
    public void execute(boolean details) throws IOException {
        try (Transaction tx = this.session.beginTransaction()) {
            String query = ("Match (m:Method  {app_key:" + (queryEngine.getKeyApp())) + "})-[:CALLS]->(e:ExternalMethod {full_name:'<init>#android.util.LruCache'}) WHERE NOT (m)-[:CALLS]->(:ExternalMethod {full_name:'getMemoryClass#android.app.ActivityManager'}) return m as nod,m.app_key as app_key";
            if (details) {
                query += ",m.full_name as full_name";
            }else {
                query += ",count(m) as UCS";
            }
            StatementResult result = tx.run(query);
            queryEngine.resultToCSV(result, "UCS");
            tx.success();
        }
    }
}

