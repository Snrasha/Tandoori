package tandoori.neo4jBolt;

import java.io.IOException;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;

public class NLMRQuery extends Query {
    private NLMRQuery(QueryEngineBolt queryEngine) {
        super(queryEngine);
    }

    public static NLMRQuery createNLMRQuery(QueryEngineBolt queryEngine) {
        return new NLMRQuery(queryEngine);
    }

    @Override
    public void execute(boolean details) throws IOException {
        try (Transaction tx = this.session.beginTransaction()) {
            String query = ("MATCH (cl:Class  {app_key:" + (queryEngine.getKeyApp())) + "}) WHERE EXISTS(cl.is_activity) AND NOT (cl:Class)-[:CLASS_OWNS_METHOD]->(:Method { name: 'onLowMemory' }) AND NOT (cl)-[:EXTENDS]->(:Class) RETURN cl as nod,cl.app_key as app_key";
            if (details) {
                query += ",cl.name as full_name";
            }else {
                query += ",count(cl) as NLMR";
            }
            StatementResult result = tx.run(query);
            queryEngine.resultToCSV(result, "NLMR");
            tx.success();
        }
    }
}

