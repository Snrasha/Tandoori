package tandoori.neo4jBolt;

import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;

public class Graph {
    public static final String LABELAPP = "Code";

    public static final String LABELQUERY = "CodeSmells";

    public static final String REL_CAS_CODE = "HAS_CODESMELL";

    public static final String REL_VERSION_CODE = "IS_STRUCTURED";

    public static final String REL_VERSION_CODESMELLS = "EXHIBITS";

    public static final String REL_CODESMELLS_CAS = "RESULT";

    public static final String APPKEY = "app_key";

    public static final String CODEA = "code_is_analyzed";

    public static final String NAMELABEL = "target";

    public static final String VERSIONLABEL = "Version";

    public static final String ANALYSEINLOAD = "analyseInLoading";

    private static final String CREATEIT = "  CREATE (it)-[:";

    private static final String RETURN = " RETURN ";

    public String create(LowNode lowNode) {
        return (((((("CREATE (" + (Graph.NAMELABEL)) + ":") + (lowNode.getLabel())) + (lowNode.parametertoData())) + ")") + (Graph.RETURN)) + (Graph.NAMELABEL);
    }

    public long getID(StatementResult result, String labelNode) {
        if (((result != null) && (result.hasNext())) && (labelNode != null)) {
            Record record = result.next();
            Node node = record.get(labelNode).asNode();
            if (node != null) {
                return node.id();
            }
        }
        return -1;
    }

    public String relation(LowNode lowNode, LowNode lowNodeTarget, String relationLabel) {
        return ((((((matchPrefabs("it", lowNode)) + (matchPrefabs(Graph.NAMELABEL, lowNodeTarget))) + (Graph.CREATEIT)) + relationLabel) + "]->(") + (Graph.NAMELABEL)) + ")";
    }

    public String relation(LowNode lowNode, LowNode lowNodeTarget, LowNode lowNodeRelation) {
        return (((((((matchPrefabs("it", lowNode)) + (matchPrefabs(Graph.NAMELABEL, lowNodeTarget))) + (Graph.CREATEIT)) + (lowNodeRelation.getLabel())) + (lowNodeRelation.parametertoData())) + "]->(") + (Graph.NAMELABEL)) + ")";
    }

    public String relationcreateRight(LowNode lowNode, LowNode lowNodeTarget, String relationLabel) {
        return ((((((((matchPrefabs("it", lowNode)) + (Graph.CREATEIT)) + relationLabel) + "]->(") + (Graph.NAMELABEL)) + ":") + (lowNodeTarget.getLabel())) + (lowNodeTarget.parametertoData())) + ")";
    }

    public String matchPrefabs(String labelname, LowNode lowNode) {
        return (((((" MATCH (" + labelname) + ":") + (lowNode.getLabel())) + (lowNode.parametertoData())) + ") ") + (lowNode.idfocus(labelname));
    }

    public String matchSee(LowNode lowNode) {
        return ((matchPrefabs(Graph.NAMELABEL, lowNode)) + (Graph.RETURN)) + (Graph.NAMELABEL);
    }

    public String matchSee(LowNode lowNode, LowNode lowNodeTarget, String relationLabel) {
        return ((((((((matchPrefabs("a", lowNode)) + (matchPrefabs(Graph.NAMELABEL, lowNodeTarget))) + " MATCH (a)-[:") + relationLabel) + "]->(") + (Graph.NAMELABEL)) + ")") + (Graph.RETURN)) + (Graph.NAMELABEL);
    }

    public String set(LowNode lowNode, LowNode newAttributsNode) {
        String result = this.matchPrefabs(Graph.NAMELABEL, lowNode);
        StringBuilder str = newAttributsNode.parametertoData();
        if ((str.length()) != 0) {
            result += ((" SET " + (Graph.NAMELABEL)) + "+=") + str;
        }
        result += (Graph.RETURN) + (Graph.NAMELABEL);
        return result;
    }

    public String deleteDataAndAllChildrends(LowNode lowNode) {
        return ((this.matchPrefabs("n", lowNode)) + " MATCH (n)-[*]->(a)") + " DETACH DELETE n,a";
    }
}

