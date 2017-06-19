package tandoori.neo4jBolt;

import java.io.IOException;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;

public class OverdrawQuery extends Query {
    private OverdrawQuery(QueryEngineBolt queryEngine) {
        super(queryEngine);
    }

    public static OverdrawQuery createOverdrawQuery(QueryEngineBolt queryEngine) {
        return new OverdrawQuery(queryEngine);
    }

    @Override
    public void execute(boolean details) throws IOException {
        try (Transaction tx = this.session.beginTransaction()) {
            String query = ((("MATCH (:Class{parent_name:\"android.view.View\",app_key:" + (queryEngine.getKeyApp())) + "})-[:CLASS_OWNS_METHOD]->(n:Method{name:\"onDraw\"})-[:METHOD_OWNS_ARGUMENT]->(:Argument{position:1,name:\"android.graphics.Canvas\"}) \n") + "WHERE NOT (n)-[:CALLS]->(:ExternalMethod{full_name:\"clipRect#android.graphics.Canvas\"}) AND NOT (n)-[:CALLS]->(:ExternalMethod{full_name:\"quickReject#android.graphics.Canvas\"})\n") + "RETURN n as nod,n.app_key as app_key";
            if (details) {
                query += ",n.full_name as full_name";
            }else {
                query += ",count(n) as UIO";
            }
            StatementResult result = tx.run(query);
            queryEngine.resultToCSV(result, "UIO");
            tx.success();
        }
    }
}

