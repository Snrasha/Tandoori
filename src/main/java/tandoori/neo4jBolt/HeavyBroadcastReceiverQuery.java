package tandoori.neo4jBolt;

import java.util.HashMap;
import java.util.ArrayList;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import java.io.IOException;
import codesmells.annotations.LM;
import java.util.List;
import org.neo4j.driver.v1.Transaction;
import java.util.Map;
import org.neo4j.driver.v1.StatementResult;

public class HeavyBroadcastReceiverQuery extends FuzzyQuery {
    protected static double high_cc = 3.5;

    protected static double veryHigh_cc = 5;

    protected static double high_noi = 17;

    protected static double veryHigh_noi = 26;

    private HeavyBroadcastReceiverQuery(QueryEngineBolt queryEngine) {
        super(queryEngine);
        fclFile = "/HeavySomething.fcl";
    }

    public static HeavyBroadcastReceiverQuery createHeavyBroadcastReceiverQuery(QueryEngineBolt queryEngine) {
        return new HeavyBroadcastReceiverQuery(queryEngine);
    }

    @Override
    public void execute(boolean details) throws IOException {
        StatementResult result;
        try (Transaction tx = this.session.beginTransaction()) {
            String query = ((((("MATCH (c:Class{is_broadcast_receiver:true,app_key:" + (queryEngine.getKeyApp())) + "})-[:CLASS_OWNS_METHOD]->(m:Method{name:'onReceive'}) WHERE m.number_of_instructions > ") + (HeavyBroadcastReceiverQuery.veryHigh_noi)) + " AND m.cyclomatic_complexity>") + (HeavyBroadcastReceiverQuery.veryHigh_cc)) + " return m as nod,m.app_key as app_key";
            if (details) {
                query += ",m.full_name as full_name";
            }else {
                query += ",count(m) as HBR";
            }
            result = tx.run(query);
            queryEngine.resultToCSV(result, "HBR_NO_FUZZY");
            tx.success();
        }
    }

    @Override
    @LM
    public void executeFuzzy(boolean details) throws IOException {
        StatementResult result;
        try (Transaction tx = this.session.beginTransaction()) {
            String query = ((((("MATCH (c:Class{is_broadcast_receiver:true,app_key:" + (queryEngine.getKeyApp())) + "})-[:CLASS_OWNS_METHOD]->(m:Method{name:'onReceive'}) WHERE m.number_of_instructions > ") + (HeavyBroadcastReceiverQuery.high_noi)) + " AND  m.cyclomatic_complexity>") + (HeavyBroadcastReceiverQuery.high_cc)) + " return m as nod,m.app_key as app_key,m.cyclomatic_complexity as cyclomatic_complexity, m.number_of_instructions as number_of_instructions";
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
                if ((cc >= (HeavyBroadcastReceiverQuery.veryHigh_cc)) && (noi >= (HeavyBroadcastReceiverQuery.veryHigh_noi))) {
                    res.put("fuzzy_value", 1);
                }else {
                    fb.setVariable("cyclomatic_complexity", cc);
                    fb.setVariable("number_of_instructions", noi);
                    fb.evaluate();
                    res.put("fuzzy_value", fb.getVariable("res").getValue());
                }
                fuzzyResult.add(res);
            } 
            queryEngine.resultToCSV(fuzzyResult, "HBR");
            tx.success();
        }
    }
}
