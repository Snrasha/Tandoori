package tandoori.neo4jBolt;

import codesmells.annotations.LM;
import java.util.ArrayList;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import java.util.HashMap;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;

public class CCQuery extends FuzzyQuery {
    protected static double high = 28;

    protected static double veryHigh = 43;

    private CCQuery(QueryEngineBolt queryEngine) {
        super(queryEngine);
        fclFile = "/ComplexClass.fcl";
    }

    public static CCQuery createCCQuery(QueryEngineBolt queryEngine) {
        return new CCQuery(queryEngine);
    }

    @Override
    public void execute(boolean details) throws IOException {
        StatementResult result;
        try (Transaction tx = this.session.beginTransaction()) {
            String query = ((("MATCH (cl:Class  {app_key:" + (queryEngine.getKeyApp())) + "}) WHERE cl.class_complexity > ") + (CCQuery.veryHigh)) + "  RETURN cl as nod, cl.app_key as app_key";
            if (details) {
                query += ",cl.name as full_name";
            }else {
                query += ",count(cl) as CC";
            }
            result = tx.run(query);
            queryEngine.resultToCSV(result, "CC_NO_FUZZY");
            tx.success();
        }
    }

    @Override
    @LM
    public void executeFuzzy(boolean details) throws IOException {
        StatementResult result;
        try (Transaction tx = this.session.beginTransaction()) {
            String query = ((("MATCH (cl:Class  {app_key:" + (queryEngine.getKeyApp())) + "}) WHERE cl.class_complexity > ") + (CCQuery.high)) + " RETURN cl as nod, cl.app_key as app_key, cl.class_complexity as class_complexity";
            if (details) {
                query += ",cl.name as full_name";
            }
            result = tx.run(query);
            int cc;
            List<Map> fuzzyResult = new ArrayList<>();
            FunctionBlock fb = this.fuzzyFunctionBlock();
            while (result.hasNext()) {
                Map<String, Object> res = new HashMap<>(result.next().asMap());
                cc = ((Long) (res.get("class_complexity"))).intValue();
                if (cc >= (CCQuery.veryHigh)) {
                    res.put("fuzzy_value", 1);
                }else {
                    fb.setVariable("class_complexity", cc);
                    fb.evaluate();
                    res.put("fuzzy_value", fb.getVariable("res").getValue());
                }
                fuzzyResult.add(res);
            } 
            queryEngine.resultToCSV(fuzzyResult, "CC");
            tx.success();
        }
    }
}

