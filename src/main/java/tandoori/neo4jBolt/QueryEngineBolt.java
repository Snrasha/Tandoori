package tandoori.neo4jBolt;

import java.io.IOException;
import codesmells.annotations.CC;
import java.util.ArrayList;
import org.neo4j.driver.v1.Transaction;
import codesmells.annotations.LM;
import java.util.List;
import codesmells.annotations.MIM;
import org.neo4j.driver.v1.StatementResult;
import java.util.Map;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;

@CC
public class QueryEngineBolt {
    protected Graph graph;

    protected Session session;

    protected long keyApp;

    protected static final String MATCHN = "MATCH (n:";

    protected static final String CLASS = "Class";

    protected static final String METHOD = "Method";

    protected static final String FUZZY = "fuzzy_value";

    protected static final String APPKEY = " {app_key: '";

    public QueryEngineBolt(long keyApp) {
        graph = new Graph();
        session = DriverBolt.getSession();
        this.keyApp = keyApp;
    }

    public Graph getGraph() {
        return this.graph;
    }

    public long getKeyApp() {
        return this.keyApp;
    }

    public void analyzedAppQuery() throws IOException {
        StatementResult result;
        try (Transaction tx = this.session.beginTransaction()) {
            result = tx.run((("MATCH (a:App {app_key:" + (this.keyApp)) + "}) RETURN  a.app_key as app_key, a.category as category,a.package as package, a.version_code as version_code, a.date_analysis as date_analysis,a.number_of_classes as number_of_classes,a.size as size,a.rating as rating,a.nb_download as nb_download, a.number_of_methods as number_of_methods, a.number_of_activities as number_of_activities,a.number_of_services as number_of_services,a.number_of_interfaces as number_of_interfaces,a.number_of_abstract_classes as number_of_abstract_classes,a.number_of_broadcast_receivers as number_of_broadcast_receivers,a.number_of_content_providers as number_of_content_providers, a.number_of_variables as number_of_variables, a.number_of_views as number_of_views, a.number_of_inner_classes as number_of_inner_classes, a.number_of_async_tasks as number_of_async_tasks"));
            resultToCSV(result, "_ANALYZED.csv");
            tx.success();
        }
    }

    public void getPropertyForAllApk(String nodeType, String property, String suffix) throws IOException {
        StatementResult result;
        try (Transaction tx = this.session.beginTransaction()) {
            String query = (((((QueryEngineBolt.MATCHN) + nodeType) + ") RETURN n.app_key as app_key, n.name as name, n.") + property) + " as ") + property;
            result = tx.run(query);
            resultToCSV(result, suffix);
            tx.success();
        }
    }

    public void getAllLCOM() throws IOException {
        getPropertyForAllApk(QueryEngineBolt.CLASS, "lack_of_cohesion_in_methods", "_ALL_LCOM.csv");
    }

    public void getAllClassComplexity() throws IOException {
        getPropertyForAllApk(QueryEngineBolt.CLASS, "class_complexity", "_ALL_CLASS_COMPLEXITY.csv");
    }

    public void getAllNumberOfMethods() throws IOException {
        getPropertyForAllApk(QueryEngineBolt.CLASS, "number_of_methods", "_ALL_NUMBER_OF_METHODS.csv");
    }

    public void getAllCyclomaticComplexity() throws IOException {
        getPropertyForAllApk(QueryEngineBolt.METHOD, "cyclomatic_complexity", "_ALL_CYCLOMATIC_COMPLEXITY.csv");
    }

    @LM
    public void resultToCSV(StatementResult result, String nameQuery) throws IOException {
        StatementResult result_query;
        try (Transaction tx = this.session.beginTransaction()) {
            Value val;
            LowNode nodeQueryData = new LowNode(nameQuery);
            nodeQueryData.addParameter(Graph.APPKEY, this.keyApp);
            result_query = tx.run(this.graph.create(nodeQueryData));
            Record record = result_query.next();
            Node node = record.get(Graph.NAMELABEL).asNode();
            LowNode nodeFastRel = new LowNode(nameQuery);
            nodeFastRel.setId(node.id());
            LowNode nodeQuery = new LowNode(Graph.LABELQUERY);
            nodeQuery.addParameter(Graph.APPKEY, this.keyApp);
            tx.run(this.graph.relation(nodeQuery, nodeFastRel, Graph.REL_CODESMELLS_CAS));
            LowNode nodeClass;
            long number = 0;
            while (result.hasNext()) {
                Record row = result.next();
                val = row.get("nod");
                if ((val != null) && (!(val.isNull()))) {
                    node = val.asNode();
                    nodeClass = new LowNode(node.labels().iterator().next());
                    nodeClass.setId(node.id());
                    LowNode nodeRel = new LowNode(Graph.REL_CAS_CODE);
                    val = row.get(QueryEngineBolt.FUZZY);
                    if ((val != null) && (!(val.isNull()))) {
                        nodeRel.addParameter(QueryEngineBolt.FUZZY, val.asObject());
                    }
                    number++;
                    tx.run(this.graph.relation(nodeFastRel, nodeClass, nodeRel));
                }
            } 
            nodeQueryData.addParameter("number", number);
            tx.run(this.graph.set(nodeFastRel, nodeQueryData));
            tx.success();
        }
    }

