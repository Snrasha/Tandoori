package tandoori.neo4jBolt;

import java.util.HashMap;
import java.io.IOException;
import codesmells.annotations.LM;
import java.util.Map;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;

public class QuartileCalculator {
    protected QueryEngineBolt queryEngine;

    protected Session session;

    public QuartileCalculator(QueryEngineBolt queryEngine) {
        this.queryEngine = queryEngine;
        session = DriverBolt.getSession();
    }

    public void calculateClassComplexityQuartile() throws IOException {
        Map<String, Double> res;
        StatementResult result;
        try (Transaction tx = this.session.beginTransaction()) {
            String query = ("MATCH (n:Class  {app_key:" + (queryEngine.getKeyApp())) + "}) WHERE NOT EXISTS(n.is_interface)  AND NOT EXISTS(n.is_abstract) RETURN n as nod,percentileCont(n.class_complexity,0.25) as Q1, percentileCont(n.class_complexity,0.5) as MED, percentileCont(n.class_complexity,0.75) as Q3";
            result = tx.run(query);
            res = calculeTresholds(result);
            tx.success();
        }
        queryEngine.statsToCSV(res, "STAT_CLASS_COMPLEXITY");
    }

    public void calculateCyclomaticComplexityQuartile() throws IOException {
        Map<String, Double> res;
        StatementResult result;
        try (Transaction tx = this.session.beginTransaction()) {
            String query = ("MATCH (n:Method  {app_key:" + (queryEngine.getKeyApp())) + "}) WHERE NOT EXISTS(n.is_getter)  AND NOT EXISTS(n.is_setter) AND n.cyclomatic_complexity > 0 RETURN n as nod,percentileCont(n.cyclomatic_complexity,0.25) as Q1, percentileCont(n.cyclomatic_complexity,0.5) as MED, percentileCont(n.cyclomatic_complexity,0.75) as Q3";
            result = tx.run(query);
            res = calculeTresholds(result);
            tx.success();
        }
        queryEngine.statsToCSV(res, "STAT_CYCLOMATIC_COMPLEXITY");
    }

    public void calculateNumberofInstructionsQuartile() throws IOException {
        Map<String, Double> res;
        StatementResult result;
        try (Transaction tx = this.session.beginTransaction()) {
            String query = ("MATCH (n:Method  {app_key:" + (queryEngine.getKeyApp())) + "}) WHERE NOT EXISTS(n.is_getter)  AND NOT EXISTS(n.is_setter) AND n.number_of_instructions > 0 RETURN n as nod,percentileCont(n.number_of_instructions,0.25) as Q1, percentileCont(n.number_of_instructions,0.5) as MED, percentileCont(n.number_of_instructions,0.75) as Q3";
            result = tx.run(query);
            res = calculeTresholds(result);
            tx.success();
        }
        queryEngine.statsToCSV(res, "STAT_NB_INSTRUCTIONS");
    }

    public Map calculateQuartile(String nodeType, String property) {
        StatementResult result = null;
        try (Transaction tx = this.session.beginTransaction()) {
            String query = ((((((((("MATCH (n:" + nodeType) + " {app_key:") + (queryEngine.getKeyApp())) + "}) RETURN n as nod, percentileCont(n.") + property) + ",0.25) as Q1,percentileCont(n.") + property) + ",0.5) as MED, percentileCont(n.") + property) + ",0.75) as Q3";
            result = tx.run(query);
            tx.success();
        }
        return calculeTresholds(result);
    }

    @LM
    private Map calculeTresholds(StatementResult result) {
        Map<String, Double> res = new HashMap<>();
        while (result.hasNext()) {
            Map<String, Object> row = result.next().asMap();
            if ((row.get("Q1")) == null)
                continue;
            
            double q1 = Double.valueOf(row.get("Q1").toString());
            double med = Double.valueOf(row.get("MED").toString());
            double q3 = Double.valueOf(row.get("Q3").toString());
            double high = q3 + (1.5 * (q3 - q1));
            double very_high = q3 + (3 * (q3 - q1));
            res.put("Q1", q1);
            res.put("Q3", q3);
            res.put("MED", med);
            res.put("HIGH", high);
            res.put("VERY_HIGH", very_high);
        } 
        return res;
    }

