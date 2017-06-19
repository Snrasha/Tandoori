package tandoori.neo4jBolt;

import java.io.IOException;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;

public class InvalidateWithoutRectQuery extends Query {
    private InvalidateWithoutRectQuery(QueryEngineBolt queryEngine) {
        super(queryEngine);
    }

    public static InvalidateWithoutRectQuery createInvalidateWithoutRectQuery(QueryEngineBolt queryEngine) {
        return new InvalidateWithoutRectQuery(queryEngine);
    }

    @Override
    public void execute(boolean details) throws IOException {
        StatementResult result;
        try (Transaction tx = this.session.beginTransaction()) {
            String query = ("MATCH (:Class{parent_name:'android.view.View',app_key:" + (queryEngine.getKeyApp())) + "})-[:CLASS_OWNS_METHOD]->(n:Method{name:'onDraw'})-[:CALLS]->(e:ExternalMethod{name:'invalidate'}) WHERE NOT (e)-[:METHOD_OWNS_ARGUMENT]->(:ExternalArgument)  return n as nod,n.app_key";
            if (details) {
                query += ",n.full_name as full_name";
            }else {
                query += ",count(n) as IWR";
            }
            result = tx.run(query);
            queryEngine.resultToCSV(result, "IWR");
            tx.success();
        }
    }
}

