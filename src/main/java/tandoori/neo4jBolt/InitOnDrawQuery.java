package tandoori.neo4jBolt;

import java.io.IOException;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;

public class InitOnDrawQuery extends Query {
    private InitOnDrawQuery(QueryEngineBolt queryEngine) {
        super(queryEngine);
    }

    public static InitOnDrawQuery createInitOnDrawQuery(QueryEngineBolt queryEngine) {
        return new InitOnDrawQuery(queryEngine);
    }

    @Override
    public void execute(boolean details) throws IOException {
        try (Transaction tx = this.session.beginTransaction()) {
            String query = ("MATCH (:Class{parent_name:'android.view.View',app_key:" + (queryEngine.getKeyApp())) + "})-[:CLASS_OWNS_METHOD]->(n:Method{name:'onDraw'})-[:CALLS]->({name:'<init>'})  return n as nod,n.app_key as app_key";
            if (details) {
                query += ",n.full_name as full_name";
            }else {
                query += ",count(n) as IOD";
            }
            StatementResult result = tx.run(query);
            queryEngine.resultToCSV(result, "IOD");
            tx.success();
        }
    }
}

