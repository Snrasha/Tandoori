package tandoori.neo4jBolt;

import java.util.ArrayList;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import java.util.HashMap;
import java.io.IOException;
import codesmells.annotations.LM;
import java.util.List;
import java.util.Map;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.StatementResult;

public class HeavyAsyncTaskStepsQuery extends FuzzyQuery {
    protected static double high_cc = 3.5;

    protected static double veryHigh_cc = 5;

    protected static double high_noi = 17;

    protected static double veryHigh_noi = 26;

    private HeavyAsyncTaskStepsQuery(QueryEngineBolt queryEngine) {
        super(queryEngine);
        fclFile = "/HeavySomething.fcl";
    }

    public static HeavyAsyncTaskStepsQuery createHeavyAsyncTaskStepsQuery(QueryEngineBolt queryEngine) {
        return new HeavyAsyncTaskStepsQuery(queryEngine);
    }

    @Override
    public void execute(boolean details) throws IOException {
        StatementResult result;
        try (Transaction tx = this.session.beginTransaction()) {
            String query = ((((("MATCH (c:Class{parent_name:'android.os.AsyncTask',app_key:" + (queryEngine.getKeyApp())) + "})-[:CLASS_OWNS_METHOD]->(m:Method) WHERE (m.name='onPreExecute' OR m.name='onProgressUpdate' OR m.name='onPostExecute')  AND  m.number_of_instructions >") + (HeavyAsyncTaskStepsQuery.veryHigh_noi)) + " AND m.cyclomatic_complexity > ") + (HeavyAsyncTaskStepsQuery.veryHigh_cc)) + " return m as nod,m.app_key as app_key";
            if (details) {
                query += ",m.full_name as full_name";
            }else {
                query += ",count(m) as HAS";
            }
            result = tx.run(query);
            queryEngine.resultToCSV(result, "HAS_NO_FUZZY");
            tx.success();
        }
    }

    @Override
    @LM
    public void executeFuzzy(boolean details) throws IOException {
        StatementResult result;
        try (Transaction tx = this.session.beginTransaction()) {
            String query = ((((("MATCH (c:Class{parent_name:'android.os.AsyncTask' ,app_key:" + (queryEngine.getKeyApp())) + "})-[:CLASS_OWNS_METHOD]->(m:Method) WHERE (m.name='onPreExecute' OR m.name='onProgressUpdate' OR m.name='onPostExecute')   AND  m.number_of_instructions >") + (HeavyAsyncTaskStepsQuery.high_noi)) + " AND m.cyclomatic_complexity > ") + (HeavyAsyncTaskStepsQuery.high_cc)) + " return m as nod,m.app_key as app_key,m.cyclomatic_complexity as cyclomatic_complexity, m.number_of_instructions as number_of_instructions";
            if (details) {
                query += ",m.full_name as full_name";
            }
            result = tx.run(query);
            int noi;
            int cc;
            List<Map> fuzzyResult = new ArrayList<>();
            FunctionBlock fb = this.fuzzyFunctionBlock();
            while (result.hasNext()) {
                Map<String, Object> res = new HashMap<>(result.next().asMap());
                cc = ((Long) (res.get("cyclomatic_complexity"))).intValue();
                noi = ((Long) (res.get("number_of_instructions"))).intValue();
                if ((cc >= (HeavyAsyncTaskStepsQuery.veryHigh_cc)) && (noi >= (HeavyAsyncTaskStepsQuery.veryHigh_noi))) {
                    res.put("fuzzy_value", 1);
                }else {
                    fb.setVariable("cyclomatic_complexity", cc);
                    fb.setVariable("number_of_instructions", noi);
                    fb.evaluate();
                    res.put("fuzzy_value", fb.getVariable("res").getValue());
                }
                fuzzyResult.add(res);
            } 
            queryEngine.resultToCSV(fuzzyResult, "HAS");
            tx.success();
        }
    }
}