    @LM
    public void resultToCSV(List<Map> rows, String nameQuery) throws IOException {
        StatementResult result_query;
        try (Transaction tx = this.session.beginTransaction()) {
            Object val;
            LowNode nodeQueryData = new LowNode(nameQuery);
            nodeQueryData.addParameter(Graph.APPKEY, this.keyApp);
            result_query = tx.run(this.graph.create(nodeQueryData));
            Record record = result_query.next();
            Node node = record.get(Graph.NAMELABEL).asNode();
            LowNode nodeFastRel = new LowNode(nameQuery);
            nodeFastRel.setId(node.id());
            LowNode nodeQuery = new LowNode(Graph.LABELQUERY);
            nodeQuery.addParameter(Graph.APPKEY, this.keyApp);
            tx.run(this.graph.relation(nodeQuery, nodeFastRel, Graph.REL_CODESMELLS_CAS));
            LowNode nodeClass;
            long number = 0;
            for (Map<String, Object> row : rows) {
                val = row.get("nod");
                if (val != null) {
                    node = ((Node) (val));
                    nodeClass = new LowNode(node.labels().iterator().next());
                    nodeClass.setId(node.id());
                    LowNode nodeRel = new LowNode(Graph.REL_CAS_CODE);
                    val = row.get(QueryEngineBolt.FUZZY);
                    if (val != null) {
                        nodeRel.addParameter(QueryEngineBolt.FUZZY, val);
                    }
                    number++;
                    tx.run(this.graph.relation(nodeFastRel, nodeClass, nodeRel));
                }
            }
            nodeQueryData.addParameter("number", number);
            tx.run(this.graph.set(nodeFastRel, nodeQueryData));
            tx.success();
        }
    }

    @MIM
    public void statsToCSV(Map<String, Double> stats, String nameQuery) throws IOException {
    }

    public void deleteQuery(String appKey) throws IOException {
        try (Transaction tx = this.session.beginTransaction()) {
            tx.run((("MATCH (n {app_key: '" + appKey) + "'})-[r]-() DELETE n,r"));
            tx.success();
        }
    }

    private void deleteExternalClasses(String appKey) {
        deleteEntityOut(appKey, "ExternalClass", "ExternalMethod", "CLASS_OWNS_METHOD");
    }

    private void deleteExternalMethods(String appKey) {
        deleteEntityIn(appKey, "ExternalMethod", QueryEngineBolt.METHOD, "CALLS");
    }

    private void deleteCalls(String appKey) {
        deleteRelations(appKey, QueryEngineBolt.METHOD, QueryEngineBolt.METHOD, "CALLS");
    }

    private void deleteMethods(String appKey) {
        deleteEntityIn(appKey, QueryEngineBolt.METHOD, QueryEngineBolt.CLASS, "CLASS_OWNS_METHOD");
    }

    private void deleteClasses(String appKey) {
        deleteEntityIn(appKey, QueryEngineBolt.CLASS, "App", "APP_OWNS_CLASS");
    }

    private void deleteVariables(String appKey) {
        deleteEntityIn(appKey, "Variable", QueryEngineBolt.CLASS, "CLASS_OWNS_VARIABLE");
    }

    private void deleteArguments(String appKey) {
        deleteEntityIn(appKey, "Argument", QueryEngineBolt.METHOD, "METHOD_OWNS_ARGUMENT");
    }

    private void deleteUses(String appKey) {
        deleteRelations(appKey, QueryEngineBolt.METHOD, "Variable", "USES");
    }

    private void deleteImplements(String appKey) {
        deleteRelations(appKey, QueryEngineBolt.CLASS, QueryEngineBolt.CLASS, "IMPLEMENTS");
    }

    private void deleteExtends(String appKey) {
        deleteRelations(appKey, QueryEngineBolt.CLASS, QueryEngineBolt.CLASS, "EXTENDS");
    }

