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

public class LMQuery extends FuzzyQuery {
    protected static double high = 17;

    protected static double veryHigh = 26;

    private LMQuery(QueryEngineBolt queryEngine) {
        super(queryEngine);
        fclFile = "/LongMethod.fcl";
    }

    public static LMQuery createLMQuery(QueryEngineBolt queryEngine) {
        return new LMQuery(queryEngine);
    }

    @Override
    public void execute(boolean details) throws IOException {
        StatementResult result;
        try (Transaction tx = this.session.beginTransaction()) {
            String query = ((("MATCH (m:Method {app_key:" + (queryEngine.getKeyApp())) + "} ) WHERE m.number_of_lines >") + (LMQuery.veryHigh)) + " RETURN m as nod,m.app_key as app_key";
            if (details) {
                query += ",m.full_name as full_name";
            }else {
                query += ",count(m) as LM";
            }
            result = tx.run(query);
            queryEngine.resultToCSV(result, "LM_NO_FUZZY");
            tx.success();
        }
    }

    @Override
    @LM
    public void executeFuzzy(boolean details) throws IOException {
        StatementResult result;
        try (Transaction tx = this.session.beginTransaction()) {
            String query = ((("MATCH (m:Method {app_key:" + (queryEngine.getKeyApp())) + "} ) WHERE m.number_of_lines >") + (LMQuery.high)) + "  RETURN m as nod,m.app_key as app_key,m.number_of_lines as number_of_lines";
            if (details) {
                query += ",m.full_name as full_name";
            }
            result = tx.run(query);
            int cc;
            List<Map> fuzzyResult = new ArrayList<>();
            FunctionBlock fb = this.fuzzyFunctionBlock();
            while (result.hasNext()) {
                Map<String, Object> res = new HashMap<>(result.next().asMap());
                cc = ((Long) (res.get("number_of_lines"))).intValue();
                if (cc >= (LMQuery.veryHigh)) {
                    res.put("fuzzy_value", 1);
                }else {
                    fb.setVariable("number_of_lines", cc);
                    fb.evaluate();
                    res.put("fuzzy_value", fb.getVariable("res").getValue());
                }
                fuzzyResult.add(res);
            } 
            queryEngine.resultToCSV(fuzzyResult, "LM");
            tx.success();
        }
    }
}

