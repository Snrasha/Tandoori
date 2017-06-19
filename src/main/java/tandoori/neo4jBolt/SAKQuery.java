package tandoori.neo4jBolt;

import java.util.ArrayList;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import java.util.HashMap;
import java.io.IOException;
import codesmells.annotations.LM;
import org.neo4j.driver.v1.Transaction;
import java.util.List;
import java.util.Map;
import org.neo4j.driver.v1.StatementResult;

public class SAKQuery extends FuzzyQuery {
    protected static double high = 8.5;

    protected static double veryHigh = 13;

    private SAKQuery(QueryEngineBolt queryEngine) {
        super(queryEngine);
        fclFile = "/SwissArmyKnife.fcl";
    }

    public static SAKQuery createSAKQuery(QueryEngineBolt queryEngine) {
        return new SAKQuery(queryEngine);
    }

    @Override
    public void execute(boolean details) throws IOException {
        StatementResult result;
        try (Transaction tx = this.session.beginTransaction()) {
            String query = ((("MATCH (cl:Class  {app_key:" + (queryEngine.getKeyApp())) + "}) WHERE EXISTS(cl.is_interface)  AND cl.number_of_methods > ") + (SAKQuery.veryHigh)) + " RETURN cl as nod,cl.app_key as app_key";
            if (details) {
                query += ",cl.name as full_name";
            }else {
                query += ",count(cl) as SAK";
            }
            result = tx.run(query);
            queryEngine.resultToCSV(result, "SAK_NO_FUZZY");
            tx.success();
        }
    }

    @Override
    @LM
    public void executeFuzzy(boolean details) throws IOException {
        StatementResult result;
        try (Transaction tx = this.session.beginTransaction()) {
            String query = ((("MATCH (cl:Class  {app_key:" + (queryEngine.getKeyApp())) + "}) WHERE EXISTS(cl.is_interface)  AND cl.number_of_methods > ") + (SAKQuery.high)) + " RETURN cl as nod,cl.app_key as app_key,cl.number_of_methods as number_of_methods";
            if (details) {
                query += ",cl.name as full_name";
            }
            result = tx.run(query);
            int cc;
            List<Map> fuzzyResult = new ArrayList<>();
            FunctionBlock fb = this.fuzzyFunctionBlock();
            while (result.hasNext()) {
                Map<String, Object> res = new HashMap<>(result.next().asMap());
                cc = ((Long) (res.get("number_of_methods"))).intValue();
                if (cc >= (SAKQuery.veryHigh)) {
                    res.put("fuzzy_value", 1);
                }else {
                    fb.setVariable("number_of_methods", cc);
                    fb.evaluate();
                    res.put("fuzzy_value", fb.getVariable("res").getValue());
                }
                fuzzyResult.add(res);
            } 
            queryEngine.resultToCSV(fuzzyResult, "SAK");
            tx.success();
        }
    }
}