    private void deleteApp(String appKey) {
        try (Transaction tx = this.session.beginTransaction()) {
            String request = ("MATCH (n:App {app_key: '" + appKey) + "'}) DELETE n";
            tx.run(request);
            tx.success();
        }
    }

    private void deleteRelations(String appKey, String nodeType1, String nodeType2, String reltype) {
        try (Transaction tx = this.session.beginTransaction()) {
            String request = ((((((((((QueryEngineBolt.MATCHN) + nodeType1) + "  {app_key: '") + appKey) + "'})-[r:") + reltype) + "]->(m:") + nodeType2) + (QueryEngineBolt.APPKEY)) + appKey) + "'}) DELETE r";
            tx.run(request);
            tx.success();
        }
    }

    private void deleteEntityIn(String appKey, String nodeType1, String nodeType2, String reltype) {
        try (Transaction tx = this.session.beginTransaction()) {
            String request = ((((((((((QueryEngineBolt.MATCHN) + nodeType1) + (QueryEngineBolt.APPKEY)) + appKey) + "'})<-[r:") + reltype) + "]-(m:") + nodeType2) + (QueryEngineBolt.APPKEY)) + appKey) + "'}) DELETE n,r";
            tx.run(request);
            tx.success();
        }
    }

    private void deleteEntityOut(String appKey, String nodeType1, String nodeType2, String reltype) {
        try (Transaction tx = this.session.beginTransaction()) {
            String request = ((((((((((QueryEngineBolt.MATCHN) + nodeType1) + (QueryEngineBolt.APPKEY)) + appKey) + "'})-[r:") + reltype) + "]->(m:") + nodeType2) + (QueryEngineBolt.APPKEY)) + appKey) + "'}) DELETE n,r";
            tx.run(request);
            tx.success();
        }
    }

    public void deleteEntireApp(String appKey) {
        deleteExternalClasses(appKey);
        deleteExternalMethods(appKey);
        deleteUses(appKey);
        deleteCalls(appKey);
        deleteVariables(appKey);
        deleteArguments(appKey);
        deleteMethods(appKey);
        deleteImplements(appKey);
        deleteExtends(appKey);
        deleteClasses(appKey);
        deleteApp(appKey);
    }

    public List<String> findKeysFromPackageName(String appName) throws IOException {
        ArrayList<String> keys = new ArrayList<>();
        try (Transaction tx = this.session.beginTransaction()) {
            StatementResult result = tx.run((("MATCH (n:App) WHERE n.package='" + appName) + "' RETURN n.app_key as key"));
            while (result.hasNext()) {
                Map<String, Object> row = result.next().asMap();
                keys.add(((String) (row.get("key"))));
            } 
            tx.success();
        }
        return keys;
    }

    public void deleteEntireAppFromPackage(String name) throws IOException {
        List<String> keys = findKeysFromPackageName(name);
        for (String key : keys) {
            deleteEntireApp(key);
        }
    }

    public void countVariables() throws IOException {
        StatementResult result;
        try (Transaction ignored = this.session.beginTransaction()) {
            result = this.session.run("MATCH (n:Variable) return n.app_key as app_key, count(n) as nb_variables");
            resultToCSV(result, "_COUNT_VARIABLE.csv");
        }
    }

    public void countInnerClasses() throws IOException {
        StatementResult result;
        try (Transaction ignored = this.session.beginTransaction()) {
            result = this.session.run("MATCH (n:Class) WHERE has(n.is_inner_class) return n.app_key as app_key,count(n) as nb_inner_classes");
            resultToCSV(result, "_COUNT_INNER.csv");
        }
    }

    public void countAsyncClasses() throws IOException {
        StatementResult result;
        try (Transaction ignored = this.session.beginTransaction()) {
            result = this.session.run("MATCH (n:Class{parent_name:'android.os.AsyncTask'}) return n.app_key as app_key,count(n) as number_of_async");
            resultToCSV(result, "_COUNT_ASYNC.csv");
        }
    }

    public void countViews() throws IOException {
        StatementResult result;
        try (Transaction ignored = this.session.beginTransaction()) {
            result = this.session.run("MATCH (n:Class{parent_name:'android.view.View'}) return n.app_key as app_key,count(n) as number_of_views");
            resultToCSV(result, "_COUNT_VIEWS.csv");
        }
    }

    public void executeRequest(String request) throws IOException {
        StatementResult result;
        try (Transaction ignored = this.session.beginTransaction()) {
            result = this.session.run(request);
            resultToCSV(result, "_CUSTOM.csv");
        }
    }
}