    public void calculateNumberOfImplementedInterfacesQuartile() throws IOException {
        Map<String, Double> res;
        StatementResult result;
        try (Transaction tx = this.session.beginTransaction()) {
            String query = ("MATCH (n:Class  {app_key:" + (queryEngine.getKeyApp())) + "}) WHERE n.number_of_implemented_interfaces > 1   RETURN n as nod, percentileCont(n.number_of_implemented_interfaces,0.25) as Q1, percentileCont(n.number_of_implemented_interfaces,0.5) as MED, percentileCont(n.number_of_implemented_interfaces,0.75) as Q3";
            result = tx.run(query);
            res = calculeTresholds(result);
            tx.success();
        }
        queryEngine.statsToCSV(res, "STAT_NB_INTERFACES");
    }

    public void calculateNumberOfMethodsForInterfacesQuartile() throws IOException {
        Map<String, Double> res;
        StatementResult result;
        try (Transaction tx = this.session.beginTransaction()) {
            String query = ("MATCH (n:Class {app_key:" + (queryEngine.getKeyApp())) + "}) WHERE EXISTS(n.is_interface)  RETURN n as nod,percentileCont(n.number_of_methods,0.25) as Q1, percentileCont(n.number_of_methods,0.5) as MED, percentileCont(n.number_of_methods,0.75) as Q3";
            result = tx.run(query);
            res = calculeTresholds(result);
            tx.success();
        }
        queryEngine.statsToCSV(res, "STAT_NB_METHODS_INTERFACE");
    }

    public void calculateLackofCohesionInMethodsQuartile() throws IOException {
        Map<String, Double> res;
        StatementResult result;
        try (Transaction tx = this.session.beginTransaction()) {
            String query = ("MATCH (n:Class {app_key:" + (queryEngine.getKeyApp())) + "}) WHERE NOT EXISTS(n.is_interface) AND NOT EXISTS(n.is_abstract) RETURN n as nod,percentileCont(n.lack_of_cohesion_in_methods,0.25) as Q1, percentileCont(n.lack_of_cohesion_in_methods,0.5) as MED, percentileCont(n.lack_of_cohesion_in_methods,0.75) as Q3";
            result = tx.run(query);
            res = calculeTresholds(result);
            tx.success();
        }
        queryEngine.statsToCSV(res, "STAT_LCOM");
    }

    public void calculateNumberOfMethodsQuartile() throws IOException {
        Map<String, Double> res;
        StatementResult result;
        try (Transaction tx = this.session.beginTransaction()) {
            String query = ("MATCH (n:Class {app_key:" + (queryEngine.getKeyApp())) + "}) WHERE NOT EXISTS(n.is_interface) AND NOT EXISTS(n.is_abstract) RETURN n as nod,percentileCont(n.number_of_methods,0.25) as Q1, percentileCont(n.number_of_methods,0.5) as MED, percentileCont(n.number_of_methods,0.75) as Q3";
            result = tx.run(query);
            res = calculeTresholds(result);
            tx.success();
        }
        queryEngine.statsToCSV(res, "STAT_NB_METHODS");
    }

    public void calculateNumberOfAttributesQuartile() throws IOException {
        Map<String, Double> res;
        StatementResult result;
        try (Transaction tx = this.session.beginTransaction()) {
            String query = ("MATCH (n:Class {app_key:" + (queryEngine.getKeyApp())) + "}) WHERE NOT EXISTS(n.is_interface) AND NOT EXISTS(n.is_abstract) RETURN n as nod,percentileCont(n.number_of_attributes,0.25) as Q1, percentileCont(n.number_of_attributes,0.5) as MED, percentileCont(n.number_of_attributes,0.75) as Q3";
            result = tx.run(query);
            res = calculeTresholds(result);
            tx.success();
        }
        queryEngine.statsToCSV(res, "STAT_NB_ATTRIBUTES");
    }
}

