package tandoori.neo4jBolt;

import java.util.HashMap;
import java.util.ArrayList;
import org.neo4j.driver.v1.Transaction;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import java.io.IOException;
import codesmells.annotations.LM;
import java.util.List;
import java.util.Map;
import org.neo4j.driver.v1.StatementResult;

public class BLOBQuery extends FuzzyQuery {
    protected static double high_lcom = 25;

    protected static double veryHigh_lcom = 40;

    protected static double high_noa = 8.5;

    protected static double veryHigh_noa = 13;

    protected static double high_nom = 14.5;

    protected static double veryHigh_nom = 22;

    private BLOBQuery(QueryEngineBolt queryEngine) {
        super(queryEngine);
        fclFile = "/Blob.fcl";
    }

    public static BLOBQuery createBLOBQuery(QueryEngineBolt queryEngine) {
        return new BLOBQuery(queryEngine);
    }

    @Override
    public void execute(boolean details) throws IOException {
        StatementResult result;
        try (Transaction tx = this.session.beginTransaction()) {
            String query = ((((((("MATCH (cl:Class {app_key:" + (queryEngine.getKeyApp())) + "} ) WHERE cl.lack_of_cohesion_in_methods >") + (BLOBQuery.veryHigh_lcom)) + " AND cl.number_of_methods > ") + (BLOBQuery.veryHigh_nom)) + " AND cl.number_of_attributes > ") + (BLOBQuery.veryHigh_noa)) + " RETURN cl as nod,cl.app_key as app_key";
            if (details) {
                query += ",cl.name as full_name";
            }else {
                query += ",count(cl) as BLOB";
            }
            result = tx.run(query);
            queryEngine.resultToCSV(result, "BLOB_NO_FUZZY");
            tx.success();
        }
    }

    @Override
    @LM
    public void executeFuzzy(boolean details) throws IOException {
        StatementResult result;
        try (Transaction tx = this.session.beginTransaction()) {
            String query = ((((((("MATCH (cl:Class {app_key:" + (queryEngine.getKeyApp())) + "} ) WHERE cl.lack_of_cohesion_in_methods >") + (BLOBQuery.high_lcom)) + "  AND cl.number_of_methods > ") + (BLOBQuery.high_nom)) + " AND cl.number_of_attributes > ") + (BLOBQuery.high_noa)) + " RETURN cl as nod,cl.app_key as app_key,cl.lack_of_cohesion_in_methods as lack_of_cohesion_in_methods,cl.number_of_methods as number_of_methods, cl.number_of_attributes as number_of_attributes";
            if (details) {
                query += ",cl.name as full_name";
            }
            result = tx.run(query);
            int lcom;
            int noa;
            int nom;
            List<Map> fuzzyResult = new ArrayList<>();
            FunctionBlock fb = this.fuzzyFunctionBlock();
            while (result.hasNext()) {
                Map<String, Object> res = new HashMap<>(result.next().asMap());
                lcom = ((Long) (res.get("lack_of_cohesion_in_methods"))).intValue();
                noa = ((Long) (res.get("number_of_attributes"))).intValue();
                nom = ((Long) (res.get("number_of_methods"))).intValue();
                if (((lcom >= (BLOBQuery.veryHigh_lcom)) && (noa >= (BLOBQuery.veryHigh_noa))) && (nom >= (BLOBQuery.veryHigh_nom))) {
                    res.put("fuzzy_value", 1);
                }else {
                    fb.setVariable("lack_of_cohesion_in_methods", lcom);
                    fb.setVariable("number_of_attributes", noa);
                    fb.setVariable("number_of_methods", nom);
                    fb.evaluate();
                    res.put("fuzzy_value", fb.getVariable("res").getValue());
                }
                fuzzyResult.add(res);
            } 
            queryEngine.resultToCSV(fuzzyResult, "BLOB");
            tx.success();
        }
    }
}

